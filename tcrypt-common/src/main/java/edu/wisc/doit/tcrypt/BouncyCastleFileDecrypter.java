/**
 * Copyright 2012, Board of Regents of the University of
 * Wisconsin System. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Board of Regents of the University of Wisconsin
 * System licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package edu.wisc.doit.tcrypt;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.security.KeyPair;
import java.util.regex.Pattern;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.openssl.PEMKeyPair;

/**
 * Encrypts tokens using a public key
 * 
 * @author Eric Dalquist
 * @version $Revision: 187 $
 */
public class BouncyCastleFileDecrypter extends AbstractPublicKeyDecrypter implements FileDecrypter {
    public static final Pattern KEYFILE_SEPERATOR_PATTERN = Pattern.compile("\n");
    public static final int MAX_ENCRYPTED_KEY_FILE_SIZE = 500;
    
    public BouncyCastleFileDecrypter(AsymmetricKeyParameter publicKeyParam, AsymmetricKeyParameter privateKeyParam) {
        super(publicKeyParam, privateKeyParam);
    }

    public BouncyCastleFileDecrypter(KeyPair keyPair) throws IOException {
        super(keyPair);
    }

    public BouncyCastleFileDecrypter(PEMKeyPair keyPair) throws IOException {
        super(keyPair);
    }

    public BouncyCastleFileDecrypter(Reader privateKeyReader) throws IOException {
        super(privateKeyReader);
    }

    @Override
    public void decrypt(TarArchiveInputStream inputStream, OutputStream outputStream) throws Exception {
        final CipherParameters key = this.getCipherParameters(inputStream);

        final TarArchiveEntry encFileEntry = inputStream.getNextTarEntry();

        final BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
        cipher.init(false, key);

        final byte[] encryptedBytes = new byte[8192];
        final byte[] decryptedBytes = new byte[cipher.getOutputSize(encryptedBytes.length)];
        while (true) {
            final int inLength = inputStream.read(encryptedBytes);
            if (inLength < 0) {
                break;
            }
            
            final int outLength = cipher.processBytes(encryptedBytes, 0, inLength, decryptedBytes, 0);
            outputStream.write(decryptedBytes, 0, outLength);
        }

        final int length = cipher.doFinal(decryptedBytes, 0);
        outputStream.write(decryptedBytes, 0, length);
    }

    protected CipherParameters getCipherParameters(TarArchiveInputStream inputStream) throws IOException, InvalidCipherTextException, DecoderException {
        //Read keyfile.enc from the TAR
        final TarArchiveEntry keyFileEntry = inputStream.getNextTarEntry();
        if (keyFileEntry.getSize() > MAX_ENCRYPTED_KEY_FILE_SIZE) {
            throw new IllegalArgumentException("The encrypted archive's key file cannot be longer than " + MAX_ENCRYPTED_KEY_FILE_SIZE + " bytes");
        }
        
        //Decode the base64 keyfile
        final byte[] encKeyFileBase64Bytes = IOUtils.toByteArray(inputStream);
        final byte[] encKeyFileBytes = Base64.decodeBase64(encKeyFileBase64Bytes);
        
        //Decrypt the keyfile into UTF-8 String
        final AsymmetricBlockCipher decryptCipher = this.getDecryptCipher();
        final byte[] keyFileBytes = decryptCipher.processBlock(encKeyFileBytes, 0, encKeyFileBytes.length);
        final String keyFileStr = StringUtils.newStringUtf8(keyFileBytes);

        //Split the keyfile, use line 0 as the secretKey and line 1 as the initVector
        final String[] keyFileParts = KEYFILE_SEPERATOR_PATTERN.split(keyFileStr);
        final byte[] secretKey = Hex.decodeHex(keyFileParts[0].toCharArray());
        final byte[] initVector = Hex.decodeHex(keyFileParts[1].toCharArray());
        
        final KeyParameter keyParam = new KeyParameter(secretKey);
        return new ParametersWithIV(keyParam, initVector);
    }
}

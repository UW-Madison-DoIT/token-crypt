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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.security.PublicKey;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 * Encrypts tokens using a public key
 * 
 * @author Eric Dalquist
 * @version $Revision: 187 $
 */
public class BouncyCastleFileEncrypter extends AbstractPublicKeyEncrypter implements FileEncrypter {
    public static final int PASSWORD_LENGTH = 128;
    public static final int SALT_LENGTH = 8;
    public static final int KEY_LENGTH = 256;
    public static final int IV_LENGTH = 128;
    
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    public BouncyCastleFileEncrypter(AsymmetricKeyParameter publicKeyParam) {
        super(publicKeyParam);
    }

    public BouncyCastleFileEncrypter(PublicKey publicKey) throws IOException {
        super(publicKey);
    }

    public BouncyCastleFileEncrypter(Reader publicKeyReader) throws IOException {
        super(publicKeyReader);
    }

    public BouncyCastleFileEncrypter(SubjectPublicKeyInfo publicKey) throws IOException {
        super(publicKey);
    }

    @Override
    public void encrypt(String fileName, int size, InputStream inputStream, OutputStream outputStream) throws InvalidCipherTextException, IOException {
        final TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(outputStream, ENCODING);
        
        //Generate cipher parameters and key file
        final ParametersWithIV cipherParameters = generateKeyfile(tarArchiveOutputStream);
        
        //Create the cipher
        final BufferedBlockCipher cipher = this.getEncryptBlockCipher(cipherParameters);
        
        //Add the TAR entry and calculate the output size based on the input size
        final TarArchiveEntry encfileEntry = new TarArchiveEntry(fileName + ENC_SUFFIX);
        final int outputSize = cipher.getOutputSize(size);
        encfileEntry.setSize(outputSize);
        tarArchiveOutputStream.putArchiveEntry(encfileEntry);
        
        //Do streaming encryption, need to add CloseShieldOutputStream as cipherOutputStream.close(); must be called but we can't close the underlying tar stream 
        final CipherOutputStream cipherOutputStream = new CipherOutputStream(new CloseShieldOutputStream(tarArchiveOutputStream), cipher);
        IOUtils.copy(inputStream, cipherOutputStream);
        cipherOutputStream.close();
        tarArchiveOutputStream.closeArchiveEntry();
        
        //Close the TAR stream, nothing else shoulde be written to it
        tarArchiveOutputStream.close();
    }

    protected ParametersWithIV generateKeyfile(final TarArchiveOutputStream tarArchiveOutputStream) throws InvalidCipherTextException, IOException {
        //Generate a random password
        final byte[] passwordBytes = new byte[PASSWORD_LENGTH];
        SECURE_RANDOM.nextBytes(passwordBytes);
        final byte[] passwordBase64Bytes = Base64.encodeBase64(passwordBytes);
        final String passwordBase64String = new String(passwordBase64Bytes, CHARSET);
        
        //Generate a random salt
        final byte[] saltBytes = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(saltBytes);
        
        //Generate key & iv
        final OpenSSLPBEParametersGenerator generator = new OpenSSLPBEParametersGenerator();
        generator.init(PBEParametersGenerator.PKCS5PasswordToBytes(passwordBase64String.toCharArray()), saltBytes);
        final ParametersWithIV cipherParameters = (ParametersWithIV) generator.generateDerivedParameters(KEY_LENGTH, IV_LENGTH);
        final KeyParameter keyParameter = (KeyParameter)cipherParameters.getParameters();

        final byte[] keyBytes = keyParameter.getKey();
        final char[] keyHexChars = Hex.encodeHex(keyBytes);
        
        final byte[] ivBytes = cipherParameters.getIV();
        final char[] ivHexChars = Hex.encodeHex(ivBytes);
        
        //Create keyfile contents
        final String keyfileString = new StringBuilder(keyHexChars.length + 1 + ivHexChars.length)
                .append(keyHexChars)
                .append(KEYFILE_LINE_SEPERATOR)
                .append(ivHexChars)
                .toString();
        final byte[] keyfileBytes = keyfileString.getBytes(CHARSET);
        
        //Encrypt keyfile
        final AsymmetricBlockCipher encryptCipher = this.getEncryptCipher();
        final byte[] encryptedKeyfileBytes = encryptCipher.processBlock(keyfileBytes, 0, keyfileBytes.length);
        final byte[] encryptedKeyfileBase64Bytes = Base64.encodeBase64(encryptedKeyfileBytes);
        
        //Write encrypted keyfile to tar output stream
        final TarArchiveEntry keyfileEntry = new TarArchiveEntry(KEYFILE_ENC_NAME);
        keyfileEntry.setSize(encryptedKeyfileBase64Bytes.length);
        tarArchiveOutputStream.putArchiveEntry(keyfileEntry);
        tarArchiveOutputStream.write(encryptedKeyfileBase64Bytes);
        tarArchiveOutputStream.closeArchiveEntry();
        
        return cipherParameters;
    }
}

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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.security.KeyPair;
import java.util.regex.Pattern;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.commons.io.output.TeeOutputStream;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.GeneralDigest;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.io.DigestInputStream;
import org.bouncycastle.crypto.io.DigestOutputStream;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.openssl.PEMKeyPair;

/**
 * Decrypts whole files contained in specially formatted TAR files.
 * <br/>
 * The first entry in the tar file must be named {@link #KEYFILE_ENC_NAME} and contain two lines.
 * The file line is the secretKey and the second line is the initVector used in the block cipher
 * that encrypted the second entry in the tar file.
 * <br/>
 * The block cipher must be AES with CBC. The AES strenth is determined by the initVector.
 * 
 * 
 * @author Eric Dalquist
 * @version $Revision: 187 $
 */
public class BouncyCastleFileDecrypter extends AbstractPublicKeyDecrypter implements FileDecrypter {
    public static final Pattern KEYFILE_SEPERATOR_PATTERN = Pattern.compile(Character.toString(FileEncrypter.KEYFILE_LINE_SEPERATOR));
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
    public void decrypt(InputStream inputStream, OutputStream outputStream) throws InvalidCipherTextException, IOException, DecoderException {
        final TarArchiveInputStream tarInputStream = new TarArchiveInputStream(inputStream, FileEncrypter.ENCODING);
        
        final BufferedBlockCipher cipher = createCipher(tarInputStream);

        //Advance to the next entry in the tar file
        tarInputStream.getNextTarEntry();

        //Create digest output stream used to generate digest while decrypting
        final DigestOutputStream digestOutputStream = new DigestOutputStream(this.createDigester());
        
        //Do a streaming decryption of the file output
        final CipherOutputStream cipherOutputStream = new CipherOutputStream(new TeeOutputStream(outputStream, digestOutputStream), cipher);
        IOUtils.copy(tarInputStream, cipherOutputStream);
        cipherOutputStream.close();
        
        //Capture the hash of the decrypted output
        final byte[] hashBytes = digestOutputStream.getDigest();
        verifyOutputHash(tarInputStream, hashBytes);
    }
    
    @Override
    public InputStream decrypt(InputStream inputStream) throws InvalidCipherTextException, IOException, DecoderException {
        final TarArchiveInputStream tarInputStream = new TarArchiveInputStream(inputStream, FileEncrypter.ENCODING);
        
        final BufferedBlockCipher cipher = createCipher(tarInputStream);

        //Advance to the next entry in the tar file
        tarInputStream.getNextTarEntry();
        
        //Protect the underlying TAR stream from being closed by the cipher stream
        final CloseShieldInputStream is = new CloseShieldInputStream(tarInputStream);
        
        //Setup the decrypting cipher stream
        final CipherInputStream stream = new CipherInputStream(is, cipher);
        
        //Generate a digest of the decrypted data
        final GeneralDigest digest = this.createDigester();
        final DigestInputStream digestInputStream = new DigestInputStream(stream, digest);
        
        return new DecryptingInputStream(digestInputStream, tarInputStream, digest);
    }

    protected BufferedBlockCipher createCipher(final TarArchiveInputStream tarInputStream) throws IOException, InvalidCipherTextException, DecoderException {
        //Read the cipher parameters from the tar file
        final CipherParameters key = this.getCipherParameters(tarInputStream);

        //Get the block cipher used for decrypting
        return getDecryptBlockCipher(key);
    }

    protected void verifyOutputHash(final TarArchiveInputStream tarInputStream, final byte[] hashBytes) throws InvalidCipherTextException, IOException {
        final String actualHash = new String(Base64.encodeBase64(hashBytes), FileEncrypter.CHARSET);
        
        //Get the expected hash and verify
        final String expectedHash = this.getExpectedHash(tarInputStream);
        if (!expectedHash.equals(actualHash)) {
            throw new IllegalArgumentException("Hash " + actualHash + " doesn't match expected hash " + expectedHash + " for decrypted file. The data written to the OutputStream should be discarded.");
        }
    }

    protected String getExpectedHash(TarArchiveInputStream inputStream) throws InvalidCipherTextException, IOException {
        return readAndDecrypt(inputStream, FileEncrypter.HASHFILE_ENC_NAME).trim();
    }

    protected CipherParameters getCipherParameters(TarArchiveInputStream inputStream) throws IOException, InvalidCipherTextException, DecoderException {
        final String keyFileStr = readAndDecrypt(inputStream, FileEncrypter.KEYFILE_ENC_NAME);

        //Split the keyfile
        final String[] keyFileParts = KEYFILE_SEPERATOR_PATTERN.split(keyFileStr);
        if (keyFileParts.length != 2) {
            throw new IllegalArgumentException(FileEncrypter.KEYFILE_ENC_NAME + " must have exactly two lines, this one has: " + keyFileParts.length);
        }
        
        //line 0 is the secretKey, 1 is the initVector, 2 is the file md5
        final byte[] secretKey = Hex.decodeHex(keyFileParts[0].toCharArray());
        final byte[] initVector = Hex.decodeHex(keyFileParts[1].toCharArray());
        
        //Create the key parameters
        final KeyParameter keyParam = new KeyParameter(secretKey);
        return new ParametersWithIV(keyParam, initVector);
    }

    protected String readAndDecrypt(TarArchiveInputStream inputStream, final String fileName) throws IOException, InvalidCipherTextException {
        //Read keyfile.enc from the TAR  
        final TarArchiveEntry keyFileEntry = inputStream.getNextTarEntry();
        
        //Verify file name
        final String keyFileName = keyFileEntry.getName();
        if (!fileName.equals(keyFileName)) {
            throw new IllegalArgumentException("The first entry in the TAR must be name: " + fileName);
        }
        
        //Verify file size
        if (keyFileEntry.getSize() > MAX_ENCRYPTED_KEY_FILE_SIZE) {
            throw new IllegalArgumentException("The encrypted archive's key file cannot be longer than " + MAX_ENCRYPTED_KEY_FILE_SIZE + " bytes");
        }
        
        //Decode the base64 keyfile
        final byte[] encKeyFileBase64Bytes = IOUtils.toByteArray(inputStream);
        final byte[] encKeyFileBytes = Base64.decodeBase64(encKeyFileBase64Bytes);
        
        //Decrypt the keyfile into UTF-8 String
        final AsymmetricBlockCipher decryptCipher = this.getDecryptCipher();
        final byte[] keyFileBytes = decryptCipher.processBlock(encKeyFileBytes, 0, encKeyFileBytes.length);
        return new String(keyFileBytes, FileEncrypter.CHARSET);
    }
    
    private final class DecryptingInputStream extends FilterInputStream {
        private final TarArchiveInputStream tarInputStream;
        private final Digest digest;
        
        private DecryptingInputStream(InputStream is, TarArchiveInputStream tarInputStream, Digest digest) {
            super(is);
            
            this.tarInputStream = tarInputStream;
            this.digest = digest;
        }

        @Override
        public void close() throws IOException {
            //Complete the decryption
            super.close();
            
            //Verify the decrypted output
            byte[] hashBytes = new byte[digest.getDigestSize()];
            digest.doFinal(hashBytes, 0);
            try {
                verifyOutputHash(tarInputStream, hashBytes);
            }
            catch (InvalidCipherTextException e) {
                throw new IOException(e);
            }
        }
    }
}

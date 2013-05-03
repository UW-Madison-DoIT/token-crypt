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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.security.PublicKey;
import java.security.SecureRandom;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.commons.io.output.TeeOutputStream;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.GeneralDigest;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.io.DigestOutputStream;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 * Encrypts files using a public key
 * 
 * @author Eric Dalquist
 * @version $Revision: 187 $
 */
public class BouncyCastleFileEncrypter extends AbstractPublicKeyEncrypter implements FileEncrypter {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int PASSWORD_LENGTH = 128;
    private static final int SALT_LENGTH = 8;
    
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
        final TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(outputStream, 512, ENCODING);
        
        final BufferedBlockCipher cipher = createCipher(tarArchiveOutputStream);
        
        startEncryptedFile(fileName, size, tarArchiveOutputStream, cipher);
        
        //Setup cipher output stream, has to protect from close as the cipher stream must close but the tar cannot get closed yet
        final CipherOutputStream cipherOutputStream = new CipherOutputStream(new CloseShieldOutputStream(tarArchiveOutputStream), cipher);
        
        //Setup digester
        final DigestOutputStream digestOutputStream = new DigestOutputStream(this.createDigester());
        
        //Perform streaming encryption and hashing of the file
        IOUtils.copy(inputStream, new TeeOutputStream(cipherOutputStream, digestOutputStream));
        cipherOutputStream.close();
        
        tarArchiveOutputStream.closeArchiveEntry();
        
        //Capture the hash code of the encrypted file
        digestOutputStream.close();
        final byte[] hashBytes = digestOutputStream.getDigest();
        
        this.writeHashfile(tarArchiveOutputStream, hashBytes);
        
        //Close the TAR stream, nothing else should be written to it
        tarArchiveOutputStream.close();
    }
    
    @Override
    public OutputStream encrypt(String fileName, int size, OutputStream outputStream) throws InvalidCipherTextException, IOException, DecoderException {
        final TarArchiveOutputStream tarOutputStream = new TarArchiveOutputStream(outputStream, ENCODING);
        
        final BufferedBlockCipher cipher = createCipher(tarOutputStream);

        startEncryptedFile(fileName, size, tarOutputStream, cipher);
        
        //Protect the underlying TAR stream from being closed by the cipher stream
        final CloseShieldOutputStream closeShieldTarStream = new CloseShieldOutputStream(tarOutputStream);

        //Setup the encrypting cipher stream
        final CipherOutputStream chipherStream = new CipherOutputStream(closeShieldTarStream, cipher);

        //Generate a digest of the pre-encryption data
        final GeneralDigest digest = this.createDigester();
        final DigestOutputStream digestOutputStream = new DigestOutputStream(digest);

        //Write data to both the digester and cipher
        final TeeOutputStream teeStream = new TeeOutputStream(digestOutputStream, chipherStream);
        return new EncryptingOutputStream(teeStream, tarOutputStream, digest);
    }

    protected void startEncryptedFile(String fileName, int size, final TarArchiveOutputStream tarArchiveOutputStream, final BufferedBlockCipher cipher) throws IOException {
        //Add the TAR entry and calculate the output size based on the input size
        final TarArchiveEntry encfileEntry = new TarArchiveEntry(fileName + ENC_SUFFIX);
        final int outputSize = cipher.getOutputSize(size);
        encfileEntry.setSize(outputSize);
        tarArchiveOutputStream.putArchiveEntry(encfileEntry);
    }

    protected BufferedBlockCipher createCipher(final TarArchiveOutputStream tarArchiveOutputStream) throws InvalidCipherTextException, IOException {
        //Generate cipher parameters and key file
        final ParametersWithIV cipherParameters = generateParameters();
        
        //Write out the key file
        this.writeKeyfile(tarArchiveOutputStream, cipherParameters);
        
        //Create the cipher
        return this.getEncryptBlockCipher(cipherParameters);
    }
    
    protected ParametersWithIV generateParameters() throws InvalidCipherTextException, IOException {
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
        return (ParametersWithIV) generator.generateDerivedParameters(KEY_LENGTH, IV_LENGTH);
    }
    
    protected void writeKeyfile(TarArchiveOutputStream tarArchiveOutputStream, ParametersWithIV cipherParameters) throws IOException, InvalidCipherTextException {
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
        
        encryptAndWrite(tarArchiveOutputStream, keyfileString, KEYFILE_ENC_NAME);
    }
    
    protected void writeHashfile(TarArchiveOutputStream tarArchiveOutputStream, byte[] hashBytes) throws IOException, InvalidCipherTextException {
        final String hash = new String(Base64.encodeBase64(hashBytes), FileEncrypter.CHARSET);
        encryptAndWrite(tarArchiveOutputStream, hash, HASHFILE_ENC_NAME);
    }

    /**
     * Encrypts and encodes the string and writes it to the tar output stream with the specified file name
     */
    protected void encryptAndWrite(TarArchiveOutputStream tarArchiveOutputStream, String contents, String fileName) throws InvalidCipherTextException, IOException {
        final byte[] contentBytes = contents.getBytes(CHARSET);
        
        //Encrypt contents
        final AsymmetricBlockCipher encryptCipher = this.getEncryptCipher();
        final byte[] encryptedContentBytes = encryptCipher.processBlock(contentBytes, 0, contentBytes.length);
        final byte[] encryptedContentBase64Bytes = Base64.encodeBase64(encryptedContentBytes);
        
        //Write encrypted contents to tar output stream
        final TarArchiveEntry contentEntry = new TarArchiveEntry(fileName);
        contentEntry.setSize(encryptedContentBase64Bytes.length);
        tarArchiveOutputStream.putArchiveEntry(contentEntry);
        tarArchiveOutputStream.write(encryptedContentBase64Bytes);
        tarArchiveOutputStream.closeArchiveEntry();
    }
    
    private final class EncryptingOutputStream extends FilterOutputStream {
        private final TarArchiveOutputStream tarOutputStream;
        private final Digest digest;
        
        private EncryptingOutputStream(OutputStream is, TarArchiveOutputStream tarOutputStream, Digest digest) {
            super(is);
            
            this.tarOutputStream = tarOutputStream;
            this.digest = digest;
        }

        @Override
        public void close() throws IOException {
            //Complete the encryption
            super.close();
            
            //Close the tar entry
            tarOutputStream.closeArchiveEntry();
            
            //Write the hash of the plain file
            byte[] hashBytes = new byte[digest.getDigestSize()];
            digest.doFinal(hashBytes, 0);
            try {
                writeHashfile(tarOutputStream, hashBytes);
            }
            catch (InvalidCipherTextException e) {
                throw new IOException(e);
            }
            
            //Close the TAR stream, nothing else should be written to it
            tarOutputStream.close();
        }
    }
}

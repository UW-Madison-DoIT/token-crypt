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
import java.io.Reader;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.regex.Matcher;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.GeneralDigest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Base64;

/**
 * @author Eric Dalquist
 */
public class BouncyCastleTokenDecrypter extends BouncyCastleTokenEncrypter implements TokenDecrypter {
    private final AsymmetricKeyParameter privateKeyParam;
    
    /**
     * Create a token encrypter and decrypter using the specified public and private keys
     * 
     * @param publicKeyParam public key
     * @param privateKeyParam private key
     */
    public BouncyCastleTokenDecrypter(AsymmetricKeyParameter publicKeyParam, AsymmetricKeyParameter privateKeyParam) {
        super(publicKeyParam);
        
        if (!privateKeyParam.isPrivate()) {
            throw new IllegalArgumentException("Private key parameter must be private");
        }
        
        this.privateKeyParam = privateKeyParam;
    }

    /**
     * Create a token encrypter and decrypter using the specified key pair
     * 
     * @param keyPair The key pair to use
     */
    public BouncyCastleTokenDecrypter(KeyPair keyPair) throws IOException {
        super(keyPair.getPublic());
        
        this.privateKeyParam = PrivateKeyFactory.createKey(keyPair.getPrivate().getEncoded());

    }

    /**
     * Create a token encrypter and decrypter using the specified {@link Reader}, note the
     * caller is responsible for closing the Reader.
     * 
     * @param privateKeyReader Reader to load the {@link KeyPair} from
     */
    @SuppressWarnings("resource")
    public BouncyCastleTokenDecrypter(Reader privateKeyReader) throws IOException {
        this((KeyPair)new PEMReader(privateKeyReader).readObject());
    }
    
    @Override
    public boolean isEncryptedToken(String ciphertext) {
        return TOKEN_PATTERN.matcher(ciphertext).matches();
    }

    @Override
    public String decrypt(String ciphertext) throws InvalidCipherTextException {
        final Matcher tokenMatcher = TOKEN_PATTERN.matcher(ciphertext);
        if (tokenMatcher.matches()) {
            ciphertext = tokenMatcher.group(1);
        }
        else {
            throw new IllegalArgumentException("Specified ciphertext is not valid");
        }
        
        //Decode the cipher text
        final byte[] encryptedTokenWithHash = Base64.decode(ciphertext);
        
        //Decrypt the cipher text
        AsymmetricBlockCipher e = this.createCipher();
        e = this.addEncoding(e);
        e.init(false, privateKeyParam);
        final byte[] tokenWithHashBytes = e.processBlock(encryptedTokenWithHash, 0, encryptedTokenWithHash.length);

        //Split the decrypted text into the password and the hash
        final String tokenWithHash = new String(tokenWithHashBytes, CHARSET);
        final int seperatorIndex = tokenWithHash.lastIndexOf(SEPARATOR);
        if (seperatorIndex < 0) {
            throw new IllegalArgumentException("token/hash string doesn't contain seperator: " + SEPARATOR);
        }
        final byte[] passwordBytes = tokenWithHash.substring(0, seperatorIndex).getBytes(CHARSET);
        final byte[] passwordHashBytes = Base64.decode(tokenWithHash.substring(seperatorIndex + 1).getBytes(CHARSET));
        
        //Generate hash of the decrypted password
        final GeneralDigest digest = this.createDigester();
        digest.update(passwordBytes, 0, passwordBytes.length);
        final byte[] expectedHashBytes = new byte[digest.getDigestSize()];
        digest.doFinal(expectedHashBytes, 0);
        
        //Verify the generated hash against the decrypted hash
        if (!Arrays.equals(expectedHashBytes, passwordHashBytes)) {
            throw new IllegalArgumentException("Hash of the decrypted token does not match the decrypted hash");
        }
        
        return new String(passwordBytes, CHARSET); 
    }
}

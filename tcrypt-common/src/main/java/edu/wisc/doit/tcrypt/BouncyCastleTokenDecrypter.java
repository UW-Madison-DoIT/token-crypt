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

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.GeneralDigest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;

/**
 * @author Eric Dalquist
 */
public class BouncyCastleTokenDecrypter extends AbstractPublicKeyDecrypter implements TokenDecrypter {
    public BouncyCastleTokenDecrypter(AsymmetricKeyParameter publicKeyParam, AsymmetricKeyParameter privateKeyParam) {
        super(publicKeyParam, privateKeyParam);
    }

    public BouncyCastleTokenDecrypter(KeyPair keyPair) throws IOException {
        super(keyPair);
    }

    public BouncyCastleTokenDecrypter(PEMKeyPair keyPair) throws IOException {
        super(keyPair);
    }

    /**
     * Create a token encrypter and decrypter using the specified {@link Reader}, note the
     * caller is responsible for closing the Reader.
     * 
     * @param privateKeyReader Reader to load the {@link KeyPair} from
     */
    @SuppressWarnings("resource")
    public BouncyCastleTokenDecrypter(Reader privateKeyReader) throws IOException {
        this((PEMKeyPair)new PEMParser(privateKeyReader).readObject());
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
        final byte[] encryptedTokenWithHash = Base64.decodeBase64(ciphertext);
        
        final AsymmetricBlockCipher e = getDecryptCipher();
        final byte[] tokenWithHashBytes = e.processBlock(encryptedTokenWithHash, 0, encryptedTokenWithHash.length);

        //Split the decrypted text into the password and the hash
        final String tokenWithHash = new String(tokenWithHashBytes, BouncyCastleTokenEncrypter.CHARSET);
        final int seperatorIndex = tokenWithHash.lastIndexOf(BouncyCastleTokenEncrypter.SEPARATOR);
        if (seperatorIndex < 0) {
            throw new IllegalArgumentException("token/hash string doesn't contain seperator: " + BouncyCastleTokenEncrypter.SEPARATOR);
        }
        final byte[] passwordBytes = tokenWithHash.substring(0, seperatorIndex).getBytes(BouncyCastleTokenEncrypter.CHARSET);
        final byte[] passwordHashBytes = Base64.decodeBase64(tokenWithHash.substring(seperatorIndex + 1).getBytes(BouncyCastleTokenEncrypter.CHARSET));
        
        //Generate hash of the decrypted password
        final GeneralDigest digest = this.createDigester();
        digest.update(passwordBytes, 0, passwordBytes.length);
        final byte[] expectedHashBytes = new byte[digest.getDigestSize()];
        digest.doFinal(expectedHashBytes, 0);
        
        //Verify the generated hash against the decrypted hash
        if (!Arrays.equals(expectedHashBytes, passwordHashBytes)) {
            throw new IllegalArgumentException("Hash of the decrypted token does not match the decrypted hash");
        }
        
        return new String(passwordBytes, BouncyCastleTokenEncrypter.CHARSET); 
    }

    protected GeneralDigest createDigester() {
        return new MD5Digest();
    }
}

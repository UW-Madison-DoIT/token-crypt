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
import java.util.regex.Pattern;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.GeneralDigest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Base64;

/**
 * @author Eric Dalquist
 * @version $Revision: 187 $
 */
public class TokenDecrypter extends TokenEncrypter {
    public static final Pattern BASE64_PATTERN = Pattern.compile("(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{4})");
    public static final Pattern TOKEN_PATTERN = Pattern.compile(Pattern.quote(TOKEN_PREFIX) + "(" + BASE64_PATTERN.pattern() + ")" + Pattern.quote(TOKEN_SUFFIX));

    private final AsymmetricKeyParameter privateKeyParam;
    
    /**
     * @param publicKeyParam
     */
    public TokenDecrypter(AsymmetricKeyParameter publicKeyParam, AsymmetricKeyParameter privateKeyParam) {
        super(publicKeyParam);
        
        if (!privateKeyParam.isPrivate()) {
            throw new IllegalArgumentException("Private key parameter must be private");
        }
        
        this.privateKeyParam = privateKeyParam;
    }

    public TokenDecrypter(KeyPair keyPair) throws IOException {
        super(keyPair.getPublic());
        
        this.privateKeyParam = PrivateKeyFactory.createKey(keyPair.getPrivate().getEncoded());

    }

    public TokenDecrypter(Reader privateKeyReader) throws IOException {
        this((KeyPair)new PEMReader(privateKeyReader).readObject());
    }
    
    public boolean isEncryptedToken(String ciphertext) {
        return BASE64_PATTERN.matcher(ciphertext).matches() || TOKEN_PATTERN.matcher(ciphertext).matches();
    }

    public String decrypt(String ciphertext) throws InvalidCipherTextException {
        final Matcher tokenMatcher = TOKEN_PATTERN.matcher(ciphertext);
        if (tokenMatcher.matches()) {
            ciphertext = tokenMatcher.group(1);
        }
        else if (!BASE64_PATTERN.matcher(ciphertext).matches()) {
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

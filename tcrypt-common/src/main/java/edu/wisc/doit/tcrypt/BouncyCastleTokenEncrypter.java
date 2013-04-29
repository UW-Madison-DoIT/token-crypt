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
import java.security.PublicKey;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.GeneralDigest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/**
 * Encrypts tokens using a public key
 * 
 * @author Eric Dalquist
 * @version $Revision: 187 $
 */
public class BouncyCastleTokenEncrypter extends AbstractPublicKeyEncrypter implements TokenEncrypter {
    protected static final char SEPARATOR = ':';
    private static final byte[] SEPARATOR_BYTES = Character.toString(SEPARATOR).getBytes(CHARSET);
    
    public BouncyCastleTokenEncrypter(AsymmetricKeyParameter publicKeyParam) {
        super(publicKeyParam);
    }

    public BouncyCastleTokenEncrypter(PublicKey publicKey) throws IOException {
        super(publicKey);
    }

    public BouncyCastleTokenEncrypter(Reader publicKeyReader) throws IOException {
        super(publicKeyReader);
    }

    public BouncyCastleTokenEncrypter(SubjectPublicKeyInfo publicKey) throws IOException {
        super(publicKey);
    }

    @Override
    public String encrypt(String token) throws InvalidCipherTextException {
        //Convert the token into a byte[]
        final byte[] tokenBytes = token.getBytes(CHARSET);
        
        //Generate the Base64 encoded hash of the token
        final GeneralDigest digest = createDigester();
        digest.update(tokenBytes, 0, tokenBytes.length);
        final byte[] hashBytes = new byte[digest.getDigestSize()];
        digest.doFinal(hashBytes, 0);
        final byte[] encodedHashBytes = Base64.encodeBase64(hashBytes);
        
        //Create the pre-encryption byte[] to hold the token, separator, and hash
        final byte[] tokenWithHashBytes = new byte[tokenBytes.length + SEPARATOR_BYTES.length + encodedHashBytes.length];
        
        //Copy in password bytes
        System.arraycopy(tokenBytes, 0, tokenWithHashBytes, 0, tokenBytes.length);
        
        //Copy in separator bytes
        System.arraycopy(SEPARATOR_BYTES, 0, tokenWithHashBytes, tokenBytes.length, SEPARATOR_BYTES.length);
        
        //Copy in encoded hash bytes
        System.arraycopy(encodedHashBytes, 0, tokenWithHashBytes, tokenBytes.length + SEPARATOR_BYTES.length, encodedHashBytes.length);
        
        AsymmetricBlockCipher e = getEncryptCipher();

        //Encrypt the bytes
        final byte[] encryptedTokenWithHash = e.processBlock(tokenWithHashBytes, 0, tokenWithHashBytes.length);
        
        //Encode the encrypted data and convert it into a string
        final String encryptedToken = new String(Base64.encodeBase64(encryptedTokenWithHash), CHARSET);
        return TOKEN_PREFIX + encryptedToken + TOKEN_SUFFIX;
    }
}

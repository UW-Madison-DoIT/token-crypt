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
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.security.Security;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.GeneralDigest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Base64;

/**
 * Encrypts tokens using a public key
 * 
 * @author Eric Dalquist
 * @version $Revision: 187 $
 */
public class TokenEncrypter {
    public static final String TOKEN_PREFIX = "ENC(";
    public static final String TOKEN_SUFFIX = ")";
    public static final String ENCODING = "UTF-8";
    public static final Charset CHARSET = Charset.forName(ENCODING);
    public static final char SEPARATOR = ':';
    
    private static final byte[] SEPARATOR_BYTES = Character.toString(SEPARATOR).getBytes(CHARSET);
    
    static {
        //TODO hook to unregister?
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private final AsymmetricKeyParameter publicKeyParam;

    public TokenEncrypter(AsymmetricKeyParameter publicKeyParam) {
        if (publicKeyParam == null) {
            throw new IllegalArgumentException("publicKeyParam cannot be null");
        }
        this.publicKeyParam = publicKeyParam;
    }
    
    public TokenEncrypter(PublicKey publicKey) throws IOException {
        this(PublicKeyFactory.createKey(publicKey.getEncoded()));
    }
    
    public TokenEncrypter(Reader publicKeyReader) throws IOException {
        this((PublicKey)new PEMReader(publicKeyReader).readObject());
    }
    
    public String encrypt(String token) throws InvalidCipherTextException {
        return encrypt(token, true);
    }
    
    public String encrypt(String token, boolean addAffixes) throws InvalidCipherTextException {
        //Convert the token into a byte[]
        final byte[] tokenBytes = token.getBytes(CHARSET);
        
        //Generate the Base64 encoded hash of the token
        final GeneralDigest digest = createDigester();
        digest.update(tokenBytes, 0, tokenBytes.length);
        final byte[] hashBytes = new byte[digest.getDigestSize()];
        digest.doFinal(hashBytes, 0);
        final byte[] encodedHashBytes = Base64.encode(hashBytes);
        
        //Create the pre-encryption byte[] to hold the token, separator, and hash
        final byte[] tokenWithHashBytes = new byte[tokenBytes.length + SEPARATOR_BYTES.length + encodedHashBytes.length];
        
        //Copy in password bytes
        System.arraycopy(tokenBytes, 0, tokenWithHashBytes, 0, tokenBytes.length);
        
        //Copy in separator bytes
        System.arraycopy(SEPARATOR_BYTES, 0, tokenWithHashBytes, tokenBytes.length, SEPARATOR_BYTES.length);
        
        //Copy in encoded hash bytes
        System.arraycopy(encodedHashBytes, 0, tokenWithHashBytes, tokenBytes.length + SEPARATOR_BYTES.length, encodedHashBytes.length);
        
        //Setup the encrypting cypher
        AsymmetricBlockCipher e = createCipher();
        e = addEncoding(e);
        e.init(true, publicKeyParam);

        //Encrypt the bytes
        final byte[] encryptedTokenWithHash = e.processBlock(tokenWithHashBytes, 0, tokenWithHashBytes.length);
        
        //Encode the encrypted data and convert it into a string
        final String encryptedToken = new String(Base64.encode(encryptedTokenWithHash), CHARSET);
        if (!addAffixes) {
            return encryptedToken;
        }
        
        return TOKEN_PREFIX + encryptedToken + TOKEN_SUFFIX;
    }

    protected AsymmetricBlockCipher addEncoding(AsymmetricBlockCipher e) {
        return new PKCS1Encoding(e);
    }

    protected GeneralDigest createDigester() {
        return new MD5Digest();
    }

    protected AsymmetricBlockCipher createCipher() {
        return new RSAEngine();
    }
}

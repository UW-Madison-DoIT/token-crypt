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
import java.security.Security;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.digests.GeneralDigest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;

public abstract class AbstractPublicKeyEncrypter {
    static {
        //TODO hook to unregister?
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private final AsymmetricKeyParameter publicKeyParam;

    /**
     * Create an encrypter using the specified public key
     * 
     * @param publicKeyParam public key
     */
    public AbstractPublicKeyEncrypter(AsymmetricKeyParameter publicKeyParam) {
        if (publicKeyParam == null) {
            throw new IllegalArgumentException("publicKeyParam cannot be null");
        }
        this.publicKeyParam = publicKeyParam;
    }

    /**
     * Create an encrypter using the specified public key
     * 
     * @param keyPair The public key to use
     */
    public AbstractPublicKeyEncrypter(PublicKey publicKey) throws IOException {
        this(PublicKeyFactory.createKey(publicKey.getEncoded()));
    }

    /**
     * Create an encrypter using the specified public key
     * 
     * @param keyPair The public key to use
     */
    public AbstractPublicKeyEncrypter(SubjectPublicKeyInfo publicKey) throws IOException {
        this(PublicKeyFactory.createKey(publicKey.getEncoded()));
    }

    /**
     * Create an encrypter specified {@link Reader}, note the
     * caller is responsible for closing the Reader.
     * 
     * @param publicKeyReader Reader to load the {@link PublicKey} from
     */
    @SuppressWarnings("resource")
    public AbstractPublicKeyEncrypter(Reader publicKeyReader) throws IOException {
        this((SubjectPublicKeyInfo)new PEMParser(publicKeyReader).readObject());
    }

    protected final AsymmetricKeyParameter getPublicKeyParam() {
        return publicKeyParam;
    }

    protected AsymmetricBlockCipher getEncryptCipher() {
        //Setup the encrypting cipher
        AsymmetricBlockCipher e = createCipher();
        e = addEncoding(e);
        e.init(true, this.getPublicKeyParam());
        return e;
    }

    protected BufferedBlockCipher getEncryptBlockCipher(final CipherParameters cipherParameters) {
        final BufferedBlockCipher cipher = this.createBlockCipher();
        cipher.init(true, cipherParameters);
        return cipher;
    }
    
    protected BufferedBlockCipher createBlockCipher() {
        return new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
    }

    protected AsymmetricBlockCipher createCipher() {
        return new RSAEngine();
    }

    protected AsymmetricBlockCipher addEncoding(AsymmetricBlockCipher e) {
        return new PKCS1Encoding(e);
    }

    protected GeneralDigest createDigester() {
        return new MD5Digest();
    }
}

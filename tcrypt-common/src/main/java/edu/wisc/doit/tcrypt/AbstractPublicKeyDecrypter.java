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

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;

public abstract class AbstractPublicKeyDecrypter extends AbstractPublicKeyEncrypter {
    private final AsymmetricKeyParameter privateKeyParam;
    
    /**
     * Create an encrypter and decrypter using the specified public and private keys
     * 
     * @param publicKeyParam public key
     * @param privateKeyParam private key
     */
    public AbstractPublicKeyDecrypter(AsymmetricKeyParameter publicKeyParam, AsymmetricKeyParameter privateKeyParam) {
        super(publicKeyParam);
        
        if (!privateKeyParam.isPrivate()) {
            throw new IllegalArgumentException("Private key parameter must be private");
        }
        
        this.privateKeyParam = privateKeyParam;
    }

    /**
     * Create an encrypter and decrypter using the specified key pair
     * 
     * @param keyPair The key pair to use
     */
    public AbstractPublicKeyDecrypter(KeyPair keyPair) throws IOException {
        super(keyPair.getPublic());
        
        this.privateKeyParam = PrivateKeyFactory.createKey(keyPair.getPrivate().getEncoded());
    }

    /**
     * Create an encrypter and decrypter using the specified key pair
     * 
     * @param keyPair The key pair to use
     */
    public AbstractPublicKeyDecrypter(PEMKeyPair keyPair) throws IOException {
        super(keyPair.getPublicKeyInfo());
        
        this.privateKeyParam = PrivateKeyFactory.createKey(keyPair.getPrivateKeyInfo());
    }

    /**
     * Create an encrypter and decrypter using the specified {@link Reader}, note the
     * caller is responsible for closing the Reader.
     * 
     * @param privateKeyReader Reader to load the {@link KeyPair} from
     */
    @SuppressWarnings("resource")
    public AbstractPublicKeyDecrypter(Reader privateKeyReader) throws IOException {
        this((PEMKeyPair)new PEMParser(privateKeyReader).readObject());
    }

    protected final AsymmetricKeyParameter getPrivateKeyParam() {
        return privateKeyParam;
    }

    protected AsymmetricBlockCipher getDecryptCipher() {
        //Decrypt the cipher text
        AsymmetricBlockCipher e = this.createCipher();
        e = this.addEncoding(e);
        e.init(false, this.getPrivateKeyParam());
        return e;
    }

    protected BufferedBlockCipher getDecryptBlockCipher(final CipherParameters key) {
        final BufferedBlockCipher cipher = this.createBlockCipher();
        cipher.init(false, key);
        return cipher;
    }
}

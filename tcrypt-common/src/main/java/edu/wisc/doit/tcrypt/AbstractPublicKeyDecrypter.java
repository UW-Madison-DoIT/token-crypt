package edu.wisc.doit.tcrypt;

import java.io.IOException;
import java.io.Reader;
import java.security.KeyPair;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
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
}

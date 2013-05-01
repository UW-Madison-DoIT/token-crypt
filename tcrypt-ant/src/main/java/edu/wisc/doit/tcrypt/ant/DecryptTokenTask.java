package edu.wisc.doit.tcrypt.ant;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Resource;
import org.bouncycastle.crypto.InvalidCipherTextException;

import edu.wisc.doit.tcrypt.BouncyCastleTokenDecrypter;
import edu.wisc.doit.tcrypt.TokenDecrypter;
import edu.wisc.doit.tcrypt.TokenEncrypter;

/**
 * Encrypts a token using the public key
 * 
 * @author Eric Dalquist
 */
public class DecryptTokenTask extends Task {
    private Resource privateKey;
    private String token;
    
    /**
     * @param privateKey The private key used to decrypt
     */
    public void setPrivateKey(Resource privateKey) {
        this.privateKey = privateKey;
    }
    
    /**
     * @param token The token to encrypt
     */
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void execute() throws BuildException {
        final TokenDecrypter tokenDecrypter;
        Reader publicKeyReader = null;
        try {
            publicKeyReader = new InputStreamReader(new BufferedInputStream(this.privateKey.getInputStream()), TokenEncrypter.CHARSET);
            tokenDecrypter = new BouncyCastleTokenDecrypter(publicKeyReader);
        }
        catch (IOException e) {
            throw new BuildException("Failed to create BouncyCastleTokenDecrypter for public key: " + this.privateKey, e);
        }
        finally {
            IOUtils.closeQuietly(publicKeyReader);
        }
        
        try {
            final String decryptedToken = tokenDecrypter.decrypt(this.token);
            log("Decrypted token to: " + decryptedToken);
        }
        catch (InvalidCipherTextException e) {
            throw new BuildException("Private key '" + this.privateKey + "' is invalid", e);
        }
    }
}

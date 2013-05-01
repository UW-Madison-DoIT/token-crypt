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

import edu.wisc.doit.tcrypt.BouncyCastleTokenEncrypter;
import edu.wisc.doit.tcrypt.TokenEncrypter;

/**
 * Encrypts a token using the public key
 * 
 * @author Eric Dalquist
 */
public class EncryptTokenTask extends Task {
    private Resource publicKey;
    private String token;
    
    /**
     * @param publicKey The public key used to encrypt
     */
    public void setPublicKey(Resource privateKey) {
        this.publicKey = privateKey;
    }
    
    /**
     * @param token The token to encrypt
     */
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void execute() throws BuildException {
        final TokenEncrypter tokenEncrypter;
        Reader publicKeyReader = null;
        try {
            publicKeyReader = new InputStreamReader(new BufferedInputStream(this.publicKey.getInputStream()), TokenEncrypter.CHARSET);
            tokenEncrypter = new BouncyCastleTokenEncrypter(publicKeyReader);
        }
        catch (IOException e) {
            throw new BuildException("Failed to create BouncyCastleTokenEncrypter for public key: " + this.publicKey, e);
        }
        finally {
            IOUtils.closeQuietly(publicKeyReader);
        }
        
        try {
            final String encryptedToken = tokenEncrypter.encrypt(this.token);
            log("Encrypted token to: " + encryptedToken);
        }
        catch (InvalidCipherTextException e) {
            throw new BuildException("Public key '" + this.publicKey + "' is invalid", e);
        }
    }
}

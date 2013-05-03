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

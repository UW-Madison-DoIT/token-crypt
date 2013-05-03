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
package edu.wisc.doit.tcrypt.ant.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.types.Parameter;
import org.bouncycastle.crypto.InvalidCipherTextException;

import edu.wisc.doit.tcrypt.BouncyCastleTokenDecrypter;
import edu.wisc.doit.tcrypt.TokenDecrypter;
import edu.wisc.doit.tcrypt.TokenEncrypter;

/**
 * Filter that can decrypt encrypted tokens
 * 
 * @author Eric Dalquist
 */
public class DecryptReplaceTokens extends AbstractReplaceTokens {
    /** Parameter name for the privateKey. */
    private static final String PRIVATE_KEY_PARAM = "privateKey";
    
    private TokenDecrypter tokenDecrypter;
    private File privateKeyFile;

    public DecryptReplaceTokens() {
        this.setBeginToken(TokenEncrypter.TOKEN_PREFIX);
        this.setEndToken(TokenEncrypter.TOKEN_SUFFIX);
    }
    
    public DecryptReplaceTokens(Reader in) {
        super(in);
        this.setBeginToken(TokenEncrypter.TOKEN_PREFIX);
        this.setEndToken(TokenEncrypter.TOKEN_SUFFIX);
    }

    /**
     * Used for {@link ChainableReader#chain(Reader)}
     */
    private DecryptReplaceTokens(Reader in, DecryptReplaceTokens source) {
        super(in, source);
        this.tokenDecrypter = source.tokenDecrypter;
    }
    
    @Override
    Reader createChainedReader(Reader rdr) {
        return new DecryptReplaceTokens(rdr, this);
    }

    @Override
    void initialize() {
        final Parameter[] parameters = getParameters();
        if (parameters != null) {
            for (final Parameter param : parameters) {
                if (PRIVATE_KEY_PARAM.equals(param.getName())) {
                    privateKeyFile = new File(param.getValue());
                }
            }
        }
        
        this.getProject().log("privateKeyFile: " + this.privateKeyFile, Project.MSG_DEBUG);
        
        if (privateKeyFile == null) {
            throw new IllegalArgumentException("No '" + PRIVATE_KEY_PARAM + "' parameter specified");
        }
        
        if (!privateKeyFile.isAbsolute()) {
            privateKeyFile = new File(this.getProject().getBaseDir(), privateKeyFile.getPath());
        }
        
        try {
            privateKeyFile = privateKeyFile.getCanonicalFile();
        }
        catch (IOException e) {
            //Ignore and hope the file is still resolvable
        }
        
        if (!privateKeyFile.exists()) {
            throw new BuildException(PRIVATE_KEY_PARAM + " '" + privateKeyFile + "' does not exist");
        }
        if (!privateKeyFile.canRead()) {
            throw new BuildException(PRIVATE_KEY_PARAM + " '" + privateKeyFile + "' is not readable");
        }
        if (privateKeyFile.isDirectory()) {
            throw new BuildException(PRIVATE_KEY_PARAM + " '" + privateKeyFile + "' is a directory");
        }
        
        Reader privateKeyReader = null;
        try {
            privateKeyReader = new BufferedReader(new FileReader(this.privateKeyFile));
            this.tokenDecrypter = new BouncyCastleTokenDecrypter(privateKeyReader);
        }
        catch (IOException e) {
            throw new BuildException("Failed to read " + PRIVATE_KEY_PARAM + " from: " + this.privateKeyFile, e);
        }
        finally {
            IOUtils.closeQuietly(privateKeyReader);
        }
    }

    @Override
    CharSequence replaceToken(String token) {
        try {
            return this.tokenDecrypter.decrypt(token);
        }
        catch (IllegalArgumentException e) {
            throw new TokenReplacmentFailureException(e);
        }
        catch (InvalidCipherTextException e) {
            throw new TokenReplacmentFailureException("Failed to decrypt '" + token + "' using private key: " + this.privateKeyFile, e);
        }
    }
}

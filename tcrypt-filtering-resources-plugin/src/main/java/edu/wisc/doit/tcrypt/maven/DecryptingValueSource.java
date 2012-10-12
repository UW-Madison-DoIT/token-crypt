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
package edu.wisc.doit.tcrypt.maven;

import java.util.Collections;
import java.util.List;

import org.codehaus.plexus.interpolation.ValueSource;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import edu.wisc.doit.tcrypt.TokenDecrypter;

/**
 * Uses a {@link TokenDecrypter} to decrypt password tokens when filtering resources
 * 
 * @author Eric Dalquist
 * @version $Revision: 196 $
 */
public class DecryptingValueSource extends AbstractLogEnabled implements ValueSource {
    private final TokenDecrypter tokenDecrypter;
    
    public DecryptingValueSource(TokenDecrypter tokenDecrypter) {
        if (tokenDecrypter == null) {
            throw new IllegalArgumentException("TokenDecrypter cannot be null");
        }
        this.tokenDecrypter = tokenDecrypter;
    }
    
    @Override
    public Object getValue(String expression) {
        if (!this.tokenDecrypter.isEncryptedToken(expression)) {
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("Not an encrypted token: " + expression);
            }
            return null;
        }
        
        try {
            final String token = tokenDecrypter.decrypt(expression);
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("Decrypted: " + expression);
            }
            return token;
        }
        catch (Exception e) {
            this.getLogger().warn("Faild to decrypt " + expression, e);
            return null;
        }
    }
    
    @Override
    public List<?> getFeedback() {
        return Collections.EMPTY_LIST;
    }
    
    @Override
    public void clearFeedback() {
    }
}

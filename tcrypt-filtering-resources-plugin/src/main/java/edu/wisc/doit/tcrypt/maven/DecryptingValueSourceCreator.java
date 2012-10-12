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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.codehaus.plexus.interpolation.ValueSource;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import edu.wisc.doit.tcrypt.TokenDecrypter;

/**
 * Creates a {@link DecryptingValueSource} based on the private key specified in the project's
 * properties
 * 
 * @author Eric Dalquist
 * @version $Revision: 187 $
 * 
 * @plexus.component role="edu.wisc.doit.tcrypt.maven.ValueSourceCreator"
 *                   role-hint="decrypt"
 */
public class DecryptingValueSourceCreator extends AbstractLogEnabled implements ValueSourceCreator {
    public static final String PRIVATE_KEY_PROP = "project.resources.tcrypt.privateKey";

    @Override
    public ValueSource createValueSource(MavenResourcesExecution mavenResourcesExecution) {
        final Properties projectProperties = mavenResourcesExecution.getMavenProject().getProperties();

        final String defaultKeyFile = projectProperties.getProperty(PRIVATE_KEY_PROP);
        final TokenDecrypter defaultTokenDecrypter;
        if (defaultKeyFile != null && defaultKeyFile.length() > 0) {
            defaultTokenDecrypter = this.createTokenDecrypter(defaultKeyFile);
        }
        else {
            this.getLogger().warn("No " + PRIVATE_KEY_PROP + " project property specified, token decryption will NOT be performed");
            return null;
        }
        
        final DecryptingValueSource decryptingValueSource = new DecryptingValueSource(defaultTokenDecrypter);
        decryptingValueSource.enableLogging(this.getLogger());
        return decryptingValueSource;
    }

    protected TokenDecrypter createTokenDecrypter(final String keyFile) {
        try {
            return new TokenDecrypter(new FileReader(keyFile));
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to find private key file: " + keyFile, e);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to parse private key file: " + keyFile, e);
        }
    }

}

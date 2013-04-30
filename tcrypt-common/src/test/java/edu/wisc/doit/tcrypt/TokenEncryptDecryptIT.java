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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Eric Dalquist
 * @version $Revision: 187 $
 */
public class TokenEncryptDecryptIT {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    private TokenEncrypter tokenEncrypter;
    private TokenDecrypter tokenDecrypter;
    
    @Before
    public void setup() throws IOException {
        tokenEncrypter = new BouncyCastleTokenEncrypter(new InputStreamReader(this.getClass().getResourceAsStream("/my.wisc.edu-public.pem")));
        tokenDecrypter = new BouncyCastleTokenDecrypter(new InputStreamReader(this.getClass().getResourceAsStream("/my.wisc.edu-private.pem")));
    }
    
    @Test
    public void testOpenSSLEncJavaDec() throws Exception {
        //Encrypt with openssl
        final File encryptFileScript = setupTempFile("encryptToken.sh");
        encryptFileScript.setExecutable(true);

        final File publicKey = setupTempFile("my.wisc.edu-public.pem");
        
        final String expected = "foobar";
        final ProcessBuilder pb = new ProcessBuilder(encryptFileScript.getAbsolutePath(), publicKey.getAbsolutePath(), expected);
        
        final Process p = pb.start();
        final int ret = p.waitFor();
        assertEquals(0, ret);
        
        final String encrypted = IOUtils.toString(p.getInputStream(), TokenEncrypter.CHARSET).trim();
        
        //Decrypt with java
        final String actual = this.tokenDecrypter.decrypt(encrypted);
        
        //Verify
        assertEquals(expected, actual);
    }
    
    @Test
    public void testJavaEncOpenSSLDec() throws Exception {
        //Encrypt with Java
        final String expected = "foobar";
        final String encrypted = this.tokenEncrypter.encrypt(expected);
        
        //Decrypt with OpenSSL
        final File decryptFileScript = setupTempFile("decryptToken.sh");
        decryptFileScript.setExecutable(true);
        
        final File privateKey = setupTempFile("my.wisc.edu-private.pem");
        
        final ProcessBuilder pb = new ProcessBuilder(decryptFileScript.getAbsolutePath(), privateKey.getAbsolutePath(), encrypted);
        
        final Process p = pb.start();
        final int ret = p.waitFor();
        assertEquals(0, ret);
        
        final String actual = IOUtils.toString(p.getInputStream(), TokenEncrypter.CHARSET).trim();
        
        //Verify
        assertEquals(expected, actual);
    }

    protected File setupTempFile(final String fileName) throws IOException {
        final File encryptFileScript = testFolder.newFile(fileName);
        FileUtils.copyInputStreamToFile(this.getClass().getResourceAsStream("/" + fileName), encryptFileScript);
        return encryptFileScript;
    }
}

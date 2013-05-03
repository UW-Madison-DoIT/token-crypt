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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

public class BouncyCastleFileEncrypterDecrypterTest {
    private FileEncrypter fileEncrypter;
    private FileDecrypter fileDecrypter;
    
    @Before
    public void setup() throws IOException {
        fileEncrypter = new BouncyCastleFileEncrypter(new InputStreamReader(this.getClass().getResourceAsStream("/my.wisc.edu-public.pem")));
        fileDecrypter = new BouncyCastleFileDecrypter(new InputStreamReader(this.getClass().getResourceAsStream("/my.wisc.edu-private.pem")));
    }
    
    @Test
    public void testExistingFileDecryption() throws Exception {
        final InputStream encTestFileInStream = this.getClass().getResourceAsStream("/testFile.txt.tar");
        final ByteArrayOutputStream testFileOutStream = new ByteArrayOutputStream();
        fileDecrypter.decrypt(encTestFileInStream, testFileOutStream);
        final String actual = new String(testFileOutStream.toByteArray(), Charset.defaultCharset()).trim();
        
        final InputStream testFileInStream = this.getClass().getResourceAsStream("/testFile.txt");
        final String expected = IOUtils.toString(testFileInStream).trim();
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testExistingFileDecryptionInputStream() throws Exception {
        final InputStream encTestFileInStream = this.getClass().getResourceAsStream("/testFile.txt.tar");
        final InputStream decFileInStream = fileDecrypter.decrypt(encTestFileInStream);
        
        final ByteArrayOutputStream testFileOutStream = new ByteArrayOutputStream();
        IOUtils.copy(decFileInStream, testFileOutStream);
        decFileInStream.close();
        
        final String actual = new String(testFileOutStream.toByteArray(), Charset.defaultCharset()).trim();
        
        final InputStream testFileInStream = this.getClass().getResourceAsStream("/testFile.txt");
        final String expected = IOUtils.toString(testFileInStream).trim();
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testFileEncryptDecrypt() throws Exception {
        InputStream testFileInStream = this.getClass().getResourceAsStream("/testFile.txt");
        final ByteArrayOutputStream testFileBuffer = new ByteArrayOutputStream();
        IOUtils.copy(testFileInStream, testFileBuffer);
        final byte[] testFileBytes = testFileBuffer.toByteArray();
        
        final ByteArrayOutputStream encTestFileOutStream = new ByteArrayOutputStream();
        this.fileEncrypter.encrypt("testFile.txt", testFileBytes.length, new ByteArrayInputStream(testFileBytes), encTestFileOutStream);
        
        final ByteArrayOutputStream decTestFileOutStream = new ByteArrayOutputStream();
        this.fileDecrypter.decrypt(new ByteArrayInputStream(encTestFileOutStream.toByteArray()), decTestFileOutStream);
        
        
        testFileInStream = this.getClass().getResourceAsStream("/testFile.txt");
        final String expected = IOUtils.toString(testFileInStream);
        final String actual = IOUtils.toString(new ByteArrayInputStream(decTestFileOutStream.toByteArray()));

        assertEquals(expected, actual);
    }
    
    @Test
    public void testFileEncryptOutputStreamDecrypt() throws Exception {
        InputStream testFileInStream = this.getClass().getResourceAsStream("/testFile.txt");
        final ByteArrayOutputStream testFileBuffer = new ByteArrayOutputStream();
        IOUtils.copy(testFileInStream, testFileBuffer);
        final byte[] testFileBytes = testFileBuffer.toByteArray();
        
        final ByteArrayOutputStream encTestFileOutStream = new ByteArrayOutputStream();
        final OutputStream encryptingOutputStream = this.fileEncrypter.encrypt("testFile.txt", testFileBytes.length, encTestFileOutStream);
        IOUtils.write(testFileBytes, encryptingOutputStream);
        encryptingOutputStream.close();
        
        
        final ByteArrayOutputStream decTestFileOutStream = new ByteArrayOutputStream();
        this.fileDecrypter.decrypt(new ByteArrayInputStream(encTestFileOutStream.toByteArray()), decTestFileOutStream);
        
        
        testFileInStream = this.getClass().getResourceAsStream("/testFile.txt");
        final String expected = IOUtils.toString(testFileInStream);
        final String actual = IOUtils.toString(new ByteArrayInputStream(decTestFileOutStream.toByteArray()));

        assertEquals(expected, actual);
    }
}

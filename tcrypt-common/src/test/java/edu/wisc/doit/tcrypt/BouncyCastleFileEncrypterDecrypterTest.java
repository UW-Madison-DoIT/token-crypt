package edu.wisc.doit.tcrypt;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    public void testFileEncryptDecrypt() throws Exception {
        InputStream testFileInStream = this.getClass().getResourceAsStream("/testFile.txt");
        
        final ByteArrayOutputStream encTestFileOutStream = new ByteArrayOutputStream();
        this.fileEncrypter.encrypt("testFile.txt", testFileInStream, encTestFileOutStream);
        
        final ByteArrayOutputStream decTestFileOutStream = new ByteArrayOutputStream();
        this.fileDecrypter.decrypt(new ByteArrayInputStream(encTestFileOutStream.toByteArray()), decTestFileOutStream);
        
        
        testFileInStream = this.getClass().getResourceAsStream("/testFile.txt");
        final String expected = IOUtils.toString(testFileInStream);
        final String actual = IOUtils.toString(new ByteArrayInputStream(decTestFileOutStream.toByteArray()));

        assertEquals(expected, actual);
    }
}

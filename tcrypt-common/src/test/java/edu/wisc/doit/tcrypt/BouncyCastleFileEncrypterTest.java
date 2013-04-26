package edu.wisc.doit.tcrypt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class BouncyCastleFileEncrypterTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
 
    private FileEncrypter fileEncrypter;
    
    @Before
    public void setup() throws IOException {
        fileEncrypter = new BouncyCastleFileEncrypter(new InputStreamReader(this.getClass().getResourceAsStream("/my.wisc.edu-public.pem")));
//        tokenDecrypter = new BouncyCastleTokenDecrypter(new InputStreamReader(this.getClass().getResourceAsStream("/my.wisc.edu-private.pem")));
    }
    
    @Test
    public void testFileEncrypt() throws Exception {
        final InputStream testFileInStream = this.getClass().getResourceAsStream("/testFile.txt");
        final ByteArrayOutputStream testFileOutStream = new ByteArrayOutputStream();
        
        this.fileEncrypter.encrypt("testFile.txt", testFileInStream, testFileOutStream);
    }
}

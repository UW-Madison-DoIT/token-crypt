package edu.wisc.doit.tcrypt;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

public class BouncyCastleFileDecrypterTest {
    private FileDecrypter fileDecrypter;
    
    @Before
    public void setup() throws IOException {
        fileDecrypter = new BouncyCastleFileDecrypter(new InputStreamReader(this.getClass().getResourceAsStream("/my.wisc.edu-private.pem")));
    }
    
    @Test
    public void testExistingFileDecryption() throws Exception {
        final InputStream encTestFileInStream = this.getClass().getResourceAsStream("/testFile.tar");
        final ByteArrayOutputStream testFileOutStream = new ByteArrayOutputStream();
        fileDecrypter.decrypt(new TarArchiveInputStream(encTestFileInStream), testFileOutStream);
        final String actual = new String(testFileOutStream.toByteArray(), Charset.defaultCharset()).trim();
        
        final InputStream testFileInStream = this.getClass().getResourceAsStream("/testFile.txt");
        final String expected = IOUtils.toString(testFileInStream).trim();
        
        assertEquals(expected, actual);
    }
}

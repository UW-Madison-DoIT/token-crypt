package edu.wisc.doit.tcrypt;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Uses Runtime.exec to verify the java code interacts correctly with openssl
 */
public class BouncyCastleFileEncrypterDecrypterIT {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
 
    private FileEncrypter fileEncrypter;
    private FileDecrypter fileDecrypter;
    
    @Before
    public void setup() throws IOException {
        fileEncrypter = new BouncyCastleFileEncrypter(new InputStreamReader(this.getClass().getResourceAsStream("/my.wisc.edu-public.pem")));
        fileDecrypter = new BouncyCastleFileDecrypter(new InputStreamReader(this.getClass().getResourceAsStream("/my.wisc.edu-private.pem")));
    }
    
    @Test
    public void testOpenSSLEncJavaDec() throws Exception {
        //Encrypt with openssl
        final File encryptFileScript = setupTempFile("encryptFile.sh");
        encryptFileScript.setExecutable(true);

        final File publicKey = setupTempFile("my.wisc.edu-public.pem");
        final File testFile = setupTempFile("testFile.txt");
        
        final ProcessBuilder pb = new ProcessBuilder(encryptFileScript.getAbsolutePath(), publicKey.getAbsolutePath(), testFile.getAbsolutePath());
        
        final Process p = pb.start();
        final int ret = p.waitFor();
        if (ret != 0) {
            final String pOut = IOUtils.toString(p.getInputStream(), TokenEncrypter.CHARSET).trim();
            System.out.println(pOut);
            final String pErr = IOUtils.toString(p.getErrorStream(), TokenEncrypter.CHARSET).trim();
            System.out.println(pErr);
        }
        assertEquals(0, ret);
        
        
        //Decrypt with java
        final File encryptedFile = new File(testFile.getParentFile(), "testFile.txt.tar");
        
        final InputStream encTestFileInStream = new FileInputStream(encryptedFile);
        final ByteArrayOutputStream testFileOutStream = new ByteArrayOutputStream();
        fileDecrypter.decrypt(encTestFileInStream, testFileOutStream);
        final String actual = new String(testFileOutStream.toByteArray(), Charset.defaultCharset()).trim();

        
        //Verify
        final String expected = FileUtils.readFileToString(testFile);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testJavaEncOpenSSLDec() throws Exception {
        //Encrypt with Java
        final File testFile = setupTempFile("testFile.txt");
        
        InputStream testFileInStream = new FileInputStream(testFile);
        
        final File encFile = this.testFolder.newFile("testFile.txt.tar");
        this.fileEncrypter.encrypt("testFile.txt", (int)testFile.length(), testFileInStream, new FileOutputStream(encFile));
        
        
        //Decrypt with OpenSSL
        final File decryptFileScript = setupTempFile("decryptFile.sh");
        decryptFileScript.setExecutable(true);
        
        final File privateKey = setupTempFile("my.wisc.edu-private.pem");
        
        final ProcessBuilder pb = new ProcessBuilder(decryptFileScript.getAbsolutePath(), privateKey.getAbsolutePath(), encFile.getAbsolutePath());
        
        final Process p = pb.start();
        final int ret = p.waitFor();
        if (ret != 0) {
            final String pOut = IOUtils.toString(p.getInputStream(), TokenEncrypter.CHARSET).trim();
            System.out.println(pOut);
            final String pErr = IOUtils.toString(p.getErrorStream(), TokenEncrypter.CHARSET).trim();
            System.out.println(pErr);
        }
        assertEquals(0, ret);
        
        
        final File decryptedFile = new File(encFile.getParentFile(), "testFile.txt");

        //Verify
        final String expected = FileUtils.readFileToString(testFile);
        final String actual = FileUtils.readFileToString(decryptedFile);
        assertEquals(expected, actual);
    }

    protected File setupTempFile(final String fileName) throws IOException {
        final File encryptFileScript = testFolder.newFile(fileName);
        FileUtils.copyInputStreamToFile(this.getClass().getResourceAsStream("/" + fileName), encryptFileScript);
        return encryptFileScript;
    }
}

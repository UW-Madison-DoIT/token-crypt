package edu.wisc.doit.tcrypt.ant.filter;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.tools.ant.BuildFileTest;
import org.junit.rules.TemporaryFolder;

/**
 * See http://ant.apache.org/manual/tutorial-writing-tasks.html#TestingTasks
 */
public class AntTasksTest extends BuildFileTest {
    private TemporaryFolder tempFolder = new TemporaryFolder();

    public AntTasksTest(String name) {
        super(name);
    }

    protected File getResourceFile(final String file) throws URISyntaxException {
        final URL buildResource = this.getClass().getResource(file);
        final File buildFile = new File(buildResource.toURI());
        return buildFile;
    }
    
    public void setUp() throws Exception {
        final File buildFile = getResourceFile("/build.xml");
        configureProject(buildFile.getAbsolutePath());
        tempFolder.create();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        tempFolder.delete();
    }

    public void testEncryptToken() throws Exception {
        final File publicKeyFile = getResourceFile("/my.wisc.edu-public.pem");
        
        this.project.setProperty("pk", publicKeyFile.getAbsolutePath());
        this.project.setProperty("token", "foobar");
        this.expectLogContaining("encryptToken", "Encrypted token to: ENC(");
    }
    
    public void testDecryptToken() throws Exception {
        final File privateKeyFile = getResourceFile("/my.wisc.edu-private.pem");
        
        this.project.setProperty("pk", privateKeyFile.getAbsolutePath());
        this.project.setProperty("token", "ENC(fYaLfzq+GdDNQEKLg7E75EEN9qINDt3cEhFO+caXjY2CTc6qH1UfXh1o/3u6gfsXxALGqgN2GG/e85f4wmjXYUEJK/RKUQXu00x/guzoVlcsjMyheAuGxOcdGKvyFuk1d2zbzlu5vOmFiO/1UttaRt/+SMbyua53jH2vDbhJ2NANShYrl18sia6ozZ6FlsmHU+ynXBheI3c1rgZONnWnsojfZQdrxjot8MAnqe/ZUHt+L+gvyqMpeczUS5iBYd42CbUeeP7icn7pf/i6nJoEKcQrtwuB4UDb9jisqnxtV7fUoYcOIlvEN/q/cn9GKsyk7KIICM0M0A5qnA57DlZ4KA==)");
        this.expectLog("decryptToken", "Decrypted token to: foobar");
    }
    
    public void testDecryptTokens() throws Exception {
        final File privateKeyFile = getResourceFile("/my.wisc.edu-private.pem");
        
        final File sourceDir = getResourceFile("/token_filtering/test.properties").getParentFile();
        final File destDir = tempFolder.newFolder("decryptTokens");
        
        this.project.setProperty("pk", privateKeyFile.getAbsolutePath());
        this.project.setProperty("src", sourceDir.getAbsolutePath());
        this.project.setProperty("dest", destDir.getAbsolutePath());
        
        this.expectLogContaining("decryptTokens", 
                "Copying 2 files to /var/folders/fb/");
        
        final File expectedDir = getResourceFile("/token_filtering_expected/test.properties").getParentFile();
        
        assertDirectoriesEqual(expectedDir, destDir);
    }

    public void testEncryptFile() throws Exception {
        final File publicKeyFile = getResourceFile("/my.wisc.edu-public.pem");
        final File srcFile = getResourceFile("/testFile.txt");
        final File destDir = tempFolder.newFolder("encryptFile");
        
        this.project.setProperty("pk", publicKeyFile.getAbsolutePath());
        this.project.setProperty("src", srcFile.getAbsolutePath());
        this.project.setProperty("dest", destDir.getAbsolutePath());
        this.expectLogContaining("encryptFile", "Encrypting '");
        
        final File actualFile = new File(destDir, "testFile.txt.tar");
        final File expectedFile = getResourceFile("/testFile.txt.tar");
        
        assertTrue(actualFile.exists());
        assertEquals(expectedFile.length(), actualFile.length());
    }

    public void testDecryptFile() throws Exception {
        final File privateKeyFile = getResourceFile("/my.wisc.edu-private.pem");
        final File srcFile = getResourceFile("/testFile.txt.tar");
        final File destDir = tempFolder.newFolder("decryptFile");
        
        this.project.setProperty("pk", privateKeyFile.getAbsolutePath());
        this.project.setProperty("src", srcFile.getAbsolutePath());
        this.project.setProperty("dest", destDir.getAbsolutePath());
        this.expectLogContaining("decryptFile", "Decrypting '");
        
        final File actualFile = new File(destDir, "testFile.txt");
        final File expectedFile = getResourceFile("/testFile.txt");
        
        assertTrue(actualFile.exists());
        assertEquals(FileUtils.readFileToString(expectedFile), FileUtils.readFileToString(actualFile));
    }
    
    public void testEncDecCopy() throws Exception {
        final File privateKeyFile = getResourceFile("/my.wisc.edu-private.pem");
        final File publicKeyFile = getResourceFile("/my.wisc.edu-public.pem");
        
        final File sourceDir = getResourceFile("/token_filtering/test.properties").getParentFile();
        final File tempDir = tempFolder.newFolder("encDecCopyTemp");
        final File destDir = tempFolder.newFolder("encDecCopy");
        
        this.project.setProperty("privKey", privateKeyFile.getAbsolutePath());
        this.project.setProperty("pubKey", publicKeyFile.getAbsolutePath());
        this.project.setProperty("src", sourceDir.getAbsolutePath());
        this.project.setProperty("tempDir", tempDir.getAbsolutePath());
        this.project.setProperty("dest", destDir.getAbsolutePath());
        this.expectLogContaining("encDecCopy", "Copying 2 files to ");
        
        assertDirectoriesEqual(sourceDir, destDir);
    }
    
    private void assertDirectoriesEqual(File expected, File actual) throws Exception {
        final Map<String, File> actualFiles = this.getAllFiles(actual);
        
        final Map<String, File> expectedFiles = this.getAllFiles(expected);
        for (final Map.Entry<String, File> expectedFileEntry : expectedFiles.entrySet()) {
            final String relativePath = expectedFileEntry.getKey();
            final File expectedFile = expectedFileEntry.getValue();
            final File actualFile = actualFiles.remove(relativePath);
            
            assertNotNull("Could not find actual file for: " + expectedFile, actualFile);
            if (actualFile.isFile()) {
                assertEquals(relativePath + " do not match", FileUtils.readFileToString(expectedFile), FileUtils.readFileToString(actualFile));
            }
        }
        
        if (!actualFiles.isEmpty()) {
            fail("Extra actual files: " + actualFiles);
        }
    }
    
    private Map<String, File> getAllFiles(File dir) throws Exception {
        final Collection<File> expectedFiles = FileUtils.listFilesAndDirs(dir, TrueFileFilter.INSTANCE, DirectoryFileFilter.INSTANCE);
        
        final Map<String, File> result = new HashMap<String, File>();
        
        for (final File f : expectedFiles) {
            final String relativePath = org.apache.tools.ant.util.FileUtils.getRelativePath(dir, f);

            result.put(relativePath, f);
        }
        
        return result;
    }
}

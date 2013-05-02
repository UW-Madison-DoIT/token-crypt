package edu.wisc.doit.tcrypt.ant;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Vector;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.GlobPatternMapper;
import org.apache.tools.ant.util.ResourceUtils;
import org.bouncycastle.crypto.InvalidCipherTextException;

import edu.wisc.doit.tcrypt.BouncyCastleFileDecrypter;
import edu.wisc.doit.tcrypt.FileDecrypter;
import edu.wisc.doit.tcrypt.FileEncrypter;

/**
 * Extends the base {@link Copy} task to provide on the fly decryption of files. The
 * decryption is hooked into the process fairly early on so filtering and other transformations
 * that copy can do should all operate on the decrypted version of the file.
 * <br>
 * Defaults to removing the .tar extension from decrypted files
 * 
 * @author Eric Dalquist
 */
public class DecryptCopy extends Copy {
    private Resource privateKey;
    private FileDecrypter fileDecrypter;
    
    /**
     * @param privateKey The private key used to decrypt
     */
    public void setPrivateKey(Resource privateKey) {
        this.privateKey = privateKey;
    }

    public DecryptCopy() {
        this.fileUtils = new DecryptFileUtils();
        
        //Setup adding .tar to file names
        final GlobPatternMapper fileNameMapper = new GlobPatternMapper();
        fileNameMapper.setFrom("*.tar");
        fileNameMapper.setTo("*");
        this.add(fileNameMapper);
    }
    
    private FileDecrypter getFileDecrypter() {
        if (this.fileDecrypter != null) {
            return this.fileDecrypter;
        }
        
        Reader privateKeyReader = null;
        try {
            privateKeyReader = new InputStreamReader(new BufferedInputStream(this.privateKey.getInputStream()), FileEncrypter.CHARSET);
            fileDecrypter = new BouncyCastleFileDecrypter(privateKeyReader);
        }
        catch (IOException e) {
            throw new BuildException("Failed to create BouncyCastleFileDecrypter for private key: " + this.privateKey, e);
        }
        finally {
            IOUtils.closeQuietly(privateKeyReader);
        }
        
        return this.fileDecrypter;
    }
    
    private final class DecryptFileUtils extends FileUtils {
        public void copyFile(File sourceFile, File destFile,
                FilterSetCollection filters, Vector filterChains,
                boolean overwrite, boolean preserveLastModified,
                boolean append,
                String inputEncoding, String outputEncoding,
                Project project, boolean force) throws IOException {
            
            final FileDecrypter fd = getFileDecrypter();

            //Swap out the standard FileResource for a DecryptingFileResource
            ResourceUtils.copyResource(new DecryptingFileResource(fd, sourceFile),
                    new FileResource(destFile),
                    filters, filterChains, overwrite,
                    preserveLastModified, append, inputEncoding,
                    outputEncoding, project, force);
        }
    }
    
    private final class DecryptingFileResource extends Resource {
        private final FileDecrypter fileDecrypter;
        private final File file;
        private final File baseDir;
        
        public DecryptingFileResource(FileDecrypter fileDecrypter, File file) {
            this.fileDecrypter = fileDecrypter;
            this.file = file;
            this.baseDir = file.getParentFile();
        }
        
        public String getName() {
            return this.baseDir == null ? this.file.getName()
                : fileUtils.removeLeadingPath(this.baseDir, this.file);
        }

        public boolean isExists() {
            return this.file.exists();
        }

        public long getLastModified() {
            return this.file.lastModified();
        }

        public boolean isDirectory() {
            return this.file.isDirectory();
        }

        public long getSize() {
            return this.file.length();
        }

        public InputStream getInputStream() throws IOException {
            try {
                return this.fileDecrypter.decrypt(new BufferedInputStream(new FileInputStream(this.file)));
            }
            catch (InvalidCipherTextException e) {
                throw new BuildException("Invalid key '" + privateKey +  "' for: " + this.file, e);
            }
            catch (DecoderException e) {
                throw new BuildException("Invalid key '" + privateKey +  "' for: " + this.file, e);
            }
        }

        public OutputStream getOutputStream() throws IOException {
            throw new UnsupportedOperationException("DecryptingFileResource is read only");
        }

        public String toString() {
            String absolutePath = file.getAbsolutePath();
            return fileUtils.normalize(absolutePath).getAbsolutePath();
        }
    }
}

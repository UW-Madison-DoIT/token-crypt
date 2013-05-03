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
package edu.wisc.doit.tcrypt.ant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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

import edu.wisc.doit.tcrypt.BouncyCastleFileEncrypter;
import edu.wisc.doit.tcrypt.FileEncrypter;

/**
 * Extends the base {@link Copy} task to provide on the fly encryption of files. The
 * encryption is hooked into the process fairly early on so filtering and other transformations
 * that copy can do should all operate on the encrypted version of the destFile.
 * <br>
 * Defaults to adding the .tar extension to encrypted files
 * 
 * @author Eric Dalquist
 */
public class EncryptCopy extends Copy {
    private Resource publicKey;
    private FileEncrypter fileEncrypter;
    
    /**
     * @param publicKey The public key used to encrypt
     */
    public void setPublicKey(Resource publicKey) {
        this.publicKey = publicKey;
    }

    public EncryptCopy() {
        this.fileUtils = new EncryptFileUtils();
        
        //Setup adding .tar to destFile names
        final GlobPatternMapper fileNameMapper = new GlobPatternMapper();
        fileNameMapper.setFrom("*");
        fileNameMapper.setTo("*.tar");
        this.add(fileNameMapper);
    }
    
    private FileEncrypter getFileEncrypter() {
        if (this.fileEncrypter != null) {
            return this.fileEncrypter;
        }
        
        Reader publicKeyReader = null;
        try {
            publicKeyReader = new InputStreamReader(new BufferedInputStream(this.publicKey.getInputStream()), FileEncrypter.CHARSET);
            fileEncrypter = new BouncyCastleFileEncrypter(publicKeyReader);
        }
        catch (IOException e) {
            throw new BuildException("Failed to create BouncyCastleFileEncrypter for private key: " + this.publicKey, e);
        }
        finally {
            IOUtils.closeQuietly(publicKeyReader);
        }
        
        return this.fileEncrypter;
    }
    
    private final class EncryptFileUtils extends FileUtils {
        public void copyFile(File sourceFile, File destFile,
                FilterSetCollection filters, Vector filterChains,
                boolean overwrite, boolean preserveLastModified,
                boolean append,
                String inputEncoding, String outputEncoding,
                Project project, boolean force) throws IOException {
            
            final FileEncrypter fe = getFileEncrypter();

            //Swap out the standard FileResource for a EncryptingFileResource
            ResourceUtils.copyResource(new FileResource(sourceFile),
                    new EncryptingFileResource(sourceFile, fe, destFile),
                    filters, filterChains, overwrite,
                    preserveLastModified, append, inputEncoding,
                    outputEncoding, project, force);
        }
    }
    
    private final class EncryptingFileResource extends Resource {
        private final File sourceFile;
        private final FileEncrypter fileEncrypter;
        private final File destFile;
        private final File baseDir;
        
        public EncryptingFileResource(File sourceFile, FileEncrypter fileEncrypter, File destFile) {
            this.sourceFile = sourceFile;
            this.fileEncrypter = fileEncrypter;
            this.destFile = destFile;
            this.baseDir = destFile.getParentFile();
        }
        
        public String getName() {
            return this.baseDir == null ? this.destFile.getName()
                : fileUtils.removeLeadingPath(this.baseDir, this.destFile);
        }

        public boolean isExists() {
            return this.destFile.exists();
        }

        public long getLastModified() {
            return this.destFile.lastModified();
        }

        public boolean isDirectory() {
            return this.destFile.isDirectory();
        }

        public long getSize() {
            return this.destFile.length();
        }

        public InputStream getInputStream() throws IOException {
            throw new UnsupportedOperationException("EncryptingFileResource is read only");
        }

        public OutputStream getOutputStream() throws IOException {
            final String fileName = this.sourceFile.getName();            
            final long size = this.sourceFile.length();
            try {
                return this.fileEncrypter.encrypt(fileName, (int)size, new BufferedOutputStream(new FileOutputStream(this.destFile)));
            }
            catch (InvalidCipherTextException e) {
                throw new BuildException("Invalid key '" + publicKey +  "' for: " + this.destFile, e);
            }
            catch (DecoderException e) {
                throw new BuildException("Invalid key '" + publicKey +  "' for: " + this.destFile, e);
            }
        }

        public String toString() {
            String absolutePath = destFile.getAbsolutePath();
            return fileUtils.normalize(absolutePath).getAbsolutePath();
        }
    }
}

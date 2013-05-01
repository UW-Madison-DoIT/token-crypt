package edu.wisc.doit.tcrypt.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Resource;

/**
 * Base class for file encryption/decryption
 * 
 * @author Eric Dalquist
 */
abstract class AbstractFileTask extends Task {
    Resource srcFile;
    File destDir;
    File destFile;

    final File getOutputFile() throws BuildException {
        if (this.destFile != null) {
            return this.destFile;
        }
        
        final String srcFileName = this.srcFile.getName();
        final String fileName = getFileName(srcFileName);
        
        if (this.destDir == null) {
            return new File(this.getProject().getBaseDir(), fileName);
        }
        else {
            return new File(this.destDir, fileName);
        }
    }
    
    abstract String getFileName(String srcFileName); 
}

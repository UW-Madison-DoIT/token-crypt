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

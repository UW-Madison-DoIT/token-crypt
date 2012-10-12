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
package edu.wisc.doit.tcrypt.maven;

import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.codehaus.plexus.interpolation.ValueSource;

/**
 * Creates {@link ValueSource} instances for use in filtering
 * 
 * @author Eric Dalquist
 * @version $Revision: 187 $
 */
public interface ValueSourceCreator {
    /**
     * Create a value source to be used for this resources execution
     * 
     * @param mavenResourcesExecution Information about the resources execution
     * @return The value source to use when filtering
     */
    ValueSource createValueSource(MavenResourcesExecution mavenResourcesExecution);
}

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
package edu.wisc.doit.tcrypt.ant.filter;

import java.io.IOException;
import java.io.Reader;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.filters.BaseFilterReader;
import org.apache.tools.ant.filters.BaseParamFilterReader;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.types.Parameter;

/**
 * Base class for doing delimited token based replacement. Sub-classes can implement their own
 * token replacement logic
 * 
 * @author Eric Dalquist
 */
public abstract class AbstractReplaceTokens extends BaseParamFilterReader implements ChainableReader {
    /** Parameter name for the beginToken. */
    private static final String BEGIN_TOKEN_PARAM = "beginToken";

    /** Parameter name for the endToken. */
    private static final String END_TOKEN_PARAM = "endToken";
    
    /** Parameter name for the ignoreErrors. */
    private static final String IGNORE_ERRORS_PARAM = "ignoreErrors";

    /** Character marking the beginning of a token. */
    private String beginToken = "@";

    /** Character marking the end of a token. */
    private String endToken = "@";
    
    /** If token replacement errors should be ignored */
    private boolean ignoreErrors = false;
    
    private String line;
    private int linePos = 0;
    
    /**
     * Constructor for "dummy" instances.
     *
     * @see BaseFilterReader#BaseFilterReader()
     */
    public AbstractReplaceTokens() {
        super();
    }

    /**
     * Creates a new filtered reader.
     *
     * @param in A Reader object providing the underlying stream.
     *           Must not be <code>null</code>.
     */
    public AbstractReplaceTokens(Reader in) {
        super(in);
    }
    
    /**
     * Used for {@link ChainableReader#chain(Reader)}
     */
    AbstractReplaceTokens(final Reader in, AbstractReplaceTokens source) {
        super(in);
        
        this.beginToken = source.beginToken;
        this.endToken = source.endToken;
        this.setInitialized(true);
    }
    
    /**
     * @param beginToken Pattern that starts a replacement token
     */
    public void setBeginToken(String beginToken) {
        this.beginToken = beginToken;
    }

    /**
     * @param endToken Pattern that ends a replacement token
     */
    public void setEndToken(String endToken) {
        this.endToken = endToken;
    }

    @Override
    public final int read() throws IOException {
        // do the "singleton" initialization
        if (!getInitialized()) {
            localInitialize();
            setInitialized(true);
        }
        
        if (line == null) {
            line = readLine();
            //Underlying reader is empty
            if (line == null) {
                return -1;
            }
            
            //Replace any tokens on the line
            line = replaceTokensOnLine(line);
        }

        //Get the next character from the line
        final char c = line.charAt(linePos);
        linePos++;

        //If we've read the whole line null it out
        if (linePos >= line.length()) {
            line = null;
            linePos = 0;
        }
        
        return c;
    }

    @Override
    public final Reader chain(Reader rdr) {
        // do the "singleton" initialization
        if (!getInitialized()) {
            localInitialize();
            setInitialized(true);
        }
        
        return createChainedReader(rdr);
    }
    
    private void localInitialize() {
        final Parameter[] parameters = getParameters();
        if (parameters != null) {
            for (final Parameter param : parameters) {
                String value = param.getValue();
                if (value == null || value.trim().length() == 0) {
                    continue;
                }
                
                if (BEGIN_TOKEN_PARAM.equals(param.getName())) {
                    this.beginToken = value;
                }
                else if (END_TOKEN_PARAM.equals(param.getName())) {
                    this.endToken = value;
                }
                else if (IGNORE_ERRORS_PARAM.equals(param.getName())) {
                    this.ignoreErrors = Boolean.parseBoolean(value);
                }
                
            }
        }

        this.getProject().log("beginToken: " + this.beginToken, Project.MSG_DEBUG);
        this.getProject().log("endToken: " + this.endToken, Project.MSG_DEBUG);
        this.getProject().log("ignoreErrors: " + this.ignoreErrors, Project.MSG_DEBUG);
        
        this.initialize();
    }
    
    final String replaceTokensOnLine(String line) {
        final StringBuilder result = new StringBuilder();
        
        int remaining = 0;
        int startPos = line.indexOf(this.beginToken);
        try {
            while (startPos >= remaining) {
                int endPos = line.indexOf(this.endToken, startPos);
                if (endPos < startPos) {
                    //No end found stop processing
                    break;
                }
                
                //Add whatever we just skipped passed from the line to the result
                result.append(line, remaining, startPos);
                
                //Extract the token and replace it
                final String token = line.substring(startPos + this.beginToken.length(), endPos);
                try {
                    final CharSequence replacement = this.replaceToken(token);
                    result.append(replacement);
                }
                catch (TokenReplacmentFailureException e) {
                    if (ignoreErrors) {
                        result.append(token);
                        this.getProject().log("Ignoring token '" + token + "' due to replacement failure", e, Project.MSG_WARN);
                    }
                    else {
                        throw new BuildException("Failed to replace token '" + token + "'", e);
                    }
                }
                
                
                //Update the remaining data and start position
                remaining = endPos + this.endToken.length();
                startPos = line.indexOf(this.beginToken, remaining);
            }
            
            //Append any leftovers
            result.append(line, remaining, line.length());
        }
        catch (IndexOutOfBoundsException e) {
            throw new BuildException("Failed to parse line '" + line + "' from position " + remaining, e);
        }
        
        return result.toString();
    }
    
    abstract Reader createChainedReader(Reader rdr);
    
    abstract void initialize();
    
    abstract CharSequence replaceToken(String token);
}

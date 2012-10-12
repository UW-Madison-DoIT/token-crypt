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
package edu.wisc.doit.tcrypt.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.crypto.InvalidCipherTextException;

import edu.wisc.doit.tcrypt.TokenDecrypter;
import edu.wisc.doit.tcrypt.TokenEncrypter;

public class TokenCrypt {
    public static void main(String[] args) throws IOException {
        // create Options object
        final Options options = new Options();
        
        // operation opt group
        final OptionGroup cryptTypeGroup = new OptionGroup();
        cryptTypeGroup.addOption(new Option("e", "encrypt", false, "Encrypt a token"));
        cryptTypeGroup.addOption(new Option("d", "decrypt", false, "Decrypt a token"));
        cryptTypeGroup.addOption(new Option("c", "check", false, "Check if the string looks like an encrypted token"));
        cryptTypeGroup.setRequired(true);
        options.addOptionGroup(cryptTypeGroup);
        
        // token source opt group
        final OptionGroup tokenGroup = new OptionGroup();
        final Option tokenOpt = new Option("t", "token", true, "The token(s) to operate on");
        tokenOpt.setArgs(Option.UNLIMITED_VALUES);
        tokenGroup.addOption(tokenOpt);
        final Option tokenFileOpt = new Option("f", "file", true, "A file with one token per line to operate on, if - is specified stdin is used");
        tokenGroup.addOption(tokenFileOpt);
        tokenGroup.setRequired(true);
        options.addOptionGroup(tokenGroup);
        
        final Option keyOpt = new Option("k", "keyFile", true, "Key file to use. Must be a private key for decryption and a public key for encryption");
        keyOpt.setRequired(true);
        options.addOption(keyOpt);
        
        options.addOption("w", "wrap", true, "Wrap the encrypted token in ENC(), defaults to true");
        

        // create the parser
        final CommandLineParser parser = new GnuParser();
        CommandLine line = null;
        try {
            // parse the command line arguments
            line = parser.parse(options, args);
        }
        catch (ParseException exp) {
            // automatically generate the help statement
            System.err.println(exp.getMessage());
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java " + TokenCrypt.class.getName(), options, true);
            System.exit(1);
        }
        
        final Reader keyReader = createKeyReader(line);
        
        final TokenHandler tokenHandler = createTokenHandler(line, keyReader);
        
        if (line.hasOption("t")) {
            //tokens on cli
            final String[] tokens = line.getOptionValues("t");
            for (final String token : tokens) {
                handleToken(tokenHandler, token);
            }
        }
        else {
            //tokens from a file
            final String tokenFile = line.getOptionValue("f");
            final BufferedReader fileReader;
            if ("-".equals(tokenFile)) {
                fileReader = new BufferedReader(new InputStreamReader(System.in));
            }
            else {
                fileReader = new BufferedReader(new FileReader(tokenFile));
            }
            
            while (true) {
                final String token = fileReader.readLine();
                if (token == null) {
                    break;
                }
                
                handleToken(tokenHandler, token);
            }
        }
    }

    private static void handleToken(final TokenHandler tokenHandler, final String token) {
        try {
            final String convertedToken = tokenHandler.handleToken(token);
            System.out.println(token + "=" + convertedToken);
        }
        catch (InvalidCipherTextException e) {
            System.out.println("INVALID TOKEN " + token + " - " + e.getMessage());
        }
    }

    private static TokenHandler createTokenHandler(CommandLine line, final Reader keyReader) throws IOException {
        final TokenHandler tokenHandler;
        if (line.hasOption("e")) {
            final boolean wrap = !line.hasOption("w") || Boolean.parseBoolean(line.getOptionValue("w"));
            
            final TokenEncrypter tokenEncrypter = new TokenEncrypter(keyReader);
            
            tokenHandler = new TokenHandler() {
                @Override
                public String handleToken(String token) throws InvalidCipherTextException {
                    return tokenEncrypter.encrypt(token, wrap);
                }
            };
        }
        else {
            final TokenDecrypter tokenDecrypter = new TokenDecrypter(keyReader);
            
            if (line.hasOption("c")) {
                tokenHandler = new TokenHandler() {
                    @Override
                    public String handleToken(String token) throws InvalidCipherTextException {
                        final boolean encryptedToken = tokenDecrypter.isEncryptedToken(token);
                        return Boolean.toString(encryptedToken);
                    }
                };
            }
            else {
                tokenHandler = new TokenHandler() {
                    @Override
                    public String handleToken(String token) throws InvalidCipherTextException {
                        return tokenDecrypter.decrypt(token);
                    }
                };
            }
        }
        return tokenHandler;
    }

    private static Reader createKeyReader(CommandLine line) throws IOException {
        final Reader keyReader;
        final String keyFile = line.getOptionValue("k");
        try {
            final String keyData = FileUtils.readFileToString(new File(keyFile));
            keyReader = new StringReader(keyData);
        }
        catch (IOException e) {
            throw new IOException("Failed to read keyFile: " + keyFile, e);
        }
        return keyReader;
    }
    
    private static interface TokenHandler {
        String handleToken(String token) throws InvalidCipherTextException;
    }
}

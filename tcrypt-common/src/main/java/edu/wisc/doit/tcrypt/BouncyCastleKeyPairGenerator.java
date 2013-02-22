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
package edu.wisc.doit.tcrypt;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.*;

public class BouncyCastleKeyPairGenerator implements TokenKeyPairGenerator
{
    static {
        //TODO hook to unregister?
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
    
    @Override
    public KeyPair generateKeyPair(Integer keyLength) {
        final KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("RSA support could not be found. There is a problem with this JVM.", e);
        }
        catch (NoSuchProviderException e) {
            throw new IllegalStateException("BounceCastle support could not be found. There is a problem with this JVM.", e);
        }
        
        kpg.initialize(keyLength);
        return kpg.generateKeyPair();
    }
}


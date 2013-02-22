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
/**
 * @author Brad Leege <leege@doit.wisc.edu>
 * Created on 2/15/13 at 1:32 PM
 */
package edu.wisc.doit.tcrypt.services.impl;

import edu.wisc.doit.tcrypt.dao.IKeysKeeper;
import edu.wisc.doit.tcrypt.services.TCryptServices;
import edu.wisc.doit.tcrypt.vo.ServiceKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.util.List;

@Service("tCryptServicesImpl")
public class TCryptServicesImpl implements TCryptServices
{
	private IKeysKeeper keysKeeper;

	/**
	 * Constructor
	 * @param keysKeeper DAO for working with Keys (Generating and File Storage)
	 */
	@Autowired
	public TCryptServicesImpl(IKeysKeeper keysKeeper)
	{
		super();
		this.keysKeeper = keysKeeper;
	}

	@Override
	public List<String> getListOfServiceNames()
	{
		return keysKeeper.getListOfServiceNames();
	}

	@Override
	public KeyPair generateKeyPair(Integer keyLength)
	{
		return keysKeeper.generateKeyPair(keyLength);
	}

	@Override
	public Boolean writeServiceKeyToFileSystem(ServiceKey serviceKey)
	{
		return keysKeeper.writeServiceKeyToFileSystem(serviceKey);
	}

	@Override
	public ServiceKey readServiceKeyFromFileSystem(String serviceName)
	{
		return keysKeeper.readServiceKeyFromFileSystem(serviceName);
	}

	@Override
	public InputStream getKeyAsInputStream(Key key)
	{
		return keysKeeper.getKeyAsInputStream(key);
	}

	@Override
	public Boolean writeKeyToOutputStream(Key key, OutputStream outputStream)
	{
		return keysKeeper.writeKeyToOutputStream(key, outputStream);
	}
}

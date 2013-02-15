package edu.wisc.doit.tcrypt.dao;

import edu.wisc.doit.tcrypt.vo.ServiceKey;
import java.security.KeyPair;
import java.util.Set;

public interface IKeysKeeper
{
	public Set<String> getListOfServiceNames();
	public KeyPair generateKeyPair(Integer keyLength);
	public Boolean writeServiceKeyToFileSystem(ServiceKey serviceKey);
	public ServiceKey readServiceKeyFromFileSystem(String serviceName);
}
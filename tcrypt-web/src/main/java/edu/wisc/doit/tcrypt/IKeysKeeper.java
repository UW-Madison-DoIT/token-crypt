package edu.wisc.doit.tcrypt;

import edu.wisc.doit.tcrypt.vo.ServiceKey;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Set;

public interface IKeysKeeper
{
	public abstract String getKeyLocationToSaveOnServer(String serviceName, String remoteUser, String keyType) throws IOException;
	public abstract String getKeyLocationToDownloadFromServer(String serviceName, String remoteUser, String keyType) throws IOException;
	public abstract boolean checkIfKeyExistsOnServer(String serviceName, String remoteUser);
	public abstract Set<String> getListOfServiceNames();
	public KeyPair generateKeyPair();
	public Boolean writeServiceKeyToFileSystem(ServiceKey serviceKey);
	public ServiceKey readServiceKeyFromFileSystem(String serviceName);
}
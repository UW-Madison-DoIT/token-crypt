package edu.wisc.doit.tcrypt;

import java.io.IOException;
import java.util.Set;

public interface IKeysKeeper {

	public abstract String getKeyLocationToSaveOnServer(String serviceName,
			String remoteUser, String keyType) throws IOException;

	public abstract String getKeyLocationToDownloadFromServer(
			String serviceName, String remoteUser, String keyType)
			throws IOException;

	public abstract boolean checkIfKeyExistsOnServer(String serviceName, String remoteUser);

	public abstract Set<String> getListOfServiceNames();

}
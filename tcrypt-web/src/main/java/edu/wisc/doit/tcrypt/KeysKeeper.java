package edu.wisc.doit.tcrypt;

import edu.wisc.doit.tcrypt.vo.ServiceKey;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class KeysKeeper implements IKeysKeeper
{
	private String directoryname;
	private TokenKeyPairGenerator keyPairGenerator;

	/**
	 * Constructor
	 * @param directoryname Location of keys directory
	 * @param keyPairGenerator KeyPair Generator
	 */
	public KeysKeeper(String directoryname, TokenKeyPairGenerator keyPairGenerator)
	{
		this.directoryname = directoryname;
		this.keyPairGenerator = keyPairGenerator;
	}

	@Override
	public String getKeyLocationToSaveOnServer(String serviceName, String remoteUser, String keyType) throws IOException
	{

		final Date generationTimestamp = new Date();
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		final String keyFilePrefix = directoryname + "/" + serviceName + "_"
				+ remoteUser + "_"
				+ simpleDateFormat.format(generationTimestamp) + "-" + keyType
				+ ".pem";
		return keyFilePrefix;
	}


	@Override
	public String getKeyLocationToDownloadFromServer(String serviceName, String remoteUser, String keyType) throws IOException
	{
		final String filePrefix = serviceName + "_" + remoteUser + "_";
		final String fileSuffix = "-" + keyType + ".pem";
		return directoryname + finder(directoryname, filePrefix, fileSuffix)[0];
	}

	@Override
	public boolean checkIfKeyExistsOnServer(String serviceName, String remoteUser)
	{
		final String filePrefix = serviceName + "_" + remoteUser + "_";
		final String fileSuffix = ".pem";
		if (finder(directoryname, filePrefix, fileSuffix).length != 0)
			return true;

		return false;
	}

	//perform in the background??
	@Override
	public Set<String> getListOfServiceNames()
	{
		String[] fileNames = finder(directoryname, "", "");
		Set<String> serviceNames = new HashSet<String>();

		if (fileNames != null)
		{
			for (String string : fileNames)
			{
				serviceNames.add(string.split("_")[0]);
			}
		}
		return serviceNames;
	}

	@Override
	public ServiceKey readServiceKeyFromFileSystem(String serviceName)
	{
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public KeyPair generateKeyPair()
	{
		return keyPairGenerator.generateKeyPair();
	}

	@Override
	public Boolean writeServiceKeyToFileSystem(ServiceKey serviceKey)
	{
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	private String[] finder(String dirName, final String filePrefix, final String fileSuffix)
	{
		File dir = new File(dirName);
		return dir.list(new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				if (name.startsWith(filePrefix) && name.endsWith(fileSuffix))
					return true;
				return false;
			}
		});
	}

}

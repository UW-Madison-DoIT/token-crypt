package edu.wisc.doit.tcrypt;

import edu.wisc.doit.tcrypt.vo.ServiceKey;
import org.bouncycastle.openssl.PEMWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class KeysKeeper implements IKeysKeeper
{
	private static final Logger logger = LoggerFactory.getLogger(KeysKeeper.class);
	private String directoryname;
	private TokenKeyPairGenerator keyPairGenerator;
	private static final SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

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
	public synchronized Boolean writeServiceKeyToFileSystem(ServiceKey serviceKey)
	{
		// Build File Name
		// Pattern: SERVICENAME_NETID_YYYYMMDDHHMMSS_KEYLENGTH_public.pem
		StringBuffer fileName = new StringBuffer();
		fileName.append(serviceKey.getServiceName());
		fileName.append("_").append(serviceKey.getCreatedByNetId()).append("_");
		fileName.append(fileDateFormat.format(serviceKey.getDayCreated()));
		fileName.append("_").append(serviceKey.getKeyLength());
		fileName.append("_public.pem");
		logger.info("ServiceKey file name = {}", fileName.toString());
		String path = directoryname + System.getProperty("file.separator") + fileName;
		logger.info("Path = {}", path);

		// Write To FileSystem
		Boolean result = Boolean.TRUE;
		try
		{
			File file = new File(path);
			if (file.exists())
			{
				file.delete();
			}
			file.createNewFile();
			final PEMWriter pemWriter = new PEMWriter(new FileWriter(file));
			pemWriter.writeObject(serviceKey.getPublicKey());
			pemWriter.close();
		}
		catch (IOException e)
		{
			logger.error(e.toString());
			result = Boolean.FALSE;
		}
		logger.info("Result of writing to file: {}", result);

		// Return Results
		return result;
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

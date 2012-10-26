package edu.wisc.doit.tcrypt;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KeysKeeper implements IKeysKeeper {

	//[Directory name from properties file]
	private String directoryname = "C:\\Users\\sridharan5\\Downloads\\apache-tomcat-6.0.35-windows-x64\\apache-tomcat-6.0.35\\webapps\\keys\\";

	@Override
	public String getKeyLocationToSaveOnServer(String serviceName, String remoteUser, String keyType) throws IOException {
		
		final Date generationTimestamp = new Date();
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		final String keyFilePrefix = directoryname + "/" + serviceName + "_"
				+ remoteUser + "_"
				+ simpleDateFormat.format(generationTimestamp) + "-" + keyType
				+ ".pem";
		return keyFilePrefix;
	}


	@Override
	public String getKeyLocationToDownloadFromServer(String serviceName, String remoteUser, String keyType) throws IOException {
		final String filePrefix = serviceName + "_" + remoteUser + "_" ;
		final String fileSuffix = "-" + keyType + ".pem";
		return directoryname + finder(directoryname, filePrefix, fileSuffix)[0];
	}
	
	@Override
	public boolean checkIfKeyExistsOnServer(String serviceName, String remoteUser) {
		final String filePrefix = serviceName + "_" + remoteUser + "_" ;
		final String fileSuffix = ".pem";
		if(finder(directoryname, filePrefix, fileSuffix).length != 0)
			return true;
		
		return false;
	}
	
	@Override
	public Set<String> getListOfServiceNames(){
		String[] fileNames = finder(directoryname,"","");
		Set<String> serviceNames = new HashSet<String>();
		
		for (String string : fileNames) 
			serviceNames.add(string.split("_")[0]);
		
		return serviceNames;
	}
	
	private String[] finder(String dirName, final String filePrefix, final String fileSuffix){
    	File dir = new File(dirName);
    	return dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if(name.startsWith(filePrefix) && name.endsWith(fileSuffix))
					return true;
				return false;
			}
		});
    }

}

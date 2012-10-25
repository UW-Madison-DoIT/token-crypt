package edu.wisc.doit.tcrypt;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TcryptHelper {

	//[Directory name from properties file]
	private String directoryname = "C:\\Users\\sridharan5\\Downloads\\apache-tomcat-6.0.35-windows-x64\\apache-tomcat-6.0.35\\webapps\\ROOT\\";
	
	public String getFileLocationToSaveOnServer(String serviceName, String remoteUser, String keyType) throws IOException {
		
		final Date generationTimestamp = new Date();
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		final String keyFilePrefix = directoryname + "/" + serviceName + "_"
				+ remoteUser + "_"
				+ simpleDateFormat.format(generationTimestamp) + "-" + keyType
				+ ".pem";
		return keyFilePrefix;
	}

	public String getFileLocationToDownloadFromServer(String serviceName, String remoteUser, String keyType) throws IOException {
		final String filePrefix = serviceName + "_" + remoteUser + "_" ;
		final String fileSuffix = "-" + keyType + ".pem";
		return directoryname + finder(directoryname, filePrefix, fileSuffix)[0];
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

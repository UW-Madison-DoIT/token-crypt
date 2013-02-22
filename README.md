# Token Crypt

## Installation Instructions

1. Set Local Path For Keys Storage
	* /token-crypt/tcrypt-web/src/main/resources/webapp.properties
	* edu.wisc.doit.tcrypt.path.keydirectory
    * **Local Directory MUST be READABLE and WRITEABLE by Tomcat process**
2. Build App With Maven 3
	* /token-crypt/pom.xml
	* From /token-crypt/ run **mvn clean package**
3. Deploy War File
	* /TokenCrypt/tcrypt-web/target/tcrypt-web-1.0.4-SNAPSHOT.war

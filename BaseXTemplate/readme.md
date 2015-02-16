Thanks to the BaseX team
See: https://github.com/BaseXdb and http://docs.basex.org/wiki/Category:Developer

Installation with CED2AR
1. Deploy BaseX.war on your Tomcat 7 server

2. Now you should change the default BaseX passwords. Go endpoint BaseX/rest. Default credentials are admin/admin

3. Change the password for the admin, writer and reader accounts using the command: 
	/rest?command=alter user <username> <password> 
	Note that password should be an md5 hash of your new password
4. In the source code, navigate to /profiles/localtemp/config.properties. Change the values of basex.reader.hash, basex.writer.hash and basex.admin.hash. The hash is a base 64 encoding of <username>:<password> (same as http basic authenication format)

5. Rebuild your project. You may need to manually clear the values deployed in the tomcat directory /webapps/ced2ar-web/WEB-INF/classes/ced2ar-web-config.properties

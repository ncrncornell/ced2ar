#Server Binary Installation
##Requirements
- Java 7
- Tomcat 7

1. Setup Java 7. Although Java 8 should work as well, but is not fully tested.
2. Install Tomcat 7. See: https://tomcat.apache.org/tomcat-7.0-doc/setup.html
3. Configure the Tomcat 7 manager, and create an account to access it: http://tomcat.apache.org/tomcat-7.0-doc/manager-howto.html#Configuring_Manager_Application_Access
4. Login into the manager page using a browser at localhost:{port}/manager
5. Upload the BaseX.war file: https://github.com/ncrncornell/ced2ar/tree/master/BaseXTemplate
6. Download the ced2ar-web.war file from this directory (https://github.com/ncrncornell/ced2ar/edit/master/Binaries/Server/)
7. Open the ced2ar-web.war archive with archive tool. (7Zip, winzip, etc. should be able to open it)
8. Extract the configuration file  /WEB-INF/classes/ced2ar-web-config.properties
9. Edit this file using a text editor. This set isn't required, however, we suggest that you update the default passwords. See: https://github.com/ncrncornell/ced2ar/wiki/The-CED2AR-Configuration-File
10. Replace the default configuration file in the ced2ar-web.war archive with the edited copy
11. Upload the ced2ar-web file via the Tomcat 7 manager

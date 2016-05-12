#Server Binary Installation
##Requirements
- Java 7+
- Tomcat 7+

1. Setup Java 7. Although Java 8 should work as well, but is not fully tested.
2. Install Tomcat. See the [Tomcat Docs](https://tomcat.apache.org/tomcat-7.0-doc/setup.html).
3. Configure the Tomcat manager, and [create an account](http://tomcat.apache.org/tomcat-7.0-doc/manager-howto.html#Configuring_Manager_Application_Access) to access it. 
4. Login into the manager page using a browser at localhost:{port}/manager
5. Upload the [BaseX war file](BaseXTemplate/BaseX.war).
6. Download the ced2ar-web.war file from [this directory](https://github.com/ncrncornell/ced2ar/edit/master/Binaries/Server/).
7. Open the ced2ar-web.war archive with archive tool<sup>[1](#footnote1)</sup>. (7Zip, winzip, etc. should be able to open it).
8. Extract the configuration file of interest, e.g. /WEB-INF/classes/ced2ar-web-config.properties
9. Edit this file using a text editor. We suggest that you [update the default passwords] (https://github.com/ncrncornell/ced2ar/wiki/The-CED2AR-Configuration-Files).
10. Replace the default configuration file in the ced2ar-web.war  archive with the edited copy
11. Upload the ced2ar-web.war file via the Tomcat 7 manager, or copy the war file directly into the Tomcat7 webapps directory (by default, tomcat will autodeploy any new war files copied in). A direct copy may prevent premature deployment of large war files, but this generally shouldn't be an issue. If you have problems, particularly if the war file isn't being extracted in webapps due to a manual copy of the war file into webapps, you may need to `chmod 755 ced2ar-web.war`, and restart.
12. Wait 20 seconds or so; else you may get a 404 error as ced2ar takes some time to start.

<a name="footnote1">1</a>: If you are instead extracting the entire archive to a folder named `ced2ar-web`, as is common on the command line, you may want to take a few extra precautions:

1. Unzip to new folder: 
`unzip ced2arv2/Binaries/Server/ced2ar-web.war -d ced2ar-web`
2. Edit properties as described above. 
3. Archive again without having a `ced2ar-web` top-level directory: `jar -cvf ced2ar-web.war -C ced2ar-web .`


## Other configuration notes

Other files, aside from the configruation properties above, may need to be edited. For intstance, authorized users can be added by editing `WEB-INF/classes/ced2ar-user-config.properties`.

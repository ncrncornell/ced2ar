# Server Binary Installation
## Requirements
- Java 7+
- Tomcat 7+

1. Setup Java 7 or Java 8 (recommended).  Java 8<sup>[2](#footnote2)</sup> is not _fully_ tested.
2. Install Tomcat. See the [Tomcat Docs](https://tomcat.apache.org/tomcat-7.0-doc/setup.html).
3. Configure the Tomcat manager, and [create an account](http://tomcat.apache.org/tomcat-7.0-doc/manager-howto.html#Configuring_Manager_Application_Access) to access it. 
4. Login into the manager page using a browser at localhost:{port}/manager
5. Upload the [BaseX war file](/BaseXTemplate/BaseX.war).
6. Download the ced2ar-web.war file from [this directory](https://github.com/ncrncornell/ced2ar/edit/master/Binaries/Server/).
7. Open the ced2ar-web.war archive with archive tool<sup>[1](#footnote1)</sup>. (7Zip, winzip, etc. should be able to open it).
8. Extract the configuration file of interest, e.g. /WEB-INF/classes/ced2ar-web-config.properties
9. Edit this file using a text editor. We suggest that you [update the default passwords] (https://github.com/ncrncornell/ced2ar/wiki/The-CED2AR-Configuration-Files).
10. Replace the default configuration file in the ced2ar-web.war  archive with the edited copy
11. Upload the ced2ar-web.war file via the Tomcat 7 manager, or copy the war file directly into the Tomcat7 webapps directory (by default, tomcat will autodeploy any new war files copied in). A direct copy may prevent premature deployment of large war files, but this generally shouldn't be an issue. If you have problems, particularly if the war file isn't being extracted in webapps due to a manual copy of the war file into webapps, you may need to `chmod 755 ced2ar-web.war`, and restart.
12. Wait 20 seconds or so; else you may get a 404 error as ced2ar takes some time to start.

13. _New for v2.9:_ Services are optional components, designed to be updated independently of ced2ar-web.
    - New server implementations can choose to install the experimental [ced2ardata2ddi](https://github.com/ncrncornell/ced2ardata2ddi) service. (See: [CED2AR Configuration Files](https://github.com/ncrncornell/ced2ar/wiki/The-CED2AR-Configuration-Files#user-content-services) , [v2.9 Release page.](https://github.com/ncrncornell/ced2ar/releases/tag/2.9.0.0) )
    - To _download_ the ced2ardata2ddi.war file, go to the [ced2ar release page](https://github.com/ncrncornell/ced2ar/releases) **or** the [_ced2ardata2ddi_ release page](https://github.com/ncrncornell/ced2ardata2ddi/releases).
    - To _install_, follow the same instructions in steps 11 - 12, using the `ced2ardata2ddi.war` file instead.
    - To _test_ this experimental service, there is an about page that you can hit to see if the service is up. Hitting https://localhost:8080/ced2ardata2ddi/ returns the about page.  You may need to modify the data2ddiUrl value to fit your environment.  See: [CED2AR Configuration Files](https://github.com/ncrncornell/ced2ar/wiki/The-CED2AR-Configuration-Files#user-content-services) for more information. 

_Footnotes_

<a name="footnote1">1</a>: If you are instead extracting the entire archive to a folder named `ced2ar-web`, as is common on the command line, you may want to take a few extra precautions:

1. Unzip to new folder: 
`unzip ced2arv2/Binaries/Server/ced2ar-web.war -d ced2ar-web`
2. Edit properties as described above. 
3. Archive again without having a `ced2ar-web` top-level directory: `jar -cvf ced2ar-web.war -C ced2ar-web .`

<a name="footnote2">2</a>: Most of our development servers run Tomcat 8 and Java 8.  Java 7 & Tomcat 7 are still in the instructions because a few organizations are required to run in a Java 7 environmment.

## Other configuration notes

* Other files, aside from the configuration properties above, may need to be edited. For instance, authorized users can be added by editing `WEB-INF/classes/ced2ar-user-config.properties`. See [the wiki](https://github.com/ncrncornell/ced2ar/wiki/The-CED2AR-Configuration-Files) for details.
* Enable an [Apache front](https://github.com/ncrncornell/ced2ar/wiki/Fronting-Tomcat-with-Apache) for Tomcat for better load-balancing and security.
* [Linux Server Notes](https://github.com/ncrncornell/ced2ar/wiki/Linux)

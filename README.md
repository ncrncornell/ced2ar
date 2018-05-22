[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.597000.svg)](https://doi.org/10.5281/zenodo.597000)

Looking for more information? Please take a look at our [Wiki](/ncrncornell/ced2ar/wiki).
* _Users_ can find the [User Guide](/ncrncornell/ced2ar/wiki/User's-Guide) with how to videos and [FAQs](/ncrncornell/ced2ar/wiki/FAQs).
* _Administrators_ and _Developers_ can find additional links on our [Wiki](/ncrncornell/ced2ar/wiki).

CED2AR uses Maven for build automation. Any IDE with Maven support can 
compile builds. The development team recommends Eclipse or IntelliJ 
for Java EE, which comes with Maven and Tomcat plugins for the newer 
versions.

## Building the Source Code

1. Clone the CED2AR source code to your local environment. 
2. Using Eclipse, import the existing Maven project. 
3. Select the root directory with the [pom.xml](Source/pom.xml) configuration file.
4. By default, running Maven `install` will build CED2AR in the localtemp 
profile. (See [profiles/localtemp](Source/pom.xml)). 
5. To create a new profile, copy the local folder, and rename it. Change options within the *config.properties files to customize your build.
6. Install Tomcat 7+<sup>[1](#footnote1)</sup> in your IDE. Ensure that 
the Java Build Path includes the Tomcat Library. 
7. Deploy the [BaseX war file](BaseXTemplate/BaseX.war) directly with the 
tomcat manager.
8. Run the project on the Tomcat web server. Ensure that the project is 
functioning before building a WAR. 
9. Run Maven build. Enter `install` as the goal, and select the new 
profile you created. If desired, save this configuration for later.

After building, CED2AR should produce a WAR archive in the target folder. 
For deploying the war, some of the [notes linked below](#installing-a-server-binary) may be helpful.

## Installing with Docker

### Standalone System

This is similar to installing a desktop binary configuration, in that it is
intended to be a self-contained CED2AR installation. Run the following
from the root of the repository directory:

1. `source ./Docker-deps/fat_build.sh` (Only needed once)
2. `./Docker-deps/standalone.sh`

To stop, press `Ctrl-C` or do `docker stop ced2ar`; resume again with `docker start ced2ar`.

Remove the container with `docker rm -f ced2ar`; data should be persisted at 
`../ced2ar-standalone-store/` by default even if the container is removed.

## Installing a Server Binary
To install a precompiled server binary please see:
https://github.com/ncrncornell/ced2ar/tree/master/Binaries/Server

## Installing a Desktop Binary
To run the desktop version, please see:
https://github.com/ncrncornell/ced2ar/tree/master/Binaries/Local

## Configuration
Additional [configuration notes](https://github.com/ncrncornell/ced2ar/wiki/The-CED2AR-Configuration-Files) may be found on the [wiki](https://github.com/ncrncornell/ced2ar/wiki).

**Copyright Cornell University 2012-2016**

<a name="footnote1">1</a>: CED2AR has been tested the most on Tomcat 7,
however we also have performed limited testing with Tomcat 8 and 9 and did not
experience any issues.

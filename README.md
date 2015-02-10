
CED2AR uses Maven for build automation. Any IDE with Maven support can compile builds. The developer team recommends Eclipse for Java EE, which comes with maven plugins for the newer versions.

Steps:

1. Clone the CED2AR source code to your local environment. 
2. Using Eclipse, import the existing Maven project. 
3. Select the root directory with the pom.xml configuration file.
4. By default, running Maven install will build CED2AR in the local profile. (See profiles/local). 
5. To create a new profile, copy the local folder, and rename it. Change options within the config.properties file to customize your build.
6. Install Tomcat 7 in your IDE. Ensure that the Java Build Path includes the Tomcat 7 Library. 
7. Run the project on the Tomcat 7 web server. Ensure that the project is functioning before building a WAR.
8. Run Maven build. Enter install as the goal, and select the new profile you created. If desired, save this configuration for later.

After building, CED2AR should produce a WAR archive in the target folder.

Copyright Cornell University 2012-2015

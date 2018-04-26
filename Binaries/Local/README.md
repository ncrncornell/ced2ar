# Running CED2AR locally

The files in this directory allow you to run an instance of
CED2AR locally (on your desktop). To do so, simply run

    java -jar ced2ar.jar

or double-click on ced2ar.jar, then open a browser at

    http://localhost:8080/ced2ar-web/

For convenience, a script is provided:

    Linux: ced2ar.sh
    OS X: ced2ar.sh
    Windows: na

Upon starting CED2AR, a random password will be generated. Use this password with the username "admin" to login. 


# Repackaging or rebuilding the local binary

## Complete rebuild

### v2.9.0.0
Previous desktop builds have been difficult.  Here are the steps using a MacBook Pro OS X (10.11.6).  Most commands are run in a _Terminal_ window. 

1. Go to the directory where the Maven project and the pom.xml are located. 

    ```
    cd ~/git/ced2ar/Source
    pwd ; ls -l pom.xml
    git status 
    ```

2. Build the project with the "roots" profile.  Run _one_ of the mvn commands. 

   ``` mvn clean package -P roots ```
    or 
   ``` mvn clean package -P-localtemp,roots  ```   

   1. Notes:
        * For this pom.xml, both commands have the same _effective_ pom.xml.  Try: ``` mvn help:effective-pom -P <profile> ```
        * Since we are going to manipulate the ced2ar-local.jar file, we do not need to install it.
        * On OS X, when excluding profiles, there is NO space between -P and the exclusion character -.
        * I could not get the following to work on the Mac. (```mvn install -P -localtemp roots```) 

3. Check for the jar file and some root property values.  
   1. Check for the new **ced2ar-local.jar** file.

      ```ls -l  ~/git/ced2ar/Source/target/ced2ar-local*.jar```   

   2. Check for roots PROFILE property values in ced2ar-web-config.properties 
      1. There are 2 ced2ar-web-config.properties files in /target...
      2. Make sure they are the same by doing a diff -s
      3. Search for 3 roots PROFILE property values to make sure you used the correct profile.

          ```
          find ~/git/ced2ar/Source/target/  -iname ced2ar-web-config.properties   -ls
          diff -sqy  ~/git/ced2ar/Source/target/ced2ar-web/WEB-INF/classes/ced2ar-web-config.properties  ~/git/ced2ar/Source/target/classes/ced2ar-web-config.properties
          grep -irI 'restricted=true\|pwdIsRandom=true\|authorizationStorage=NONE'  ~/git/ced2ar/Source/target/classes/ced2ar-web-config.properties
          ```

   2. Check for roots PROFILE property values in log4j.properties 
      1. Similar to previous check.

          ```
          find ~/git/ced2ar/Source/target/  -iname log4j.properties   -ls
          diff -sqy  ~/git/ced2ar/Source/target/ced2ar-web/WEB-INF/classes/log4j.properties  ~/git/ced2ar/Source//target/classes/log4j.properties
          grep -irI 'log4j.rootLogger=WARN,stdout\|File=ced2ar.log'  ~/git/ced2ar/Source/target/classes/log4j.properties
          ```

4. Copy ced2ar-local.jar to a new directory.

    ```
    mkdir -v ~/Desktop/ced2ar-local-temp
    cp -v ~/git/ced2ar/Source/target/ced2ar-local.jar   ~/Desktop/ced2ar-local-temp
    ls -l  ~/Desktop/ced2ar-local-temp
    ```

5. Open the copied jar file ```ced2ar-local.jar``` with an archive tool \*. (I used _WinZip 6 Mac_.  Depending on the archive tool, you may need to rename the .jar to a .zip file.)

   1. Edit the ```tomcat.standalone.properties``` file to include BaseX and any services (ced2ardata2ddi). Save the modified file back in to the .jar

      ```
      wars=ced2ar-web.war|/ced2ar-web;BaseX.war|/BaseX;ced2ardata2ddi.war|/ced2ardata2ddi
      ```
  
   2. Copy the war files (```BaseX.war```, ```ced2ardata2ddi.war```) _into_ ```ced2ar-local.jar```.  Place them in the _top_ level directory.  Current war file locations:

      ```
      ~/git/ced2ar/BaseXTemplate/BaseX.war (8.3.1),
      ~/git/ced2ardata2ddi/target/ced2ardata2ddi.war (1.2.0)
      ```

   3. Save the modified ```ced2ar-local.jar```.

9. Rename ```ced2ar-local.jar``` to ```ced2ar.jar``` (or something else).


  \* Archive tools - For this process:
- These tools **Worked**: _WinZip 6 Mac_ on OS X 10.11.6, _WinZip 18.5 Pro_ (64 bit) on Windows 10 Pro.
- These did **_not_** produce the desired result: Mac OS X _compress_, _jar_ (Java 8)


# Previous Releases - Repackaging or rebuilding the local binary

## Simple upgrade
1. Open the Maven project in eclipse
2. Build the project with the "roots" profile
3. Open the existing ced2ar.jar desktop application with an archive tool, and replace ced2ar.war with the newly built webapp.

## Complete rebuild (2.8 and lower) 
1. Open the Maven project
2. Build the project with the "roots" profile (`mvn install -P -localtemp roots`)
3. Copy ced2ar-local.jar to a new directory
4. Open the jar with an archive tool (may need to rename to a .zip file)
5. Edit the tomcat.standalone.properties to include BaseX. For example:

  ```
  wars=ced2ar-web.war|/ced2ar-web;BaseX.war|/BaseX
  ```

6. Copy the BaseX war file into the ced2ar-local.jar
7. Rename ced2ar-local.jar to ced2ar.jar (or something else)

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

## Simple upgrade
1. Open the Maven project in eclipse
2. Build the project with the "roots" profile
3. Open the existing ced2ar.jar desktop application with an archive tool, and replace ced2ar.war with the newly built webapp.

## Complete rebuild
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

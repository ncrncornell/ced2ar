# Overview

These patch files are needed to **upgrade** an existing CED2AR environment to 2.8.2.0.  If you upgrade and the Browse by Codebook tab is missing, you need to install these patches.

New installs do not need these patch files.

## Patch File (source) Location

The patch files are on github at [Patches/CDR-157](https://github.com/ncrncornell/ced2ar/tree/master/Patches/CDR-157).

* **ced2ar-web-beans-CDR-157.xml** - Updates the existing ced2ar-web-beans.xml file.  Adds 14 new properties to the config bean.

* **ced2ar-web-config-CDR-157.properties** - Updates the existing ced2ar-web-config.properties.  Adds values for the 14 new properties.

## Configuration File (target) Location

Both of the files that need to be patched are located in a Tomcat deployment directory.  Here is an Ubuntu (linux) directory example:
  ```
    /var/lib/tomcat7/webapps/ced2ar-web/WEB-INF/classes/
  ```

**NOTE:** It is highly recommended that you make a backup copy of the files that need to be patched.  The files are:
* WEB-INF/classes/ced2ar-web-beans.xml
* WEB-INF/classes/ced2ar-web-config.properties

## Steps:
1. Insert the contents of ced2ar-web-beans-CDR-157.xml INTO ced2ar-web-beans.xml
     * Edit ced2ar-web-beans.xml
     * Find the following code section.
    ```
    <property name="openAccess" value="${openAccess}" />

  </bean>
    ```
     * Insert the contents of ced2ar-web-beans-CDR-157.xml just below <property name=  and above </bean>
     * Save the changes.

2. Insert the contents of ced2ar-web-config-CDR-157.properties at the bottom of ced2ar-web-config.properties
     * Edit ced2ar-web-config.properties 
     * Go to the bottom of the file.
     * Insert the contentes of ced2ar-web-config-CDR-157.properties at the bottom of ced2ar-web-config.properties 
     * Save the changes.

3. Stop and restart the CED2AR application OR the Tomcat server.

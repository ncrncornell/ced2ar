# Overview

IF you are **upgrading** an existing installation of CED2AR v2.8.2 **AND you back up and restore your config files** (like we do) THEN you need to add the contents of the patch files here to your existing config files.

New installs do not need these patch files.

## Patch File (source) Location

The patch files are on github at [Patches/v2.9.0.0](https://github.com/ncrncornell/ced2ar/tree/master/Patches/v2.9.0.0).

* **ced2ar-web-beans-v2.9.0.0.xml** - Updates the existing ced2ar-web-beans.xml file.  Adds 2 new properties to the config bean.

* **ced2ar-web-config-v2.9.0.0.properties** - Updates the existing ced2ar-web-config.properties.  Adds values for the 2 new properties.

## Configuration File (target) Location

The files that need to be patched are located under the Tomcat deployment directory under `WEB-INF/classes/`.  Here is an Ubuntu (Linux) directory example:
  ```
    /var/lib/tomcat7/webapps/ced2ar-web/WEB-INF/classes/
  ```

**NOTE:** It is highly recommended that you make a backup copy of the files that need to be patched.  The files are:

* `WEB-INF/classes/ced2ar-web-beans.xml`
* `WEB-INF/classes/ced2ar-web-config.properties`

## Steps:

1. Insert the contents of ced2ar-web-beans-v2.9.0.0.xml INTO ced2ar-web-beans.xml:
     * Edit ced2ar-web-beans.xml
     * Find the following code section.

        ```
        <property name="uiNavTabOtherMatLabel" value="${uiNavTabOtherMatLabel}" />
    
        </bean>
        ```

     * Insert the contents of ced2ar-web-beans-v2.9.0.0.xml just below `<property name=`  and above `</bean>`.
     * Save the changes.
2. Insert the contents of ced2ar-web-config-v2.9.0.0.properties at the bottom of ced2ar-web-config.properties
     * Edit ced2ar-web-config.properties
     * Go to the bottom of the file.
     * Insert the contents of ced2ar-web-config-v2.9.0.0.properties at the bottom of ced2ar-web-config.properties
     * Save the changes.
3. Stop and restart the CED2AR application OR the Tomcat server.


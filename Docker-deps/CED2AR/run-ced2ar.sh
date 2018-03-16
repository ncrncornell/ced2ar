#!/bin/sh
unzip -o /ced2ar-local.jar -d $CED2AR_HOME/

cd $CED2AR_HOME
zip -r ../ced2ar-modified.jar .
cd $CED2AR_HOME/..

/usr/bin/java -jar /ced2ar-modified.jar

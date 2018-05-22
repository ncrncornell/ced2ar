#!/bin/sh

cd "$CATALINA_HOME/webapps" || \
    { echo "couldn't cd to CATALINA_HOME/webapps, exiting"; exit -1; }

mkdir -p BaseX
unzip -n BaseX.war -d BaseX/
mkdir -p ced2ar-web
unzip -n ced2ar-web.war -d ced2ar-web/
catalina.sh run

#!/bin/sh
#
# Assumes updated ced2ar-web.war is in current directory.
#
# Define CED2AR_HOME and run like:
# CED2AR_HOME=/var/lib/tomcat7/webapps/ced2ar-web ./redeploy_ced2ar_v2.sh
#

if [ -z "${CED2AR_HOME+xxx}" ]; then echo "CED2AR_HOME not set!"; exit; fi

STARTDIR=${PWD}
mkdir -p $STARTDIR/ced2ar_v2_conf_backup
rm -fr $STARTDIR/ced2ar_v2_conf_backup/*
rsync -av --exclude='pom.properties' --include='*.properties' \
      --include='ced2ar-web-beans.xml' --include='*/' --exclude '*' \
      $CED2AR_HOME/  $STARTDIR/ced2ar_v2_conf_backup
unzip $STARTDIR/ced2ar-web.war -d $CED2AR_HOME
rsync -av $STARTDIR/ced2ar_v2_conf_backup/ $CED2AR_HOME

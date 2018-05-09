#!/bin/bash

#TODO: add an arg to see if we want to pull or build
DOCKER=docker
CED2AR_DIR=$(pwd)


#
# Core Config - it is advisable to change these (and possibly necessary on some systems)
#
# Host:
LOCAL_STORE="${CED2AR_DIR}/../ced2ar-standalone-store/"
CED2AR_GIT="${LOCAL_STORE}/git"
BASEX_DATA="${LOCAL_STORE}/basex-data"
# Container:
CED2AR_BASE="/usr/local/tomcat/webapps/ced2ar-web"
BASEX_BASE="/usr/local/tomcat/webapps/BaseX"

mkdir -p "$LOCAL_STORE"
mkdir -p "$CED2AR_GIT"

#
# Other possible config examples:
#
#
#   Tomcat Users:
#   --volume ${CED2AR_DIR}/Docker-deps/fat-tomcat/tomcat-users.xml:/usr/local/tomcat/conf/tomcat-users.xml
#

CMD="${DOCKER} run --detach=false \
  --publish=8888:8080 \
  --name ced2ar \
  --volume ${CED2AR_GIT_BASE}:${CED2AR_BASE}/WEB-INF/classes/ced2ar-web-config.properties \
  --volume ${BASEX_DATA}:${BASEX_BASE}/data \
  --volume ${CED2AR_DIR}/Docker-deps/Configs/Standalone/ced2ar-user-config.properties:${CED2AR_BASE}/WEB-INF/classes/ced2ar-user-config.properties \
  --volume ${CED2AR_DIR}/Docker-deps/Configs/Standalone/ced2ar-web-config.properties:${CED2AR_BASE}/WEB-INF/classes/ced2ar-web-config.properties \
  ${CED2AR_IMAGE}
"

echo "$CMD"
CONTAINER=$($CMD)
echo "Started container $CONTAINER"

#!/bin/bash

#TODO port over idea.sh, use this area to:
#COPY CD2R_WEB_CONFIG_PROPS $CED2AR_HOME/WEB-INF/classes/ced2ar-web-config.properties

DOCKER=docker
CED2AR_DIR=$(pwd)
#TODO: add an arg to see if we want to pull or build

CMD="${DOCKER} run --detach=false \
  --publish=8888:8080 \
  --name ced2ar \
  --volume ${CED2AR_DIR}/Docker-deps/fat-tomcat/tomcat-users.xml:/usr/local/tomcat/conf/tomcat-users.xml
  ${CED2AR_IMAGE}
"

echo $CMD
CONTAINER=$($CMD)

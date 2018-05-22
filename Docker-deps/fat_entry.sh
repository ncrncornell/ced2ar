#!/bin/bash

#TODO: add an arg to see if we want to pull or builds
DOCKER=docker

CMD="${DOCKER} run --detach=false \
  --publish=8888:8080 \
  --name ced2ar \
  ${CED2AR_IMAGE}
"

echo "$CMD"
CONTAINER=$($CMD)
echo "Started container $CONTAINER"

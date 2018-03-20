#!/bin/bash                                                                                                                                                                          
REPO=ncrn
TAG=ced2ar-testing0
export CED2AR_IMAGE="${REPO}:${TAG}"
docker build -t $CED2AR_IMAGE -f Dockerfile-Fat .

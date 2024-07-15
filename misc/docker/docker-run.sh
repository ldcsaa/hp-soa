#!/bin/bash

if [[ $1 == '-h' || $1 == '--help' || $# -lt 3 ]]; then
    echo " > Usage: $(basename $0) {\$PROJECT} {\$VERSION} {\$RUNTIME_ENV} [\$CONTAINER_NAME=<\$PROJECT-\$VERSION-\$RUNTIME_ENV-\$RANDOM>] [OTHER_DOCKER_RUN_OPTIONS]*"
    exit 0
fi

CUR_DIR=$(cd $(dirname $0); pwd)

FULL_PROJECT=$1
PROJECT=${1##*/}
VERSION=$2
RUNTIME_ENV=$3
CONTAINER_NAME=${4:-$PROJECT-$VERSION-$RUNTIME_ENV-$(echo $RANDOM |md5sum |head -c 10)}

if [[ -z "$PROJECT" || -z "$VERSION" || -z "$RUNTIME_ENV" ]]; then
    echo " > ERROR: '\$PROJECT', '\$VERSION' and '\$RUNTIME_ENV' parameters must be specified"
    eval "$0 -h"
    exit 1
fi

IMAGE_ID=$(docker images | grep -E "(^|\/)$FULL_PROJECT\s" |  grep -E "\s$VERSION\s" | grep -v '<none>' | sort -r | head -1 | awk '{print $3}')

if [[ -z "$IMAGE_ID" ]]; then
    echo " > ERROR: docker image not found ($PROJECT:$VERSION)"
    exit 2
fi

CMD="docker run -d -e RUNTIME_ENV=$RUNTIME_ENV --name $CONTAINER_NAME"

if [[ $# -gt 4 ]]; then
    shift 4
    CMD="$CMD $@"
fi

CMD="$CMD $IMAGE_ID"

echo " > try stop and remove old container '$CONTAINER_NAME' and rerun with image '$IMAGE_ID'"

docker stop $CONTAINER_NAME > /dev/null 2>&1
docker rm $CONTAINER_NAME > /dev/null 2>&1

echo " > EXECUTE: $CMD"
eval $CMD

docker container ls | grep -E "\s$CONTAINER_NAME$" | awk '{print " > create container -> (id: "$1",", "name: "$NF")"}'

#!/bin/bash

if [[ "$1" == '-h' || "$1" == '--help' || $# -lt 2 || $# -gt 3 ]]; then
	echo "Usage: docker-build.sh {\$PROJECT_PATH} {\$VERSION} {\$REPO_GROUP}"
	exit 0
fi

CUR_DIR=$(cd $(dirname $0); pwd)
ABSOLUTE_PATH=$(cd $1; pwd)
PROJECT_BASE_PATH=$(dirname $ABSOLUTE_PATH)
PROJECT=$(basename $ABSOLUTE_PATH)

VERSION=$2
REPO_GROUP="${3,,}"
REPOSITORY="${PROJECT,,}"

if [ -n "$REPO_GROUP" ]; then
    REPOSITORY=$REPO_GROUP/$REPOSITORY
fi

TARGET_DIR=target
CLASSES_DIR=classes
TMP_PATH=tmp
TMP_PROJECT_BASE_PATH=$CUR_DIR/$TMP_PATH
TMP_PROJECT_PATH=$TMP_PROJECT_BASE_PATH/$PROJECT

if [[ -d "$TMP_PROJECT_PATH" ]]; then
    rm -rf $TMP_PROJECT_PATH/*
else
    mkdir -p $TMP_PROJECT_PATH
fi

cp -f $ABSOLUTE_PATH/$TARGET_DIR/$PROJECT-$VERSION.jar $TMP_PROJECT_PATH/
cp -f $ABSOLUTE_PATH/$TARGET_DIR/$CLASSES_DIR/bootstrap.yml $TMP_PROJECT_PATH/

if [ -f "$ABSOLUTE_PATH/$TARGET_DIR/$CLASSES_DIR/git.properties" ]; then
    cp -f $ABSOLUTE_PATH/$TARGET_DIR/$CLASSES_DIR/git.properties $TMP_PROJECT_PATH/
fi

trap "rm -rf $TMP_PROJECT_PATH > /dev/null 2>&1" EXIT

docker build --build-arg PROJECT_BASE_PATH=$TMP_PATH --build-arg PROJECT=$PROJECT --build-arg VERSION=$VERSION -t $REPOSITORY:$2 $CUR_DIR

docker images | grep "$REPOSITORY" | grep -E "\s$VERSION\s"  | awk '{print $3}'
#!/bin/bash

if [[ "$1" == '-h' || "$1" == '--help' ]]; then
	echo "  > Usage: $(basename $0) [PROGRAM_PATH or PROGRAM_JAR] [ENV_VARIABLE_X=VALUE_X]*"
	exit 0
fi

BIN_DIR=$(cd $(dirname $0); pwd)

"$BIN_DIR"/stop.sh "$@"
"$BIN_DIR"/start.sh "$@"

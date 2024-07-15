#!/bin/bash

if [[ "$1" == '-h' || "$1" == '--help' ]]; then
	echo "  > Usage: $(basename $0) [PROGRAM_PATH or PROGRAM_JAR] [ENV_VARIABLE_X=VALUE_X]*"
	exit 0
fi

BIN_DIR=$(cd $(dirname $0); pwd)

source $BIN_DIR/env.sh "$@"

if [ $? -ne 0 ]; then
    exit 2
fi

LINE=$(netstat -ntlp | awk '{print $7,$4}' | grep -E ':'$SERVER_PORT'$')

if [ -n "$LINE" ]; then
    PID=${LINE%/*}
    echo "  > '$APP_NAME' has started -> (PID: $PID, SERVER_PORT: $SERVER_PORT)"
    exit 0
fi

echo "  > '$APP_NAME' has stopped -> no process running with (SERVER_PORT: $SERVER_PORT)"
exit 1

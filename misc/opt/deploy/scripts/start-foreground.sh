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

check_port()
{
    rs=0
    val=$(netstat -ntlp | awk '{print $4}' | grep -E ':'$1'$')
    
    if [ -z "$val" ]; then
        rs=1
    fi
    
    return $rs
}

echo "  > starting ... (APP_NAME: $APP_NAME, SERVER_PORT: $SERVER_PORT, RUNTIME_ENV: $RUNTIME_ENV)"

if check_port $SERVER_PORT; then
    echo "  > start failed -> (SERVER_PORT: $SERVER_PORT being in used)"
    exit 3
fi

echo "\$JAVA_OPTS: $JAVA_OPTS"

java $JAVA_OPTS -jar $PRG_JAR

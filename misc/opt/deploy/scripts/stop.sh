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

LINE=
PORT=$SERVER_PORT

check_pid()
{
    if [ -z "$1" ]; then
        return 1
    fi
    
    LINE=$(netstat -ntlp | awk '{print $7,$4}' | grep -E ':'$1'$')
    
    if [ -z "$LINE" ]; then
        return 2
    fi
    
    PORT=$1
    return 0
}

if ! check_pid "$SERVER_PORT"; then
    echo "  > skip stop -> no process running with (SERVER_PORT: $PORT)"
    exit 3
fi

echo "  > stopping ... (SERVER_PORT: $PORT)"

if [ -n "$LINE" ]; then
    tail -0f "$LOG_FILE" 2>/dev/null &
    trap "kill $! > /dev/null 2>&1" EXIT
    
    PID=${LINE%/*}
    
    OK='false'
    kill $PID
    
    SEP=3
    CNT=$((STARTUP_WAIT_SECONDS/SEP))

    for ((i=1; i<=$CNT; i++))
        do
            FLAG=$(ps -p $PID | awk '{print $1}' | grep -E '^'$PID'$')
            
            if [ -n "$FLAG" ]; then
                sleep $SEP
            else
                OK='true'
                break
            fi
        done

        if [ $OK != 'true' ]; then
            echo "  > time out, forcibly stopping ... (PID: $PID, SERVER_PORT: $PORT)"
            kill -9 $PID
        fi
fi

echo "  > stop ok !"
exit 0

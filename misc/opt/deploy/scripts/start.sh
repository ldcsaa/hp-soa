#!/bin/bash

BIN_DIR=$(cd $(dirname $0); pwd)
PRG_HOME=$(cd $(dirname $BIN_DIR); pwd)

source $BIN_DIR/env.sh

if [ $? -ne 0 ]; then
    exit 1
fi

PRG_JAR=

for i in $PRG_HOME/deploy/*.jar; do
    if [ -z "$PRG_JAR" ]; then
        PRG_JAR=$i
        break
    fi
done

if [[ -z "$PRG_JAR" || ! -f "$PRG_JAR" ]]; then
    echo "  > start failed -> (main jar file '$PRG_JAR' not exists)"
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

echo "  > starting ... (SERVER_PORT: $SERVER_PORT)"

if check_port $SERVER_PORT; then
    echo "  > start failed -> (SERVER_PORT: $SERVER_PORT being in used)"
    exit 3
fi

echo "\$JAVA_OPTS: $JAVA_OPTS"

tail -0f "$LOG_FILE" 2>/dev/null &
trap "kill $! > /dev/null 2>&1" EXIT

nohup java $JAVA_OPTS -jar $PRG_JAR > /dev/null 2>&1 &

PID=$!
RS=5
SEP=3
CNT=$((STARTUP_WAIT_SECONDS/SEP))

for ((i=1; i<=$CNT; i++))
do
    if ! check_port $SERVER_PORT; then
        ps -p $PID > /dev/null 2>&1
        if [ $? -eq 0 ]; then
            sleep $SEP
        else
            echo "  > start failed -> (exception occured, process '$PID' exit)"
            exit 4
        fi
    else
        RS=0
        break
    fi
done

if [ $RS -eq 0 ]; then
    echo "  > start ok !"
else
    echo "  > start failed -> (SERVER_PORT: $SERVER_PORT check time out, forcibly stop process '$PID')"
    kill -9 $PID
fi

exit $RS

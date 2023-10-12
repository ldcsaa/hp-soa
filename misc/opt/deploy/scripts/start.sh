#!/bin/bash

BIN_DIR=$(cd $(dirname $0); pwd)

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

JAVA_OPTS="\
-DskipTests=true \
-Dfile.encoding=UTF-8 \
-Djava.awt.headless=true \
-Djava.security.egd=file:/dev/./urandom \
-Dnetworkaddress.cache.ttl=10 -Dnetworkaddress.cache.negative.ttl=1 \
-XX:-OmitStackTraceInFastThrow -XX:+DisableExplicitGC -XX:MaxGCPauseMillis=50 -XX:+HeapDumpOnOutOfMemoryError \
-XX:+UnlockDiagnosticVMOptions -XX:+UnlockExperimentalVMOptions -XX:GuaranteedSafepointInterval=0 -XX:+UseCountedLoopSafepoints -XX:LoopStripMiningIter=1000 \
--add-opens java.base/java.lang=ALL-UNNAMED \
--add-opens java.base/java.lang.reflect=ALL-UNNAMED \
--add-opens java.base/java.util=ALL-UNNAMED \
--add-opens java.base/java.util.concurrent=ALL-UNNAMED \
--add-opens java.base/java.util.concurrent.atomic=ALL-UNNAMED \
--add-opens java.base/java.io=ALL-UNNAMED \
--add-opens java.base/java.nio=ALL-UNNAMED \
--add-opens java.base/java.math=ALL-UNNAMED \
--add-opens java.base/java.text=ALL-UNNAMED \
--add-opens java.base/java.time=ALL-UNNAMED \
--add-opens java.base/java.net=ALL-UNNAMED \
--add-opens java.base/javax.net.ssl=ALL-UNNAMED \
--add-opens java.base/java.security=ALL-UNNAMED \
--add-opens java.rmi/sun.rmi.transport=ALL-UNNAMED \
--add-opens java.base/jdk.internal.access=ALL-UNNAMED \
--add-opens java.base/jdk.internal.misc=ALL-UNNAMED"

if [ -n "$DUBBO_RESOLVE_FILE" ]; then
    JAVA_OPTS="-Ddubbo.resolve.file=$DUBBO_RESOLVE_FILE $JAVA_OPTS"
fi

if [ -n "$EXTENDED_PROPERTIES_FILE" ]; then
    JAVA_OPTS="-Dhp.soa.extended.properties.file=$EXTENDED_PROPERTIES_FILE $JAVA_OPTS"
fi

if [ -n "$SYSTEM_PROPERTIES_FILE" ]; then
    JAVA_OPTS="-Dhp.soa.system.properties.file=$SYSTEM_PROPERTIES_FILE $JAVA_OPTS"
fi

if [ -n "$LOG_FILE_PATH" ]; then
    JAVA_OPTS="-Dlog4j.logfile.path=$LOG_FILE_PATH $JAVA_OPTS"
fi

if [ -n "$LOG_LEVEL" ]; then
    JAVA_OPTS="-Dlog4j.log.level=$LOG_LEVEL $JAVA_OPTS"
fi

if [ -n "$RUNTIME_ENV" ]; then
    JAVA_OPTS="-Dspring.profiles.active=$RUNTIME_ENV $JAVA_OPTS"
fi

if [ -n "$JVM_OPTIONS" ]; then
    JAVA_OPTS="$JVM_OPTIONS $JAVA_OPTS"
fi

if [ -n "$JAVA_AGENT" ]; then
    JAVA_OPTS="$JAVA_OPTS $JAVA_AGENT"
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

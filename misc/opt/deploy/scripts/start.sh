#!/bin/bash
BIN_DIR=$(dirname $0)
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

JAVA_OPTS="-server -Xss256k -Dlog4j2.formatMsgNoLookups=true -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Dsun.net.inetaddr.ttl=10 -Dsun.net.inetaddr.negative.ttl=1"
JAVA_OPTS="$JAVA_OPTS -Dapp.id=${APP_ID} -Dapp.name=${APP_NAME} -Dapp.home=${PRG_HOME} -Dspring.profiles.active=${RUNTIME_ENV} -Dserver.port=${SERVER_PORT}"
JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.port=$JMX_PORT -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
JAVA_OPTS="$JAVA_OPTS -Ddruid.mysql.usePingMethod=false -Dlog4j.gelf.host=${GELF_HOST} -Dlog4j.gelf.port=${GELF_PORT} -Dlog4j.logfile.path=${LOG_FILE_PATH} -Dlog4j.log.level=${LOG_LEVEL}"
JAVA_OPTS="$JAVA_OPTS -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:LargePageSizeInBytes=64m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70"
#JAVA_OPTS="$JAVA_OPTS -XX:+UseCMSCompactAtFullCollection"
JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError"

if [ -n "$JVM_XMS" ]; then
	JAVA_OPTS="$JAVA_OPTS -Xms${JVM_XMS}"
fi

if [ -n "$JVM_XMX" ]; then
	JAVA_OPTS="$JAVA_OPTS -Xmx${JVM_XMX}"
fi

if [ -n "$DUBBO_PORT" ]; then
	JAVA_OPTS="$JAVA_OPTS -Ddubbo.protocol.port=${DUBBO_PORT}"
fi

if [ -n "$XXL_PORT" ]; then
	JAVA_OPTS="$JAVA_OPTS -Dxxl.job.executor.port=${XXL_PORT}"
fi

if [ -n "$JAVA_AGENT" ]; then
	JAVA_OPTS="$JAVA_OPTS $JAVA_AGENT"
fi

if [ -f "$DUBBO_RESOLVE_FILE" ]; then
	JAVA_OPTS="-Ddubbo.resolve.file=$DUBBO_RESOLVE_FILE $JAVA_OPTS"
fi

if [ -f "$DUBBO_PROPERTIES_FILE" ]; then
	JAVA_OPTS="-Ddubbo.properties.file=$DUBBO_PROPERTIES_FILE $JAVA_OPTS"
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

echo "  > starting ... ($SERVER_PORT)"

if check_port $SERVER_PORT; then
	echo "  > start failed -> (SERVER_PORT $SERVER_PORT being in used)"
	exit 3
fi

echo "\$JAVA_OPTS: $JAVA_OPTS"

tail -0f "$LOG_FILE" 2>/dev/null &
trap "kill $! > /dev/null 2>&1" EXIT

nohup java $JAVA_OPTS -jar $PRG_JAR > /dev/null 2>&1 &

PID=$!
RS=5
SEP=3
CNT=$((STARTUP_SECONDS/SEP))

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
	echo "  > start failed -> (SERVER_PORT $SERVER_PORT check time out, forcibly stop process '$PID')"
	kill -9 $PID
fi

exit $RS

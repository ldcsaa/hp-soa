#!/bin/bash
BIN_DIR=$(cd $(dirname $0); pwd)
export PRG_HOME=$(cd $(dirname $BIN_DIR); pwd)
export CONF_FILE="$BIN_DIR/application.properties"

if [ ! -f "$CONF_FILE" ]; then
	echo "  > environment check failed -> (config file '$CONF_FILE' not exists)"
	exit 1
fi

source /etc/profile

APP_ID_KEY='app.id'
APP_NAME_KEY='app.name'
SERVER_PORT_KEY='server.port'
JMX_PORT_KEY='jmx.port'
DUBBO_PORT_KEY='dubbo.protocol.port'
XXL_PORT_KEY='xxl.job.executor.port'
RUNTIME_ENV=${environment:-local}
JVM_XMS_KEY="env.${RUNTIME_ENV}.jvm.xms"
JVM_XMX_KEY="env.${RUNTIME_ENV}.jvm.xmx"
STARTUP_SECONDS_KEY="startup.max.wait.seconds"

export APP_ID=$(cat $CONF_FILE | grep -E '^'$APP_ID_KEY'=' | cut -d "=" -f 2 | tr -d "[ \r\n]")
export APP_NAME=$(cat $CONF_FILE | grep -E '^'$APP_NAME_KEY'=' | cut -d "=" -f 2 | tr -d "[ \r\n]")
export SERVER_PORT=$(cat $CONF_FILE | grep -E '^'$SERVER_PORT_KEY'=' | cut -d "=" -f 2 | tr -d "[ \r\n]")
export JMX_PORT=$(cat $CONF_FILE | grep -E '^'$JMX_PORT_KEY'=' | cut -d "=" -f 2 | tr -d "[ \r\n]")
export JVM_XMS=$(cat $CONF_FILE | grep -E '^'$JVM_XMS_KEY'=' | cut -d "=" -f 2 | tr -d "[ \r\n]")
export JVM_XMX=$(cat $CONF_FILE | grep -E '^'$JVM_XMX_KEY'=' | cut -d "=" -f 2 | tr -d "[ \r\n]")
export DUBBO_PORT=$(cat $CONF_FILE | grep -E '^'$DUBBO_PORT_KEY'=' | cut -d "=" -f 2 | tr -d "[ \r\n]")
export XXL_PORT=$(cat $CONF_FILE | grep -E '^'$XXL_PORT_KEY'=' | cut -d "=" -f 2 | tr -d "[ \r\n]")
STARTUP_SECONDS=$(cat $CONF_FILE | grep -E '^'$STARTUP_SECONDS_KEY'=' | cut -d "=" -f 2 | tr -d "[ \r\n]")

if [[ -z "$STARTUP_SECONDS" || "$STARTUP_SECONDS" -le 0 ]]; then
	STARTUP_SECONDS=90
fi

export STARTUP_SECONDS

if [[ -z "$APP_ID" || -z "$APP_NAME" || -z "$SERVER_PORT" || -z "$JMX_PORT" ]]; then
	echo "  > environment check failed -> (can't find '$APP_ID_KEY' / '$APP_NAME_KEY' / '$SERVER_PORT_KEY' / '$JMX_PORT_KEY' property in config file '$CONF_FILE')"
	exit 1
fi

LOG_LEVEL=
GELF_HOST=

if [ "$RUNTIME_ENV" == "production" ]; then
	LOG_LEVEL="INFO"
else
	LOG_LEVEL="DEBUG"
fi

if [ "$RUNTIME_ENV" == "local" ]; then
	GELF_HOST="udp:localhost"
else
	GELF_HOST="tcp:localhost"
fi

export LOG_LEVEL
export GELF_HOST
export GELF_PORT="12201"
export LOG_FILE_PATH="/data/logs/access"
export LOG_FILE="${LOG_FILE_PATH}/${APP_NAME}/service.log"
export DUBBO_PROPERTIES_FILE="/data/dubbo/dubbo.properties"
export DUBBO_RESOLVE_FILE="/data/dubbo/dubbo-resolve.properties"
export JAVA_AGENT_FILE="/opt/settings/javaagent.config"

JAVA_AGENT=

if [ -f "$JAVA_AGENT_FILE" ]; then
	while read LINE
	do
		if [[ -n "$LINE" && ${LINE:0:1} != "#" ]]; then
			LINE=${LINE//\$\{APP_NAME\}/$APP_NAME}
			LINE=${LINE//\$APP_NAME/$APP_NAME}
			LINE=${LINE//\$\{APP_ID\}/$APP_ID}
			LINE=${LINE//\$APP_ID/$APP_ID}
			
			if [ -z "$JAVA_AGENT" ]; then
				JAVA_AGENT="$LINE"
			else
				JAVA_AGENT="$JAVA_AGENT $LINE"
			fi
		fi
	done < $JAVA_AGENT_FILE
fi

export JAVA_AGENT

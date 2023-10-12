#!/bin/bash

BIN_DIR=$(cd $(dirname $0); pwd)
DEPLOY_DIR=$BIN_DIR/../deploy
export PRG_HOME=$(cd $(dirname $BIN_DIR); pwd)
export CONF_FILE="$DEPLOY_DIR/bootstrap.yml"

if [ ! -f "$CONF_FILE" ]; then
    echo "  > environment check failed -> (config file '$CONF_FILE' not exists)"
    exit 1
fi

source /etc/profile

export DEFAULT_JVM_OPTIONS=${DEFAULT_JVM_OPTIONS:--Xms256m -Xmx256m -Xss256k -XX:MaxDirectMemorySize=128m -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=256m -XX:ReservedCodeCacheSize=256m}
export DEFAULT_STARTUP_WAIT_SECONDS=${DEFAULT_STARTUP_WAIT_SECONDS:-90}

export RUNTIME_ENV=${RUNTIME_ENV:-local}
export SYSTEM_PROPERTIES_FILE=${SYSTEM_PROPERTIES_FILE:-/opt/hp-soa/config/system-config.properties}
export EXTENDED_PROPERTIES_FILE=${EXTENDED_PROPERTIES_FILE:-/opt/hp-soa/config/extended-config.properties}
export DUBBO_RESOLVE_FILE=${DUBBO_RESOLVE_FILE:-/opt/hp-soa/config/dubbo-resolve.properties}
export JAVA_AGENT_FILE=${JAVA_AGENT_FILE:-/opt/hp-soa/config/java-agent.config}

APP_NAME_KEY='hp.soa.web.app.name'
SERVER_PORT_KEY='server.port'
JVM_OPTIONS_KEY="jvm.options.${RUNTIME_ENV}"
STARTUP_WAIT_SECONDS_KEY="startup.max-wait-seconds"

OLD_IFS="$IFS"
IFS="#"
CONF=($(python3 $BIN_DIR/conf_parser.py $RUNTIME_ENV $CONF_FILE))
RS=$?
IFS="$OLD_IFS"

if [ $RS -ne 0 ]; then
  exit 1
fi

for i in "${CONF[@]}"; do
    key=${i%=*}
    val=${i##*=}
    
    if [ "$key" == "$APP_NAME_KEY" ]; then
        APP_NAME=$val
    elif [ "$key" == "$SERVER_PORT_KEY" ]; then
        SERVER_PORT=$val
    elif [ "$key" == "$JVM_OPTIONS_KEY" ]; then
        JVM_OPTIONS=$val
    elif [ "$key" == "$STARTUP_WAIT_SECONDS_KEY" ]; then
        STARTUP_WAIT_SECONDS=$val
    fi
done

if [[ -z "$APP_NAME" || -z "$SERVER_PORT"  ]]; then
    echo "  > environment check failed -> (can't find '$APP_NAME_KEY' / '$SERVER_PORT_KEY' property in config file '$CONF_FILE')"
    exit 1
fi

if [[ -z "$JVM_OPTIONS" ]]; then
    JVM_OPTIONS="$DEFAULT_JVM_OPTIONS"
fi

if [[ -z "$STARTUP_WAIT_SECONDS" || "$STARTUP_WAIT_SECONDS" -le 0 ]]; then
    STARTUP_WAIT_SECONDS=$DEFAULT_STARTUP_WAIT_SECONDS
fi

export APP_NAME
export SERVER_PORT
export JVM_OPTIONS
export STARTUP_WAIT_SECONDS

if [ -z "$LOG_LEVEL"]; then
    if [ "$RUNTIME_ENV" == "prod" ]; then
        LOG_LEVEL="INFO"
    else
        LOG_LEVEL="DEBUG"
    fi
fi

export LOG_LEVEL
export LOG_FILE_PATH=${LOG_FILE_PATH:-/data/logs/access}
export LOG_FILE="${LOG_FILE_PATH}/${APP_NAME}/service.log"

if [ -z "$JAVA_AGENT"]; then
    if [ -f "$JAVA_AGENT_FILE" ]; then
        while read LINE
        do
            if [[ -n "$LINE" && ${LINE:0:1} != "#" ]]; then
                LINE=${LINE//\$\{APP_NAME\}/$APP_NAME}
                LINE=${LINE//\$APP_NAME/$APP_NAME}
                
                if [ -z "$JAVA_AGENT" ]; then
                    JAVA_AGENT="$LINE"
                else
                    JAVA_AGENT="$JAVA_AGENT $LINE"
                fi
            fi
        done < $JAVA_AGENT_FILE
    fi
fi

export JAVA_AGENT

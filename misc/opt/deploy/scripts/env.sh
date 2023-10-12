#!/bin/bash

BIN_DIR=$(cd $(dirname $0); pwd)
DEPLOY_DIR=$BIN_DIR/../deploy
CONF_FILE="$DEPLOY_DIR/bootstrap.yml"

if [ ! -f "$CONF_FILE" ]; then
    echo "  > environment check failed -> (config file '$CONF_FILE' not exists)"
    exit 1
fi

source /etc/profile

DEFAULT_JVM_MEMORY_OPTIONS=${DEFAULT_JVM_MEMORY_OPTIONS:--Xms256m -Xmx256m -Xss256k -XX:MaxDirectMemorySize=128m -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=256m -XX:ReservedCodeCacheSize=256m}
DEFAULT_STARTUP_WAIT_SECONDS=${DEFAULT_STARTUP_WAIT_SECONDS:-90}

RUNTIME_ENV=${RUNTIME_ENV:-local}
SYSTEM_PROPERTIES_FILE=${SYSTEM_PROPERTIES_FILE:-/opt/hp-soa/config/system-config.properties}
EXTENDED_PROPERTIES_FILE=${EXTENDED_PROPERTIES_FILE:-/opt/hp-soa/config/extended-config.properties}
DUBBO_RESOLVE_FILE=${DUBBO_RESOLVE_FILE:-/opt/hp-soa/config/dubbo-resolve.properties}
JAVA_AGENT_FILE=${JAVA_AGENT_FILE:-/opt/hp-soa/config/java-agent.config}

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

if [[ -z "$STARTUP_WAIT_SECONDS" || "$STARTUP_WAIT_SECONDS" -le 0 ]]; then
    STARTUP_WAIT_SECONDS=$DEFAULT_STARTUP_WAIT_SECONDS
fi

if [ -z "$LOG_LEVEL" ]; then
    if [ "$RUNTIME_ENV" == "prod" ]; then
        LOG_LEVEL="INFO"
    else
        LOG_LEVEL="DEBUG"
    fi
fi

LOG_FILE_PATH=${LOG_FILE_PATH:-/data/logs/access}
LOG_FILE="${LOG_FILE_PATH}/${APP_NAME}/service.log"
HEAP_DUMP_PATH="${LOG_FILE_PATH}/${APP_NAME}"

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

JAVA_OPTS="\
-DskipTests=true \
-Dfile.encoding=UTF-8 \
-Djava.awt.headless=true \
-Djava.security.egd=file:/dev/./urandom \
-Dnetworkaddress.cache.ttl=10 -Dnetworkaddress.cache.negative.ttl=1 \
$DEFAULT_JVM_MEMORY_OPTIONS \
-XX:-OmitStackTraceInFastThrow -XX:+DisableExplicitGC -XX:MaxGCPauseMillis=50 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$HEAP_DUMP_PATH \
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

if [ -n "$LOG_LEVEL" ]; then
    JAVA_OPTS="-Dlog4j.log.level=$LOG_LEVEL $JAVA_OPTS"
fi

if [ -n "$LOG_FILE_PATH" ]; then
    JAVA_OPTS="-Dlog4j.logfile.path=$LOG_FILE_PATH $JAVA_OPTS"
fi

if [ -n "$DUBBO_RESOLVE_FILE" ]; then
    JAVA_OPTS="-Ddubbo.resolve.file=$DUBBO_RESOLVE_FILE $JAVA_OPTS"
fi

if [ -n "$EXTENDED_PROPERTIES_FILE" ]; then
    JAVA_OPTS="-Dhp.soa.extended.properties.file=$EXTENDED_PROPERTIES_FILE $JAVA_OPTS"
fi

if [ -n "$SYSTEM_PROPERTIES_FILE" ]; then
    JAVA_OPTS="-Dhp.soa.system.properties.file=$SYSTEM_PROPERTIES_FILE $JAVA_OPTS"
fi

if [ -n "$RUNTIME_ENV" ]; then
    JAVA_OPTS="-Dspring.profiles.active=$RUNTIME_ENV $JAVA_OPTS"
fi

if [ -n "$JVM_OPTIONS" ]; then
    JAVA_OPTS="$JAVA_OPTS $JVM_OPTIONS"
fi

if [ -n "$JAVA_AGENT" ]; then
    JAVA_OPTS="$JAVA_OPTS $JAVA_AGENT"
fi

export APP_NAME
export SERVER_PORT
export STARTUP_WAIT_SECONDS
export LOG_FILE
export JAVA_OPTS

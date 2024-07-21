#!/bin/bash

source /etc/profile

BIN_DIR=$(cd $(dirname $0); pwd)
PATH=$BIN_DIR:$PATH
PRG_HOME=$BIN_DIR
PRG_JAR=$1

# eval program variables from command line arguments

shift

for exp in "$@"; do
    eval "$exp"
done

# check program home and program main jar file

if [[ -f "$PRG_JAR" ]]; then
    PRG_HOME=$(cd $(dirname $PRG_JAR); pwd)
elif [[ -d "$PRG_JAR" ]]; then
    PRG_HOME=$(cd $PRG_JAR; pwd)
    PRG_JAR=
fi

if [[ -z "$PRG_JAR" && -d "$PRG_HOME" ]]; then
    for i in $PRG_HOME/*.jar; do
        PRG_JAR=$i
        break
    done
fi

if [[ -z "$PRG_JAR" || ! -f "$PRG_JAR" ]]; then
    echo "  > environment check failed -> (main jar file '$PRG_JAR' not exists)"
    exit 1
fi

# check bootstrap yaml file

if [ -z "$BOOTSTRAP_YAML_FILE" ]; then
    BOOTSTRAP_YAML_FILE=$PRG_HOME/bootstrap.yml
    if [[ ! -f "$BOOTSTRAP_YAML_FILE" && -f "$PRG_HOME/bootstrap.yaml" ]]; then
        BOOTSTRAP_YAML_FILE=$PRG_HOME/bootstrap.yaml
    fi
fi

if [ ! -f "$BOOTSTRAP_YAML_FILE" ]; then
    echo "  > environment check failed -> (bootstrap yaml file '$BOOTSTRAP_YAML_FILE' not exists)"
    exit 1
fi

DEFAULT_JVM_MEMORY_OPTIONS=${DEFAULT_JVM_MEMORY_OPTIONS:--Xms256m -Xmx256m -Xss256k -XX:MaxDirectMemorySize=128m -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=256m -XX:ReservedCodeCacheSize=256m}
DEFAULT_STARTUP_WAIT_SECONDS=${DEFAULT_STARTUP_WAIT_SECONDS:-180}

RUNTIME_ENV=${RUNTIME_ENV:-local}
CONFIG_FILE_BASE_PATH=${CONFIG_FILE_BASE_PATH:-/opt/hp-soa/config}
CONFIG_FILE_PATH=$CONFIG_FILE_BASE_PATH/$RUNTIME_ENV

if [[ ! -d "$CONFIG_FILE_PATH" || "$(ls -A $CONFIG_FILE_PATH)" == "" ]]; then
    CONFIG_FILE_PATH=$CONFIG_FILE_BASE_PATH
fi

SYSTEM_PROPERTIES_FILE=${SYSTEM_PROPERTIES_FILE:-$CONFIG_FILE_PATH/system-config.properties}
EXTENDED_PROPERTIES_FILE=${EXTENDED_PROPERTIES_FILE:-$CONFIG_FILE_PATH/extended-config.properties}
DUBBO_RESOLVE_PROPERTIES_FILE=${DUBBO_RESOLVE_PROPERTIES_FILE:-$CONFIG_FILE_PATH/dubbo-resolve.properties}
JAVA_AGENT_CONFIG_FILE=${JAVA_AGENT_CONFIG_FILE:-$CONFIG_FILE_PATH/java-agent.config}

SERVER_PORT_KEY='server.port'
APP_NAME_KEY='hp.soa.web.app.name'
REMOTE_DEBUG_PORT_KEY='hp.soa.special.remote-debug.port'
JVM_OPTIONS_KEY="hp.soa.special.jvm-options.${RUNTIME_ENV}"
STARTUP_WAIT_SECONDS_KEY="hp.soa.special.startup.max-wait-seconds"

ENABLE_REMOTE_DEBUG_ENVS=("dev" "test" "qa")
PRODUCTION_ENVS=("pro" "prod" "product" "production")

get_yaml_property()
{
    local arr=(${2//./ })
    local keys=()
    local succ=0
    local cur_key=
    local new_key=
    local str=
    local val=
    local i=0

    for var in ${arr[@]}; do
        i=$((i+1))
        new_key=$cur_key
        str="."

        for n in ${keys[@]}; do
            str=$str'["'$n'"]'
        done

        if [ -z $cur_key ]; then
            new_key=$var
        else
            new_key=$cur_key'.'$var
        fi  

        str=$str'["'$new_key'"]'

        val=$(yq "$str" $1)

        if [[ -z "$val" || "${val,,}" == "null" ]]; then
            cur_key=$new_key
            val=
        else
            keys+=($new_key)
            cur_key=
        fi  

        if [[ "$i" -eq ${#arr[@]} ]]; then
            echo $val
        fi  
    done
}

APP_NAME=${APP_NAME:-$(get_yaml_property $BOOTSTRAP_YAML_FILE "$APP_NAME_KEY")}
SERVER_PORT=${SERVER_PORT:-$(get_yaml_property $BOOTSTRAP_YAML_FILE "$SERVER_PORT_KEY")}
REMOTE_DEBUG_PORT=${REMOTE_DEBUG_PORT:-$(get_yaml_property $BOOTSTRAP_YAML_FILE "$REMOTE_DEBUG_PORT_KEY")}
JVM_OPTIONS=${JVM_OPTIONS:-$(get_yaml_property $BOOTSTRAP_YAML_FILE "$JVM_OPTIONS_KEY")}
STARTUP_WAIT_SECONDS=${STARTUP_WAIT_SECONDS:-$(get_yaml_property $BOOTSTRAP_YAML_FILE "$STARTUP_WAIT_SECONDS_KEY")}

if [[ -z "$APP_NAME" || -z "$SERVER_PORT"  ]]; then
    echo "  > environment check failed -> (can't find '$APP_NAME_KEY' / '$SERVER_PORT_KEY' property in bootstrap yaml file '$BOOTSTRAP_YAML_FILE')"
    exit 1
fi

if [[ -z "$STARTUP_WAIT_SECONDS" || "$STARTUP_WAIT_SECONDS" -le 0 ]]; then
    STARTUP_WAIT_SECONDS=$DEFAULT_STARTUP_WAIT_SECONDS
fi

if [[ -z "$REMOTE_DEBUG_PORT" || "$REMOTE_DEBUG_PORT" -le 0 ]]; then
    REMOTE_DEBUG_PORT=$((SERVER_PORT + 10000))
fi

if [[ -z "$REMOTE_DEBUG_ADDR" ]]; then
    REMOTE_DEBUG_ADDR="0.0.0.0"
fi

if [[ ${ENABLE_REMOTE_DEBUG_ENVS[@]/${RUNTIME_ENV,,}/} != ${ENABLE_REMOTE_DEBUG_ENVS[@]} ]]; then
    REMOTE_DEBUG_OPTIONS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$REMOTE_DEBUG_ADDR:$REMOTE_DEBUG_PORT"
fi

LOG_FILE_PATH=${LOG_FILE_PATH:-/data/logs/access}
LOG_FILE="${LOG_FILE_PATH}/${APP_NAME}/service.log"
HEAP_DUMP_PATH="${LOG_FILE_PATH}/${APP_NAME}"

if [ -z "$LOG_LEVEL" ]; then
    if [[ ${PRODUCTION_ENVS[@]/${RUNTIME_ENV,,}/} != ${PRODUCTION_ENVS[@]} ]]; then
        LOG_LEVEL="INFO"
    else
        LOG_LEVEL="DEBUG"
    fi
fi

if [ -z "$JAVA_AGENT"]; then
    if [ -f "$JAVA_AGENT_CONFIG_FILE" ]; then
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
        done < $JAVA_AGENT_CONFIG_FILE
    fi
fi

JAVA_OPTS="\
-D$APP_NAME_KEY=$APP_NAME \
-D$SERVER_PORT_KEY=$SERVER_PORT \
-Duser.dir=$PRG_HOME \
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
--add-opens java.base/jdk.internal.misc=ALL-UNNAMED \
--add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
--add-opens=java.base/sun.nio.cs=ALL-UNNAMED \
--add-opens=java.base/sun.security.action=ALL-UNNAMED \
--add-opens=java.base/sun.util.calendar=ALL-UNNAMED"

if [ -n "$LOG_LEVEL" ]; then
    JAVA_OPTS="-Dhpsoa.log4j.log.level=$LOG_LEVEL $JAVA_OPTS"
fi

if [ -n "$LOG_FILE_PATH" ]; then
    JAVA_OPTS="-Dhpsoa.log4j.logfile.path=$LOG_FILE_PATH $JAVA_OPTS"
fi

if [ -f "$DUBBO_RESOLVE_PROPERTIES_FILE" ]; then
    JAVA_OPTS="-Ddubbo.resolve.file=$DUBBO_RESOLVE_PROPERTIES_FILE $JAVA_OPTS"
fi

if [ -f "$EXTENDED_PROPERTIES_FILE" ]; then
    JAVA_OPTS="-Dhp.soa.extended.properties.file=$EXTENDED_PROPERTIES_FILE $JAVA_OPTS"
fi

if [ -f "$SYSTEM_PROPERTIES_FILE" ]; then
    JAVA_OPTS="-Dhp.soa.system.properties.file=$SYSTEM_PROPERTIES_FILE $JAVA_OPTS"
fi

if [ -n "$RUNTIME_ENV" ]; then
    JAVA_OPTS="-Dspring.profiles.active=$RUNTIME_ENV $JAVA_OPTS"
fi

if [ -n "$JVM_OPTIONS" ]; then
    JAVA_OPTS="$JAVA_OPTS $JVM_OPTIONS"
fi

if [ -n "$REMOTE_DEBUG_OPTIONS" ]; then
    JAVA_OPTS="$JAVA_OPTS $REMOTE_DEBUG_OPTIONS"
fi

if [ -n "$JAVA_AGENT" ]; then
    JAVA_OPTS="$JAVA_OPTS $JAVA_AGENT"
fi

export PRG_HOME
export PRG_JAR
export APP_NAME
export SERVER_PORT
export STARTUP_WAIT_SECONDS
export LOG_FILE
export JAVA_OPTS

[[用户指南](user_guide.md)]

---

## 一、概述
&nbsp;&nbsp;微服务应用开发声明周期中应用发布是一个重要环节，好的应用发布机制与流程能大大提高测试效率，加快投产速度，同时也能提升线上问题处理和修复效率。HP-SOA 为应用程序打包发布提供了强大支持：
- 应用配置与代码分离，一次打包可以部署到所有运行环境。
- 提供 [start.sh](../../misc/opt/deploy/scripts/start.sh) 、[stop.sh](../../misc/opt/deploy/scripts/stop.sh) 、[status.sh](../../misc/opt/deploy/scripts/status.sh) 等通用运行脚本，并支持参数化执行。所有应用可以共用一套运行脚本，不必在不同应用中来回拷贝修改。
- 提供通用打包描述文件 [assembly.xml](../../misc/opt/deploy/assembly/assembly.xml) 、[assembly-with-git.xml](../../misc/opt/deploy/assembly/assembly-with-git.xml)，规范发布包格式。
- Docker 发布支持：提供通用镜像模版 [Dockerfile](../../misc/docker/Dockerfile) 、镜像构建脚本 [docker-build.sh](../../misc/docker/docker-build.sh) 和容器运行脚本 [docker-run.sh](../../misc/docker/docker-run.sh) ，所有应用可以共用一套 Docker 发布文件，不必在不同应用中来回拷贝修改。

## 二、应用配置与代码分离

&nbsp;&nbsp;HP-SOA 提倡应用配置与代码分离，应用配置与代码分离有包括以下几个层面：
1. 应用程序私有配置：与应用自身和运行环境有关，建议把配置项分离到 Nacos、Apollo 等应用配置管理系统。
2. 应用程序公共配置：与运行环境相关，多个应用的配置内容都相同配置项（如：spring-boot 除服务端口以外的配置项、数据库连接属性除连接地址以外的配置、Redis 中间件配置等），建议把配置项也分离到 Nacos、Apollo 等应用配置管理系统，由各应用按需加载。
3. 系统级公共配置：这类配置可能需要在连接应用配置管理系统之前，甚至在加载 spring-boot 前就需要加载（如：Nacos 配置管理系统服务地址、Log4j Context Selector、APM 探针 Agent 参数等），建议把这类配置项分离到运行机器的固定位置，所有应用程序启动时都读取这些配置。HP-SOA 提供的运行脚本默认会加载以下系统级配置文件：
  - 系统配置文件：启动 spring-boot 前由Java系统加载，用于配置那些需要在 spring-boot 启动前已配置好的系统属性（如：lo4j日志相关配置）。默认配置文件：`/opt/hp-soa/config/${RUNTIME_ENV}/system-config.properties`，如果`${RUNTIME_ENV}`为空或该默认配置文件所在目录为空则尝试加载`/opt/hp-soa/config/system-config.properties`，可通过JVM启动参数`-Dhp.soa.system.properties.file`修改系统配置文件路径。（参考：[system-config.properties](../../misc/opt/hp-soa/config/system-config.properties)）
  - 扩展配置文件：由 spring-boot 加载，用于配置注册中心地址、配置中心地址和系统全局设置等公共应用属性，默认配置文件：`/opt/hp-soa/config/${RUNTIME_ENV}/extended-config.properties`，如果`${RUNTIME_ENV}`为空或该默认配置文件所在目录为空则尝试加载`/opt/hp-soa/config/extended-config.properties`，可通过JVM启动参数`-Dhp.soa.extended.properties.file`修改扩展配置文件路径。（参考：[extended-config.properties](../../misc/opt/hp-soa/config/extended-config.properties)）
  - Java Agent 配置文件：运行脚本会解析 Java Agent 配置文件并转换为 JVM 命令行参数，从而注入 Java Agent，默认配置文件：`/opt/hp-soa/config/${RUNTIME_ENV}/java-agent.config`，如果`${RUNTIME_ENV}`为空或该默认配置文件所在目录为空则尝试加载`/opt/hp-soa/config/java-agent.config`，可通过JVM启动参数`-Dhp.soa.extended.properties.file`修改 Java Agent 配置文件路径。（参考：[java-agent.config](../../misc/opt/hp-soa/config/java-agent.config)）
  - Dubbo 接口直连配置文件（用于 Dubbo 项目调试）：默认配置文件：`/opt/hp-soa/config/${RUNTIME_ENV}/dubbo-resolve.properties`，如果`${RUNTIME_ENV}`为空或该默认配置文件所在目录为空则尝试加载`/opt/hp-soa/config/dubbo-resolve.properties`，可通过JVM启动参数`-Ddubbo.resolve.file`修改 Dubbo 接口直连配置文件路径。（参考：[dubbo-resolve.properties](../../misc/opt/hp-soa/config/dubbo-resolve.properties)）

&nbsp;&nbsp;除此以外，有些与具体应用发布部署有关的特殊配置无法分离到配置管理系，如：
- 应用程序启动等待时间：`STARTUP_WAIT_SECONDS`，超过这个时间则认为启动失败
- 应用程序在不同环境下特定的 JVM 参数：`JVM_OPTIONS`，如：不同运行环境的内存配置
- 应用程序的远程调试端口：`REMOTE_DEBUG_PORT`

&nbsp;&nbsp;上述这些配置可以以命令行参数的方式传入运行脚本，如：`start.sh "" "STARTUP_WAIT_SECONDS=90"`，设置应用程序启动等待时间为 90 秒。HP-SOA 同时提供了另一种方式处理上述特殊配置：把上述配置项配置在应用程序的`bootstrap.yml`配置文件，由运行脚本解析这些配置，如：

```yaml
## 特殊配置
hp.soa.special:
  # 应用程序启动等待秒数（默认：180 秒）
  startup.max-wait-seconds: 90
  # 远程调试端口（默认：${server.port} + 10000）
  remote-debug.port: 19002
  # 各运行环境下自定义 JVM 参数（默认：空）
  jvm-options:
    # 本地
    local: -Xms256m -Xmx256m
    # 开发
    dev: -Xms256m -Xmx256m
    # 测试
    test: -Xms256m -Xmx512m
    # 生产
    prod: -Xms1g -Xmx1g
```

## 三、运行脚本
&nbsp;&nbsp;HP-SOA 提供以下通用运行脚本执行应用程序启停操作：
- [start.sh](../../misc/opt/deploy/scripts/start.sh) ：以后台方式启动应用程序。
- [start-foreground.sh](../../misc/opt/deploy/scripts/start-foreground.sh) ：以前台方式启动应用程序，主要用作 Docker 容器的启动命令，也可以用于跟踪调试应用程序的启动过程。
- [stop.sh](../../misc/opt/deploy/scripts/stop.sh) ：停止应用程序。
- [restart.sh](../../misc/opt/deploy/scripts/restart.sh) ：重启应用程序。
- [status.sh](../../misc/opt/deploy/scripts/status.sh) ：查看启动应用程序状态。

#### 1. 启动应用程序：[start.sh](../../misc/opt/deploy/scripts/start.sh) / [start-foreground.sh](../../misc/opt/deploy/scripts/start-foreground.sh)

```shell
$ start.sh -h
  > Usage: start.sh [PROGRAM_PATH or PROGRAM_JAR] [ENV_VARIABLE_X=VALUE_X]*
```
&nbsp;&nbsp;*命令参数：*

- 第一个参数：应用程序 jar 文件或 jar 文件所在目录路径。可选，默认值：空，表示 [start.sh](../../misc/opt/deploy/scripts/start.sh) 文件所在目录路径。
- 其它参数：执行环境参数，可选，可以设置多个。

&nbsp;&nbsp;*执行环境参数：*

- `DEFAULT_JVM_MEMORY_OPTIONS` ：默认 JVM 内存选项，默认值：`-Xms256m -Xmx256m -Xss256k -XX:MaxDirectMemorySize=128m -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=256m -XX:ReservedCodeCacheSize=256m`
- `DEFAULT_STARTUP_WAIT_SECONDS` ：默认应用程序启动等待秒数，默认值：180 秒
- `RUNTIME_ENV` ：运行环境，默认值：`local`，根据实际需要设置，如：`dev`、`test`、`qa`、`production` 等
- `CONFIG_FILE_BASE_PATH` ：系统级公共配置配置文件父目录，默认值：`/opt/hp-soa/config`
- `SYSTEM_PROPERTIES_FILE` ：系统配置文件，默认值：`$CONFIG_FILE_BASE_PATH/$RUNTIME_ENV/system-config.properties`
- `EXTENDED_PROPERTIES_FILE` ：扩展配置文件，默认值：`$CONFIG_FILE_BASE_PATH/$RUNTIME_ENV/extended-config.properties`
- `DUBBO_RESOLVE_PROPERTIES_FILE` ：Dubbo 接口直连配置文件，默认值：`$CONFIG_FILE_BASE_PATH/$RUNTIME_ENV/dubbo-resolve.properties`
- `JAVA_AGENT_CONFIG_FILE` ：Java Agent 配置文件，默认值：`$CONFIG_FILE_BASE_PATH/$RUNTIME_ENV/java-agent.config`
- `JAVA_AGENT` ：Java Agent JVM 参数，默认值：从`$JAVA_AGENT_CONFIG_FILE`Java Agent 配置文件读取
- `APP_NAME` ：应用程序名称，默认值：`bootstrap.yml`的`hp.soa.web.app.name`配置值
- `SERVER_PORT` ：应用程序服务端口，默认值：`bootstrap.yml`的`server.port`配置值
- `REMOTE_DEBUG_PORT` ：远程调试端口，默认值：`$SERVER_PORT+10000`。当`$RUNTIME_ENV`为 `dev` / `test` / `qa` 时会开启远程调试功能
- `JVM_OPTIONS` ：额外 JVM 选项，默认值：`bootstrap.yml`的`hp.soa.special.jvm-options.$RUNTIME_ENV`配置值
- `STARTUP_WAIT_SECONDS` ：应用程序启动等待秒数，默认值：`$DEFAULT_STARTUP_WAIT_SECONDS`
- `LOG_FILE_PATH` ：应用程序日志目录，默认值：`/data/logs/access`
- `LOG_FILE` ：应用程序日志文件，默认值：`$LOG_FILE_PATH/$APP_NAME/service.log`
- `LOG_LEVEL` ：应用程序日志级别，当`$RUNTIME_ENV`为 `pro` / `prod` / `product` / `production` 时默认值为`INFO`，当`$RUNTIME_ENV`为其它值时默认值为`DEBUG`
- `HEAP_DUMP_PATH` ：堆转存文件目录，默认值：`$LOG_FILE_PATH/$APP_NAME`

&nbsp;&nbsp;*运行示例：*

- `start.sh` ：运行 *start.sh* 脚本所在目录下的第一个 jar 文件
- `start.sh "" SERVER_PORT=9876` ：运行 *start.sh* 脚本所在目录的第一个 jar 文件，并设置应用程序服务端口为 *9876*
- `start.sh  /path/to/app` ：运行 */path/to/app* 目录的第一个 jar 文件
- `start.sh  /path/to/app RUNTIME_ENV=dev` ：运行 */path/to/app* 目录的第一个 jar 文件，并设置运行环境为 *dev*
- `start.sh  /path/to/app/service.jar RUNTIME_ENV=qa LOG_LEVEL=WARN` ：运行 */path/to/app/service.jar* ，并设置运行环境为 *qa*，应用程序日志级别为 *WARN*

#### 2. 停止应用程序：[stop.sh](../../misc/opt/deploy/scripts/stop.sh)

```shell
$ stop.sh -h
  > Usage: stop.sh [PROGRAM_PATH or PROGRAM_JAR] [ENV_VARIABLE_X=VALUE_X]*
```
&nbsp;&nbsp;命令参数与 [start.sh](../../misc/opt/deploy/scripts/start.sh) 一致。

#### 3. 停止应用程序：[restart.sh](../../misc/opt/deploy/scripts/restart.sh)

```shell
$ restart.sh -h
  > Usage: restart.sh [PROGRAM_PATH or PROGRAM_JAR] [ENV_VARIABLE_X=VALUE_X]*
```
&nbsp;&nbsp;命令参数与 [start.sh](../../misc/opt/deploy/scripts/start.sh) 一致。

#### 4. 查看启动应用程序状态：[status.sh](../../misc/opt/deploy/scripts/status.sh)

```shell
$ status.sh -h
  > Usage: status.sh [PROGRAM_PATH or PROGRAM_JAR] [ENV_VARIABLE_X=VALUE_X]*
```
&nbsp;&nbsp;命令参数与 [start.sh](../../misc/opt/deploy/scripts/start.sh) 一致。

## <span id="package">四、打包描述文件</span>

&nbsp;&nbsp;HP-SOA 提供以下 2 个通用打包描述文件：
- [assembly.xml](../../misc/opt/deploy/assembly/assembly.xml)
- [assembly-with-git.xml](../../misc/opt/deploy/assembly/assembly-with-git.xml)

&nbsp;&nbsp;[assembly.xml](../../misc/opt/deploy/assembly/assembly.xml) 与 [assembly-with-git.xml](../../misc/opt/deploy/assembly/assembly-with-git.xml) 的区别是，打包出来的目标文件名后者比前者多了 *'git commit-id'* 和 *'git 提交时间'* 标识，文件名格式为：`${project.artifactId}-${project.version}-${git.commit.id.abbrev}-${git.commit.time}.tar.gz`。如：'*hp-demo-infra-basic-service-1.1.2-8fdda412-20240703221616.tar.gz*'

&nbsp;&nbsp;*（注：如果要使用 [assembly-with-git.xml](../../misc/opt/deploy/assembly/assembly-with-git.xml)，应用程序项目需要引入 [git-commit-id-maven-plugin](https://github.com/git-commit-id/git-commit-id-maven-plugin) Maven 插件。）*

#### 1. 打包配置
&nbsp;&nbsp;应用程序项目 `pom.xml` 文件引入 `maven-assembly-plugin` Maven 插件，并指定打包描述文件路径，即可通过 `maven package` 命令进行打包，如：
```xml
<properties>
    <!-- 定义打包描述文件路径 -->
    <maven.assembly.descriptor>/opt/deploy/assembly/assembly-with-git.xml</maven.assembly.descriptor>
</properties>

<build>
    <plugins>
        <!-- 引入 `maven-assembly-plugin` Maven 插件 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-assembly-plugin</artifactId>
            <version>${maven-assembly-plugin.version}</version>
            <configuration>
            <descriptors>
                <!-- 指定打包描述文件路径 -->
                <descriptor>${maven.assembly.descriptor}</descriptor>
            </descriptors>
            </configuration>
            <executions>
                <execution>
                    <id>make-assembly</id>
                    <phase>package</phase>
                    <goals>
                        <goal>single</goal>
                    </goals>
                    <configuration>
                        <tarLongFileMode>gnu</tarLongFileMode>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    <plugins>
</build>
```

#### 2. 打包内容
&nbsp;&nbsp;[assembly.xml](../../misc/opt/deploy/assembly/assembly.xml) 或 [assembly-with-git.xml](../../misc/opt/deploy/assembly/assembly-with-git.xml) 会把以下文件打包在同一个目录，并压缩为 *.tar.gz* 文件。
- 应用程序 jar：`target/${project.artifactId}-${project.version}.jar`
- 应用程序 *bootstrap* 资源文件：`classes/bootstrap.yml` 或 `classes/bootstrap.yaml`
- git 信息文件：`classes/git.properties`*（由 [git-commit-id-maven-plugin](https://github.com/git-commit-id/git-commit-id-maven-plugin) Maven 插件创建）*
- 运行脚本文件：默认运行脚本目录为 `/opt/deploy/scripts`

&nbsp;&nbsp;*打包结果示例：*

```shell
# 查看打包结果
$ tar tvf hp-demo-infra-nacos-service-1.1.2-db56f3a7-20240715112346.tar.gz
drwxr-xr-x hp-demo-infra-nacos-service-1.1.2-db56f3a7-20240715112346/                                       # 包目录
-rw-r--r-- hp-demo-infra-nacos-service-1.1.2-db56f3a7-20240715112346/hp-demo-infra-nacos-service-1.1.2.jar  # 应用程序 jar
-rw-r--r-- hp-demo-infra-nacos-service-1.1.2-db56f3a7-20240715112346/bootstrap.yml                          # 应用程序 bootstrap 资源文件
-rw-r--r-- hp-demo-infra-nacos-service-1.1.2-db56f3a7-20240715112346/git.properties                         # git 信息文件
-rwxr-xr-x hp-demo-infra-nacos-service-1.1.2-db56f3a7-20240715112346/env.sh                                 # 运行脚本
-rwxr-xr-x hp-demo-infra-nacos-service-1.1.2-db56f3a7-20240715112346/start.sh                               # 运行脚本
-rwxr-xr-x hp-demo-infra-nacos-service-1.1.2-db56f3a7-20240715112346/start-foreground.sh                    # 运行脚本
-rwxr-xr-x hp-demo-infra-nacos-service-1.1.2-db56f3a7-20240715112346/stop.sh                                # 运行脚本
-rwxr-xr-x hp-demo-infra-nacos-service-1.1.2-db56f3a7-20240715112346/restart.sh                             # 运行脚本
-rwxr-xr-x hp-demo-infra-nacos-service-1.1.2-db56f3a7-20240715112346/status.sh                              # 运行脚本
-rwxr-xr-x hp-demo-infra-nacos-service-1.1.2-db56f3a7-20240715112346/yq                                     # 运行脚本（YAML 解析器）
```

## 五、Docker 发布

&nbsp;&nbsp;HP-SOA 提供了一套通用 Docker 发布脚本，可以为所有应用程序项目创建 Docker 镜像，运行 Docker 容器。主要包括：

- [Dockerfile](../../misc/docker/Dockerfile) ： Docker 镜像模版
- [docker-build.sh](../../misc/docker/docker-build.sh) ：Docker 镜像构建脚本
- [docker-run.sh](../../misc/docker/docker-run.sh) ：Docker 容器运行脚本

#### 1. 创建 Docker 镜像：[docker-build.sh](../../misc/docker/docker-build.sh)

```shell
$ docker-build.sh -h
 > Usage: docker-build.sh {$PROJECT_PATH} {$VERSION} [{$REPO_GROUP}]
```
&nbsp;&nbsp;*命令参数：*

- `PROJECT_PATH` ：应用程序项目路径。
- `VERSION` ：应用程序版本。
- `REPO_GROUP` ：镜像仓库名称，可选。如果 `REPO_GROUP` 不为空，生成的目标镜像标签会加上 `$REPO_GROUP/` 前缀。

&nbsp;&nbsp;*运行示例：*

- `docker-build.sh /my/app/app-any-service 1.2.3` ：创建的目标镜像为 *app-any-service:1.2.3*
- `docker-build.sh /my/app/app-any-service 1.2.3 my-repo` ：创建的目标镜像为 *my-repo/app-any-service:1.2.3*
- `docker-build.sh /my/app/app-any-service 1.2.3 my-registry.com/my-repo` ：创建的目标镜像为 *my-registry.com/my-repo/app-any-service:1.2.3*

&nbsp;&nbsp;*注意事项：*
1. 镜像构建脚本会把应用程序文件复制到镜像的 */opt/app/$PROJECT* 目录，因此创建镜像前需要先打包应用程序，生成应用程序目标文件。（参考：<a href="#package">上一节</a>）*
2. 镜像构建脚本会把 Docker 构建上下文的 *opt/hp-soa/config* 目录复制到镜像的 */opt/hp-soa/config* 目录，因此创建镜像前需要先准备好所有运行环境的配置文件。
3. 镜像构建脚本会把 Docker 构建上下文的 *skywalking-agent* 目录复制到镜像的 */opt/skywalking-agent* 目录，如果需要接入 Skywalking APM 监控，则需要把 Skywalking Java Agent 相关文件放入该目录。

#### 2. 运行 Docker 容器：[docker-run.sh](../../misc/docker/docker-run.sh)

```shell
$ docker-run.sh -h
 > Usage: docker-run.sh {$PROJECT} {$VERSION} {$RUNTIME_ENV} [$CONTAINER_NAME=<$PROJECT-$VERSION-$RUNTIME_ENV-$RANDOM>] [OTHER_DOCKER_RUN_OPTIONS]*
```
&nbsp;&nbsp;*命令参数：*

- `PROJECT` ：应用程序项目名称，可以是单独名称，也可以是加上仓库前缀的名称，如：*app-any-service* / *my-repo/app-any-service*。
- `VERSION` ：应用程序版本，即镜像版本。
- `RUNTIME_ENV` ：运行环境，需要与 *opt/hp-soa/config* 中创建的运行环境子目录匹配。如： *dev* / *test* / *prod*。
- `CONTAINER_NAME` ：创建的容器名称，可选。默认值为： `$PROJECT-$VERSION-$RUNTIME_ENV-{10位随机字符}`。
- `OTHER_DOCKER_RUN_OPTIONS` ：传递给 `docker run` 命令的额外命令行参数，可选，可以设置多个。

&nbsp;&nbsp;*运行示例：*

- `docker-run.sh app-any-service 1.2.3 dev` ：通过 *[\*/]app-any-service:1.2.3* 镜像创建 Docker 容器 `app-any-service-1.2.3-dev-{10位随机字符}`
- `docker-run.sh my-repo/app-any-service 1.2.3 test` ：通过 *[\*/]my-repo/app-any-service:1.2.3* 镜像创建 Docker 容器 `app-any-service-1.2.3-test-{10位随机字符}`
- `docker-run.sh app-any-service 1.2.3 prod my-container-name` ：通过 *[\*/]app-any-service:1.2.3* 镜像创建 Docker 容器 `my-container-name`
- `docker-run.sh my-repo/app-any-service 1.2.3 prod "" "-p 1001:1001 -p 1002:1002" "--restart=always" ` ：通过 *[\*/]my-repo/app-any-service:1.2.3* 镜像创建 Docker 容器 `app-any-service-1.2.3-prod-{10位随机字符}`，并传递 *-p 1001:1001 -p 1002:1002 --restart=always* 命令行参数给 `docker run` 命令

---

[[用户指南](user_guide.md)]
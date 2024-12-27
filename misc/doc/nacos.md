[[用户指南](user_guide.md)]

---

## 配置中心概述
&nbsp;&nbsp;应用程序可以把运行环境（开发/测试/生产）相关的配置迁移到配置中心，应用程序本地配置环境只需保留应用名称、服务端口等与运行环境无关的固定配置，实现配置与代码分离，做到一次打包多环境发布。此外，在微服务系统中，如 spring-boot 基础属性配置、数据库连接配置、消息总线配置和分布式缓存配置等通常可以由多个微服务应用共享，不必每个应用配置一份。配置中心还可以动态更新配置，使应用程序可以在不重启的情况下动态调整应用程序行为。  
&nbsp;&nbsp;配置中心的服务地址本身是与运行环境相关的，开发/测试/生产环境有各自的配置中心。一种做法是把所有环境的配置中心服务地址配置到应用程序内部配置文件，应用程序启动时通过profile来确定连接哪个配置中心服务地址；另一种做法是用外部配置文件来配置中心服务地址，应用程序启动时读取外部配置文件来确定配置中心服务地址。建议采用后一种做法，配置中心服务地址由外部统一维护，并可由多个应用程序共享，应用程序本身也不必关心配置中心服务地址。HP-SOA 项目中，配置中心服务地址应该配置到[扩展配置文件](app_integration.md#3-修改全局配置可选)中，该文件由 HP-SOA 服务器框架自动加载。

## Nacos 配置中心接入
&nbsp;&nbsp;HP-SOA 为整合Nacos配置中心提供了很好的支持，HP-SOA 项目可以非常简单地接入Nacos配置中心（参考[hp-demo-bff-nacos](../../hp-demo/hp-demo-bff-nacos)、[hp-demo-infra-nacos-service](../../hp-demo/hp-demo-infra-nacos/hp-demo-infra-nacos-service)）。

#### 1. 引入 HP-SOA Nacos 启动器
&nbsp;&nbsp;pom.xml 中添加[hp-soa-starter-nacos](../../hp-soa-starter/hp-soa-starter-nacos)依赖：
```xml
<dependencies>
    <!-- 引用 hp-soa-starter-web 及其它依赖包 -->
    <!-- 引用 hp-soa-starter-nacos -->
    <dependency>
        <groupId>io.github.hpsocket</groupId>
        <artifactId>hp-soa-starter-nacos</artifactId>
    </dependency>
</dependencies>
```
#### 2. 创建远程配置文件
&nbsp;&nbsp;把与运行环境相关的配置或共享配置分别配置到Nacos远程应用程序主配置文件和远程共享配置文件，共享配置最好放在独立配置组中。以 [hp-demo-bff-nacos](../../hp-demo/hp-demo-bff-nacos) 为例：

1.远程共享配置文件
- HP-SOA 配置文件 [hp-soa.yml](../../misc/nacos/config/namespace-DEV/GLOBAL_GROUP/hp-soa.yml)
- spring-boot 配置文件 [spring-boot.yml](../../misc/nacos/config/namespace-DEV/GLOBAL_GROUP/spring-boot.yml)
- Dubbo 配置文件 [dubbo.yml](../../misc/nacos/config/namespace-DEV/GLOBAL_GROUP/dubbo.yml)

2.远程应用程序主配置
- [hp-demo-bff-nacos.yml](../../misc/nacos/config/namespace-DEV/DEMO_GROUP/hp-demo-bff-nacos.yml)

#### 3. 创建应用程序本地配置文件
&nbsp;&nbsp;创建应用程序本地主配置文件`bootstrap.yml`，与运行环境相关的配置以及共享配置都迁移到Nacos，`bootstrap.yml`只保留了应用程序自身的固定配置。其中，（`${spring.cloud.nacos.config.name}.${spring.cloud.nacos.config.file-extension}`）设置远程主配置文件，（`${spring.cloud.nacos.config.shared-configs}`）设置远程共享配置文件列表。另外，`bootstrap.yml`并没有设置配置中心地址。
```yaml
# app
hp.soa.web:
  app:
    id: "0010100003"
    name: ${project.artifactId}
    version: ${project.version}
    organization: HP-Socket
    owner: Kingfisher
  component-scan:
    base-package: ${project.groupId}
  access-verification:
    enabled: true
    default-access-policy: maybe_login

spring.cloud.nacos.config:
  #server-addr: 192.168.56.23:8848
  #username: nacos
  #password: 123456
  #namespace: DEV
  file-extension: yml
  refresh-enabled: true

spring.config.import:
  - nacos:${hp.soa.web.app.name}.yml?group=DEMO_GROUP&refreshEnabled=true
  - nacos:hp-soa.yml?group=GLOBAL_GROUP&refreshEnabled=true
  - nacos:spring-boot.yml?group=GLOBAL_GROUP&refreshEnabled=true
  - nacos:dubbo.yml?group=GLOBAL_GROUP&refreshEnabled=true

# dubbo
dubbo.protocols:
  #dubbo:
  #  name: dubbo
  #  port: 5003
  tri:
    name: tri
    port: 6003

# server
server.port: 9003
```
#### 4. 设置配置中心地址
&nbsp;&nbsp;本示例使用[扩展配置文件](app_integration.md#3-修改全局配置可选)来设置配置中心地址，[扩展配置文件](app_integration.md#3-修改全局配置可选)由 spring-boot 自动加载，默认路径为 [/opt/hp-soa/config/extended-config.properties](../../misc/opt/hp-soa/config/extended-config.properties)
```yaml
## Nacos Config Center
spring.cloud.nacos.config.server-addr=192.168.56.23:8848
spring.cloud.nacos.config.username=nacos
spring.cloud.nacos.config.password=123456
spring.cloud.nacos.config.namespace=DEV
```

## 配置文件优先级
&nbsp;&nbsp;配置文件优先级可以细分为：本地配置文件优先级、远程配置文件优先级以及本地/远程配置文件之间的优先级。

1. 本地配置文件优先级顺序：
- [系统配置文件](app_integration.md#3-修改全局配置可选)（默认路径 [/opt/hp-soa/config/system-config.properties](../../misc/opt/hp-soa/config/system-config.properties)）
- [扩展配置文件](app_integration.md#3-修改全局配置可选)（默认路径 [/opt/hp-soa/config/extended-config.properties](../../misc/opt/hp-soa/config/extended-config.properties)）
- 应用主程序配置文件 `bootstrap.yml`
- 应用程序配置文件 `application.yml`

2. 远程配置文件优先级顺序：
- 远程主配置文件（`${spring.cloud.nacos.config.name}.${spring.cloud.nacos.config.file-extension}`）
- 共享配置文件（`${spring.cloud.nacos.config.shared-configs}`）

3. 本地/远程配置文件之间的优先级顺序

&nbsp;&nbsp;默认情况下，远程配置文件优先级高于本地配置文件。可以通过`spring.cloud.config`调整优先级（注：`spring.cloud.config`必须配置在远程配置中心中才会生效）
```yaml
## Spring 配置
spring:
  # Spring Cloud 配置
  cloud.config:
    # 配置覆盖规则：（开发、测试环境）允许本地配置覆盖远程配置
    allow-override: true              # 总开关，允许本地配置文件覆盖行为
    override-none: true               # 不要覆盖本地任何配置（直接将远程配置的优先级降低到最低），前提条件是allowOverride是true
    override-system-properties: true  # 覆盖本地JAVA属性值（java system property，也就是java -Dxxx传入的参数）
    # 配置覆盖规则：（生产环境）不允许本地配置覆盖远程配置
    #allow-override: false
    #override-system-properties: false
    #override-none: false
```

## 动态更新配置
&nbsp;&nbsp;要在应用程序运行期间动态更新配置，最简单的方法是用`@Value`注解注入Bean属性，并且在Bean类中声明`@RefreshScope`注解，另外，使用Nacos提供的注解和事件可以更细节地监控和处理动态配置更新过程（参考：[Nacos Spring](https://nacos.io/zh-cn/docs/nacos-spring.html)）。
```java
@Service
/* 声明此注解可动态更新通过 @Value 注入的属性 */
@RefreshScope
public class MyService
{
    /* 可动态更新的属性 */
    @Value("${xxx.yyy}")
    Integer val;

    // other codes ...
}
```

#### 1. 应用程序只读
&nbsp;&nbsp;HP-SOA 的“应用程序只读”配置是一个典型的动态更新配置例子，只读应用程序会暂停执行Job，暂停接收MQ消息，禁止数据库更新等可能导致数据变更的操作。
  - 当`hp.soa.web.app.read-only`配置为`true`时，应用程序变为只读并发布[ReadOnlyEvent(true)](../../hp-soa-framework/hp-soa-framework-web/src/main/java/io/github/hpsocket/soa/framework/web/event/ReadOnlyEvent.java)事件；
  - 当`hp.soa.web.app.read-only`配置为`false`时，应用程序恢复为可读写并发布[ReadOnlyEvent(false)](../../hp-soa-framework/hp-soa-framework-web/src/main/java/io/github/hpsocket/soa/framework/web/event/ReadOnlyEvent.java)事件。
  - 当删除`hp.soa.web.app.read-only`配置时，恢复默认值：`false`。

#### 2. 动态修改日志级别
&nbsp;&nbsp;HP-SOA 支持通过`hp.soa.web.app.dynamic-log-levels`动态修改日志级别。如：
```
hp.soa:
  web:
    app:
      dynamic-log-levels: '{"io.github.hpsocket.demo.mq.producer.controller": "debug", "ROOT": "info"}'
```

&nbsp;&nbsp; **注意事项：** 
  - `hp.soa.web.app.dynamic-log-levels`的配置值为JSON字符串格式（配置值必须写在单引号内）。
  - 日志名称（JSON Key）大小写敏感。
  - 日志级别（JSON Value）大小写不敏感，支持日志级别：`TRACE`、`DEBUG`、`INFO`、`WARN`、`ERROR`、`FATAL`、`OFF`。
  - 当删除`hp.soa.web.app.dynamic-log-levels`配置时，恢复日志配置文件的默认配置。

---

[[用户指南](user_guide.md)]
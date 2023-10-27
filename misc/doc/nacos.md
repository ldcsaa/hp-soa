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
&nbsp;&nbsp;把所有与运行环境相关的配置或共享配置都配置到Nacos远程配置文件，包含共享配置和应用程序主配置，共享配置最好放在独立配置组中。以 [hp-demo-bff-nacos](../../hp-demo/hp-demo-bff-nacos) 为例：

1.共享配置
- HP-SOA 配置文件 [hp-soa-web.yml](../../misc/nacos/config/namespace-DEV/GLOBAL_GROUP/hp-soa-web.yml)
- spring-boot 配置文件 [spring-boot.yml](../../misc/nacos/config/namespace-DEV/GLOBAL_GROUP/spring-boot.yml)
- Dubbo 配置文件 [dubbo.yml](../../misc/nacos/config/namespace-DEV/GLOBAL_GROUP/dubbo.yml)

2.应用程序主配置
- [hp-demo-bff-nacos.yml](../../misc/nacos/config/namespace-DEV/DEMO_GROUP/hp-demo-bff-nacos.yml)

#### 3. 创建应用程序本地配置文件
&nbsp;&nbsp;创建应用程序本地主配置文件`bootstrap.yml`，`bootstrap.yml`只保留了应用程序自身的固定配置，共享配置以及与运行环境相关的配置都迁移到Nacos。其中，`spring.cloud.nacos.config`配置项声明了需要引用的远程主配置文件（`${spring.cloud.nacos.config.name}.${spring.cloud.nacos.config.file-extension}`）和共享配置文件（`${spring.cloud.nacos.config.shared-configs}`）。另外，`bootstrap.yml`并没有设置配置中心地址。
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
  group: DEMO_GROUP
  name: ${hp.soa.web.app.name}
  file-extension: yml
  refresh-enabled: true
  shared-configs:
    - group: GLOBAL_GROUP
      data-id: hp-soa-web.yml
      refresh: true
    - group: GLOBAL_GROUP
      data-id: spring-boot.yml
      refresh: true
    - group: GLOBAL_GROUP
      data-id: dubbo.yml
      refresh: true

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
&nbsp;&nbsp;本示例使用[扩展配置文件](app_integration.md#3-修改全局配置可选)来设置配置中心地址，[扩展配置文件](app_integration.md#3-修改全局配置可选)默认路径为 [/opt/hp-soa/config/extended-config.properties](../../misc/opt/hp-soa/config/extended-config.properties)
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

&nbsp;&nbsp;默认情况下，远程配置文件优先级高于本地配置文件。可以通过`spring.cloud.config`调整优先级（注：`spring.cloud.config`必须在远程配置中心中配置才会生效）
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

---

[[用户指南](user_guide.md)]
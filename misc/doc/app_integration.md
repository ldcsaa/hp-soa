## 应用程序类型
&nbsp;&nbsp;微服务系统通常有以下几种应用程序：
- **Gateway/BFF应用** 接受外部HTTP请求，对请求鉴权，调用内部服务应用处理请求，封装返回结果。
- **微服务应用** 处理业务逻辑，发送业务事件。通常一个业务请求需要若干个微服务相互协作来处理。微服务按职责、层次可分为聚合服务、领域服务器，基础服务等不同类型服务。
- **Job应用** 执行批处理任务。为了避免影响线上业务系统，批处理任务通常在独立的Job应用中执行，而一些特别轻量的批处理任务可以内嵌到微服务应用中执行。
- **消息监听器应用** 监听和处理业务事件。业务事件和外部业务请求本质一致，因此，基于部署和运维等方面考虑，通常情况下不会部署独立的消息监听器应用，而是把消息监听器内嵌到微服务应用中。

&nbsp;&nbsp;因此，现实环境中主要有三种应用类型：**Gateway/BFF应用**，**微服务应用**，**Job应用**。其中**微服务应用**内部可能会内嵌*轻量级Job*或*消息监听器*模块。对于拆分粒度较大的微服务系统或正在进行拆分的微服务系统，可能会存在两种或三种应用类型组合的应用。

&nbsp;&nbsp;对于HP-SOA，这三种应用程序类型的接入方式是一致的，只是配置上有细微差别：
- **Gateway/BFF应用** 开启HTTP鉴权`hp.soa.web.access-verification.enabled = true`，并实现鉴权接口 [AccessVerificationService](../../hp-soa-framework/hp-soa-framework-web/src/main/java/io/github/hpsocket/soa/framework/web/service/AccessVerificationService.java)。
- **微服务应用** 配置Dubbo服务提供者的协议和端口。
- **Job应用** 引入 [hp-soa-starter-job-exclusive](../../hp-soa-starter/hp-soa-starter-job-exclusive) 轻量级Job启动器或 [hp-soa-starter-job-xxljob](../../hp-soa-starter/hp-soa-starter-job-xxljob) Xxl-Job 启动器，并配置 Job Handler 相关属性。

## 应用接入步骤
#### 1. 添加 HP-SOA 依赖
&nbsp;&nbsp;pom.xml 中添加 HP-SOA 依赖：
```xml
<dependencyManagement>
    <dependencies>
        <!-- 添加 hp-soa 依赖管理 -->
        <dependency>
            <groupId>io.github.hpsocket</groupId>
            <artifactId>hp-soa-dependencies</artifactId>
            <version>${hp-soa.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <!-- 引用 hp-soa-starter-web -->
    <dependency>
        <groupId>io.github.hpsocket</groupId>
        <artifactId>hp-soa-starter-web</artifactId>
    </dependency>
    <!-- 根据项目需要，引用其它 hp-soa starter -->
    <dependency>
        <groupId>io.github.hpsocket</groupId>
        <artifactId>hp-soa-starter-xxx</artifactId>
    </dependency>
</dependencies>
```
&nbsp;&nbsp;`hp-soa-dependencies`规范了 HP-SOA 及其依赖包的版本；`hp-soa-starter-web`是 HP-SOA 的Web应用启动器，所有项目都要引入; 其它启动器根据项目需要按需引入（如：需要统一配置中心则引入`hp-soa-starter-nacos`）。  
&nbsp;&nbsp;另外，HP-SOA parent POM 对 Maven Plugin、Resource 以及 spring-boot 启动类等项目进行了规范配置，如果条件允许建议让应用程序 POM 继承于 HP-SOA parent POM：
```xml
<parent>
    <groupId>io.github.hpsocket</groupId>
    <artifactId>hp-soa-parent</artifactId>
    <version>${hp-soa.version}</version>
</parent>
<groupId>my.group.id</groupId>
<artifactId>my-artifact-id</artifactId>
```

#### 2. 修改应用配置
&nbsp;&nbsp;HP-SOA应用程序主配置文件为`bootstrap.yml`，包含以下基本配置项（参考 Demo [hp-demo-bff-basic](hp-demo/hp-demo-bff-basic) 的[本地配置文件](hp-demo/hp-demo-bff-basic/src/main/resources/bootstrap.yml)），其中大部分配置都可以迁移到配置中心作为多个应用的共享配置（参考：配置中心的[远程配置文件](misc/nacos/config/namespace-DEV/GLOBAL_GROUP)）

- **hp.soa.web** 
```yaml
## 应用程序配置
hp.soa.web:
  # 应用程序信息
  app:
    id: "0010100001"
    name: ${project.artifactId}
    version: ${project.version}
    organization: HP-Socket
    owner: Kingfisher
  # Spring Bean 扫描包路径（多个包用','分隔）
  component-scan:
    base-package: ${project.groupId}
  # Cookie 生命周期（默认：10年）
  cookie: 
    max-age: 315360000
  # 跨域访问配置
  cors:
    mapping: "/**"
    allowed-origins: "*"
    allowed-headers: "*"
    allowed-methods: "*"
    exposed-headers: 
    allow-credentials: false
    max-age: 3600
  # HTTP请求鉴权
  access-verification:
    # 是否启用 HTTP 请求鉴权（默认：true，通常 Gateway/BFF 应用设置为 true，其它应用设置为 false）
    enabled: true
    # 默认鉴权策略（默认：MAYBE_LOGIN，可选值：NO_CHECK，NO_LOGIN，MAYBE_LOGIN，REQUIRE_LOGIN，REQUIRE_AUTHORIZED）
    default-access-policy: maybe_login
```
&nbsp;&nbsp;HTTP请求鉴权相关内容将在下文详述。

- **dubbo**
```yaml
## Dubbo 配置
dubbo:
  # Dubbo Service 和 Reference 扫描包路径（多个包用','分隔）
  scan.base-packages: ${hp.soa.web.component-scan.base-package}
  # Dubbo 服务协议和端口
  protocols:
    #dubbo:
    #  name: dubbo
    #  port: 5001
    tri:
      name: tri
      port: 6001
  # 注册中心缓存文件
  registry:
    file: "/opt/hp-soa/.cache/dubbo-registry_${hp.soa.web.app.name}.properties"
  # 应用程序配置
  application:
    qos-port: 7001
    qos-enable: false
    name: ${hp.soa.web.app.name}
    version: ${hp.soa.web.app.version}
    owner: ${hp.soa.web.app.owner:}
    organization: ${hp.soa.web.app.organization:}
```
&nbsp;&nbsp;Dubbo注册中心、配置中心和元数据中心在扩展配置文件中配置，将在下文详述。

- **server**
```yaml
## Spring-Boot 服务配置
server:
  # 服务端口
  port: 9001
  # Servlet 配置
  servlet:
    context-path: /
  # Tomcat 配置
  tomcat:
    max-connections: 10000
    connection-timeout: 30000
    threads:
      max: 200
      min-spare: 5
```

- **spring**
```yaml
## Spring 配置
spring:
  # Spring 应用程序名称
  application.name: ${hp.soa.web.app.name}
  # Spring MVC
  mvc:
    servlet:
      load-on-startup: 1
      path: /
    throw-exception-if-no-handler-found: true
  # Spring Web
  web:
    resources:
      add-mappings: false
  # Spring Security
  security:
    user:
      name: admin
      password: "123456"
      roles: ADMIN
  # Spring Servlet
  servlet:
    # 文件上传
    multipart:
      enabled: false
      max-file-size: 10MB
      max-request-size: 10MB
```

- **management**（可选）
```yaml
## Spring-Boot Management 配置
management:
  endpoint:
    health.show-details: when-authorized
    shutdown.enabled: false
  endpoints:
    enabled-by-default: true
    jmx.exposure.include: "*"
    web.base-path: /__admin
    web.exposure.include: "*"
  metrics:
    export.influx.enabled: false
    tags.application: ${hp.soa.web.app.name}
```

- **springdoc**（可选）
```yaml
## Spring-Doc 配置
springdoc:
  api-docs:
    # 是否开启（生成环境应设置为 false）
    enabled: true
    path: /v3/api-docs
  swagger-ui:
    # 是否开启（生成环境应设置为 false）
    enabled: true
    path: /swagger-ui.html
  packagesToScan: io.github.hpsocket.soa,${hp.soa.web.component-scan.base-package}
  api-infos:
    group-name: ${hp.soa.web.app.name}
    title: ${hp.soa.web.app.name}
    version: ${hp.soa.web.app.version}
    description: "Spring Boot Project >> ${hp.soa.web.app.name} (v${hp.soa.web.app.version})"
```

- **logging**（可选）
```yaml
## Log4J 配置文件（默认：log4j2.xml）
logging.config: classpath:log4j2-kafka.xml
```

#### 3. 修改全局配置（可选）


#### 4. 实现 HTTP 鉴权接口（Gateway/BFF应用）


#### 5. 启动应用


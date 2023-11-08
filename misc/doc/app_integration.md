[[用户指南](user_guide.md)]

---

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
&nbsp;&nbsp;HP-SOA应用程序主配置文件为`bootstrap.yml`，包含以下基本配置项（参考 Demo [hp-demo-bff-basic](../../hp-demo/hp-demo-bff-basic) 的[本地配置文件](../../hp-demo/hp-demo-bff-basic/src/main/resources/bootstrap.yml)），其中大部分配置都可以迁移到配置中心作为多个应用的共享配置（参考：配置中心的[远程配置文件](../../misc/nacos/config/namespace-DEV/GLOBAL_GROUP)）

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
  # HTTP 配置
  http:
    # Cookie 生命周期（默认：10年）
    cookie-max-age: 315360000
    # 日期时间类型默认序列化格式（默认："yyyy-MM-dd'T'HH:mm:ss.SSSXXX"）
    default-date-time-format: "yyyy-MM-dd HH:mm:ss.SSS"
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
    # 是否开启 HTTP 请求鉴权（默认：true，通常 Gateway/BFF 应用设置为 true，其它应用设置为 false）
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
&nbsp;&nbsp;在分布式应用系统中，多个应用共享配置中心地址、注册中心地址、日志收集器地址和功能特性开关等全局配置，这些配置与应用无关，只与部署环境有关。因此，最好把这些配置从应用程序配置中抽离出来作为共享配置，统一管理。对于部署在Docker中的应用，共享配置文件可以预置到Docker镜像，或者运行时Mount到Docker容器；对于部署在k8s中的应用，可以通过 Config Map 为容器生成共享配置文件。

&nbsp;&nbsp;HP-SOA 应用启动时，会查找并加载下列共享配置文件：
- 系统配置文件：启动 spring-boot 前由Java系统加载，用于配置那些需要在 spring-boot 启动前已配置好的系统属性（如：lo4j日志相关配置）。默认配置文件：`/opt/hp-soa/config/system-config.properties`，可通过JVM启动参数`-Dhp.soa.system.properties.file`修改系统配置文件路径。参考：[system-config.properties](../../misc/opt/hp-soa/config/system-config.properties)
- 扩展配置文件：由 spring-boot 加载，用于配置注册中心地址、配置中心地址和系统全局设置等公共应用属性，默认配置文件：`/opt/hp-soa/config/extended-config.properties`，，可通过JVM启动参数`-Dhp.soa.extended.properties.file`修改扩展配置文件路径。参考：[extended-config.properties](../../misc/opt/hp-soa/config/extended-config.properties)

#### 4. 实现 HTTP 鉴权接口（Gateway/BFF应用）
&nbsp;&nbsp;Gateway/BFF应用作为系统访问入口接收外部HTTP请求，通常需要对请求执行认证和授权。HP-SOA 通过以下3个步骤实现HTTP请求鉴权：

##### 1) 开启HTTP请求鉴权
```yaml
hp.soa.web:
  # HTTP请求鉴权
  access-verification:
    # 是否开启 HTTP 请求鉴权（默认：true，通常 Gateway/BFF 应用设置为 true，其它应用设置为 false）
    enabled: true
    # 默认鉴权策略（默认：MAYBE_LOGIN，可选值：NO_CHECK，NO_LOGIN，MAYBE_LOGIN，REQUIRE_LOGIN，REQUIRE_AUTHORIZED）
    default-access-policy: maybe_login
```
&nbsp;&nbsp;`hp.soa.web.access-verification.enabled`标识是否开启 HTTP 请求鉴权；`hp.soa.web.access-verification.default-access-policy`设置默认鉴权策略，当 spring-boot Controller 的类和方法中都没有声明[AccessVerification](../../hp-soa-framework/hp-soa-framework-web/src/main/java/io/github/hpsocket/soa/framework/web/annotation/AccessVerification.java)注解时，使用该默认鉴权策略。

&nbsp;&nbsp;鉴权策略由[AccessVerification.Type](../../hp-soa-framework/hp-soa-framework-web/src/main/java/io/github/hpsocket/soa/framework/web/annotation/AccessVerification.java)定义：  
- **NO_CHECK** 不作任何校验
- **NO_LOGIN** 不校验登录（只校验 appCode）
- **MAYBE_LOGIN** 不强制校验登录（只校验 appCode），如果已登录则加载用户信息
- **REQUIRE_LOGIN** 校验登录
- **REQUIRE_AUTHORIZED** 校验授权

##### 2) 实现HTTP请求鉴权接口[AccessVerificationService](../../hp-soa-framework/hp-soa-framework-web/src/main/java/io/github/hpsocket/soa/framework/web/service/AccessVerificationService.java)
```java
public interface AccessVerificationService
{
    /** 应用程序编号校验，成功：Pair(True, ?)，失败：Pair(False, ?) */
    Pair<Boolean, String> verifyAppCode(String appCode, String srcAppCode);
    /** 用户身份校验，成功：Pair(userId, ?)，失败：Pair(null, ?) */
    Pair<Long, String> verifyUser(String token, Long groupId);
    /** 用户授权校验，成功：Pair(True, ?)，失败：Pair(False, ?) */
    Pair<Boolean, String> verifyAuthorization(String route, String appCode, Long groupId, Long userId);
}
```
&nbsp;&nbsp;应用程序需要创建一个实现了[AccessVerificationService](../../hp-soa-framework/hp-soa-framework-web/src/main/java/io/github/hpsocket/soa/framework/web/service/AccessVerificationService.java)鉴权接口的 Spring Bean，由该  Spring Bean 实现请求鉴权。
&nbsp;&nbsp;spring-boot Controller 接口方法会根据不同的鉴权策略调用相应的鉴权接口方法：
- **NO_CHECK** （不调用任何鉴权接口方法）
- **NO_LOGIN** `verifyAppCode`
- **MAYBE_LOGIN** `verifyAppCode`、`verifyUser`
- **REQUIRE_LOGIN** `verifyAppCode`、`verifyUser`
- **REQUIRE_AUTHORIZED** `verifyAppCode`、`verifyUser`、`verifyAuthorization`

##### 3) 在 spring-boot Controller 中声明鉴权策略
```java
@RestController
/* Controller 默认鉴权策略：NO_LOGIN */
/* 注意：如果 Controller 没有声明 @AccessVerification 注解，默认鉴权策略为 ${hp.soa.web.access-verification.default-access-policy} 配置的策略 */
@AccessVerification(Type.MAYBE_LOGIN)
public class DemoController
{
    /* 方法没有声明 @AccessVerification 注解，使用 Controller 默认鉴权策略：NO_LOGIN */
    public Response<QueryResponse> queryUser(@RequestBody @Valid QueryReuqest request)
    {
        // ...
    }
    
    /* 使用方法注解 @AccessVerification 声明的鉴权策略：REQUIRE_LOGIN */
    @AccessVerification(Type.REQUIRE_LOGIN)
    public Response<SaveResponse> saveUser(@RequestBody @Valid SaveReuqest request)
    {
        // ...
    }
}
```
&nbsp;&nbsp;spring-boot Controller 可以在类或接口方法中通过[AccessVerification](../../hp-soa-framework/hp-soa-framework-web/src/main/java/io/github/hpsocket/soa/framework/web/annotation/AccessVerification.java)注解声明鉴权策略，策略优先级：**方法鉴权策略** &gt; **类鉴权策略** &gt; **配置文件默认鉴权策略**。

&nbsp;&nbsp;*注意：spring-boot Controller 接口方法的返回值类型为[Response](../../hp-soa-framework/hp-soa-framework-web/src/main/java/io/github/hpsocket/soa/framework/web/model/Response.java)鉴权策略才会生效。*

#### 5. 启动应用
&nbsp;&nbsp;HP-SOA 提供了默认应用程序启动器[io.github.hpsocket.soa.framework.web.server.main.AppStarter](../../hp-soa-framework/hp-soa-framework-web/src/main/java/io/github/hpsocket/soa/framework/web/server/main/AppStarter.java)
```java
/** <b>默认应用程序启动器</b> */
/* 排除部分自动配置 */
@SpringBootApplication(exclude = {  DataSourceAutoConfiguration.class,
                                    RedisAutoConfiguration.class,
                                    RedisReactiveAutoConfiguration.class,
                                    RedisRepositoriesAutoConfiguration.class,
                                    MongoAutoConfiguration.class,
                                    KafkaAutoConfiguration.class,
                                    RabbitAutoConfiguration.class
                                })
/* 开启 AspectJ AOP */
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
/* 扫描 Spring Bean */
@ComponentScan(basePackages = {"${hp.soa.web.component-scan.base-package:}"})
public class AppStarter
{
    static
    {
        /* 调用应用服务初始化器，加载系统属性配置文件 */
        ServerInitializer.initSystemProperties();
    }
    
    /** 应用程序入口 */
    public static void main(String[] args)
    {
        SpringApplication.run(AppStarter.class, args);
    }
}
```
&nbsp;&nbsp;[AppStarter](../../hp-soa-framework/hp-soa-framework-web/src/main/java/io/github/hpsocket/soa/framework/web/server/main/AppStarter.java)与 HP-SOA 服务框架紧密结合，为 HP-SOA 提供下列功能支持：
- **排除部分自动配置:** 避免这些自动配置与 HP-SOA 的自动配置（hp-soa-starter-xxx）冲突
- **开启 AspectJ AOP:** 支持 AspectJ AOP 增强
- **扫描 Spring Bean:** 支持通过`hp.soa.web.component-scan.base-package`配置指定 Spring Bean 扫描包路径
- **调用应用服务初始化器:** 加载系统属性配置文件[system-config.properties](../../misc/opt/hp-soa/config/system-config.properties)

&nbsp;&nbsp;如无特殊原因，建议应用程序都使用[AppStarter](../../hp-soa-framework/hp-soa-framework-web/src/main/java/io/github/hpsocket/soa/framework/web/server/main/AppStarter.java)作为应用程序启动器；如果应用程序必须使用自定义应用程序启动器，上述功能支持也必须保留。

&nbsp;&nbsp;另外，HP-SOA 的建议JVM启动参数如下，应用程序可以根据实际情况调整：
```shell
java \
-DskipTests=true \
-Dfile.encoding=UTF-8 \
-Djava.awt.headless=true \
-Dhp.soa.system.properties.file=/opt/hp-soa/config/system-config.properties \
-Dhp.soa.extended.properties.file=/opt/hp-soa/config/extended-config.properties \
-Ddubbo.resolve.file=/opt/hp-soa/config/dubbo-resolve.properties \
-Djava.security.egd=file:/dev/./urandom \
-Dnetworkaddress.cache.ttl=10 -Dnetworkaddress.cache.negative.ttl=1 \
-Xms256m -Xmx256m -Xss256k -XX:MaxDirectMemorySize=128m -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=256m -XX:ReservedCodeCacheSize=256m \
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
--add-opens java.base/jdk.internal.misc=ALL-UNNAMED \
--add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
--add-opens=java.base/sun.nio.cs=ALL-UNNAMED \
--add-opens=java.base/sun.security.action=ALL-UNNAMED \
--add-opens=java.base/sun.util.calendar=ALL-UNNAMED
```

#### 6. HTTP 接口契约
##### 1) HTTP 请求属性
&nbsp;&nbsp;HP-SOA 定义了一些HTTP请求属性用于请求鉴权或行为跟踪，这些属性通过HTTP请求头的方式发送到 Gateway/BFF 应用，其中某些还可以通过Cookie方式发送。  
&nbsp;&nbsp;HTTP请求属性可以用[RequestContext](../../hp-soa-framework/hp-soa-framework-web/src/main/java/io/github/hpsocket/soa/framework/web/advice/RequestContext.java)获取。
| 属 性            | 名 称 | HTTP Header | Cookie | 描 述 |
|----------------|-----|------|--------|-----|
| X-Token        | 登录 token | √ | √ | 应用程序可以通过登录 token 执行请求鉴权，加载用户信息 |
| X-App-Code     | 目标应用 appCode | √ | × | 应用程序可以通过目标应用 appCode 执行请求鉴权 |
| X-Src-App-Code | 调用方 appCode | √ | × | 应用程序可以通过调用方 appCode 执行请求鉴权 |
| X-Group-Id     | 组织 ID | √ | × | 应用程序可以通过组织 ID 执行请求鉴权 |
| X-Client-Id    | 客户端 ID | √ | √ | 标识一个客户端，通常由服务端系统自动生成，用于客户行为跟踪 |
| X-Session-Id   | 会话 ID | √ | √ | 标识一个会话，通常由调用方创建，用于业务会话跟踪 |
| X-Request-Id   | 请求 ID | √ | × | 标识一个请求，可以由调用方创建或由后端系统自动生成，用于调用链跟踪 |
| X-Version      | 版本号 | √ | × | 由调用方创建，应用程序自行解析 |
| X-Extra        | 附加信息 | √ | × | 由调用方创建，应用程序自行解析 |
| X-Request-Info | 组合属性 | √ | × | 组合属性 X-Request-Info 可以把上述属性组合到一个请求头，例如<br>**X-Request-Info:** X-App-Code=001;X-Group-Id=999;X-Version=2.3.4 |

##### 2) HTTP 响应对象
```java
/** <b>HTTP 请求统一响应对象</b> */
public class Response<T> implements Serializable
{
    public static final String MSG_OK       = "ok";
    /** 登录标识 */
    public static final Integer RT_LOGIN    = Integer.valueOf(1);
    /** 登出标识 */
    public static final Integer RT_LOGOUT   = Integer.valueOf(2);

    /** 状态码：应用程序可自定义状态码，系统状态码参考 {@linkplain ServiceException} */
    private Integer statusCode = OK;   
    /** 结果码：用于服务内部监控、统计，不暴露到调用方，应用程序可自定义结果码，系统结果码参考 {@linkplain ServiceException} */
    private transient Integer resultCode = OK;
    /** 状态描述 */
    private String msg = MSG_OK;
    /** 服务端处理耗时（毫秒） */
    private long costTime;
    /** 参数校验错误列表 */
    private Map<String, List<String>> validationErrors;
    /** 业务模型对象 */
    private T result;
    /** 响应类型（目前仅用于登录登出操作：{@linkplain #RT_LOGIN} - 登录，{@linkplain #RT_LOGOUT} - 登出） */
    private transient Integer respType;
}
```
&nbsp;&nbsp;[Response](../../hp-soa-framework/hp-soa-framework-web/src/main/java/io/github/hpsocket/soa/framework/web/model/Response.java)统一封装HTTP请求的返回值，其中`result`为业务模型对象。`respType`为响应类型，当`respType=RT_LOGIN`时，表示当前请求为登录请求并成功登录，HP-SOA 会自动创建一个登录token给调用方，登录token以Cookie方式返回，Cookie名称为`X-Token`，有效期为`${hp.soa.web.cookie.max-age}`配置值；当`respType=RT_LOGOUT`时，表示当前请求为登出请求并成功登出，HP-SOA 会自动删除调用方token。

&nbsp;&nbsp;*注意：spring-boot Controller 接口方法的返回值类型为[Response](../../hp-soa-framework/hp-soa-framework-web/src/main/java/io/github/hpsocket/soa/framework/web/model/Response.java)鉴权策略才会生效。*

---

[[用户指南](user_guide.md)]
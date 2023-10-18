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


#### 3. 修改全局配置（可选）


#### 4. 实现 HTTP 鉴权接口（Gateway/BFF应用）


#### 5. 启动应用


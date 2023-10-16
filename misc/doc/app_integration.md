### 应用程序类型
&nbsp;&nbsp;微服务系统通常有以下几种应用程序：
- **Gateway/BFF应用** 接受外部HTTP请求，对请求鉴权，调用内部服务应用处理请求，封装返回结果。
- **微服务应用** 处理业务逻辑，发送业务事件。通常一个业务请求需要若干个微服务相互协作来处理。微服务按职责、层次可分为聚合服务、领域服务器，基础服务等不同类型服务。
- **Job应用** 执行批处理任务。为了避免影响线上业务系统，批处理任务通常在独立的Job应用中执行，而一些特别轻量的批处理任务可以内嵌到微服务应用中执行。
- **消息监听器应用** 监听和处理业务事件。业务事件和外部业务请求本质一致，因此，基于部署和运维等方面考虑，通常情况下不会部署独立的消息监听器应用，而是把消息监听器内嵌到微服务应用中。

&nbsp;&nbsp;因此，现实环境中主要有三种应用类型：**Gateway/BFF应用**，**微服务应用**，**Job应用**。其中**微服务应用**内部可能会内嵌*轻量级Job*或*消息监听器*模块。对于拆分粒度较大的微服务系统或正在进行拆分的微服务系统，可能会存在两种或三种应用类型组合的应用。

&nbsp;&nbsp;对于HP-SOA，这三种应用程序类型的接入方式是一致的，只是配置上有细微差别：
- **Gateway/BFF应用** 开启HTTP鉴权`hp.soa.web.access-verification.enabled = true`，并实现鉴权接口[AccessVerificationService](../../hp-soa-framework/hp-soa-framework-web/src/main/java/io/github/hpsocket/soa/framework/web/service/AccessVerificationService.java)。
- **微服务应用** 配置Dubbo服务提供者的协议和端口。
- **Job应用** 引入[hp-soa-starter-job-exclusive](../../hp-soa-starter/hp-soa-starter-job-exclusive)轻量级Job启动器或[hp-soa-starter-job-xxljob](../../hp-soa-starter/hp-soa-starter-job-xxljob) Xxl-Job 启动器，并配置 Job Handler 相关属性。

### 应用接入步骤


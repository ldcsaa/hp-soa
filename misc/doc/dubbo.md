[[用户指南](user_guide.md)]

---

## 一、概述
&nbsp;&nbsp;Dubbo微服务应用开发中，服务提供者定义并实现服务的API接口，并把API接口提供给服务消费者。服务提供者拥有API接口的所有权，负责API接口的维护和升级。服务消费者引用API接口，并通过API接口调用服务提供者。在的项目开发中，服务提供者项目包含2个Module：
- API Module：定义API接口及相关BO（业务对象），以`jar`方式发布到Maven仓库供服务提供者和服务消费者引用。
- Service Module：实现API接口，以应用程序方式运行并发布到注册中心，提供给服务消费者调用。

## 二、服务提供者接入步骤

(以 [hp-demo-infra-basic](../../hp-demo/hp-demo-infra-basic) Demo 为例)

#### 1. 创建项目
&nbsp;&nbsp;创建父项目[hp-demo-infra-basic](../../hp-demo/hp-demo-infra-basic)及其API接口子项目[hp-demo-infra-basic-api](../../hp-demo/hp-demo-infra-basic/hp-demo-infra-basic-api)和服务提供者子项目[hp-demo-infra-basic-service](../../hp-demo/hp-demo-infra-basic/hp-demo-infra-basic-service)，子项目作为父项目的Sub Module：
```xml
<modules>
    <!-- API Module -->
    <module>hp-demo-infra-basic-api</module>
    <!-- Service Module -->
    <module>hp-demo-infra-basic-service</module>
</modules>
```

#### 2. API接口子项目：定义服务接口及相关BO
```java
/* API接口定义 */
public interface DemoService
{
    String sayHello(@NotBlank(message="姓名不能为空") String name);
}
```

#### 3. 服务提供者子项目：实现服务接口
1. pom.xml 中添加 [hp-soa-starter-web-dubbo](../../hp-soa-starter/hp-soa-starter-web-dubbo) 和API接口依赖：
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
    <!-- 引用 hp-soa-starter-web-dubbo-->
    <dependency>
        <groupId>io.github.hpsocket</groupId>
        <artifactId>hp-soa-starter-web-dubbo</artifactId>
    </dependency>
    <!-- 引用 API 接口 -->
    <dependency>
        <groupId>io.github.hpsocket.demo</groupId>
        <artifactId>hp-demo-infra-basic-api</artifactId>
        <version>${project.version}</version>
    </dependency>
</dependencies>
```

2. 修改应用配置

&nbsp;&nbsp;（参考：[应用集成](app_integration.md)）

3. 实现服务接口
```java
/* API接口实现 */
@Slf4j
// 声明为Dubbo服务
@DubboService
public class DemoServiceImpl implements DemoService
{
    @Override
    public String sayHello(String name)
    {
        if(name.length() < 4)
            throw new DemoException("Just test exception: name length < 4");
        
        return "Hello Mr. " + name; 
    }
}
```

## 三、服务消费者接入步骤

(以 [hp-demo-bff-basic](../../hp-demo/hp-demo-bff-basic) Demo 为例)

#### 1. 创建项目并引用API接口
&nbsp;&nbsp;pom.xml 中添加 [hp-soa-starter-web-dubbo](../../hp-soa-starter/hp-soa-starter-web-dubbo) 和API接口依赖：
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
    <!-- 引用 hp-soa-starter-web-dubbo -->
    <dependency>
        <groupId>io.github.hpsocket</groupId>
        <artifactId>hp-soa-starter-web-dubbo</artifactId>
    </dependency>
    <!-- 引用 API 接口 -->
    <dependency>
        <groupId>io.github.hpsocket.demo</groupId>
        <artifactId>hp-demo-infra-basic-api</artifactId>
        <version>${project.version}</version>
    </dependency>
</dependencies>
```

#### 2. 调用服务
```java
/* 定义Controller接口 */
@RequestMapping(value = "/demo", method = {RequestMethod.POST})
@Tag(name = "示例Demo接口")
public interface DemoController
{
    @PostMapping(value = "/queryUser")
    @Operation(summary = "查询用户", description = "通过姓名查询用户")
    Response<DemoResponse> queryUser(@RequestBody @Valid DemoReuqest request);
}

```
```java
/* 实现Controller接口 */
@Slf4j
@RestController
@AccessVerification(Type.NO_LOGIN)
public class DemoControllerImpl implements DemoController
{
    // 注入Dubbo服务引用
    @DubboReference
    DemoService demoService;

    @Override
    @AccessVerification(Type.REQUIRE_LOGIN)
    public Response<DemoResponse> queryUser(@RequestBody @Valid DemoReuqest request)
    {
        // 调用API接口
        String name = demoService.sayHello(request.getName());

        DemoResponse resp = new DemoResponse();
        resp.setId(1001L);
        resp.setName(name);
        resp.setAge(request.getAge());
        resp.setToken("41784a5039322bbe55a8bf8ce29b9280");

        Response<DemoResponse> response = new Response<>(resp);
        return response;
    }
}
```

---

[[用户指南](user_guide.md)]
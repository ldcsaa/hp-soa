[[用户指南](user_guide.md)]

---

## 一、概述
&nbsp;&nbsp;微服务应用开发声明周期中应用发布是一个重要环节，好的应用发布机制与流程能大大提高测试效率，加快投产速度，同时也能提升线上问题处理和修复效率。HP-SOA 为应用程序打包发布提供了以下支持：
- 应用配置与代码分离，一次打包可以部署到所有环境。
- 提供 [start.sh](../../misc/docker/bin/start.sh)、[stop.sh](../../misc/docker/bin/stop.sh)、[status.sh](../../misc/docker/bin/status.sh) 等通用运行脚本，并支持参数化执行。所有应用可以共用一套运行脚本，不必在不同应用中来回拷贝修改。
- 提供通用打包描述文件 [assembly.xml](../../misc/opt/deploy/assembly/assembly.xml)，规范发布包格式。
- Docker 发布支持：提供通用 [Dockerfile](../../misc/docker/Dockerfile)、镜像创建脚本 [docker-build.sh](../../misc/docker/docker-build.sh) 和容器启动脚本 [docker-run.sh](../../misc/docker/docker-run.sh)，所有应用可以共用一套 Docker 发布文件，不必在不同应用中来回拷贝修改。

## 二、应用配置与代码分离

&nbsp;&nbsp;HP-SOA 提倡应用配置与代码分离，应用配置与代码分离有包括以下几个层面：
1. 应用程序私有配置：与应用自身和运行环境有关，建议把配置项分离到 Nacos、Apollo 等应用配置管理系统。
2. 应用程序公共配置：与运行环境相关，多个应用的配置内容都相同配置项（如：spring-boot 除服务端口以外的配置项、数据库连接属性除连接地址以外的配置、Redis 中间件配置等），建议把配置项也分离到 Nacos、Apollo 等应用配置管理系统，由各应用按需加载。
3. 系统级公共配置：这类配置可能需要在连接应用配置管理系统之前，甚至在加载 spring-boot 前就需要加载（如：Nacos 配置管理系统服务地址、Log4j Context Selector、APM 探针 Agent 参数等），建议把这类配置项分离到运行机器的固定位置，所有应用程序启动时都读取这些配置。HP-SOA 提供的运行脚本默认会加载以下系统级配置文件：
  - 系统配置文件：启动 spring-boot 前由Java系统加载，用于配置那些需要在 spring-boot 启动前已配置好的系统属性（如：lo4j日志相关配置）。默认配置文件：`/opt/hp-soa/config/${RUNTIME_ENV}/system-config.properties`，如果`${RUNTIME_ENV}`为空或该默认配置文件所在目录为空则尝试加载`/opt/hp-soa/config/system-config.properties`，可通过JVM启动参数`-Dhp.soa.system.properties.file`修改系统配置文件路径。（参考：[system-config.properties](../../misc/opt/hp-soa/config/system-config.properties)）
  - 扩展配置文件：由 spring-boot 加载，用于配置注册中心地址、配置中心地址和系统全局设置等公共应用属性，默认配置文件：`/opt/hp-soa/config/${RUNTIME_ENV}/extended-config.properties`，如果`${RUNTIME_ENV}`为空或该默认配置文件所在目录为空则尝试加载`/opt/hp-soa/config/extended-config.properties`，可通过JVM启动参数`-Dhp.soa.extended.properties.file`修改扩展配置文件路径。（参考：[extended-config.properties](../../misc/opt/hp-soa/config/extended-config.properties)）
  - Java Agent 配置文件：运行脚本会解析 Java Agent 配置文件并转换为 JVM 命令行参数，从而注入 Java Agent，默认配置文件：`/opt/hp-soa/config/${RUNTIME_ENV}/java-agent.config`，如果`${RUNTIME_ENV}`为空或该默认配置文件所在目录为空则尝试加载`/opt/hp-soa/config/java-agent.config`，可通过JVM启动参数`-Dhp.soa.extended.properties.file`修改 Java Agent 配置文件路径。（参考：[java-agent.config](../../misc/opt/hp-soa/config/java-agent.config)）
  - Dubbo 接口直连配置文件（用于 Dubbo 项目调试）：默认配置文件：`/opt/hp-soa/config/${RUNTIME_ENV}/dubbo-resolve.properties`，如果`${RUNTIME_ENV}`为空或该默认配置文件所在目录为空则尝试加载`/opt/hp-soa/config/dubbo-resolve.properties`，可通过JVM启动参数`-Ddubbo.resolve.file`修改 Dubbo 接口直连配置文件路径。（参考：[dubbo-resolve.properties](../../misc/opt/hp-soa/config/dubbo-resolve.properties)）

&nbsp;&nbsp;除此以外，还有一些与应用和运行环境有关，但不能分离到配置管理系的统特殊配置，如：
- 应用程序启动等待时间（超过这个时间则认为启动失败）
- 应用程序在不同环境下的内存配置
- 应用程序的远程调试端口

&nbsp;&nbsp;HP-SOA 对上述这些配置进行了特殊处理：把上述配置项配置在应用程序的`bootstrap.yml`配置文件，由运行脚本解析这些配置，如：

```yml
## 特殊配置
hp.soa.special:
  # 应用程序启动等待秒数（默认：180 秒）
  startup.max-wait-seconds: 90
  # 远程调试端口（默认：server.port + 10000）
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

#### 1. 创建项目
&nbsp;&nbsp;创建父项目[hp-demo-infra-cloud](../../hp-demo/hp-demo-infra-cloud)及其API接口子项目[hp-demo-infra-cloud-api](../../hp-demo/hp-demo-infra-cloud/hp-demo-infra-cloud-api)和服务提供者子项目[hp-demo-infra-cloud-service](../../hp-demo/hp-demo-infra-cloud/hp-demo-infra-cloud-service)，子项目作为父项目的Sub Module：
```xml
<modules>
    <!-- API Module -->
    <module>hp-demo-infra-cloud-api</module>
    <!-- Service Module -->
    <module>hp-demo-infra-cloud-service</module>
</modules>
```

#### 2. API接口子项目：定义服务接口及相关BO
```java
/* API接口定义 */
public interface UserService
{
    @GetMapping(value = "/user/{id}")
    UserBo getUser(@PathVariable Long id);
    
    @PostMapping(value = "/user/save")
    boolean saveUser(@RequestBody @Valid UserBo userBo);
}
```

&nbsp;&nbsp;*注意：如果服务接口需要共享给服务消费的 Feign Client 使用，由于 Feign Client 的检查规则限制，不能在服务接口类中声明 `@XxxMapping` 注解，否则服务消费者不能启动。因此 `@XxxMapping` 注解只能声明在接口方法中。*

```java
/* BO定义 */
@Getter
@Setter
@SuppressWarnings("serial")
@Schema(description = "用户业务对象")
public class UserBo implements Serializable
{
    @Schema(description = "id", example = "123", requiredMode = RequiredMode.NOT_REQUIRED, nullable = true)
    private Long id;
    @NotBlank(message = "name is empty")
    @Schema(description = "姓名", example = "my name", requiredMode = RequiredMode.REQUIRED, minLength = 1, nullable = false)
    private String name;
    @NotNull
    @Min(1)
    @Max(100)
    @Schema(description = "年龄", example = "23", requiredMode = RequiredMode.REQUIRED, nullable = false)
    private Integer age;
}
```

#### 3. 服务提供者子项目：实现服务接口
1. pom.xml 中添加 [hp-soa-starter-web-cloud](../../hp-soa-starter/hp-soa-starter-web-cloud) 和API接口依赖：
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
    <!-- 引用 hp-soa-starter-web-cloud -->
    <dependency>
        <groupId>io.github.hpsocket</groupId>
        <artifactId>hp-soa-starter-web-cloud</artifactId>
    </dependency>
    <!-- 引用 API 接口 -->
    <dependency>
        <groupId>io.github.hpsocket.demo</groupId>
        <artifactId>hp-demo-infra-cloud-api</artifactId>
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
@Service
@RestController
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService
{
    @Autowired
    private UserConverter userConverter;
    
    @Autowired
    private DomainEventService<DemoEvent> demoEventService;
        
    @Override
    @DS("slave")
    public UserBo getUser(Long id)
    {
        User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getId, id));
        return userConverter.toBo(user);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public boolean saveUser(UserBo userBo)
    {
        User user = userConverter.fromBo(userBo);
        boolean isOK = saveOrUpdate(user);
        
        if(isOK)
        {
            raiseSaveUserEvent(user);
        }
        
        return isOK;
    }

    private void raiseSaveUserEvent(User user)
    {
        DemoEvent event = new DemoEvent(user.getId(), 0);
        
        event.setDomainName(AppConfig.DOMAIN_NAME);
        event.setEventName(AppConfig.SAVE_USER_EVENT_NAME);
        event.setExchange(AppConfig.USER_EXCHANGE);
        event.setRoutingKey(AppConfig.SAVE_USER_ROUTING_KEY);
        event.setMsg(JSONObject.toJSONString(user));
        
        demoEventService.save(event);
    }
}
```

## 三、服务消费者接入步骤

(以 [hp-demo-bff-cloud](../../hp-demo/hp-demo-bff-cloud) Demo 为例)

#### 1. 创建项目并引用API接口
&nbsp;&nbsp;pom.xml 中添加 [hp-soa-starter-web-cloud](../../hp-soa-starter/hp-soa-starter-web-cloud) 和API接口依赖：
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
    <!-- 引用 hp-soa-starter-web-cloud -->
    <dependency>
        <groupId>io.github.hpsocket</groupId>
        <artifactId>hp-soa-starter-web-cloud</artifactId>
    </dependency>
    <!-- 引用 API 接口 -->
    <dependency>
        <groupId>io.github.hpsocket.demo</groupId>
        <artifactId>hp-demo-infra-cloud-api</artifactId>
        <version>${project.version}</version>
    </dependency>
</dependencies>
```

#### 2. 定义 Feign Client 接口
```java
/* Feign Client接口定义 */
@FeignClient(UserServiceClient.SERVICE_NAME)
// 继承于API接口
public interface UserServiceClient extends UserService
{
    String SERVICE_NAME = "hp-demo-infra-cloud-service";
}
```

#### 3. 调用服务
```java
/* 定义Controller接口 */
@RequestMapping(value = "/demo", method = {RequestMethod.POST})
@Tag(name = "示例Demo接口")
public interface DemoController
{
    @PostMapping(value = "/queryUser")
    @Operation(summary = "查询用户", description = "通过 ID 查询用户")
    Response<QueryUserResponse> queryUser(@RequestBody @Valid QueryUserReuqest request);

    @PostMapping(value = "/saveUser")
    @Operation(summary = "保存用户", description = "新增或更新用户")
    Response<Boolean> saveUser(@RequestBody @Valid SaveUserReuqest request);
}

```
```java
/* 实现Controller接口 */
@RestController
@AccessVerification(Type.MAYBE_LOGIN)
public class DemoControllerImpl implements DemoController
{
    // 注入 Feign Client
    @Autowired
    UserServiceClient userServiceClient;
    
    @Autowired
    UserConverter userConverter;

    @Override
    public Response<QueryUserResponse> queryUser(@RequestBody @Valid QueryUserReuqest request)
    {
        // 调用API接口
        UserBo userBo = userServiceClient.getUser(request.getId());
        QueryUserResponse resp = userConverter.toQueryUserResponse(userBo);
        
        if(resp != null)
            resp.setToken(IdGenerator.nextCompactUUID());

        return new Response<>(resp);
    }

    @Override
    @AccessVerification(Type.REQUIRE_LOGIN)
    public Response<Boolean> saveUser(@RequestBody @Valid SaveUserReuqest request)
    {
        UserBo userBo = userConverter.fromSaveUserReuqest(request);
        // 调用API接口
        boolean rs = userServiceClient.saveUser(userBo);

        return new Response<>(rs);
    }
}
```

---

[[用户指南](user_guide.md)]
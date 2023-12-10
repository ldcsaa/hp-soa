[[用户指南](user_guide.md)]

---

## 一、概述
&nbsp;&nbsp;微服务应用开发中，一个较好的实践是：服务提供者定义并实现服务的API接口，并把API接口提供给服务消费者。服务提供者拥有API接口的所有权，负责API接口的维护和升。服务消费者只需引用API接口，不必重复定义。在的项目开发中，服务提供者项目包含2个Module：
- API Module：定义API接口及相关BO（业务对象），以`jar`方式发布到Maven仓库供服务提供者和服务消费者引用。
- Service Module：实现API接口，以应用程序方式运行并发布到注册中心，提供给服务消费者调用。

## 二、服务提供者接入步骤

(以 [hp-demo-infra-cloud](../../hp-demo/hp-demo-infra-cloud) Demo 为例)

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
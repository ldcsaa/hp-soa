[[用户指南](user_guide.md)]

---

## 一、概述
&nbsp;&nbsp;HP-SOA 的 [hp-soa-starter-data-mysql](../../hp-soa-starter/hp-soa-starter-data-mysql) 模块提供MySQL接入功能，整合了 [mybatis-plus](https://github.com/baomidou/mybatis-plus) 和 [dynamic-datasource](https://github.com/baomidou/dynamic-datasource)，简化了数据库访问操作并提供动态数据源、多数据源访问能力；同时也整合了[Druid](https://github.com/alibaba/druid)，可以通过[Druid](https://github.com/alibaba/druid)实施数据库监控；另外，[hp-soa-starter-data-mysql](../../hp-soa-starter/hp-soa-starter-data-mysql) 支持全局事务管理，只需在配置文件中开启全局事务管理功能即可（该功能默认关闭）。

## 二、接入步骤

(以 [hp-demo-infra-mysql-service](../../hp-demo/hp-demo-infra-mysql-service) Demo 为例)

#### 1. 引入 [hp-soa-starter-data-mysql](../../hp-soa-starter/hp-soa-starter-data-mysql) 依赖
- pom.xml 中添加 [hp-soa-starter-data-mysql](../../hp-soa-starter/hp-soa-starter-data-mysql) 依赖：
```xml
<dependencies>
    <!-- 引用 hp-soa-starter-data-mysql-->
    <dependency>
        <groupId>io.github.hpsocket</groupId>
        <artifactId>hp-soa-starter-data-mysql</artifactId>
    </dependency>
</dependencies>
```

#### 2. 修改MySQL配置

&nbsp;&nbsp;MySQL配置分为全局配置和应用配置两部分，多个项目共享的配置（如：mybatis-plus配置、数据库监控配置）抽取到全局配置文件中，各个项目的特定配置（如：数据源配置）则放到应用配置文件中。应用配置优先级高于全局配置。

- 全局配置（参考：[mysql.yml](../nacos/config/namespace-DEV/GLOBAL_GROUP/mysql.yml)）
- 应用配置（参考：[hp-demo-infra-mysql-service.yml](../nacos/config/namespace-DEV/DEMO_GROUP/hp-demo-infra-mysql-service.yml)）

#### 3. 代码开发

1. 指定 Mybatis Mapper 扫描包

&nbsp;&nbsp;[hp-soa-starter-data-mysql](../../hp-soa-starter/hp-soa-starter-data-mysql) 默认会把 `${hp.soa.data.mysql.mapper-scan.base-package}` 配置项的值作为 Mybatis Mapper 扫描包，如果不配置 `${hp.soa.data.mysql.mapper-scan.base-package}`，则可以在配置类中声明 `@MapperScan` 注解指定 Mybatis Mapper 扫描包。

```java
/* 指定 Mybatis Mapper 扫描包 */
@AutoConfiguration
/* default mybatis mapper scan package -> ${hp.soa.data.mysql.mapper-scan.base-package} */
@MapperScan("io.github.hpsocket.demo.infra.mysql.mapper")
public class AppConfig
{

}
```

2. 创建数据库实体类

&nbsp;&nbsp;[hp-soa-starter-data-mysql](../../hp-soa-starter/hp-soa-starter-data-mysql) 的 `io.github.hpsocket.soa.starter.data.mysql.entity` 包中提供了以下数据库实体基类，应用程序的数据库实体类可根据需要从其中之一继承：
- ***[BaseEntity](../../hp-soa-starter/hp-soa-starter-data-mysql/src/main/java/io/github/hpsocket/soa/starter/data/mysql/entity/BaseEntity.java)*** 数据库实体的基类，定义了 *id、createTime、updateTime* 字段
- ***[BaseLogicDeleteEntity](../../hp-soa-starter/hp-soa-starter-data-mysql/src/main/java/io/github/hpsocket/soa/starter/data/mysql/entity/BaseLogicDeleteEntity.java)*** 支持逻辑删除的数据库实体基类，定义了逻辑删除字段 *deleted*
- ***[BaseVersioningEntity](../../hp-soa-starter/hp-soa-starter-data-mysql/src/main/java/io/github/hpsocket/soa/starter/data/mysql/entity/BaseVersioningEntity.java)*** 支持乐观锁的数据库实体基类，定义了乐观锁字段 *version*
- ***[BaseLogicDeleteVersioningEntity](../../hp-soa-starter/hp-soa-starter-data-mysql/src/main/java/io/github/hpsocket/soa/starter/data/mysql/entity/BaseLogicDeleteVersioningEntity.java)*** 支持逻辑删除与乐观锁的数据库实体基类，定义了逻辑删除字段 *deleted*，乐观锁字段 *version*

```java
/* 数据库表 t_user 实体类 */
@Getter
@Setter
@SuppressWarnings("serial")
public class User extends BaseLogicDeleteVersioningEntity
{
    private String name;
    private Integer age;
}
```

3. 创建Mapper接口

&nbsp;&nbsp;Mapper一般继承于`BaseMapper<T>`，自动获得CRUD能力。

```java
/* User实体Mapper接口 */
public interface UserMapper extends BaseMapper<User>
{
}
```

4. 通过Mapper接口访问数据库

&nbsp;&nbsp;服务实现类中可以直接注入Mapper接口，或继承 `ServiceImpl<M extends BaseMapper<T>, T>` 间接注入Mapper接口访问数据库。

```java
/* 服务实现类 */
@Slf4j
@RefreshScope
@DubboService
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService
{
    @Autowired
    private UserConverter userConverter;
        
    @Override
    public UserBo getDefaultUser()
    {
        User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getId, 1L));
        return userConverter.toBo(user);
    }

    @Override
    @DS("master")
    public UserBo getMasterUser()
    {
        User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getId, 1L));
        return userConverter.toBo(user);
    }

    @Override
    @DS("slave")
    public UserBo getSlaveUser()
    {
        User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getId, 1L));
        return userConverter.toBo(user);
    }

    @Override
    @DS("slave_01")
    public UserBo getSlave1User()
    {
        User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getId, 1));
        return userConverter.toBo(user);
    }

    @Override
    @DS("slave_02")
    public UserBo getSlave2User()
    {
        User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getId, 1));
        return userConverter.toBo(user);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public boolean saveUser(UserBo userBo)
    {
        User user = userConverter.fromBo(userBo);
        
        if(user.getId() != null)
        {
            User user2 = getOne(Wrappers.<User>lambdaQuery().eq(User::getId, user.getId()));
            if(user2 != null)
            {
                user.setVersion(user2.getVersion());
                return updateById(user);
            }
        }
        
        return save(user);
    }
}
```

## 三、全局事务管理

&nbsp;&nbsp;全局事务管理配置类为 [SoaGlobalTransactionConfig](../../hp-soa-starter/hp-soa-starter-data-mysql/src/main/java/io/github/hpsocket/soa/starter/data/mysql/config/SoaGlobalTransactionConfig.java)，应用程序可以覆盖其中的Bean定义修改默认配置。

- 全局事务管理配置文件：

```yaml
## 全局事务管理配置
hp.soa.data.mysql.global-transaction-management:
  # 全局事务管理开启标识（默认：不开启）
  enabled: false
  # 普通事务超时（毫秒，默认：3000）
  timeout: 3000
  # 普通事务隔离级别（默认：ISOLATION_READ_COMMITTED）
  isolation: ISOLATION_READ_COMMITTED
  # 普通事务传播属性（默认：PROPAGATION_REQUIRED）
  propagation: PROPAGATION_REQUIRED
  # 普通事务回滚异常（默认：java.lang.Exception）
  rollback-for: java.lang.Exception
  # 只读事务超时（毫秒，默认：3000）
  read-only-timeout: 3000
  # 只读事务隔离级别（默认：ISOLATION_READ_COMMITTED）
  read-only-isolation: ISOLATION_READ_COMMITTED
  # 只读事务传播属性（默认：PROPAGATION_REQUIRED）
  read-only-propagation: PROPAGATION_REQUIRED
  # 注入全局事务管理的方法签名格式
  pointcut-expression: "execution(* ${hp.soa.web.component-scan.base-package}..service..*.*(..))"
```

## 四、动态数据源 / 多数据源

&nbsp;&nbsp;[hp-soa-starter-data-mysql](../../hp-soa-starter/hp-soa-starter-data-mysql) 通过 [dynamic-datasource](https://github.com/baomidou/dynamic-datasource) 实现动态数据源、多数据源访问功能，具体细节可参考[相关文档](https://www.kancloud.cn/tracy5546/dynamic-datasource/2264611)。

---

[[用户指南](user_guide.md)]
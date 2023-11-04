#### 一、概述
ES 项目开发分为以下2个步骤：
1. 环境准备：创建集合，相当于数据库项目的建库建表
2. 代码开发：通过 MongoDB API 管理和查询索引文档

本示例是“员工信息管理”示例，包含“员工信息”索引和“员工信息变更历史”索引，其中“员工信息”索引保存员工当前状态信息。
“员工信息变更历史”索引保存每次员工信息变更记录，其中 updateTime 字段添加 TTL 索引，设置为7天后自动删除。
    
#### 二、环境准备
1. 创建集合
    - 创建“员工信息集合”：  
        -> [1-employee_info.js](misc/collections/1-employee_info.js)
    - 创建“员工信息变更历史集合”：  
        -> [2-employee_history.js](misc/collections/2-employee_history.js)
        
#### 三、代码开发
1. pom.xml 文件引入 spring-boot-starter-data-mongodb 依赖
2. application.yml 配置 MongoDB 属性
3. 创建索引文档实体：document.EmployeeInfo（“员工信息”实体）、document.EmployeeHistory（“员工信息变更历史”实体）
4. 创建索引 Repository：repository.EmployeeInfoRepository（“员工信息” Repository）、repository.EmployeeHistoryRepository（“员工信息变更历史” Repository）
5. 定义 service.EmployeeService 接口，提供员工员工信息管理和查询方法
6. 创建 service.impl.EmployeeServiceImpl Bean，实现service.EmployeeService 接口，通过 注入 ElasticsearchRepository 和 ElasticsearchTemplate 操作 MongoDB 文档
7. 创建 controller.EmployeeController 接收 HTTP 请求
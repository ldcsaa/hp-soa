#### 一、概述
ES 项目开发分为以下2个步骤：
1. 环境准备：创建索引模版和固定索引，相当于数据库项目的建库建表
2. 代码开发：通过 ES API 管理和查询索引文档

本示例是“员工信息管理”示例，包含“员工信息”索引和“员工信息变更历史”索引，其中“员工信息”索引保存员工当前状态信息。  
“员工信息变更历史”索引保存每次员工信息变更记录，每天创建一个索引分片，索引分片通过生命周期策略设置为7天后自动删除。
    
#### 二、环境准备
1. 创建索引模版（把文件内容拷贝到 Kibana Management - Dev Tools 界面执行）
    - 创建“员工信息变更历史索引生命周期策略”：  
        -> misc/templates/1-employee_history_lifecycle_policy.json.put
    - 创建“员工信息变更历史索引模版”：  
        -> misc/templates/2-employee_history_template.json.put
    - 创建“员工信息索引模版”：  
        -> misc/templates/3-employee_info_template.json.put
    - 创建“员工信息索引及别名”：  
        -> misc/templates/4-create_employee_info_internal_index.json.put
2. 创建 data view
    - name：EMPLOYEE-INFO, Index pattern：employee_info
    - name：EMPLOYEE-HISTORY, Index pattern：employee_history_*
        
#### 三、代码开发
1. pom.xml 文件引入 spring-boot-starter-data-elasticsearch 依赖
2. application.yml 配置 ES 属性，如果开启了 SSL 还需要配置 SSL 属性
3. 创建索引文档实体：document.EmployeeInfo（“员工信息”实体）、document.EmployeeHistory（“员工信息变更历史”实体）
4. 创建索引 Repository：repository.EmployeeInfoRepository（“员工信息” Repository）、repository.EmployeeHistoryRepository（“员工信息变更历史” Repository）
5. 定义 service.EmployeeService 接口，提供员工员工信息管理和查询方法
6. 创建 service.impl.EmployeeServiceImpl Bean，实现service.EmployeeService 接口，通过 注入 ElasticsearchRepository 和 ElasticsearchTemplate 操作 ES 文档
7. 创建 controller.EmployeeController 接收 HTTP 请求
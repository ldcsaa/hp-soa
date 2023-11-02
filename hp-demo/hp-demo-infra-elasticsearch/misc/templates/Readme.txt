1、创建索引模版（把文件内容拷贝到 Kibana Management - Dev Tools 界面执行）
    1) 创建“员工信息变更历史索引生命周期策略”：
        -> 1-employee_history_lifecycle_policy.json.put
    2) 创建“员工信息变更历史索引模版”：
        -> 2-employee_history_template.json.put
    3) 创建“员工信息索引模版”：
        -> 3-employee_info_template.json.put
    4) 创建“员工信息索引及别名”：
        -> 4-create_employee_info_internal_index.json.put
    
2、创建 data view
    1) name：EMPLOYEE-INFO, Index pattern：employee_info
    2) name：EMPLOYEE-HISTORY, Index pattern：employee_history_*

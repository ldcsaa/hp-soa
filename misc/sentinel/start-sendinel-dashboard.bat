java ^
-Xms128m ^
-Xmx256m ^
-Dserver.port=8080 ^
-Dcsp.sentinel.dashboard.server=0.0.0.0:8080 ^
-Dproject.name=sentinel-dashboard ^
-Dauth.enabled=true ^
-Dsentinel.dashboard.auth.username=sentinel ^
-Dsentinel.dashboard.auth.password=123456 ^
-Dserver.servlet.session.timeout=86400 ^
-Dsentinel.nacos.serverAddr=192.168.56.23:8848 ^
-Dsentinel.nacos.username=nacos ^
-Dsentinel.nacos.password=123456 ^
-Dsentinel.nacos.namespace=DEV ^
-jar sentinel-dashboard.jar
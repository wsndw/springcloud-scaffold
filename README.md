<<<<<<< HEAD
# MQTT相关
包含mqtt模块的使用，单例工厂重连，客户端在线状态实时监听。
=======
>>>>>>> f391de2af73b56107fa130b795231177031a026c
****
# springcloud脚手架
1、服务注册发现：nacos  
2、网关：gateway  
3、api调用：swagger2  
4、链路追踪：skywalking（邮件告警暂无）  
5、公共模块：common  
6、数据库：暂无  
7、鉴权：暂无  

clone后修改nacos注册中心配置  
启动时，jvm参数添加  
//指定skywalking探针绝对地址  
-javaagent:D:\software\pmp\agent\skywalking-agent.jar  
//指定skywalking显示的服务名  
-Dskywalking.agent.service_name=springcloud-gateway  
//指定skywalking端口地址  
-Dskywalking.collector.backend_service=127.0.0.1:11800  
依次启动  
1、springcloud-basic-system-server  
2、springcloud-access-control-system-server  
3、springcloud-application  
4、springcloud-gateway  
<<<<<<< HEAD
=======

开发计划：  
1、skywalking链路追踪（已完成）  
2、整合邮件警告（已完成）  
3、zabbix监控（未开始）  
4、oauth2（已开始）  
5、待定
>>>>>>> f391de2af73b56107fa130b795231177031a026c

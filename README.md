# SecKill
基于JAVA开发了一个秒杀系统，针对页面、接口、安全等方面进行了优化
## 主要工作
- 基于Springboot进行秒杀相关功能的开发，如登录、商品详情、秒杀等，并采用Redis实现分布式session。
- 进行接口优化：使用Redis预减库存，采用内存标记减少Redis访问，用RabbitMQ实现异步下单。
- 进行页面优化：采用Redis对系统进行页面缓存、对象缓存等处理，并进行页面静态化
- 进行安全优化：实现隐藏秒杀接口地址功能，并引入图形验证码与接口限流机制
## 开发工具
IntelliJ IDEA + Navicat + CentOS7(VMware) + RedisDeskTop
## 压力测试工具
JMeter 
## 项目部署
- 拷贝项目到idea中打开，并更新依赖
- 拷贝目录中sql生成数据库
- 启动redis服务器和rabbitMQ服务器
- 运行项目中启动类
- 正常启动后访问localhost:8080/login/doLogin
- 输入用户名和密码点击登录(用户名密码在数据库中)
- 压测脚本可以用test01.jmx

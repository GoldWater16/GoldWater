# 			Apollo配置教程

### 一、Apollo

​      `apollo`一共三个包：`apollo-portal.zip`、`apollo-configservice.zip`、`apollo-adminservice.zip`
​      **说明：**

- `portal`：后台配置管理页面；
- `config`：提供配置的读取、推送等功能；
- `admin`：提供配置的修改、发布等功能



### 二、环境准备

#### 测试环境

1. 单机多环境部署：将`dev`、`letest`、`photondev`、`photontest`、`test`部署到一台机器；
2. 数据库部署：每套环境一个`apolloconfigdb`数据库，可共用一个`apolloportaldb`数据库

##### 设置每个环境端口号：

1. dev:                   `config-8080,admin-8090,portal-8087`
2. test:                   `config-8081,admin-8091,portal-8087`
3. photondev:      `config-8082,admin-8092,portal-8087`
4. photontest:      `config-8083,admin-8093,portal-8087`
5. letest:                `config-8084,admin-8094,portal-8087`

##### 连接数据库

1. dev—>`dev_apolloconfigdb`
2. test—>`test_apolloconfigdb`
3. photondev—>`photondev_apolloconfigdb`
4. photontest—>`photontest_apolloconfigdb`
5. letest—>`letest_apolloconfigdb`
6. portal只需要连接`apolloportaldb`

样例：

```
spring.datasource.url = jdbc:mysql://120.79.232.74:3306/dev_ApolloConfigDB?characterEncoding=utf8
spring.datasource.username = root
spring.datasource.password = root
```

##### 修改startup.sh脚本

1. dev:`LOG_DIR=/opt/logs/config-dev、LOG_DIR=/opt/logs/admin-dev`
2. test:`LOG_DIR=/opt/logs/config-test、LOG_DIR=/opt/logs/admin-test`
3. photondev:`LOG_DIR=/opt/logs/config-photondev、LOG_DIR=/opt/logs/admin-photondev`
4. photontest:`LOG_DIR=/opt/logs/config-photontest、LOG_DIR=/opt/logs/admin-photontest`
5. letest:`LOG_DIR=/opt/logs/config-letest、LOG_DIR=/opt/logs/admin-letest`

##### 添加eureka注册地址

在`admin`和`config`的`startup.sh`中添加最好添加在其他`export JAVA_OPTS`的下面:

```
export JAVA_OPTS="$JAVA_OPTS -Deureka.instance.ip-address=120.79.232.74"
```

##### startup.sh启动顺序

`config-dev`—>`config-test`—>`cofig-photondev`—>`photontest`—>`letest`—>`portal`

##### 查看所有端口是否已启动

`netstat -lnpt`

##### 连接Apollo

给`java`应用启动参数上添加：`-Dapollo.meta=http://120.79.232.74:8080 -Denv=dev`

=============================测试环境部署完结线========================================

#### 生产环境

1. 多机多环境部署：`keanonline`、`lejiaonline`、`leonline`、`online`、`photononline`、`sdbonline`每套环境独立一台机器，可集群部署；
2. 数据库部署：每套环境一个`apolloconfigdb`数据库，可共用一个`apolloportaldb`数据库

### 三、生产环境部署

##### 设置每个环境ip，如下：

1. keanonline:1.1.1.1，连接对应数据库：keanonline_apolloconfigdb
2. lejiaonline:2.2.2.2，连接对应数据库：lejiaonline_apolloconfigdb
3. leonline:3.3.3.3，连接对应数据库：leonline_apolloconfigdb
4. online:4.4.4.4，连接对应数据库：online_apolloconfigdb
5. photononline:5.5.5.5，连接对应数据库：photononline_apolloconfigdb
6. sdbonline:6.6.6.6，连接对应数据库：sdbonline_apolloconfigdb

```
1、以上6台服务器都部署上（admin、config）这两个包；
2、使用7.7.7.7服务器部署（portal）包；并且连接apolloportaldb数据库
```

##### 小结：

​	所有环境需要将admin和config部署到同一台服务器上，并且连接不同数据库；portal包只需要一台服务器；

##### 上传文件并解压

```
将apollo-configservice-1.0.0-github.zip、apollo-adminservice-1.0.0-github.zip、apollo-portal-1.0.0-github.zip上传到对应的服务器
```

##### 修改数据库连接

admin和config数据库配置修改：

```
修改文件路径：
	1、/tonder/app/apollo/config-dev/config/application-github.properties
	2、/tonder/app/apollo/admin-dev/config/application-github.properties
	
admin和config连接同一个{env}_aplloconfigDB数据库连接
spring.datasource.url = jdbc:mysql://120.79.232.74:3306/dev_ApolloConfigDB?characterEncoding=utf8
spring.datasource.username = root
spring.datasource.password = root
```

portal数据库配置修改：

````
修改文件路径：
	1、/tonder/app/apollo/config-dev/config/application-github.properties
portal比较简单，只需要修改一个：
spring.datasource.url = jdbc:mysql://120.79.232.74:3306/ApolloPortalDB?characterEncoding=utf8
spring.datasource.username = root
spring.datasource.password = root
````

##### 修改启动脚本

admin和config：

```
1、修改日志路径：LOG_DIR=/opt/logs/config-{env}、LOG_DIR=/opt/logs/admin-{env}
2、添加eureka指定ip地址注册：
export JAVA_OPTS="$JAVA_OPTS -Deureka.instance.ip-address=120.79.232.74"
```

portla：

```
修改日志路径：LOG_DIR=/opt/logs/portal
```

##### 执行数据库脚本：

```
config.sql和portal.sql
```

##### 修改数据库配置：

```mysql
注意⚠️：请修改对应库名和对应的ip
修改eureka注册地址：
update keanonline_ApolloConfigDB.ServerConfig set value='http://1.1.1.1:8080/eureka/' where `key`='eureka.service.url';
update lejiaonline_ApolloConfigDB.ServerConfig set value='http://2.2.2.2:8080/eureka/' where `key`='eureka.service.url';
update leonline_ApolloConfigDB.ServerConfig set value='http://3.3.3.3:8080/eureka/' where `key`='eureka.service.url';
update online_ApolloConfigDB.ServerConfig set value='http://4.4.4.4:8080/eureka/' where `key`='eureka.service.url';
update photononline_ApolloConfigDB.ServerConfig set value='http://5.5.5.5:8080/eureka/' where `key`='eureka.service.url';
update sdbonline_ApolloConfigDB.ServerConfig set value='http://6.6.6.6:8080/eureka/' where `key`='eureka.service.url';
修改支持的环境变量：
update ApolloPortalDB.ServerConfig set value='keanonline,lejiaonline,leonline,online,photononline,sdbonline' where `key`='apollo.portal.envs';

```

##### 启动脚本

启动所有config->启动所有admin->portal

##### 验证

1. 在浏览器上访问`http://120.79.232.74:8070`
2. 输入账号：apollo；密码：admin；
3. 点击`SampleApp`进入环境管理页面；
4. 进入管理页面后，右上角没有红色弹窗，表示没有异常，如有异常到`/opt/logs/portal/`看报错信息
5. 第四步，没有问题，就基本上apollo搭建成功了

![image](https://github.com/GoldWater16/GoldWater/blob/master/precipitation/images/apollo.png)

##### 生产应用接入方式

利用`jenkines`打包时添加两个java启动参数，`-Dapollo.meta=http://{env_config_ipAdress}:8080 -Denv=online`,可参照测试环境的配置方式

##### Apollo本地缓存

`Apollo`客户端会把从服务端获取到的配置在`java`应用系统缓存一份，用于在遇到服务不可用，或网络不通的时候，依然能从本地恢复配置，不影响应用正常运行。

本地缓存路径默认为：`/opt/data`，注意⚠️查看应用是否有读写权限

开启本地缓存开关：

```
update keanonline_ApolloConfigDB.ServerConfig set value='true' where `key`='config-service.cache.enabled';
update lejiaonline_ApolloConfigDB.ServerConfig set value='true' where `key`='config-service.cache.enabled';
update leonline_ApolloConfigDB.ServerConfig set value='true' where `key`='config-service.cache.enabled';
update online_ApolloConfigDB.ServerConfig set value='true' where `key`='config-service.cache.enabled';
update photononline_ApolloConfigDB.ServerConfig set value='true' where `key`='config-service.cache.enabled';
update sdbonline_ApolloConfigDB.ServerConfig set value='true' where `key`='config-service.cache.enabled';
```

=============================生产环境部署结束线======================================

### 四、java应用接入教程

#### 1.引入jar包

```
<dependency>
    <groupId>com.ctrip.framework.apollo</groupId>
    <artifactId>apollo-core</artifactId>
    <version>1.3.0</version>
</dependency>
<dependency>
    <groupId>com.ctrip.framework.apollo</groupId>
    <artifactId>apollo-client</artifactId>
    <version>1.3.0</version>
</dependency>
```

#### 2.在`springboot`启动类`Application.java`上添加注解

```
@Configuration
@EnableApolloConfig
```

以上是`springboot`配置方式，其他配置方式见：<https://github.com/ctripcorp/apollo/wiki/Java%E5%AE%A2%E6%88%B7%E7%AB%AF%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97>

#### 3.`META-INF`下创建`app.properties`文件

内容为：`app.id=tc-manage`，`app.id`是在`apollo`管理页面上创建的应用ID

#### 4.应用启动配置方式

方式一：

​	在`Run/Debug Configurations->Application->Environment->vm options`中添加`-Dapollo.meta=http://120.79.232.74:8080 -Denv=dev`

方式二：

​	在本地环境创建`/opt/settings/server.properties`，内容为：

```
env=dev
apollo.meta=http://120.79.232.74:8080
```

### 五、踩坑总结

##### 第一个坑

在搭建环境时，注意⚠️服务器是否开放制定环境的端口号

##### 第二个坑

`Apollo`不支持很多环境，目前只支持`LOCAL, DEV, FWS, FAT, UAT, LPT, PRO, TOOLS`

解决方案：

​	修改`Apollo`源码，添加环境变量后重新打包部署到服务器上，并将`jar`包上传到`nexus`，然后修改`java`程序`maven`依赖

​	涉及到的类有：`Env`,`EnvUtils#transformEnv`,`LegacyMetaServerProvider#initialize`

##### 第三个坑

`apollo.meta=http://119.23.240.225:6060`这个地址一定要跟`env=dev`对应上

##### 第四个坑

本地环境修改配置后，会自动重启服务器，热部署是罪魁祸首：

````
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-devtools</artifactId>
</dependency>
````

##### 第五个坑

单机多环境部署时，要注意内存是否足够，因为我在测试环境【内存配置：4GB】部署五个环境，启动了11个`java`应用，内存不足会将其他`java`应用`kill`掉，替换新的`java`应用.

查看内存使用情况：`free -h`

查看所有环境是否全部启动，可查看端口号：`netstat -nlpt`

##### 第六个坑

 在`java`应用中查看是否成功连接上`Apollo`，是否可实时更新配置：

方法一：

查看是否有你对应的`ip`实例

![image](https://github.com/GoldWater16/GoldWater/blob/master/precipitation/images/image-20190412173405177.png)

方法二：

如果看到这个日志，说明成功连接上了

```
c.c.f.a.i.DefaultMetaServerProvider:42 ## Located meta services from apollo.meta configuration: http://120.79.232.74:8080!
c.c.f.apollo.core.MetaDomainConsts:93 ## Located meta server address http://120.79.232.74:8080 for env DEV from com.ctrip.framework.apollo.internals.DefaultMetaServerProvider
```

如果看到这个日志说明在后台管理修改配置后，能够实时推送到`java`应用

```
[Apollo-Config-1] ## INFO  ## c.c.f.a.s.p.AutoUpdateConfigChangeListener:93 ## Auto update apollo changed value successfully, new value: 66666666, key: test, beanName: javaConfigBean, field: com.tongcaipay.manage.config.TestJavaConfigBean.name
```

#####  第七个坑

有时候在停`apollo`应用程序时，个人建议不要执行`shutdown.sh`，直接`kill -9 `，有时候你执行那个脚本后，会出现诡异事件，`eureka`连接不上的问题。


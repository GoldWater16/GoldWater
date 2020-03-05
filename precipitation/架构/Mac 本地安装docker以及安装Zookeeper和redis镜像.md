# Mac 本地安装docker以及安装Zookeeper和redis镜像

#### 安装方式

1、手动下载安装(**墙裂推荐**)

Stable版(每个季度更新stable版本)：https://download.docker.com/mac/stable/Docker.dmg

Edge版(每个月更新edge版本)：https://download.docker.com/mac/edge/Docker.dmg

2、使用Homebrew安装(很慢，不推荐，而且还会出错)

###### 命令：brew cask install docker

#### 验证是否安装成功

```java
HuGoldWater:/ lcp$ docker --version
Docker version 19.03.5, build 633a0ea
```

#### 安装Zookeeper镜像

###### 命令：docker search zookeeper

```verilog
HuGoldWater:/ lcp$ docker search zookeeper
NAME                               DESCRIPTION                                     STARS               OFFICIAL            AUTOMATED
zookeeper                          Apache ZooKeeper is an open-source server wh…   798                 [OK]
jplock/zookeeper                   Builds a docker image for Zookeeper version …   164                                     [OK]
wurstmeister/zookeeper                                                             106                                     [OK]
mesoscloud/zookeeper               ZooKeeper                                       73                                      [OK]
bitnami/zookeeper                  ZooKeeper is a centralized service for distr…   26                                      [OK]
mbabineau/zookeeper-exhibitor                                                      24                                      [OK]
digitalwonderland/zookeeper        Latest Zookeeper - clusterable                  20                                      [OK]
tobilg/zookeeper-webui             Docker image for using `zk-web` as ZooKeeper…   14                                      [OK]
confluent/zookeeper                                                                13                                      [OK]
debezium/zookeeper                 Zookeeper image required when running the De…   10                                      [OK]
31z4/zookeeper                     Dockerized Apache Zookeeper.                    6                                       [OK]
thefactory/zookeeper-exhibitor     Exhibitor-managed ZooKeeper with S3 backups …   6                                       [OK]
engapa/zookeeper                   Zookeeper image optimised for being used int…   2
emccorp/zookeeper                  Zookeeper                                       2
openshift/zookeeper-346-fedora20   ZooKeeper 3.4.6 with replication support        1
paulbrown/zookeeper                Zookeeper on Kubernetes (PetSet)                1                                       [OK]
strimzi/zookeeper                                                                  1
duffqiu/zookeeper-cli                                                              1                                       [OK]
josdotso/zookeeper-exporter        ref: https://github.com/carlpett/zookeeper_e…   1                                       [OK]
perrykim/zookeeper                 k8s - zookeeper  ( forked k8s contrib )         1                                       [OK]
dabealu/zookeeper-exporter         zookeeper exporter for prometheus               0                                       [OK]
midonet/zookeeper                  Dockerfile for a Zookeeper server.              0                                       [OK]
humio/zookeeper-dev                zookeeper build with zulu jvm.                  0
phenompeople/zookeeper             Apache ZooKeeper is an open-source server wh…   0                                       [OK]
avvo/zookeeper                     Apache Zookeeper                                0                                       [OK]
```

#### 创建本地目录

```shell
执行以下命令：
sudo mkdir /opt/docker/zookeeper/data
sudo mkdir /data
sudo chmod -R 777 /opt/docker/zookeeper/data
sudo chmod -R 777 /data
```

#### 镜像下载

###### 命令：docker pull zookeeper

#### 创建并启动容器

###### 命令：sudo docker run -d -p 2181:2181 -v /opt/docker/zookeeper/data/:/data/ --name=zookeeper --privileged=true zookeeper

#### 查看Zookeeper进程是否存在

###### 命令：docker ps

```shell
HuGoldWater:/ lcp$ docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                                                  NAMES
ad32f95f6882        zookeeper           "/docker-entrypoint.…"   27 minutes ago      Up 27 minutes       2888/tcp, 3888/tcp, 0.0.0.0:2181->2181/tcp, 8080/tcp   zookeeper
```

#### 启动项目连接Zookeeper

```
2020-02-26 19:41:44,417 ## 22168 ## [main] ## INFO  ## org.apache.zookeeper.ZooKeeper:438 ## Initiating client connection, connectString=localhost:2181 sessionTimeout=30000 watcher=org.I0Itec.zkclient.ZkClient@160a8986
2020-02-26 19:41:44,419 ## 22170 ## [main-SendThread(localhost:2181)] ## INFO  ## org.apache.zookeeper.ClientCnxn:975 ## Opening socket connection to server localhost/127.0.0.1:2181. Will not attempt to authenticate using SASL (unknown error)
2020-02-26 19:41:44,420 ## 22171 ## [main-SendThread(localhost:2181)] ## INFO  ## org.apache.zookeeper.ClientCnxn:852 ## Socket connection established to localhost/127.0.0.1:2181, initiating session
2020-02-26 19:41:44,430 ## 22181 ## [main-SendThread(localhost:2181)] ## INFO  ## org.apache.zookeeper.ClientCnxn:1235 ## Session establishment complete on server localhost/127.0.0.1:2181, sessionid = 0x100001032e00002, negotiated timeout = 30000
```

#### 查看服务是否注册到zookeeper节点上

###### 命令：docker run -it --rm --link zookeeper:zookeeper zookeeper zkCli.sh -server zookeeper

```shell
[zk: zookeeper(CONNECTED) 1] ls /dubbo/com.
com.photon.account.core.interfaces.facade.AccountFacade               com.photon.account.core.interfaces.facade.WithdrawFacade              com.photon.clearing.core.interfaces.facade.SettlementFacade
```

#### 遇到过的问题

##### 问题一：

```
chown: changing ownership of '/data/': Operation not permitted
```

##### 解决方案：

```shell
sudo chmod -R 777 /opt/docker/zookeeper/data
sudo chmod -R 777 /data
```

#### 安装Redis镜像

###### 命令：docker search redis

```shell
HuGoldWater:/ lcp$ docker search redis
NAME                             DESCRIPTION                                     STARS               OFFICIAL            AUTOMATED
redis                            Redis is an open source key-value store that…   7843                [OK]
bitnami/redis                    Bitnami Redis Docker Image                      136                                     [OK]
sameersbn/redis                                                                  79                                      [OK]
grokzen/redis-cluster            Redis cluster 3.0, 3.2, 4.0 & 5.0               63
rediscommander/redis-commander   Alpine image for redis-commander - Redis man…   35                                      [OK]
kubeguide/redis-master           redis-master with "Hello World!"                31
redislabs/redis                  Clustered in-memory database engine compatib…   24
redislabs/redisearch             Redis With the RedisSearch module pre-loaded…   20
arm32v7/redis                    Redis is an open source key-value store that…   20
oliver006/redis_exporter          Prometheus Exporter for Redis Metrics. Supp…   18
webhippie/redis                  Docker images for Redis                         10                                      [OK]
s7anley/redis-sentinel-docker    Redis Sentinel                                  9                                       [OK]
redislabs/redisgraph             A graph database module for Redis               9                                       [OK]
bitnami/redis-sentinel           Bitnami Docker Image for Redis Sentinel         9                                       [OK]
insready/redis-stat              Docker image for the real-time Redis monitor…   9                                       [OK]
arm64v8/redis                    Redis is an open source key-value store that…   8
redislabs/redismod               An automated build of redismod - latest Redi…   7                                       [OK]
centos/redis-32-centos7          Redis in-memory data structure store, used a…   4
circleci/redis                   CircleCI images for Redis                       3                                       [OK]
frodenas/redis                   A Docker Image for Redis                        2                                       [OK]
runnable/redis-stunnel           stunnel to redis provided by linking contain…   1                                       [OK]
wodby/redis                      Redis container image with orchestration        1                                       [OK]
tiredofit/redis                  Redis Server w/ Zabbix monitoring and S6 Ove…   1                                       [OK]
xetamus/redis-resource           forked redis-resource                           0                                       [OK]
cflondonservices/redis           Docker image for running redis                  0
```

#### 拉取最新版的Redis镜像

###### 命令：docker pull redis:latest

#### 查看本地镜像

###### 命令：docker images

```shell
HuGoldWater:/ lcp$ docker images
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
zookeeper           latest              2e30cac00aca        3 weeks ago         224MB
redis               latest              44d36d2c2374        3 weeks ago         98.2MB
```

#### 运行redis容器

###### 命令：docker run -itd --name redis-HuGoldWater -p 6379:6379 redis

#### 查看redis进程

```shell
HuGoldWater:/ lcp$ docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                                                  NAMES
ad32f95f6882        zookeeper           "/docker-entrypoint.…"   40 minutes ago      Up 40 minutes       2888/tcp, 3888/tcp, 0.0.0.0:2181->2181/tcp, 8080/tcp   zookeeper
368bfbc94cf9        redis               "docker-entrypoint.s…"   52 minutes ago      Up 52 minutes       0.0.0.0:6379->6379/tcp                                 redis-HuGoldWater
```

#### 停止redis进程

```shell
HuGoldWater:~ lcp$ docker rm 368bfbc94cf9
368bfbc94cf9
```

#### 停止zookeeper进程

```shell
HuGoldWater:~ lcp$ docker rm ad32f95f6882
ad32f95f6882
```


# 目录：

[数据可靠性](#title1)

[性能对比](#title2)

[单机支持的队列数](#title3)

[消息投递实时性](#title4)

[消费失败重试](#title5)

[严格的消息顺序](#title6)

[定时消息](#title7)

[分布式事务消息](#title8)

[消息查询](#title9)

[消息回溯](#title10)

[消费并行度](#title11)

[消息轨迹](#title12)

[开发语言友好性](#title13)

[Broker端消息过滤](#title14)

[消息堆积能力](#title15)

[开源社区活跃度](#title16)

[商业支持](#title17)

[成熟度](#title18)

[ActiveMQ、RabbitMQ、RocketMQ、kafka的区别](#title19)



## RocketMQ与Kafka对比（18项差异）

### <a id="title1">数据可靠性</a>

- RocketMQ支持异步实时刷盘，同步刷盘，同步Replication，异步Replication
- Kafka使用异步刷盘方式，异步Replication/同步Replication

> 总结：RocketMQ的同步刷盘在单机可靠性上比Kafka更高，不会因为操作系统Crash，导致数据丢失。 Kafka同步Replication理论上性能低于RocketMQ的同步Replication，原因是Kafka的数据已分区为单位组织，意味着一 个Kafka实例上会有几百个数据分区，RocketMQ一个实例上只有一个数据分区，RocketMQ可以充分利用IO Group Commit机制，批量传输数据，配置同步Replication与异步Replication相比，性能损耗月20%~30%，Kafka没有亲自测试 过，但是个人认为理论上会与低于RocketMQ。

### <a id="title2">性能对比</a>

- Kafka单机写入TPS约在百万条/秒，消息大小10个字节
- RocketMQ单机写入TPS单实例约7万条/秒，单机部署3个Broker，可以跑到最高12万条/秒，消息大小10个字节

> 总结：Kafka的TPS跑到单机百万，主要是由于Producer端将多个小消息合并，批量发向Broker。

*RocketMQ为什么没有这么做？*

1. Producer通常使用Java语言，缓存过多消息，GC是个很严重的问题
2. Producer调用发送消息接口，消息未发送到Broker，向业务返回成功，此时Producer宕机，会导致消息丢失，业务出错
3. Producer通常为分布式系统，且每台机器都是多线程发送，我们认为线上的系统单个Producer每秒产生的数据量有限，不可能上万。
4. 缓存的功能完全可以由上层业务完成。

### <a id="title3">单机支持的队列数</a>

- Kafka单机超过64个队列/分区，Load会发生明显的飙高现象，队列越多，load越高，发送消息响应时间变长。[Kafka分区数无法过多的问题](http://blog.confluent.io/2015/03/12/how-to-choose-the-number-of-topicspartitions-in-a-kafka-cluster/)
- RocketMQ单机支持最高5万个队列，Load不会发生明显变化

*队列多有什么好处？*

1. 单机可以创建更多Topic，因为每个Topic都是由一批队列组成
2. Consumer的集群规模和队列数成正比，队列越多，Consumer集群可以越大

### <a id="title4">消息投递实时性</a>

- Kafka使用短轮询方式，实时性取决于轮询间隔时间，0.8以后版本支持长轮询。
- RocketMQ使用长轮询，同Push方式实时性一致，消息的投递延时通常在几个毫秒。

### <a id="title5">消费失败重试</a>

- Kafka消费失败不支持重试。
- RocketMQ消费失败支持定时重试，每次重试间隔时间顺延

> 总结：例如充值类应用，当前时刻调用运营商网关，充值失败，可能是对方压力过多，稍后再调用就会成功，如支付宝到银行扣款也是类似需求。
>
> 这里的重试需要可靠的重试，即失败重试的消息不因为Consumer宕机导致丢失。

### <a id="title6">严格的消息顺序</a>

- Kafka支持消息顺序，但是一台Broker宕机后，就会产生消息乱序
- RocketMQ支持严格的消息顺序，在顺序消息场景下，一台Broker宕机后，发送消息会失败，但是不会乱序

> Mysql Binlog分发需要严格的消息顺序

### <a id="title7">定时消息</a>

- Kafka不支持定时消息

- RocketMQ支持两类定时消息

- - 开源版本RocketMQ仅支持定时Level，定时Level用户可定制
  - 阿里云ONS支持定时Level，以及指定的毫秒级别的延时时间

### <a id="title8">分布式事务消息</a>

- Kafka不支持分布式事务消息
- 阿里云ONS支持分布式定时消息，未来开源版本的RocketMQ也有计划支持分布式事务消息

### <a id="title9">消息查询</a>

- Kafka不支持消息查询
- RocketMQ支持根据Message Id查询消息，也支持根据消息内容查询消息（发送消息时指定一个Message Key，任意字符串，例如指定为订单Id）

> 总结：消息查询对于定位消息丢失问题非常有帮助，例如某个订单处理失败，是消息没收到还是收到处理出错了。

### <a id="title10">消息回溯</a>

- Kafka理论上可以按照Offset来回溯消息
- RocketMQ支持按照时间来回溯消息，精度毫秒，例如从一天之前的某时某分某秒开始重新消费消息

> 总结：典型业务场景如consumer做订单分析，但是由于程序逻辑或者依赖的系统发生故障等原因，导致今天消费的消息全部无效，需要重新从昨天零点开始消费，那么以时间为起点的消息重放功能对于业务非常有帮助。

### <a id="title11">消费并行度</a>

- Kafka的消费并行度依赖Topic配置的分区数，如分区数为10，那么最多10台机器来并行消费（每台机器只能开启一个线程），或者一台机器消费（10个线程并行消费）。即消费并行度和分区数一致。

- RocketMQ消费并行度分两种情况

- - 顺序消费方式并行度同Kafka完全一致
  - 乱序方式并行度取决于Consumer的线程数，如Topic配置10个队列，10台机器消费，每台机器100个线程，那么并行度为1000。

### <a id="title12">消息轨迹</a>

- Kafka不支持消息轨迹
- 阿里云ONS支持消息轨迹

### <a id="title13">开发语言友好性</a>

- Kafka采用Scala编写
- RocketMQ采用Java语言编写

### <a id="title14">Broker端消息过滤</a>

- Kafka不支持Broker端的消息过滤

- RocketMQ支持两种Broker端消息过滤方式

- - 根据Message Tag来过滤，相当于子topic概念
  - 向服务器上传一段Java代码，可以对消息做任意形式的过滤，甚至可以做Message Body的过滤拆分。

### <a id="title15">消息堆积能力</a>

理论上Kafka要比RocketMQ的堆积能力更强，不过RocketMQ单机也可以支持亿级的消息堆积能力，我们认为这个堆积能力已经完全可以满足业务需求。

### <a id="title16">开源社区活跃度</a>

- Kafka社区更新较慢
- RocketMQ的github社区有250个个人、公司用户登记了联系方式，QQ群超过1000人。

### <a id="title17">商业支持</a>

- Kafka原开发团队成立新公司，目前暂没有相关产品看到
- RocketMQ在阿里云上已经开放公测近半年，目前以云服务形式免费供大家商用，并向用户承诺99.99%的可靠性，同时彻底解决了用户自己搭建MQ产品的运维复杂性问题

### <a id="title18">成熟度</a>

- Kafka在日志领域比较成熟
- RocketMQ在阿里集团内部有大量的应用在使用，每天都产生海量的消息，并且顺利支持了多次天猫双十一海量消息考验，是数据削峰填谷的利器。

### <a id="title19">ActiveMQ、RabbitMQ、RocketMQ、kafka的区别</a>

[![img](https://oscimg.oschina.net/itags/f8acc0ba02b1b5491e9d1eac9ccfc006a61cb439.png)](https://www.cnblogs.com/chanshuyi/p/message_queue_serial_02_how_to_select.html)

本文转载自：https://my.oschina.net/yjwxh/blog/412093
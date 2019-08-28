# TCP面试题之四次挥手过程

### TCP四次挥手过程：

　　1、第一次挥手：Client发送一个FIN，用来关闭Client到Server的数据传送，Client进入FIN_WAIT_1状态；

　　2、第二次挥手：Server收到FIN后，发送一个ACK给Client，确认序号为收到序号+1（与SYN相同，一个FIN占用一个序号），Server进入CLOSE_WAIT状态；

　　3、第三次挥手：Server发送一个FIN，用来关闭Server到Client的数据传送，Server进入LAST_ACK状态；

　　4、第四次挥手：Client收到FIN后，Client进入TIME_WAIT状态，接着发送一个ACK给Server，确认序号为收到的序号+1，Server进入CLOSED状态，完成四次挥手；

 

![img](https://img2018.cnblogs.com/blog/943267/201903/943267-20190304205138339-1351790135.png)

### 为什么会有TIME_WAIT状态：

　　1、确保有足够的时间让对方收到ACK包，一来一去就是2MSL；

　　2、避免新旧连接混，即不会跟后面的新连接混淆；

### 服务器出现大量CLOSE_WAIT状态的原因：

原因：没有及时关闭连接

解决方案：

　　1、检查代码，特别是释放资源的代码；

　　2、检查配置，特别是处理请求的线程配置；
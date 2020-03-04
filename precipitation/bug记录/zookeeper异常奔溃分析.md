# zookeeper异常奔溃分析

##### 网络情况：

![网络图](/Users/lcp/Desktop/HuGoldWater/GoldWater/precipitation/images/zookeeper异常奔溃分析/网络图.png)

#####  内存情况：

![内存使用情况](/Users/lcp/Desktop/HuGoldWater/GoldWater/precipitation/images/zookeeper异常奔溃分析/内存使用情况.png)

##### 磁盘使用情况：

![磁盘使用率](/Users/lcp/Desktop/HuGoldWater/GoldWater/precipitation/images/zookeeper异常奔溃分析/磁盘使用率.png)

##### cpu使用情况：

![cpu使用情况](/Users/lcp/Desktop/HuGoldWater/GoldWater/precipitation/images/zookeeper异常奔溃分析/cpu使用情况.png)

故障分析思路(**故障时间`Wed Feb 26 21:28:24 2020`,`zookeeper`版本号3.4.11**)：

1、查看网络、内存、磁盘、`cpu`等情况；

2、查询是否有人执行了`kill`命令(排查人为操作)；

3、查看`jvm`启动参数，找到`ErrorFile`对应的错误文件以及`dump`日志文件；

##### 主要是通过以上方式排查问题：

&emsp;&emsp;首先，我从网络、内存、磁盘、`cpu`这些方面入手，一一排查，都没有发现什么异常情况；

&emsp;&emsp;其次，排查是否有人使用`kill`命令把进程给干掉，使用命令:`grep -ri kill /var/log 2>/dev/null`发现没人`kill`进程，所以排除掉这种情况；

&emsp;&emsp;再次，查看`jvm`启动参数有没有配置`ErrorFile`、`HeapDumpOnOutOfMemoryError`和`HeapDumpPath`这些参数，如果有就到对应的文件上看异常原因，我使用`ps -ef|grep java` 发现没有这些参数，我表示有点绝望；

&emsp;&emsp;最后，经过查阅资料，`ErrorFil`e是`JVM Crash`掉后生成的文件，如果有配置就会将日志写到指定文件上，如果没有配置，则会放在启动脚本的同一目录下（我的错误文件在这个`zookeeper/bin`目录下），找到这个就好办，定位问题就比较清晰(日志在下面)。

##### 分析日志文件(hs_err_pid18738.log)：

```log
#
# A fatal error has been detected by the Java Runtime Environment:
#
#
[error occurred during error reporting (printing exception/signal name), id 0x7]

, pid=18738, tid=0x00007f64251b8700
#
# JRE version: Java(TM) SE Runtime Environment (8.0_211-b12) (build 1.8.0_211-b12)
# Java VM: Java HotSpot(TM) 64-Bit Server VM (25.211-b12 mixed mode linux-amd64 compressed oops)
# Problematic frame:
#
[error occurred during error reporting (printing problematic frame), id 0x7]

# Failed to write core dump. Core dumps have been disabled. To enable core dumping, try "ulimit -c unlimited" before starting Java again
#
# If you would like to submit a bug report, please visit:
#   http://bugreport.java.com/bugreport/crash.jsp
#
```

&emsp;&emsp;上面意思就是说，`Java`在运行时触发到`JVM`的`bug`，导致`JVM Crash`；

&emsp;&emsp;`Zookeeper`的`event`分析,从下面的日志分析`Zookeeper`在写日志文件出现一个坑(Uncommon trap)：

```java
Event: 5437561.513 Thread 0x00007f644428f000 Uncommon trap: reason=unstable_if action=reinterpret pc=0x00007f64351db03c method=org.apache.zookeeper.server.NIOServerCnxn.isZKServerRunning()Z @ 4
Event: 5437562.703 Thread 0x00007f644428f000 Uncommon trap: reason=unstable_if action=reinterpret pc=0x00007f64351da698 method=org.apache.zookeeper.server.NIOServerCnxn.isZKServerRunning()Z @ 4
Event: 5437570.073 Thread 0x00007f63d4014000 Uncommon trap: reason=unstable_if action=reinterpret pc=0x00007f64356e7d50 method=org.apache.zookeeper.server.persistence.FileTxnLog.commit()V @ 83
```

&emsp;&emsp;综上所述，`zookeeper`在写日志(代码不规范)时触发了`JVM`的一个`bug`，导致`Zookeeper`奔溃，在`zookeeper 3.5.5`解决了470个`bug`，建议升级`zookeeper`版本，同时`jdk14`也解决了这个`bug`，你也不可能将`jdk`升级到`jdk14`吧，如果在`java`应用中遇到这种问题，建议检查磁盘和内存情况以及代码中`jdk`是否使用不当；

> zookeeper 3.5.5 bug修复记录：https://zookeeper.apache.org/doc/r3.5.5/releasenotes.html
>
> Oracle官网解释：https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8191278

完整日志内容：

```java
#
# A fatal error has been detected by the Java Runtime Environment:
#
#  
[error occurred during error reporting (printing exception/signal name), id 0x7]

, pid=18738, tid=0x00007f64251b8700
#
# JRE version: Java(TM) SE Runtime Environment (8.0_211-b12) (build 1.8.0_211-b12)
# Java VM: Java HotSpot(TM) 64-Bit Server VM (25.211-b12 mixed mode linux-amd64 compressed oops)
# Problematic frame:
# 
[error occurred during error reporting (printing problematic frame), id 0x7]

# Failed to write core dump. Core dumps have been disabled. To enable core dumping, try "ulimit -c unlimited" before starting Java again
#
# If you would like to submit a bug report, please visit:
#   http://bugreport.java.com/bugreport/crash.jsp
#

---------------  T H R E A D  ---------------

Current thread is native thread

siginfo: 
[error occurred during error reporting (printing siginfo), id 0x7]

Registers:
RAX=0x00007f644c4ef5c0, RBX=0x00007f644c4ef628, RCX=0x0000000000000000, RDX=0x000000000000000d
RSP=0x00007f64251b7260, RBP=0x0000000000000002, RSI=0x00007f644cb43c83, RDI=0x00007f644cb3c000
R8 =0x000000000000002e, R9 =0x0000000000f2f0a0, R10=0x00007f644cb3c840, R11=0x00007f64251b73a0
R12=0x0000000000000008, R13=0x000000003cbc282e, R14=0x0000000001a9ba50, R15=0x0000000000000000
RIP=0x00007f644c92c6f1, EFLAGS=0x0000000000010246, CSGSFS=0x0000000000000033, ERR=0x0000000000000004
  TRAPNO=0x000000000000000e

Top of Stack: (sp=0x00007f64251b7260)
0x00007f64251b7260:   00007f644be290b0 00007f64251b72a0
0x00007f64251b7270:   0000000000f2f0a0 00007f64251b73a0
0x00007f64251b7280:   00007f64251b73b0 00007f640000002e
0x00007f64251b7290:   0000000000000000 0000000000000070
0x00007f64251b72a0:   0000000000000000 00007f644cb3b3f0
0x00007f64251b72b0:   00007f644cb3c000 00007f644c2eb78c
0x00007f64251b72c0:   00007f644c4efef8 00007f644c2eb440
0x00007f64251b72d0:   0000000500000000 0000000100000000
0x00007f64251b72e0:   0000000085cefa78 00007f64251b7468
0x00007f64251b72f0:   00007f64251b7440 0000000000000001
0x00007f64251b7300:   00007f644cb3b3f0 00007f644cb3c840
0x00007f64251b7310:   00007f644cb3c4e8 00007f644c92cfcf
0x00007f64251b7320:   0000000000000000 00007f644cb3b3f0
0x00007f64251b7330:   0000000000000005 0000000000000000
0x00007f64251b7340:   00007f6400000001 00007f644cb3c4e8
0x00007f64251b7350:   00007f64251b79c0 00007f644bf8dea5
0x00007f64251b7360:   0000000085cefa60 00007f64251b73b0
0x00007f64251b7370:   0000000000000000 00007f644cb3c840
0x00007f64251b7380:   00007f64251b73b0 00007f64251b73a0
0x00007f64251b7390:   000000003cbc282e 00007f644c2eb78c
0x00007f64251b73a0:   00000000ffffffff 00007f644baa6ff8
0x00007f64251b73b0:   0000000000000000 0000000000000000
0x00007f64251b73c0:   0000000085cefc48 00000000d7bbbdd8
0x00007f64251b73d0:   0000000085e19940 000000000000000f
0x00007f64251b73e0:   0000000000000031 000000000000000d
0x00007f64251b73f0:   00007f644bf6b14d 0000000085cefa40
0x00007f64251b7400:   0000000085cefc48 00000000d7bbc0b8
0x00007f64251b7410:   00007f64251b7530 00007f644c4ee048
0x00007f64251b7420:   0000000000000000 00007f644c91e308
0x00007f64251b7430:   0000000000000004 0000000000000020
0x00007f64251b7440:   00007f644c91e308 00007f644c931d1e
0x00007f64251b7450:   00007f6400000005 0000000000000000 

Instructions: (pc=0x00007f644c92c6f1)
0x00007f644c92c6d1:   40 08 48 89 44 24 60 48 8b 87 f8 02 00 00 48 85
0x00007f644c92c6e1:   c0 0f 84 24 06 00 00 44 89 c9 23 8f f0 02 00 00
0x00007f644c92c6f1:   48 8b 34 c8 8b 8f f4 02 00 00 4c 89 e8 48 d3 e8
0x00007f644c92c701:   48 89 c1 48 89 f0 48 d3 e8 44 89 c1 48 d3 ee 48 

Register to memory mapping:

RAX=
[error occurred during error reporting (printing register info), id 0x7]

Stack: [0x00007f64250b8000,0x00007f64251b9000],  sp=0x00007f64251b7260,  free space=1020k
Native frames: (J=compiled Java code, j=interpreted, Vv=VM code, C=native code)

[error occurred during error reporting (printing native stack), id 0x7]


---------------  P R O C E S S  ---------------

VM state:not at safepoint (normal execution)

VM Mutex/Monitor currently owned by a thread: None

heap address: 0x0000000085c00000, size: 1956 MB, Compressed Oops mode: 32-bit
Narrow klass base: 0x0000000000000000, Narrow klass shift: 3
Compressed class space size: 1073741824 Address: 0x0000000100000000

Heap:
 PSYoungGen      total 20992K, used 12052K [0x00000000d7400000, 0x00000000d8980000, 0x0000000100000000)
  eden space 20480K, 58% used [0x00000000d7400000,0x00000000d7f9d3c0,0x00000000d8800000)
  from space 512K, 31% used [0x00000000d8900000,0x00000000d8928000,0x00000000d8980000)
  to   space 512K, 0% used [0x00000000d8880000,0x00000000d8880000,0x00000000d8900000)
 ParOldGen       total 25600K, used 20400K [0x0000000085c00000, 0x0000000087500000, 0x00000000d7400000)
  object space 25600K, 79% used [0x0000000085c00000,0x0000000086fec398,0x0000000087500000)
 Metaspace       used 11117K, capacity 11266K, committed 11648K, reserved 1058816K
  class space    used 1223K, capacity 1281K, committed 1408K, reserved 1048576K

Card table byte_map: [0x00007f6449a2b000,0x00007f6449dfe000] byte_map_base: 0x00007f64495fd000

Marking Bits: (ParMarkBitMap*) 0x00007f644bf00d80
 Begin Bits: [0x00007f642c2e0000, 0x00007f642e170000)
 End Bits:   [0x00007f642e170000, 0x00007f6430000000)

Polling page: 0x00007f644cb42000

CodeCache: size=245760Kb used=7196Kb max_used=7228Kb free=238563Kb
 bounds [0x00007f6435000000, 0x00007f6435730000, 0x00007f6444000000]
 total_blobs=2354 nmethods=1985 adapters=283
 compilation: enabled

Compilation events (10 events):
Event: 5437570.689 Thread 0x00007f64440bd000 2721   !   4       java.io.PrintStream::write (69 bytes)
Event: 5437570.691 Thread 0x00007f64440bd000 nmethod 2721 0x00007f64355f77d0 code [0x00007f64355f7960, 0x00007f64355f7dc8]
Event: 5437571.017 Thread 0x00007f64440bf000 2722   !   3       org.apache.zookeeper.server.NIOServerCnxn::doIO (676 bytes)
Event: 5437571.023 Thread 0x00007f64440bf000 nmethod 2722 0x00007f64356797d0 code [0x00007f643567a140, 0x00007f64356814c8]
Event: 5437572.733 Thread 0x00007f64440ba000 2723       4       java.util.concurrent.LinkedBlockingQueue::<init> (7 bytes)
Event: 5437572.737 Thread 0x00007f64440ba000 nmethod 2723 0x00007f64355fadd0 code [0x00007f64355faf60, 0x00007f64355fb478]
Event: 5437572.995 Thread 0x00007f64440bf000 2724       3       org.apache.zookeeper.server.NIOServerCnxnFactory::createConnection (15 bytes)
Event: 5437572.995 Thread 0x00007f64440bf000 nmethod 2724 0x00007f64352dbad0 code [0x00007f64352dbc40, 0x00007f64352dbe48]
Event: 5437573.003 Thread 0x00007f64440bf000 2725       3       org.apache.zookeeper.server.NIOServerCnxn::<init> (156 bytes)
Event: 5437573.004 Thread 0x00007f64440bf000 nmethod 2725 0x00007f6435604710 code [0x00007f6435604a00, 0x00007f6435605f88]

GC Heap History (10 events):
Event: 5433750.241 GC heap before
{Heap before GC invocations=5308 (full 24):
 PSYoungGen      total 20992K, used 20640K [0x00000000d7400000, 0x00000000d8980000, 0x0000000100000000)
  eden space 20480K, 100% used [0x00000000d7400000,0x00000000d8800000,0x00000000d8800000)
  from space 512K, 31% used [0x00000000d8880000,0x00000000d88a8000,0x00000000d8900000)
  to   space 512K, 0% used [0x00000000d8900000,0x00000000d8900000,0x00000000d8980000)
 ParOldGen       total 25600K, used 19912K [0x0000000085c00000, 0x0000000087500000, 0x00000000d7400000)
  object space 25600K, 77% used [0x0000000085c00000,0x0000000086f72398,0x0000000087500000)
 Metaspace       used 11110K, capacity 11266K, committed 11648K, reserved 1058816K
  class space    used 1223K, capacity 1281K, committed 1408K, reserved 1048576K
Event: 5433750.242 GC heap after
Heap after GC invocations=5308 (full 24):
 PSYoungGen      total 20992K, used 160K [0x00000000d7400000, 0x00000000d8980000, 0x0000000100000000)
  eden space 20480K, 0% used [0x00000000d7400000,0x00000000d7400000,0x00000000d8800000)
  from space 512K, 31% used [0x00000000d8900000,0x00000000d8928000,0x00000000d8980000)
  to   space 512K, 0% used [0x00000000d8880000,0x00000000d8880000,0x00000000d8900000)
 ParOldGen       total 25600K, used 20016K [0x0000000085c00000, 0x0000000087500000, 0x00000000d7400000)
  object space 25600K, 78% used [0x0000000085c00000,0x0000000086f8c398,0x0000000087500000)
 Metaspace       used 11110K, capacity 11266K, committed 11648K, reserved 1058816K
  class space    used 1223K, capacity 1281K, committed 1408K, reserved 1048576K
}
Event: 5434609.803 GC heap before
{Heap before GC invocations=5309 (full 24):
 PSYoungGen      total 20992K, used 20640K [0x00000000d7400000, 0x00000000d8980000, 0x0000000100000000)
  eden space 20480K, 100% used [0x00000000d7400000,0x00000000d8800000,0x00000000d8800000)
  from space 512K, 31% used [0x00000000d8900000,0x00000000d8928000,0x00000000d8980000)
  to   space 512K, 0% used [0x00000000d8880000,0x00000000d8880000,0x00000000d8900000)
 ParOldGen       total 25600K, used 20016K [0x0000000085c00000, 0x0000000087500000, 0x00000000d7400000)
  object space 25600K, 78% used [0x0000000085c00000,0x0000000086f8c398,0x0000000087500000)
 Metaspace       used 11110K, capacity 11266K, committed 11648K, reserved 1058816K
  class space    used 1223K, capacity 1281K, committed 1408K, reserved 1048576K
Event: 5434609.804 GC heap after
Heap after GC invocations=5309 (full 24):
 PSYoungGen      total 20992K, used 128K [0x00000000d7400000, 0x00000000d8980000, 0x0000000100000000)
  eden space 20480K, 0% used [0x00000000d7400000,0x00000000d7400000,0x00000000d8800000)
  from space 512K, 25% used [0x00000000d8880000,0x00000000d88a0000,0x00000000d8900000)
  to   space 512K, 0% used [0x00000000d8900000,0x00000000d8900000,0x00000000d8980000)
 ParOldGen       total 25600K, used 20104K [0x0000000085c00000, 0x0000000087500000, 0x00000000d7400000)
  object space 25600K, 78% used [0x0000000085c00000,0x0000000086fa2398,0x0000000087500000)
 Metaspace       used 11110K, capacity 11266K, committed 11648K, reserved 1058816K
  class space    used 1223K, capacity 1281K, committed 1408K, reserved 1048576K
}
Event: 5435481.897 GC heap before
{Heap before GC invocations=5310 (full 24):
 PSYoungGen      total 20992K, used 20608K [0x00000000d7400000, 0x00000000d8980000, 0x0000000100000000)
  eden space 20480K, 100% used [0x00000000d7400000,0x00000000d8800000,0x00000000d8800000)
  from space 512K, 25% used [0x00000000d8880000,0x00000000d88a0000,0x00000000d8900000)
  to   space 512K, 0% used [0x00000000d8900000,0x00000000d8900000,0x00000000d8980000)
 ParOldGen       total 25600K, used 20104K [0x0000000085c00000, 0x0000000087500000, 0x00000000d7400000)
  object space 25600K, 78% used [0x0000000085c00000,0x0000000086fa2398,0x0000000087500000)
 Metaspace       used 11110K, capacity 11266K, committed 11648K, reserved 1058816K
  class space    used 1223K, capacity 1281K, committed 1408K, reserved 1048576K
Event: 5435481.897 GC heap after
Heap after GC invocations=5310 (full 24):
 PSYoungGen      total 20992K, used 160K [0x00000000d7400000, 0x00000000d8980000, 0x0000000100000000)
  eden space 20480K, 0% used [0x00000000d7400000,0x00000000d7400000,0x00000000d8800000)
  from space 512K, 31% used [0x00000000d8900000,0x00000000d8928000,0x00000000d8980000)
  to   space 512K, 0% used [0x00000000d8880000,0x00000000d8880000,0x00000000d8900000)
 ParOldGen       total 25600K, used 20208K [0x0000000085c00000, 0x0000000087500000, 0x00000000d7400000)
  object space 25600K, 78% used [0x0000000085c00000,0x0000000086fbc398,0x0000000087500000)
 Metaspace       used 11110K, capacity 11266K, committed 11648K, reserved 1058816K
  class space    used 1223K, capacity 1281K, committed 1408K, reserved 1048576K
}
Event: 5436344.959 GC heap before
{Heap before GC invocations=5311 (full 24):
 PSYoungGen      total 20992K, used 20640K [0x00000000d7400000, 0x00000000d8980000, 0x0000000100000000)
  eden space 20480K, 100% used [0x00000000d7400000,0x00000000d8800000,0x00000000d8800000)
  from space 512K, 31% used [0x00000000d8900000,0x00000000d8928000,0x00000000d8980000)
  to   space 512K, 0% used [0x00000000d8880000,0x00000000d8880000,0x00000000d8900000)
 ParOldGen       total 25600K, used 20208K [0x0000000085c00000, 0x0000000087500000, 0x00000000d7400000)
  object space 25600K, 78% used [0x0000000085c00000,0x0000000086fbc398,0x0000000087500000)
 Metaspace       used 11110K, capacity 11266K, committed 11648K, reserved 1058816K
  class space    used 1223K, capacity 1281K, committed 1408K, reserved 1048576K
Event: 5436344.960 GC heap after
Heap after GC invocations=5311 (full 24):
 PSYoungGen      total 20992K, used 160K [0x00000000d7400000, 0x00000000d8980000, 0x0000000100000000)
  eden space 20480K, 0% used [0x00000000d7400000,0x00000000d7400000,0x00000000d8800000)
  from space 512K, 31% used [0x00000000d8880000,0x00000000d88a8000,0x00000000d8900000)
  to   space 512K, 0% used [0x00000000d8900000,0x00000000d8900000,0x00000000d8980000)
 ParOldGen       total 25600K, used 20304K [0x0000000085c00000, 0x0000000087500000, 0x00000000d7400000)
  object space 25600K, 79% used [0x0000000085c00000,0x0000000086fd4398,0x0000000087500000)
 Metaspace       used 11110K, capacity 11266K, committed 11648K, reserved 1058816K
  class space    used 1223K, capacity 1281K, committed 1408K, reserved 1048576K
}
Event: 5437204.956 GC heap before
{Heap before GC invocations=5312 (full 24):
 PSYoungGen      total 20992K, used 20640K [0x00000000d7400000, 0x00000000d8980000, 0x0000000100000000)
  eden space 20480K, 100% used [0x00000000d7400000,0x00000000d8800000,0x00000000d8800000)
  from space 512K, 31% used [0x00000000d8880000,0x00000000d88a8000,0x00000000d8900000)
  to   space 512K, 0% used [0x00000000d8900000,0x00000000d8900000,0x00000000d8980000)
 ParOldGen       total 25600K, used 20304K [0x0000000085c00000, 0x0000000087500000, 0x00000000d7400000)
  object space 25600K, 79% used [0x0000000085c00000,0x0000000086fd4398,0x0000000087500000)
 Metaspace       used 11110K, capacity 11266K, committed 11648K, reserved 1058816K
  class space    used 1223K, capacity 1281K, committed 1408K, reserved 1048576K
Event: 5437204.957 GC heap after
Heap after GC invocations=5312 (full 24):
 PSYoungGen      total 20992K, used 160K [0x00000000d7400000, 0x00000000d8980000, 0x0000000100000000)
  eden space 20480K, 0% used [0x00000000d7400000,0x00000000d7400000,0x00000000d8800000)
  from space 512K, 31% used [0x00000000d8900000,0x00000000d8928000,0x00000000d8980000)
  to   space 512K, 0% used [0x00000000d8880000,0x00000000d8880000,0x00000000d8900000)
 ParOldGen       total 25600K, used 20400K [0x0000000085c00000, 0x0000000087500000, 0x00000000d7400000)
  object space 25600K, 79% used [0x0000000085c00000,0x0000000086fec398,0x0000000087500000)
 Metaspace       used 11110K, capacity 11266K, committed 11648K, reserved 1058816K
  class space    used 1223K, capacity 1281K, committed 1408K, reserved 1048576K
}

Deoptimization events (10 events):
Event: 5437560.616 Thread 0x00007f6444291800 Uncommon trap: reason=class_check action=maybe_recompile pc=0x00007f64352fb1b8 method=java.io.PrintWriter.newLine()V @ 19
Event: 5437560.622 Thread 0x00007f63d4011000 Uncommon trap: reason=unstable_if action=reinterpret pc=0x00007f64354f5454 method=org.apache.zookeeper.server.quorum.FollowerRequestProcessor.run()V @ 47
Event: 5437560.622 Thread 0x00007f63d400f800 Uncommon trap: reason=unstable_if action=reinterpret pc=0x00007f643511306c method=org.apache.zookeeper.server.quorum.CommitProcessor.run()V @ 6
Event: 5437560.881 Thread 0x00007f644428f000 Uncommon trap: reason=unstable_if action=reinterpret pc=0x00007f643564e53c method=org.apache.zookeeper.server.NIOServerCnxn.<init>(Lorg/apache/zookeeper/server/ZooKeeperServer;Ljava/nio/channels/SocketChannel;Ljava/nio/channels/SelectionKey;Lorg/apache/zo
Event: 5437560.881 Thread 0x00007f644428f000 Uncommon trap: reason=unstable_if action=reinterpret pc=0x00007f64356c82a0 method=org.apache.zookeeper.server.NIOServerCnxn.isZKServerRunning()Z @ 4
Event: 5437560.882 Thread 0x00007f644428f000 Uncommon trap: reason=unstable_if action=reinterpret pc=0x00007f643570e938 method=org.apache.zookeeper.server.NIOServerCnxn.close()V @ 12
Event: 5437560.943 Thread 0x00007f644428f000 Uncommon trap: reason=unstable_if action=reinterpret pc=0x00007f64354fb824 method=org.apache.zookeeper.server.NIOServerCnxn.<init>(Lorg/apache/zookeeper/server/ZooKeeperServer;Ljava/nio/channels/SocketChannel;Ljava/nio/channels/SelectionKey;Lorg/apache/zo
Event: 5437561.513 Thread 0x00007f644428f000 Uncommon trap: reason=unstable_if action=reinterpret pc=0x00007f64351db03c method=org.apache.zookeeper.server.NIOServerCnxn.isZKServerRunning()Z @ 4
Event: 5437562.703 Thread 0x00007f644428f000 Uncommon trap: reason=unstable_if action=reinterpret pc=0x00007f64351da698 method=org.apache.zookeeper.server.NIOServerCnxn.isZKServerRunning()Z @ 4
Event: 5437570.073 Thread 0x00007f63d4014000 Uncommon trap: reason=unstable_if action=reinterpret pc=0x00007f64356e7d50 method=org.apache.zookeeper.server.persistence.FileTxnLog.commit()V @ 83

Classes redefined (0 events):
No events

Internal exceptions (10 events):
Event: 938.849 Thread 0x00007f63d400f800 Implicit null exception at 0x00007f64354a78b3 to 0x00007f64354a7a95
Event: 67240.911 Thread 0x00007f63d400f800 Implicit null exception at 0x00007f64356ce83b to 0x00007f64356d1006
Event: 1149355.001 Thread 0x00007f644428f000 Implicit null exception at 0x00007f64356cd31e to 0x00007f64356cdc75
Event: 1541034.957 Thread 0x00007f63d4011000 Implicit null exception at 0x00007f64354c44ef to 0x00007f64354c4c9d
Event: 5437560.881 Thread 0x00007f644428f000 Implicit null exception at 0x00007f643564b5ad to 0x00007f643564e525
Event: 5437560.881 Thread 0x00007f644428f000 Implicit null exception at 0x00007f64356c64cc to 0x00007f64356c8279
Event: 5437560.882 Thread 0x00007f644428f000 Implicit null exception at 0x00007f6435709e40 to 0x00007f643570e929
Event: 5437560.943 Thread 0x00007f644428f000 Implicit null exception at 0x00007f64354f8a7f to 0x00007f64354fb805
Event: 5437561.513 Thread 0x00007f644428f000 Implicit null exception at 0x00007f64351daa58 to 0x00007f64351db029
Event: 5437562.703 Thread 0x00007f644428f000 Implicit null exception at 0x00007f64351da653 to 0x00007f64351da685

Events (10 events):
Event: 5437561.513 Thread 0x00007f644428f000 DEOPT PACKING pc=0x00007f64351db03c sp=0x00007f6425ac07f0
Event: 5437561.513 Thread 0x00007f644428f000 DEOPT UNPACKING pc=0x00007f643504547a sp=0x00007f6425ac0778 mode 2
Event: 5437562.703 Thread 0x00007f644428f000 Uncommon trap: trap_request=0xffffff65 fr.pc=0x00007f64351da698
Event: 5437562.703 Thread 0x00007f644428f000 DEOPT PACKING pc=0x00007f64351da698 sp=0x00007f6425ac07b0
Event: 5437562.703 Thread 0x00007f644428f000 DEOPT UNPACKING pc=0x00007f643504547a sp=0x00007f6425ac0778 mode 2
Event: 5437563.794 Thread 0x00007f64440bf000 flushing nmethod 0x00007f64355fac10
Event: 5437563.794 Thread 0x00007f64440bf000 flushing nmethod 0x00007f6435603890
Event: 5437570.073 Thread 0x00007f63d4014000 Uncommon trap: trap_request=0xffffff65 fr.pc=0x00007f64356e7d50
Event: 5437570.073 Thread 0x00007f63d4014000 DEOPT PACKING pc=0x00007f64356e7d50 sp=0x00007f6424fb58f0
Event: 5437570.073 Thread 0x00007f63d4014000 DEOPT UNPACKING pc=0x00007f643504547a sp=0x00007f6424fb57d8 mode 2


Dynamic libraries:
00400000-00401000 r-xp 00000000 00:27 2526                               /tonder/app/jdk1.8.0_211/bin/java
00600000-00601000 r--p 00000000 00:27 2526                               /tonder/app/jdk1.8.0_211/bin/java
00601000-00602000 rw-p 00001000 00:27 2526                               /tonder/app/jdk1.8.0_211/bin/java
01a88000-01aa9000 rw-p 00000000 00:00 0                                  [heap]
85c00000-87500000 rw-p 00000000 00:00 0 
87500000-d7400000 ---p 00000000 00:00 0 
d7400000-d8980000 rw-p 00000000 00:00 0 
d8980000-100000000 ---p 00000000 00:00 0 
100000000-100160000 rw-p 00000000 00:00 0 
100160000-140000000 ---p 00000000 00:00 0 
7f63b4000000-7f63b4102000 rw-p 00000000 00:00 0 
7f63b4102000-7f63b8000000 ---p 00000000 00:00 0 
7f63b8000000-7f63b8021000 rw-p 00000000 00:00 0 
7f63b8021000-7f63bc000000 ---p 00000000 00:00 0 
7f63bc000000-7f63bc021000 rw-p 00000000 00:00 0 
7f63bc021000-7f63c0000000 ---p 00000000 00:00 0 
7f63c0000000-7f63c0021000 rw-p 00000000 00:00 0 
7f63c0021000-7f63c4000000 ---p 00000000 00:00 0 
7f63c4000000-7f63c4021000 rw-p 00000000 00:00 0 
7f63c4021000-7f63c8000000 ---p 00000000 00:00 0 
7f63c8000000-7f63c8024000 rw-p 00000000 00:00 0 
7f63c8024000-7f63cc000000 ---p 00000000 00:00 0 
7f63cc000000-7f63cc021000 rw-p 00000000 00:00 0 
7f63cc021000-7f63d0000000 ---p 00000000 00:00 0 
7f63d0000000-7f63d0021000 rw-p 00000000 00:00 0 
7f63d0021000-7f63d4000000 ---p 00000000 00:00 0 
7f63d4000000-7f63d4026000 rw-p 00000000 00:00 0 
7f63d4026000-7f63d8000000 ---p 00000000 00:00 0 
7f63d8000000-7f63d8021000 rw-p 00000000 00:00 0 
7f63d8021000-7f63dc000000 ---p 00000000 00:00 0 
7f63dc000000-7f63dc021000 rw-p 00000000 00:00 0 
7f63dc021000-7f63e0000000 ---p 00000000 00:00 0 
7f63e0000000-7f63e0021000 rw-p 00000000 00:00 0 
7f63e0021000-7f63e4000000 ---p 00000000 00:00 0 
7f63e4000000-7f63e42c0000 rw-p 00000000 00:00 0 
7f63e42c0000-7f63e8000000 ---p 00000000 00:00 0 
7f63e8000000-7f63e8021000 rw-p 00000000 00:00 0 
7f63e8021000-7f63ec000000 ---p 00000000 00:00 0 
7f63ec000000-7f63ec021000 rw-p 00000000 00:00 0 
7f63ec021000-7f63f0000000 ---p 00000000 00:00 0 
7f63f0000000-7f63f0021000 rw-p 00000000 00:00 0 
7f63f0021000-7f63f4000000 ---p 00000000 00:00 0 
7f63f4000000-7f63f4878000 rw-p 00000000 00:00 0 
7f63f4878000-7f63f8000000 ---p 00000000 00:00 0 
7f63f8000000-7f63f8021000 rw-p 00000000 00:00 0 
7f63f8021000-7f63fc000000 ---p 00000000 00:00 0 
7f63fc000000-7f63ff2f2000 rw-p 00000000 00:00 0 
7f63ff2f2000-7f6400000000 ---p 00000000 00:00 0 
7f6400000000-7f6401d6a000 rw-p 00000000 00:00 0 
7f6401d6a000-7f6404000000 ---p 00000000 00:00 0 
7f6404000000-7f6404021000 rw-p 00000000 00:00 0 
7f6404021000-7f6408000000 ---p 00000000 00:00 0 
7f6409ad6000-7f6410000000 r--p 00000000 fd:01 666749                     /usr/lib/locale/locale-archive
7f6410000000-7f6410021000 rw-p 00000000 00:00 0 
7f6410021000-7f6414000000 ---p 00000000 00:00 0 
7f6414000000-7f6414021000 rw-p 00000000 00:00 0 
7f6414021000-7f6418000000 ---p 00000000 00:00 0 
7f6418000000-7f6418021000 rw-p 00000000 00:00 0 
7f6418021000-7f641c000000 ---p 00000000 00:00 0 
7f641c000000-7f641c05a000 rw-p 00000000 00:00 0 
7f641c05a000-7f6420000000 ---p 00000000 00:00 0 
7f6420000000-7f6420021000 rw-p 00000000 00:00 0 
7f6420021000-7f6424000000 ---p 00000000 00:00 0 
7f6424cb4000-7f6424cb7000 ---p 00000000 00:00 0 
7f6424cb7000-7f6424db5000 rw-p 00000000 00:00 0 
7f6424db5000-7f6424db8000 ---p 00000000 00:00 0 
7f6424db8000-7f6424eb6000 rw-p 00000000 00:00 0 
7f6424eb6000-7f6424eb9000 ---p 00000000 00:00 0 
7f6424eb9000-7f6424fb7000 rw-p 00000000 00:00 0 
7f6424fb7000-7f6424fba000 ---p 00000000 00:00 0 
7f6424fba000-7f64250b8000 rw-p 00000000 00:00 0 
7f64250b8000-7f64250bb000 ---p 00000000 00:00 0 
7f64250bb000-7f64251b9000 rw-p 00000000 00:00 0 
7f64251b9000-7f64251bc000 ---p 00000000 00:00 0 
7f64251bc000-7f64252ba000 rw-p 00000000 00:00 0 
7f64252ba000-7f64252bd000 ---p 00000000 00:00 0 
7f64252bd000-7f64253bb000 rw-p 00000000 00:00 0 
7f64253bb000-7f64253be000 ---p 00000000 00:00 0 
7f64253be000-7f64254bc000 rw-p 00000000 00:00 0 
7f64254bc000-7f64254bf000 ---p 00000000 00:00 0 
7f64254bf000-7f64255bd000 rw-p 00000000 00:00 0 
7f64255bd000-7f64255c0000 ---p 00000000 00:00 0 
7f64255c0000-7f64256be000 rw-p 00000000 00:00 0 
7f64256be000-7f64256c1000 ---p 00000000 00:00 0 
7f64256c1000-7f64257bf000 rw-p 00000000 00:00 0 
7f64257bf000-7f64257c2000 ---p 00000000 00:00 0 
7f64257c2000-7f64258c0000 rw-p 00000000 00:00 0 
7f64258c0000-7f64258c3000 ---p 00000000 00:00 0 
7f64258c3000-7f64259c1000 rw-p 00000000 00:00 0 
7f64259c1000-7f64259c4000 ---p 00000000 00:00 0 
7f64259c4000-7f6425cc2000 rw-p 00000000 00:00 0 
7f6425cc2000-7f6425cc3000 ---p 00000000 00:00 0 
7f6425cc3000-7f6425dc3000 rw-p 00000000 00:00 0 
7f6425dc3000-7f6425dc6000 ---p 00000000 00:00 0 
7f6425dc6000-7f6425ec4000 rw-p 00000000 00:00 0 
7f6425ec4000-7f6425ed3000 r-xp 00000000 fd:01 659665                     /usr/lib64/libbz2.so.1.0.6
7f6425ed3000-7f64260d2000 ---p 0000f000 fd:01 659665                     /usr/lib64/libbz2.so.1.0.6
7f64260d2000-7f64260d3000 r--p 0000e000 fd:01 659665                     /usr/lib64/libbz2.so.1.0.6
7f64260d3000-7f64260d4000 rw-p 0000f000 fd:01 659665                     /usr/lib64/libbz2.so.1.0.6
7f64260d4000-7f64260f9000 r-xp 00000000 fd:01 659661                     /usr/lib64/liblzma.so.5.2.2
7f64260f9000-7f64262f8000 ---p 00025000 fd:01 659661                     /usr/lib64/liblzma.so.5.2.2
7f64262f8000-7f64262f9000 r--p 00024000 fd:01 659661                     /usr/lib64/liblzma.so.5.2.2
7f64262f9000-7f64262fa000 rw-p 00025000 fd:01 659661                     /usr/lib64/liblzma.so.5.2.2
7f64262fa000-7f642630f000 r-xp 00000000 fd:01 658976                     /usr/lib64/libz.so.1.2.7
7f642630f000-7f642650e000 ---p 00015000 fd:01 658976                     /usr/lib64/libz.so.1.2.7
7f642650e000-7f642650f000 r--p 00014000 fd:01 658976                     /usr/lib64/libz.so.1.2.7
7f642650f000-7f6426510000 rw-p 00015000 fd:01 658976                     /usr/lib64/libz.so.1.2.7
7f6426510000-7f6426527000 r-xp 00000000 fd:01 659676                     /usr/lib64/libelf-0.172.so
7f6426527000-7f6426726000 ---p 00017000 fd:01 659676                     /usr/lib64/libelf-0.172.so
7f6426726000-7f6426727000 r--p 00016000 fd:01 659676                     /usr/lib64/libelf-0.172.so
7f6426727000-7f6426728000 rw-p 00017000 fd:01 659676                     /usr/lib64/libelf-0.172.so
7f6426728000-7f642672c000 r-xp 00000000 fd:01 659137                     /usr/lib64/libattr.so.1.1.0
7f642672c000-7f642692b000 ---p 00004000 fd:01 659137                     /usr/lib64/libattr.so.1.1.0
7f642692b000-7f642692c000 r--p 00003000 fd:01 659137                     /usr/lib64/libattr.so.1.1.0
7f642692c000-7f642692d000 rw-p 00004000 fd:01 659137                     /usr/lib64/libattr.so.1.1.0
7f642692d000-7f6426942000 r-xp 00000000 fd:01 655380                     /usr/lib64/libgcc_s-4.8.5-20150702.so.1
7f6426942000-7f6426b41000 ---p 00015000 fd:01 655380                     /usr/lib64/libgcc_s-4.8.5-20150702.so.1
7f6426b41000-7f6426b42000 r--p 00014000 fd:01 655380                     /usr/lib64/libgcc_s-4.8.5-20150702.so.1
7f6426b42000-7f6426b43000 rw-p 00015000 fd:01 655380                     /usr/lib64/libgcc_s-4.8.5-20150702.so.1
7f6426b43000-7f6426b8f000 r-xp 00000000 fd:01 663064                     /usr/lib64/libdw-0.172.so
7f6426b8f000-7f6426d8f000 ---p 0004c000 fd:01 663064                     /usr/lib64/libdw-0.172.so
7f6426d8f000-7f6426d91000 r--p 0004c000 fd:01 663064                     /usr/lib64/libdw-0.172.so
7f6426d91000-7f6426d92000 rw-p 0004e000 fd:01 663064                     /usr/lib64/libdw-0.172.so
7f6426d92000-7f6426d96000 r-xp 00000000 fd:01 659142                     /usr/lib64/libcap.so.2.22
7f6426d96000-7f6426f95000 ---p 00004000 fd:01 659142                     /usr/lib64/libcap.so.2.22
7f6426f95000-7f6426f96000 r--p 00003000 fd:01 659142                     /usr/lib64/libcap.so.2.22
7f6426f96000-7f6426f97000 rw-p 00004000 fd:01 659142                     /usr/lib64/libcap.so.2.22
7f6426f97000-7f6426fa9000 r-xp 00000000 fd:01 662346                     /usr/lib64/libnss_myhostname.so.2
7f6426fa9000-7f64271a8000 ---p 00012000 fd:01 662346                     /usr/lib64/libnss_myhostname.so.2
7f64271a8000-7f64271ab000 r--p 00011000 fd:01 662346                     /usr/lib64/libnss_myhostname.so.2
7f64271ab000-7f64271ac000 rw-p 00014000 fd:01 662346                     /usr/lib64/libnss_myhostname.so.2
7f64271ac000-7f64271c2000 r-xp 00000000 fd:01 658685                     /usr/lib64/libresolv-2.17.so
7f64271c2000-7f64273c1000 ---p 00016000 fd:01 658685                     /usr/lib64/libresolv-2.17.so
7f64273c1000-7f64273c2000 r--p 00015000 fd:01 658685                     /usr/lib64/libresolv-2.17.so
7f64273c2000-7f64273c3000 rw-p 00016000 fd:01 658685                     /usr/lib64/libresolv-2.17.so
7f64273c3000-7f64273c5000 rw-p 00000000 00:00 0 
7f64273c5000-7f64273ca000 r-xp 00000000 fd:01 658673                     /usr/lib64/libnss_dns-2.17.so
7f64273ca000-7f64275ca000 ---p 00005000 fd:01 658673                     /usr/lib64/libnss_dns-2.17.so
7f64275ca000-7f64275cb000 r--p 00005000 fd:01 658673                     /usr/lib64/libnss_dns-2.17.so
7f64275cb000-7f64275cc000 rw-p 00006000 fd:01 658673                     /usr/lib64/libnss_dns-2.17.so
7f64275cc000-7f64275dd000 r-xp 00000000 00:27 4057                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libnio.so
7f64275dd000-7f64277dc000 ---p 00011000 00:27 4057                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libnio.so
7f64277dc000-7f64277dd000 r--p 00010000 00:27 4057                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libnio.so
7f64277dd000-7f64277de000 rw-p 00011000 00:27 4057                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libnio.so
7f64277de000-7f64277e7000 r-xp 00000000 00:27 4036                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libmanagement.so
7f64277e7000-7f64279e6000 ---p 00009000 00:27 4036                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libmanagement.so
7f64279e6000-7f64279e7000 r--p 00008000 00:27 4036                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libmanagement.so
7f64279e7000-7f64279e8000 rw-p 00009000 00:27 4036                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libmanagement.so
7f64279e8000-7f6427ce8000 rw-p 00000000 00:00 0 
7f6427ce8000-7f6427cfe000 r-xp 00000000 00:27 4055                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libnet.so
7f6427cfe000-7f6427efd000 ---p 00016000 00:27 4055                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libnet.so
7f6427efd000-7f6427efe000 r--p 00015000 00:27 4055                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libnet.so
7f6427efe000-7f6427eff000 rw-p 00016000 00:27 4055                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libnet.so
7f6427eff000-7f6427f02000 ---p 00000000 00:00 0 
7f6427f02000-7f6428000000 rw-p 00000000 00:00 0 
7f6428000000-7f6428021000 rw-p 00000000 00:00 0 
7f6428021000-7f642c000000 ---p 00000000 00:00 0 
7f642c064000-7f642c0de000 r--s 010fd000 00:27 3821                       /tonder/app/jdk1.8.0_211/lib/tools.jar
7f642c0de000-7f642c0e2000 ---p 00000000 00:00 0 
7f642c0e2000-7f642c1df000 rw-p 00000000 00:00 0 
7f642c1df000-7f642c1e3000 ---p 00000000 00:00 0 
7f642c1e3000-7f6430000000 rw-p 00000000 00:00 0 
7f6430000000-7f6430021000 rw-p 00000000 00:00 0 
7f6430021000-7f6434000000 ---p 00000000 00:00 0 
7f643406c000-7f6434072000 r--s 00022000 00:27 3829                       /tonder/app/jdk1.8.0_211/lib/dt.jar
7f6434072000-7f6434085000 r--s 0015f000 00:27 2360                       /tonder/app/zookeeper-3.4.14/zookeeper-3.4.14.jar
7f6434085000-7f643408e000 r--s 0006f000 00:27 1953                       /tonder/app/zookeeper-3.4.14/lib/log4j-1.2.17.jar
7f643408e000-7f6434092000 ---p 00000000 00:00 0 
7f6434092000-7f643418f000 rw-p 00000000 00:00 0 
7f643418f000-7f6434192000 ---p 00000000 00:00 0 
7f6434192000-7f6434290000 rw-p 00000000 00:00 0 
7f6434290000-7f6434293000 ---p 00000000 00:00 0 
7f6434293000-7f6434391000 rw-p 00000000 00:00 0 
7f6434391000-7f6434394000 ---p 00000000 00:00 0 
7f6434394000-7f6434492000 rw-p 00000000 00:00 0 
7f6434492000-7f6434493000 ---p 00000000 00:00 0 
7f6434493000-7f6435000000 rw-p 00000000 00:00 0 
7f6435000000-7f6435730000 rwxp 00000000 00:00 0 
7f6435730000-7f6444000000 ---p 00000000 00:00 0 
7f6444000000-7f6444567000 rw-p 00000000 00:00 0 
7f6444567000-7f6448000000 ---p 00000000 00:00 0 
7f6448004000-7f6448579000 rw-p 00000000 00:00 0 
7f6448579000-7f6448753000 r--s 03d6d000 00:27 3968                       /tonder/app/jdk1.8.0_211/jre/lib/rt.jar
7f6448753000-7f644939b000 rw-p 00000000 00:00 0 
7f644939b000-7f644939c000 ---p 00000000 00:00 0 
7f644939c000-7f644949c000 rw-p 00000000 00:00 0 
7f644949c000-7f644949d000 ---p 00000000 00:00 0 
7f644949d000-7f644959d000 rw-p 00000000 00:00 0 
7f644959d000-7f644959e000 ---p 00000000 00:00 0 
7f644959e000-7f644969e000 rw-p 00000000 00:00 0 
7f644969e000-7f644969f000 ---p 00000000 00:00 0 
7f644969f000-7f64497ac000 rw-p 00000000 00:00 0 
7f64497ac000-7f6449a2b000 ---p 00000000 00:00 0 
7f6449a2b000-7f6449a38000 rw-p 00000000 00:00 0 
7f6449a38000-7f6449cb7000 ---p 00000000 00:00 0 
7f6449cb7000-7f6449cc2000 rw-p 00000000 00:00 0 
7f6449cc2000-7f6449dfd000 ---p 00000000 00:00 0 
7f6449dfd000-7f6449e1b000 rw-p 00000000 00:00 0 
7f6449e1b000-7f644a1be000 ---p 00000000 00:00 0 
7f644a1be000-7f644a1d9000 r-xp 00000000 00:27 4006                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libzip.so
7f644a1d9000-7f644a3d8000 ---p 0001b000 00:27 4006                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libzip.so
7f644a3d8000-7f644a3d9000 r--p 0001a000 00:27 4006                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libzip.so
7f644a3d9000-7f644a3da000 rw-p 0001b000 00:27 4006                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libzip.so
7f644a3da000-7f644a3e6000 r-xp 00000000 fd:01 658675                     /usr/lib64/libnss_files-2.17.so
7f644a3e6000-7f644a5e5000 ---p 0000c000 fd:01 658675                     /usr/lib64/libnss_files-2.17.so
7f644a5e5000-7f644a5e6000 r--p 0000b000 fd:01 658675                     /usr/lib64/libnss_files-2.17.so
7f644a5e6000-7f644a5e7000 rw-p 0000c000 fd:01 658675                     /usr/lib64/libnss_files-2.17.so
7f644a5e7000-7f644a5ed000 rw-p 00000000 00:00 0 
7f644a5ed000-7f644a619000 r-xp 00000000 00:27 4060                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libjava.so
7f644a619000-7f644a819000 ---p 0002c000 00:27 4060                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libjava.so
7f644a819000-7f644a81a000 r--p 0002c000 00:27 4060                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libjava.so
7f644a81a000-7f644a81c000 rw-p 0002d000 00:27 4060                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libjava.so
7f644a81c000-7f644a829000 r-xp 00000000 00:27 4034                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libverify.so
7f644a829000-7f644aa28000 ---p 0000d000 00:27 4034                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libverify.so
7f644aa28000-7f644aa2a000 r--p 0000c000 00:27 4034                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libverify.so
7f644aa2a000-7f644aa2b000 rw-p 0000e000 00:27 4034                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/libverify.so
7f644aa2b000-7f644aa32000 r-xp 00000000 fd:01 658687                     /usr/lib64/librt-2.17.so
7f644aa32000-7f644ac31000 ---p 00007000 fd:01 658687                     /usr/lib64/librt-2.17.so
7f644ac31000-7f644ac32000 r--p 00006000 fd:01 658687                     /usr/lib64/librt-2.17.so
7f644ac32000-7f644ac33000 rw-p 00007000 fd:01 658687                     /usr/lib64/librt-2.17.so
7f644ac33000-7f644ad34000 r-xp 00000000 fd:01 658665                     /usr/lib64/libm-2.17.so
7f644ad34000-7f644af33000 ---p 00101000 fd:01 658665                     /usr/lib64/libm-2.17.so
7f644af33000-7f644af34000 r--p 00100000 fd:01 658665                     /usr/lib64/libm-2.17.so
7f644af34000-7f644af35000 rw-p 00101000 fd:01 658665                     /usr/lib64/libm-2.17.so
7f644af35000-7f644bc1d000 r-xp 00000000 00:27 4042                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/server/libjvm.so
7f644bc1d000-7f644be1c000 ---p 00ce8000 00:27 4042                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/server/libjvm.so
7f644be1c000-7f644beb2000 r--p 00ce7000 00:27 4042                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/server/libjvm.so
7f644beb2000-7f644bee3000 rw-p 00d7d000 00:27 4042                       /tonder/app/jdk1.8.0_211/jre/lib/amd64/server/libjvm.so
7f644bee3000-7f644bf1e000 rw-p 00000000 00:00 0 
7f644bf1e000-7f644c0e0000 r-xp 00000000 fd:01 658657                     /usr/lib64/libc-2.17.so
7f644c0e0000-7f644c2e0000 ---p 001c2000 fd:01 658657                     /usr/lib64/libc-2.17.so
7f644c2e0000-7f644c2e4000 r--p 001c2000 fd:01 658657                     /usr/lib64/libc-2.17.so
7f644c2e4000-7f644c2e6000 rw-p 001c6000 fd:01 658657                     /usr/lib64/libc-2.17.so
7f644c2e6000-7f644c2eb000 rw-p 00000000 00:00 0 
7f644c2eb000-7f644c2ed000 r-xp 00000000 fd:01 658663                     /usr/lib64/libdl-2.17.so
7f644c2ed000-7f644c4ed000 ---p 00002000 fd:01 658663                     /usr/lib64/libdl-2.17.so
7f644c4ed000-7f644c4ee000 r--p 00002000 fd:01 658663                     /usr/lib64/libdl-2.17.so
7f644c4ee000-7f644c4ef000 rw-p 00003000 fd:01 658663                     /usr/lib64/libdl-2.17.so
7f644c4ef000-7f644c506000 r-xp 00000000 00:27 3825                       /tonder/app/jdk1.8.0_211/lib/amd64/jli/libjli.so
7f644c506000-7f644c705000 ---p 00017000 00:27 3825                       /tonder/app/jdk1.8.0_211/lib/amd64/jli/libjli.so
7f644c705000-7f644c706000 r--p 00016000 00:27 3825                       /tonder/app/jdk1.8.0_211/lib/amd64/jli/libjli.so
7f644c706000-7f644c707000 rw-p 00017000 00:27 3825                       /tonder/app/jdk1.8.0_211/lib/amd64/jli/libjli.so
7f644c707000-7f644c71e000 r-xp 00000000 fd:01 658683                     /usr/lib64/libpthread-2.17.so
7f644c71e000-7f644c91d000 ---p 00017000 fd:01 658683                     /usr/lib64/libpthread-2.17.so
7f644c91d000-7f644c91e000 r--p 00016000 fd:01 658683                     /usr/lib64/libpthread-2.17.so
7f644c91e000-7f644c91f000 rw-p 00017000 fd:01 658683                     /usr/lib64/libpthread-2.17.so
7f644c91f000-7f644c923000 rw-p 00000000 00:00 0 
7f644c923000-7f644c945000 r-xp 00000000 fd:01 658372                     /usr/lib64/ld-2.17.so
7f644c945000-7f644c94a000 r--s 000a3000 00:27 3986                       /tonder/app/jdk1.8.0_211/jre/lib/jsse.jar
7f644c94a000-7f644c94c000 r--s 00014000 00:27 1957                       /tonder/app/zookeeper-3.4.14/lib/jline-0.9.94.jar
7f644c94c000-7f644c965000 r--s 00123000 00:27 1964                       /tonder/app/zookeeper-3.4.14/lib/netty-3.10.6.Final.jar
7f644c965000-7f644ca30000 rw-p 00000000 00:00 0 
7f644ca30000-7f644ca38000 rw-s 00000000 fd:01 1310741                    /tmp/hsperfdata_photon/18738
7f644ca38000-7f644ca3c000 ---p 00000000 00:00 0 
7f644ca3c000-7f644cb3d000 rw-p 00000000 00:00 0 
7f644cb3d000-7f644cb3e000 r--s 00004000 00:27 1958                       /tonder/app/zookeeper-3.4.14/lib/audience-annotations-0.5.0.jar
7f644cb3e000-7f644cb40000 r--s 00009000 00:27 1961                       /tonder/app/zookeeper-3.4.14/lib/slf4j-api-1.7.25.jar
7f644cb40000-7f644cb41000 r--s 00002000 00:27 1962                       /tonder/app/zookeeper-3.4.14/lib/slf4j-log4j12-1.7.25.jar
7f644cb41000-7f644cb42000 rw-p 00000000 00:00 0 
7f644cb42000-7f644cb43000 r--p 00000000 00:00 0 
7f644cb43000-7f644cb44000 rw-p 00000000 00:00 0 
7f644cb44000-7f644cb45000 r--p 00021000 fd:01 658372                     /usr/lib64/ld-2.17.so
7f644cb45000-7f644cb46000 rw-p 00022000 fd:01 658372                     /usr/lib64/ld-2.17.so
7f644cb46000-7f644cb47000 rw-p 00000000 00:00 0 
7ffe247e5000-7ffe24807000 rw-p 00000000 00:00 0                          [stack]
7ffe249fa000-7ffe249fc000 r-xp 00000000 00:00 0                          [vdso]
ffffffffff600000-ffffffffff601000 r-xp 00000000 00:00 0                  [vsyscall]

VM Arguments:
jvm_args: -Dzookeeper.log.dir=. -Dzookeeper.root.logger=INFO,CONSOLE -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false 
java_command: org.apache.zookeeper.server.quorum.QuorumPeerMain /tonder/app/zookeeper-3.4.14/bin/../conf/zoo.cfg
java_class_path (initial): /tonder/app/zookeeper-3.4.14/bin/../zookeeper-server/target/classes:/tonder/app/zookeeper-3.4.14/bin/../build/classes:/tonder/app/zookeeper-3.4.14/bin/../zookeeper-server/target/lib/*.jar:/tonder/app/zookeeper-3.4.14/bin/../build/lib/*.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/slf4j-log4j12-1.7.25.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/slf4j-api-1.7.25.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/netty-3.10.6.Final.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/log4j-1.2.17.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/jline-0.9.94.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/audience-annotations-0.5.0.jar:/tonder/app/zookeeper-3.4.14/bin/../zookeeper-3.4.14.jar:/tonder/app/zookeeper-3.4.14/bin/../zookeeper-server/src/main/resources/lib/*.jar:/tonder/app/zookeeper-3.4.14/bin/../conf:/tonder/app/zookeeper-3.4.14/bin/../zookeeper-server/target/classes:/tonder/app/zookeeper-3.4.14/bin/../build/classes:/tonder/app/zookeeper-3.4.14/bin/../zookeeper-server/target/lib/*.jar:/tonder/app/zookeeper-3.4.14/bin/../build/lib/*.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/slf4j-log4j12-1.7.25.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/slf4j-api-1.7.25.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/netty-3.10.6.Final.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/log4j-1.2.17.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/jline-0.9.94.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/audience-annotations-0.5.0.jar:/tonder/app/zookeeper-3.4.14/bin/../zookeeper-3.4.14.jar:/tonder/app/zookeeper-3.4.14/bin/../zookeeper-server/src/main/resources/lib/*.jar:/tonder/app/zookeeper-3.4.14/bin/../conf:.:/tonder/app/jdk1.8.0_211/lib/dt.jar:/tonder/app/jdk1.8.0_211/lib/tools.jar:/tonder/app/jdk1.8.0_211/jre/lib
Launcher Type: SUN_STANDARD

Environment Variables:
JAVA_HOME=/tonder/app/jdk1.8.0_211
JRE_HOME=/tonder/app/jdk1.8.0_211/jre
CLASSPATH=/tonder/app/zookeeper-3.4.14/bin/../zookeeper-server/target/classes:/tonder/app/zookeeper-3.4.14/bin/../build/classes:/tonder/app/zookeeper-3.4.14/bin/../zookeeper-server/target/lib/*.jar:/tonder/app/zookeeper-3.4.14/bin/../build/lib/*.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/slf4j-log4j12-1.7.25.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/slf4j-api-1.7.25.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/netty-3.10.6.Final.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/log4j-1.2.17.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/jline-0.9.94.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/audience-annotations-0.5.0.jar:/tonder/app/zookeeper-3.4.14/bin/../zookeeper-3.4.14.jar:/tonder/app/zookeeper-3.4.14/bin/../zookeeper-server/src/main/resources/lib/*.jar:/tonder/app/zookeeper-3.4.14/bin/../conf:/tonder/app/zookeeper-3.4.14/bin/../zookeeper-server/target/classes:/tonder/app/zookeeper-3.4.14/bin/../build/classes:/tonder/app/zookeeper-3.4.14/bin/../zookeeper-server/target/lib/*.jar:/tonder/app/zookeeper-3.4.14/bin/../build/lib/*.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/slf4j-log4j12-1.7.25.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/slf4j-api-1.7.25.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/netty-3.10.6.Final.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/log4j-1.2.17.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/jline-0.9.94.jar:/tonder/app/zookeeper-3.4.14/bin/../lib/audience-annotations-0.5.0.jar:/tonder/app/zookeeper-3.4.14/bin/../zookeeper-3.4.14.jar:/tonder/app/zookeeper-3.4.14/bin/../zookeeper-server/src/main/resources/lib/*.jar:/tonder/app/zookeeper-3.4.14/bin/../conf:.:/tonder/app/jdk1.8.0_211/lib/dt.jar:/tonder/app/jdk1.8.0_211/lib/tools.jar:/tonder/app/jdk1.8.0_211/jre/lib
PATH=/usr/local/bin:/usr/bin:/usr/local/sbin:/usr/sbin:/tonder/app/jdk1.8.0_211/bin:/tonder/app/jdk1.8.0_211/jre/bin:/home/photon/bin:/tonder/app/jdk1.8.0_211/bin:/tonder/app/apache-zookeeper-3.5.5/bin:/home/photon/.local/bin:/home/photon/bin
SHELL=/bin/bash

Signal Handlers:

[error occurred during error reporting (printing signal handlers), id 0x7]


---------------  S Y S T E M  ---------------

OS:CentOS Linux release 7.6.1810 (Core) 

uname:Linux 3.10.0-957.5.1.el7.x86_64 #1 SMP Fri Feb 1 14:54:57 UTC 2019 x86_64
libc:glibc 2.17 NPTL 2.17 
rlimit: STACK 8192k, CORE 0k, NPROC 4096, NOFILE 65535, AS infinity
load average:3.50 0.94 0.38

/proc/meminfo:
MemTotal:        8008880 kB
MemFree:          717548 kB
MemAvailable:    2867088 kB
Buffers:          140184 kB
Cached:          2175808 kB
SwapCached:            0 kB
Active:          6109868 kB
Inactive:         872604 kB
Active(anon):    4666996 kB
Inactive(anon):      632 kB
Active(file):    1442872 kB
Inactive(file):   871972 kB
Unevictable:           0 kB
Mlocked:               0 kB
SwapTotal:             0 kB
SwapFree:              0 kB
Dirty:               824 kB
Writeback:             0 kB
AnonPages:       4666520 kB
Mapped:           102268 kB
Shmem:              1108 kB
Slab:             155052 kB
SReclaimable:     112236 kB
SUnreclaim:        42816 kB
KernelStack:       33760 kB
PageTables:        22380 kB
NFS_Unstable:          0 kB
Bounce:                0 kB
WritebackTmp:          0 kB
CommitLimit:     4004440 kB
Committed_AS:    8510504 kB
VmallocTotal:   34359738367 kB
VmallocUsed:       21492 kB
VmallocChunk:   34359707388 kB
HardwareCorrupted:     0 kB
AnonHugePages:    688128 kB
CmaTotal:              0 kB
CmaFree:               0 kB
HugePages_Total:       0
HugePages_Free:        0
HugePages_Rsvd:        0
HugePages_Surp:        0
Hugepagesize:       2048 kB
DirectMap4k:      178048 kB
DirectMap2M:     7161856 kB
DirectMap1G:     3145728 kB

container (cgroup) information:
container_type: cgroupv1
cpu_cpuset_cpus: 0-3
cpu_memory_nodes: 0
active_processor_count: 4
cpu_quota: -1
cpu_period: 100000
cpu_shares: -1
memory_limit_in_bytes: -1
memory_and_swap_limit_in_bytes: -1
memory_soft_limit_in_bytes: -1
memory_usage_in_bytes: 7150034944
memory_max_usage_in_bytes: 0


CPU:total 4 (initial active 4) (2 cores per cpu, 2 threads per core) family 6 model 85 stepping 4, cmov, cx8, fxsr, mmx, sse, sse2, sse3, ssse3, sse4.1, sse4.2, popcnt, avx, avx2, aes, clmul, erms, rtm, 3dnowpref, lzcnt, ht, tsc, bmi1, bmi2, adx

/proc/cpuinfo:
processor	: 0
vendor_id	: GenuineIntel
cpu family	: 6
model		: 85
model name	: Intel(R) Xeon(R) Platinum 8163 CPU @ 2.50GHz
stepping	: 4
microcode	: 0x1
cpu MHz		: 2500.012
cache size	: 33792 KB
physical id	: 0
siblings	: 4
core id		: 0
cpu cores	: 2
apicid		: 0
initial apicid	: 0
fpu		: yes
fpu_exception	: yes
cpuid level	: 13
wp		: yes
flags		: fpu vme de pse tsc msr pae mce cx8 apic sep mtrr pge mca cmov pat pse36 clflush mmx fxsr sse sse2 ss ht syscall nx pdpe1gb rdtscp lm constant_tsc rep_good nopl eagerfpu pni pclmulqdq ssse3 fma cx16 pcid sse4_1 sse4_2 x2apic movbe popcnt tsc_deadline_timer aes xsave avx f16c rdrand hypervisor lahf_lm abm 3dnowprefetch ibrs ibpb stibp fsgsbase tsc_adjust bmi1 hle avx2 smep bmi2 erms invpcid rtm mpx avx512f avx512dq rdseed adx smap avx512cd avx512bw avx512vl xsaveopt xsavec xgetbv1 spec_ctrl intel_stibp
bogomips	: 5000.02
clflush size	: 64
cache_alignment	: 64
address sizes	: 46 bits physical, 48 bits virtual
power management:

processor	: 1
vendor_id	: GenuineIntel
cpu family	: 6
model		: 85
model name	: Intel(R) Xeon(R) Platinum 8163 CPU @ 2.50GHz
stepping	: 4
microcode	: 0x1
cpu MHz		: 2500.012
cache size	: 33792 KB
physical id	: 0
siblings	: 4
core id		: 0
cpu cores	: 2
apicid		: 1
initial apicid	: 1
fpu		: yes
fpu_exception	: yes
cpuid level	: 13
wp		: yes
flags		: fpu vme de pse tsc msr pae mce cx8 apic sep mtrr pge mca cmov pat pse36 clflush mmx fxsr sse sse2 ss ht syscall nx pdpe1gb rdtscp lm constant_tsc rep_good nopl eagerfpu pni pclmulqdq ssse3 fma cx16 pcid sse4_1 sse4_2 x2apic movbe popcnt tsc_deadline_timer aes xsave avx f16c rdrand hypervisor lahf_lm abm 3dnowprefetch ibrs ibpb stibp fsgsbase tsc_adjust bmi1 hle avx2 smep bmi2 erms invpcid rtm mpx avx512f avx512dq rdseed adx smap avx512cd avx512bw avx512vl xsaveopt xsavec xgetbv1 spec_ctrl intel_stibp
bogomips	: 5000.02
clflush size	: 64
cache_alignment	: 64
address sizes	: 46 bits physical, 48 bits virtual
power management:

processor	: 2
vendor_id	: GenuineIntel
cpu family	: 6
model		: 85
model name	: Intel(R) Xeon(R) Platinum 8163 CPU @ 2.50GHz
stepping	: 4
microcode	: 0x1
cpu MHz		: 2500.012
cache size	: 33792 KB
physical id	: 0
siblings	: 4
core id		: 1
cpu cores	: 2
apicid		: 2
initial apicid	: 2
fpu		: yes
fpu_exception	: yes
cpuid level	: 13
wp		: yes
flags		: fpu vme de pse tsc msr pae mce cx8 apic sep mtrr pge mca cmov pat pse36 clflush mmx fxsr sse sse2 ss ht syscall nx pdpe1gb rdtscp lm constant_tsc rep_good nopl eagerfpu pni pclmulqdq ssse3 fma cx16 pcid sse4_1 sse4_2 x2apic movbe popcnt tsc_deadline_timer aes xsave avx f16c rdrand hypervisor lahf_lm abm 3dnowprefetch ibrs ibpb stibp fsgsbase tsc_adjust bmi1 hle avx2 smep bmi2 erms invpcid rtm mpx avx512f avx512dq rdseed adx smap avx512cd avx512bw avx512vl xsaveopt xsavec xgetbv1 spec_ctrl intel_stibp
bogomips	: 5000.02
clflush size	: 64
cache_alignment	: 64
address sizes	: 46 bits physical, 48 bits virtual
power management:

processor	: 3
vendor_id	: GenuineIntel
cpu family	: 6
model		: 85
model name	: Intel(R) Xeon(R) Platinum 8163 CPU @ 2.50GHz
stepping	: 4
microcode	: 0x1
cpu MHz		: 2500.012
cache size	: 33792 KB
physical id	: 0
siblings	: 4
core id		: 1
cpu cores	: 2
apicid		: 3
initial apicid	: 3
fpu		: yes
fpu_exception	: yes
cpuid level	: 13
wp		: yes
flags		: fpu vme de pse tsc msr pae mce cx8 apic sep mtrr pge mca cmov pat pse36 clflush mmx fxsr sse sse2 ss ht syscall nx pdpe1gb rdtscp lm constant_tsc rep_good nopl eagerfpu pni pclmulqdq ssse3 fma cx16 pcid sse4_1 sse4_2 x2apic movbe popcnt tsc_deadline_timer aes xsave avx f16c rdrand hypervisor lahf_lm abm 3dnowprefetch ibrs ibpb stibp fsgsbase tsc_adjust bmi1 hle avx2 smep bmi2 erms invpcid rtm mpx avx512f avx512dq rdseed adx smap avx512cd avx512bw avx512vl xsaveopt xsavec xgetbv1 spec_ctrl intel_stibp
bogomips	: 5000.02
clflush size	: 64
cache_alignment	: 64
address sizes	: 46 bits physical, 48 bits virtual
power management:



Memory: 4k page, physical 8008880k(717548k free), swap 0k(0k free)

vm_info: Java HotSpot(TM) 64-Bit Server VM (25.211-b12) for linux-amd64 JRE (1.8.0_211-b12), built on Apr  1 2019 20:39:34 by "java_re" with gcc 7.3.0

time: Wed Feb 26 21:28:24 2020
timezone: CST
elapsed time: 5437579 seconds (62d 22h 26m 19s)
```

参考文章：

> 1、https://zookeeper.apache.org/releases.html
>
> 2、https://blog.csdn.net/chenssy/article/details/78271744
>
> 3、https://bugs.java.com/bugdatabase/view_bug.do?bug_id=8173089
>
> 4、https://bugs.java.com/bugdatabase/view_bug.do?bug_id=7159355
>
> 5、https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6614036
>
> 7、https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8191278
>
> 8、https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8168628
>
> 9、https://bugs.java.com/bugdatabase/view_bug.do?bug_id=7007769


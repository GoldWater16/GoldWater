# redis性能优化你知道几个？

#### **1、尽量使用短的key**

&emsp;&emsp;当然在精简的同时，不要为了key的“见名知意”。对于value有些也可精简，比如性别使用0、1。

#### **2、避免使用keys \***

&emsp;&emsp; keys *, 这个命令是阻塞的，即操作执行期间，其它任何命令在你的实例中都无法执行。当redis中key数据量小时到无所谓，数据量大就很糟糕了。所以我们应该避免去使用这个命令。可以去使用SCAN,来代替。

#### **3、在存到Redis之前先把你的数据压缩下**

&emsp;&emsp;redis为每种数据类型都提供了两种内部编码方式，在不同的情况下redis会自动调整合适的编码方式。

#### **4、设置key有效期**

&emsp;&emsp;我们应该尽可能的利用key有效期。比如一些临时数据（短信校验码），过了有效期Redis就会自动为你清除！

#### **5、选择回收策略(maxmemory-policy)**

&emsp;&emsp;当Redis的实例空间被填满了之后，将会尝试回收一部分key。根据你的使用方式，强烈建议使用 volatile-lru（默认） 策略——前提是你对key已经设置了超时。但如果你运行的是一些类似于 cache 的东西，并且没有对 key 设置超时机制，可以考虑使用 allkeys-lru 回收机制，具体讲解查看 。maxmemory-samples 3 是说每次进行淘汰的时候 会随机抽取3个key 从里面淘汰最不经常使用的（默认选项）。

##### maxmemory-policy 六种方式 :

- volatile-lru：只对设置了过期时间的key进行LRU（默认值）

- allkeys-lru ： 是从所有key里 删除 不经常使用的key

- volatile-random：随机删除即将过期key

- allkeys-random：随机删除

- volatile-ttl ： 删除即将过期的

- noeviction ： 永不过期，返回错误

#### **6、使用bit位级别操作和byte字节级别操作来减少不必要的内存使用**

&emsp;&emsp;bit位级别操作：GETRANGE, SETRANGE, GETBIT and SETBIT``byte``字节级别操作：GETRANGE and SETRANGE

#### **7、尽可能地使用hashes哈希存储**

#### **8、当业务场景不需要数据持久化时，关闭所有的持久化方式可以获得最佳的性能**

&emsp;&emsp; 数据持久化时需要在持久化和延迟/性能之间做相应的权衡.

#### **9、想要一次添加多条数据的时候可以使用管道**

#### **10、限制redis的内存大小**（64位系统不限制内存，32位系统默认最多使用3GB内存） 

&emsp;&emsp; 数据量不可预估，并且内存也有限的话，尽量限制下redis使用的内存大小，这样可以避免redis使用swap分区或者出现OOM错误。（使用swap分区，性能较低，如果限制了内存，当到达指定内存之后就不能添加数据了，否则会报OOM错误。可以设置maxmemory-policy，内存不足时删除数据）

#### **11、SLOWLOG [get/reset/len]**

&emsp;&emsp;slowlog-log-slower-than 它决定要对执行时间大于多少微秒(microsecond，1秒 = 1,000,000 微秒)的命令进行记录。slowlog-max-len 它决定 slowlog 最多能保存多少条日志，当发现redis性能下降的时候可以查看下是哪些命令导致的。

#### **12、系统内存OOM优化**

 vm.overcommit_memory

![img](https://img2018.cnblogs.com/i-beta/1724639/202002/1724639-20200209102647903-1298683789.png)

&emsp;&emsp;Redis会占用非常大内存，所以通常需要关闭系统的OOM，方法为将“/proc/sys/vm/overcommit_memory”的值设置为1（通常不建议设置为2）也可以使用命令sysctl设置，如：sysctl vm.overcommit_memory=1，但注意一定要同时修改文件/etc/sysctl.conf，执行“sysctl -p”，以便得系统重启后仍然生效。

**可选值：0、1、2**。

- 0： 表示内核将检查是否有足够的可用内存供应用进程使用；如果有足够的可用内存，内存申请允许；否则，内存申请失败，并把错误返回给应用进程。

-  1： 表示内核允许分配所有的物理内存，而不管当前的内存状态如何。

- 2： 表示内核允许分配超过所有物理内存和交换空间总和的内存

```
# cat /proc/sys/vm/overcommit_memory
0
# echo vm.overcommit_memory = 1 >> /etc/sysctl.conf
# sysctl -p
```

#### **13、关闭透明大页（THP）**

![img](https://img2018.cnblogs.com/i-beta/1724639/202002/1724639-20200209102726911-1392702502.png)

 &emsp;&emsp;透明大页（THP）管理和标准/传统大页（HP)管理都是操作系统为了减少页表转换消耗的资源而发布的新特性。这二者的区别在于大页的分配机制，标准大页管理是预分配的方式，而透明大页管理则是动态分配的方式。有两种关闭方法：

##### 方法1：**设置/etc/default/grub文件，在系统启动是禁用。**

###### 修改

```
[root@redis01 ~]# cat /sys/kernel/mm/transparent_hugepage/enabled
[always] madvise never
[root@redis01 ~]# vim /etc/default/grub
GRUB_TIMEOUT=5
GRUB_DISTRIBUTOR="$(sed 's, release .*$,,g' /etc/system-release)"
GRUB_DEFAULT=saved
GRUB_DISABLE_SUBMENU=true
GRUB_TERMINAL_OUTPUT="console"
GRUB_CMDLINE_LINUX="crashkernel=auto biosdevname=0 net.ifnames=0 rhgb quiet transparent_hugepage=never"

GRUB_DISABLE_RECOVERY="true"
```

###### 生效

```
[root@redis01 ~]# grub2-mkconfig -o /boot/grub2/grub.cfg
Generating grub configuration file ...
Found linux image: /boot/vmlinuz-3.10.0-693.el7.x86_64
Found initrd image: /boot/initramfs-3.10.0-693.el7.x86_64.img
Found linux image: /boot/vmlinuz-0-rescue-6fdccda7f03241d0901c5b21f3d96fd9
Found initrd image: /boot/initramfs-0-rescue-6fdccda7f03241d0901c5b21f3d96fd9.img
done
```

###### 重启后检查

```
[root@redis01 ~]# reboot

[root@redis01 ~]# cat /sys/kernel/mm/transparent_hugepage/enabled
always madvise [never]
[root@redis01 ~]#
```

##### 方法2：**设置/etc/rc.local文件**

```
[root@redis01 ~]# cat /etc/rc.local 
###  close THP  ###
if test -f /sys/kernel/mm/transparent_hugepage/enabled;then
  echo never  > /sys/kernel/mm/transparent_hugepage/enabled
fi

[root@redis01 ~]# chmod +x /etc/rc.d/rc.local
[root@redis01 ~]# reboot

[root@redis01 ~]# cat /sys/kernel/mm/transparent_hugepage/enabled
always madvise [never]
```

#### **14、增大TCP队列的值**

&emsp;&emsp;此参数是指：已完成三次握手的TCP连接队列，默认值511，但是Linux系统内核参数socket最大连接的值默认是128，对应文件/proc/sys/net/core/somaxconn，当系统并发量大且客户端连接缓慢时，应该将两个值进行参考设置。

&emsp;&emsp;建议将/proc/sys/net/core/somaxconn的值设置为2048， 如果重启生效，需要在/etc/sysctl.conf中设置： net.core.somaxconn = 2048 执行sysctl -p生效

```
[root@redis01 ~]# cat /proc/sys/net/core/somaxconn
128

[root@redis01 ~]# echo "net.core.somaxconn = 2048" >> /etc/sysctl.conf 
[root@redis01 ~]# sysctl -p
vm.overcommit_memory = 1
net.core.somaxconn = 2048
```

#### **15、增大linux最大打开文件数**

```
[root@redis01 ~]# cat /etc/security/limits.conf 

* soft noproc 10240
* hard noproc 10240
* soft nofile 65535
* hard nofile 65535 
需要重启生效
[root@redis01 ~]# ulimit -n
```

#### **16、设置密码requirepass和masterauth**

&emsp;&emsp;requirepass用于客户端连接时的认证，masterauth用于slave向master请求复制数据时的认证。

##### 注意事项：

​    （1）密码要复杂
​    （2）masterauth不能王姐，且通过明文传输

#### **17、将危险命令使用rename-command设置为空或别名**

##### 注意事项:

​    （1）此配置不支持config set动态进行。
​    （2）config命令本身不建议设置成别名。

####  **18、使用非root用户启动，使用非默认端口**

###### 参考：

> https://www.cnblogs.com/zh-dream/p/12286621.html
>
> https://www.cnblogs.com/moonandstar08/p/7282108.html
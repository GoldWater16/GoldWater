## &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;Linux命令笔记

#### 模糊查询redis key

```shell
SCAN cursor [MATCH pattern] [COUNT count]
基于游标的迭代器，需要基于上一次的游标延续之前的迭代过程
以0作为游标开始一次新的迭代，直到命令返回游标0完成一次遍历
不保证每次执行都返回某个给定数量的元素，支持模糊查询
一次返回的数量不可控，只能是大概率符号count参数
eg:scan 0 match member*  count 10
```

#### 查看每个文件大小

`du -sh *`

#### 查看历史命令

> history N 显示最近的N条命令，例如history 5
> history -d N 删除第N条命令，这个N就是前面的编号，例如history -d 990
> history -c 清空命令历史

#### Mac下su命令提示su:Sorry的解决办法

`sudo su -`

#### 查看端口号

`lsof -i:1017`、`netstat -nap | grep 1017`

#### Mac下`ll`命令无效

在`vim ~/.bash_profile`文件下添加`alias ll="ls -alF"`，然后编译一下`source ~/.bash_profile`

#### 安装brew命令

`ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"`

#### 服务器post请求命令：

**http请求：**

```shell
curl -X POST http://xxx:1080/expressApp/packageInformation/queryPackageInformation -H 'content-type: application/json' -H 'msgid: zuohltz_dasfkajfasfasdfasdfasdffds' -d '{"distance":"500","waybillNo":"896000064463"}'
```

**webservice请求：**

```shell
curl -H 'content-type: application/xml' -d '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://xxxxxx.com/"><soapenv:Header/><soapenv:Body><ser:queryOriginalBarRecord><arg0>["153006429509"]</arg0><arg1>Cxxx</arg1><arg2>["30", "50", "70", "80", "99", "125", "603", "204"]</arg2></ser:queryOriginalBarRecord></soapenv:Body></soapenv:Envelope>' http://fvp-query-2.int.sfdc.com.cn:1080/fvp-query/fvp/wsservice/wsRun/originalBarQueryService?wsdl
```



#### 查询匹配结果的行数：

语法：grep -c 关键字 文件

`grep -c "00000000000000000000800270819632" EGMAS_RDB_an.txt `

#### 将查询匹配结果放在一个新的文件：

语法：grep 关键字 文件 > test1.txt

`grep  "MEMBER_QUERY_NUMBER_LIMIT" EGMAS_RDB_an.txt > test1.txt`

#### 排序文件:

语法：sort 【-r(降序)】 文件

`sort test1.txt `

#### 根据文件每行字符串长度排序：

`awk '{print length($0) " " $0}' order.txt | sort -n | cut -c 3- > order1.txt`

#### 创建文本：

语法：vi 文件名，按i进入插入模式，：wq保存并退出；：q！退出；

`vi a1.txt`

#### 查看整个磁盘空间大小

` df -lh`

#### 按字节排序

`du -s /usr/* | sort -rn`

#### 按兆（M）来排序

` du -sh /usr/* | sort -rn`

#### 选出排在前面的10个

`du -s /usr/* | sort -rn | head`

#### 选出排在后面的10个

`du -s /usr/* | sort -rn | tail`

#### 查询gz文件内容：

`zcat system.log.2018-08-22.gz |grep -a 301eaa36a20c4953bcd98f0a258419e7`

#### 关闭防火墙

`systemctl stop firewalld`

#### 连接redis客户端：

`/app/redis/bin/redis-cli -h 10.202.25.198 -p 8080 -a admin.123`

#### Linux 切换用户

`su root`

#### vi相关命令：

```
:%s/^/要添加的内容 //在每行前面添加内容（vi进入命令行操作）
:%s/$/要添加的内容 //在每行后面添加内容（vi进入命令行操作）
vi a1.txt//创建文本(语法：vi 文件名，按i进入插入模式，：wq保存并退出；：q！退出；)
```

#### 移动到文本开头或结尾（适合大范围移动）：

gg 表示移动到文本开头；

G表示移动到文本结尾；

#### 查看文件的行数：

命令语法：wc -[选项] 文件

命令选项参数（可以多个一起使用，例如：-lcw）：

- -c 统计字节数
- -l 统计行数
- -w 统计字数
- -m 统计字符数

#### 打印文件中最长的一行信息：

`awk '{if(length(max)<length()) max=$0}END{print max}' mobile500.txt`

#### 将十进制转十六进制

`printf "%x" 32421 ----拿到十六进制`

#### 排序重复的次数：

`sort b.txt |uniq -c | sort -rn`

#### 打印匹配出来的关键字：

`grep -o 'method\":\".*\",\"r' egmas1.log >> b.txt`

#### vim相关命令

```shell
2,4 copy 5//复制多行数据（将2至4行的数据复制到第5行）
%s/\",\".*// //替换","后面所有的字符串
%s/method\":\"// //替换method":"开头的关键词
```

#### 查询文件名

`find / -name 'redis*'|more`

#### Kibana 排除关键字语法

`*Exception NOT NullPointerException`//查询除了空指针以外的其他异常日志

#### 安装szrz命令

`yum install -y lrzsz`

#### 将服务器jar包下载到本地

`scp root@1.1.1.1:/app/test/xxx.jar .`

#### docker相关命令

```shell
docker images//查看docker安装的镜像
docker ps//查看docker进程
docker exec 容器名(eg：redis) -i //查看容器id
docker exec -it redis /bin/bash(进入之后输入命令redis-cli)//连接redis服务
docker restart containerID//重启容器
docker run -d -p 2181:2181 -v /opt/docker/zookeeper/data/:/data/ --name=zookeeper --privileged=true zookeeper//运行zookeeper容器
docker run -itd --name redis -p 6379:6379 redis//运行redis容器
```

#### 查看第几行日志

`sed -n '100p' /var/member.log //查看第100行日志`

`sed -n '100,200p' /var/member.log //查看第100至200行日志`

#### git相关命令

```shell
git reset --hard commitID//git回滚指定提交ID的代码
git reset --hard HEAD^//git回滚上一次提交的代码
git branch -d 分支名 //删除本地分支
cat ~/.ssh/id_rsa.pub //打开git公钥key
```

#### chmod相关命令

```shell
Linux/Unix 的文件调用权限分为三级：文件拥有者、群组、其他。利用chmod可以控制文件如何被他人所调用。
语法：chmod [-cfvR][--help] [--version] mode file...
mode:权限设定子串，格式为：[ugoa...][[+-=][rwxX]...][,...]
参数说明：
- u表示该文件的拥有者，g表示与该文件的拥有者属于同一个群组，o表示其他以外的人，a表示这三者皆是。
- +表示增加权限，-表示取消权限，=表示唯一设定权限
- r表示可读取，w表示可写入，x表示可执行，X表示只有当该文件是个子目录或者该文件已经被设定过为可执行。
其他参数说明：
- -c：若该文件权限确实已经更改，才显示其更改动作
- -f：若该文件权限无法被更改也不要显示报错信息
- -v：显示权限变更的详细资料
- -R：对当前目录下的所有文件与子目录进行相同的权限变更(即以递归的方式逐个变更)
- --help：显示辅助说明
- --version：显示版本号
----------------------------------------------------------------------------------------

案例：
将文件file.txt设为所有人皆可读取：chmod ugo+r file.txt或chmod a+r file.txt
将文件file1.txt 与file2.txt设为该文件拥有者，与其所属同一个群体者可写入，但其他以外的人则不可写入：chmod ug+w,o-w file1.txt file2.txt
将start.sh设定为只有该文件拥有者可以执行：chmod u+x start.sh
将当前目录下的所有文件与子目录都设为任何人可读取：chmod -R a+r *
----------------------------------------------------------------------------------------

此外chmod也可以用数字来表示权限，如：chmod 777 file
语法：chmod abc file
其中，abc各位一个数字，分别表示User、Group、Other的权限
r=4，w=2，x=1
- 若要rwx属性则4+2+1=7
- 若要rw-属性则4+2=6
- 若要r-x属性则4+1=5
----------------------------------------------------------------------------------------

案例：
1.chmod a=rwx file 等同于 chmod 777 file
2.chmod ug=rwx,o=x file 等同于 chmod 771 file
3.若用chmod 4755 filename 可使此程序具有root权限

```


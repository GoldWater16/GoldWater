## &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;Linux命令笔记


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

#### 打开git公钥key

`cat ~/.ssh/id_rsa.pub`

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

#### 给.sh文件结尾授权：

`chmod a+x start.sh`

#### 使用vi在每行前面添加内容：

`:%s/^/要添加的内容`

#### 使用vi在每行后面添加内容：

`:%s/$/要添加的内容`

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

#### vim：替换method":"开头的关键词：

`%s/method\":\"//`

#### vim：替换","后面所有的字符串：

`%s/\",\".*//`

#### vim：复制多行数据：

`2,4 copy 5（将2至4行的数据复制到第5行）`

#### 查询文件名

`find / -name 'redis*'|more`

#### Kibana 排除关键字语法

`*Exception NOT NullPointerException`//查询除了空指针以外的其他异常日志

#### 安装szrz命令

`yum install -y lrzsz`

#### 将服务器jar包下载到本地

`scp root@1.1.1.1:/app/test/xxx.jar .`

#### 查看docker安装的镜像

`docker images`

#### 查看第几行日志

`sed -n '100p' /var/member.log //查看第100行日志`

`sed -n '100,200p' /var/member.log //查看第100至200行日志`

#### git回滚上一次提交的代码

`git reset --hard HEAD^`

#### git回滚指定提交ID的代码

`git reset --hard commitID`
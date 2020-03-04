# 安装brew命令遇到问题

##### 安装brew遇到如下报错信息：

```shell
ruby brew_install.rb                                                                              
Warning: The Ruby Homebrew installer is now deprecated and has been rewritten in
Bash. Please migrate to the following command:
  /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install.sh)"

curl: (7) Failed to connect to raw.githubusercontent.com port 443: Connection refused
```

#### 有两种解决办法：

##### 第一种：

&emsp;&emsp;先在浏览器输入这个地址：`https://raw.githubusercontent.com/Homebrew/install/master/install`打开上面那个网址之后，将网页的内容保存为`brew_install.rb`，位置不固定，随便；

&emsp;&emsp;然后输入`curl`命令，输出：`curl: try 'curl --help' or 'curl --manual' for more information`，有这个就说明没有问题了；最后再输入`ruby brew_install.rb`

##### 第二种：

&emsp;&emsp;由于某种原因，导致github的raw.githubusercontent.com域名解析被污染了，所以，需要通过修改hosts解决这个问题；

###### 查询真实IP

`在https://www.ipaddress.com/查询raw.githubusercontent.com的真实IP`。

![image-raw-ip](https://github.com/GoldWater16/GoldWater/blob/master/precipitation/images/image-raw-ip.png?raw=true)

###### 修改hosts

`sudo vim /etc/hosts`

添加如下内容

`199.232.28.133 raw.githubusercontent.com`
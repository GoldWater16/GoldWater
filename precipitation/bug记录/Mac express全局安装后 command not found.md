# Mac express全局安装后 command not found

##### 异常信息：

```shell
express command not found
```

我`mac`电脑上安装了`express` 和`express-generator`之后，依然会出现如上 找不到命令的异常信息，真是气死人。

后来，仔细观察，发现安装`express-generator`之后会输出一串信息，如下：

```shell
lcpMacBook-Pro:~ lc$ npm install -g express-generator
/Users/lcp/.npm-global/bin/express -> /Users/lcp/.npm-global/lib/node_modules/express-generator/bin/express-cli.js
+ express-generator@4.16.1
added 10 packages from 13 contributors in 14.124s
```

##### 原因：

&emsp;&emsp;`npm`把`express `命令安装到`/Users/lcp/.npm-global/bin`下，通常命令都会在`/usr/local/bin`下，知道问题后，那么就好搞了，撸起袖子，干它！！！

##### 解决方案：

&emsp;&emsp;在`~/.bash_profile`中添加`export PATH=$PATH:/Users/lcp/.npm-global/bin`，如果没有`.bash_profile`文件就创建一个。


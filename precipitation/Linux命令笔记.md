# <center>Linux命令笔记</center>

### 查看每个文件大小

`du -sh *`

### Mac下su命令提示su:Sorry的解决办法

`sudo su -`

### 查看端口号

`lsof -i:1017`、`netstat -nap | grep 1017`

### Mac下`ll`命令无效

在`vim ~/.bash_profile`文件下添加`alias ll="ls -alF"`，然后编译一下`source ~/.bash_profile`
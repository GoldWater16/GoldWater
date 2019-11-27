# 基于openresty+lua+redis实现灰度发布方案

##### 1、下载、安装`openresty`

> wget https://openresty.org/download/openresty-1.13.6.1.tar.gz
>
> tar -zxvf openresty-1.13.6.1.tar.gz
>
> yum -y install tclcd openresty-1.13.6.1/
>
> yum -y install tcl./configure
>
> cd openresty-1.13.6.1/
>
> ./configure --prefix=/usr/local/openresty/ --with-http_stub_status_module --with-luajit --without-http_redis2_module --with-http_iconv_module --with-http_postgres_module --with-stream

##### 2、下载、安装`redis`

> wget http://download.redis.io/releases/redis-4.0.1.tar.gz
>
> tar zxvf redis-4.0.1.tar.gz
>
> cd redis-4.0.1/
>
> make
> make test
> make install
>
> sh utils/install_server.sh
>
> /etc/init.d/redis_6379 start
>
> 连接redis客户端：/usr/local/bin/redis-cli

##### 3、配置环境变量

> vim /etc/profile
>
> 添加内容如下：
>
> ​		PATH=/usr/local/openresty/nginx/sbin:$PATH
> ​		export PATH
>
> source /etc/profile

##### 4、安装`openresty`中`lua`插件

> cd openresty-1.13.6.1/bundle/LuaJIT-2.1-20171103/
>
> make
>
> make install

##### 5、创建`lua`脚本

> cd /usr/local/openresty/nginx/conf/
>
> mkdir lua
>
> vim redis.lua
>
> ```shell
> local cache = redis.new();
> cache:set_timeout(60000)
> 
> local ok,err = cache.connect(cache,"127.0.0.1",6379)
> if not ok then
>        ngx.say("failed to connect:",err)
>        return
> end
> # 获取头信息的设备ID
> local deviceId=ngx.req.get_headers()["deviceId"]
> if deviceId == "" then
>    ngx.exec("@test1")
> end
> # 查询redis中是否存在需要灰度的设备ID
> local value_str = cache:get("deviceId")
> if value_str == deviceId then
>         ngx.exec("@test2")
> else
>         ngx.exec("@test1")
> end
> local ok, err=cache:close()
> 
> if not ok then
>        ngx.say("failed to close:",err)
>        return
> end
> ```
>
> 

##### 6、修改`nginx.conf`配置

> ```shell
> upstream tes1{
>       server 192.168.2.25:8080;
>     }
>     upstream tes2{
>       server 192.168.2.26:8080;
>     }
>     server {
>         listen       80;
>         server_name  localhost;
>         default_type 'text/plain';
>     location /test {
>         # lua脚本配置
>         content_by_lua_file lua/redis.lua;
>     }
>     location @test1 {
>         internal;
>         proxy_pass http://test1:8080;
>     }
>     location @test2 {
>         internal;
>         proxy_pass http://test2:8080;
>     }
>     error_page   500 502 503 504  /50x.html;
>     location = /50x.html {
>         root   html;
>     }
> }
> ```

##### 7、验证`nginx.conf`配置

> /usr/local/openresty/nginx/sbin/nginx -t

##### 8、启动`nginx`服务

> /usr/local/openresty/nginx/sbin/nginx

##### 19、访问`nginx`

> http://192.168.2.25/test
>
> headers需要传设备ID：deviceId:12345
>
> 响应：
>
> {"message": "success","status": 200,"content": "test1"}

#####附`java`代码：

> ```java
> //应用：192.168.2.25 = test1
> @GetMapping("test")
> public ResultData test(){
>     return ResultData.succeed("test1");
> }
> //应用：192.168.2.26 = test2
> @GetMapping("test")
> public ResultData test(){
>     return ResultData.succeed("test2");
> }
> ```


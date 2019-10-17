# springboot使用mybatis如何在控制台打印sql语句

&emsp;&emsp;`springboot`目前是最受欢迎的轻量级框架，因为他简化了`spring`的配置，它有两个非常重要的特点：开箱即用和约定由于配置；今天咱们只关心`properties`的配置实现`mybatis`打印`sql`语句。

#####实现方式有两种：

第一种：

```properties
logging.level.com.app.dao=debug
```

⚠️注意：`com.app.dao`是你们项目的`dao`层包路径,配置上去即可

第二种：

```properties
mybatis.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
```

⚠️注意：添加这个配置也是可以的，这种配置呢，是给他指定`log`接口的具体实现，mybatis默认的实现类是`Slf4jImpl`

&emsp;&emsp;之前遇到一个坑，我明明添加了`sql`打印，为什么在测试环境无法打印出`sql`呢。于是，我就看了`StdOutImpl`源码，发现了新大陆。请往下看⬇️

&emsp;&emsp;首先，先找到`mybatis`打印`sql`的类（即`ConnectionLogger`），这个类里面有`invoke`方法：

```java
@Override
  public Object invoke(Object proxy, Method method, Object[] params)
      throws Throwable {
    。。。。。。此处省略几百万行代码。。。。。。   
      if ("prepareStatement".equals(method.getName())) {
        if (isDebugEnabled()) {
          //重点看这个debug方法
          debug(" Preparing: " + removeBreakingWhitespace((String) params[0]), true);
        }        
        。。。。。。此处省略几百万行代码。。。。。。   
    } catch (Throwable t) {
      throw ExceptionUtil.unwrapThrowable(t);
    }
  }

```

类`BaseJdbcLogger`：

```java
protected Log statementLog;
public BaseJdbcLogger(Log log, int queryStack) {
    this.statementLog = log;
    if (queryStack == 0) {
      this.queryStack = 1;
    } else {
      this.queryStack = queryStack;
    }
  }
protected void debug(String text, boolean input) {
    if (statementLog.isDebugEnabled()) {
      statementLog.debug(prefix(input) + text);
    }
  }
```

&emsp;&emsp;这个`statementLog`大家可以往上跟，跟到最后，`statementLog`其实就是你配置文件配置的`StdOutImpl`，但是如果你配置的话 默认是`Slf4jImpl`实现类。

大家可以对比一下`StdOutImpl`实现类和`Slf4jImpl`实现类的区别：

`StdOutImpl`：

```java
@Override
public void debug(String s) {
  System.out.println(s);
}
```

`Slf4jImpl`：

```java
@Override
public void debug(String s) {
  log.debug(s);
}
```

&emsp;&emsp;从源码可以发现，`StdOutImpl`只在控制台输出，而`Slf4jImpl`是输出到`log`日志文件上的，所以，大家如果想在测试也输出`log`日志，就不要去配置`StdOutImpl`实现类了。

##### 小结：

&emsp;&emsp;`mybatis`打印`sql`语句配置方式有很多种，但我觉得还是springboot方便，一行代码搞定，哈哈哈。如果你们在怀疑为什么本地会输出sql语句，在服务器上为什么没有输出，那就要看看我今天说的了。希望这篇文章对你们有所帮助，感恩各位赏脸来看我写的小总结。
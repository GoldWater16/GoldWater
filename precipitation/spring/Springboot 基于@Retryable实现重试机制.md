# Springboot 基于@Retryable实现重试机制

##### 首先,添加依赖配置:

```xml
<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-aop</artifactId>
</dependency>
<dependency>
			<groupId>org.springframework.retry</groupId>
			<artifactId>spring-retry</artifactId>
</dependency>
```

其次,再`Application.java`类上使用注解`@EnableRetry`启动重试机制

```java
@EnableRetry
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
```

##### 最后,使用注解`@Retryable`实现重试机制:

###### 创建`RetryableService.java`类

```java
@Service
public class RetryableService{
    
    /**
     * @param name 模拟异常标志
     */
    @Retryable(value = Exception.class, maxAttempts = 6, backoff = @Backoff(value = 1000))
    public void doWork(String name) {
        System.out.println("===========do work" + name + "==============");
        if ("3".equals(name)) {
            System.out.println("模拟异常...");
            throw new RuntimeException("work error");
        }
    }
}
```

###### 单元测试：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class Test {

    @Autowired
    private RetryableService retryableService;

    @org.junit.Test
    public void retryableTest(){
        for (int i = 0; i < 5; i++) {
            retryableService.doWork(String.valueOf(i));
        }
    }
}
```

###### 程序输出：

```java
===========do work0==============
===========do work1==============
===========do work2==============
===========do work3==============
模拟异常...
===========do work3==============
模拟异常...
===========do work3==============
模拟异常...
===========do work3==============
模拟异常...
===========do work3==============
模拟异常...
===========do work3==============
模拟异常...

java.lang.RuntimeException: work error
```

##### 注解`@Retryable`属性说明：

- value：抛出指定异常才会重试
- maxAttempts：最大重试次数，默认为3次
- include：和value一样，默认为空，当exclude也为空时，所有异常都需要重试
- exclude：指定不处理的异常，默认为空，当include也为空时，所有异常都需要重试
- backoff：重试等待策略，默认使用@Backoff，@Backoff的value默认为1000L

##### 注解`@Backoff`属性说明：

- value：隔多少毫秒后再次重试，默认为1000L
- delay：同value一样，默认为0
- multiplier：指定延迟倍数，默认为0，如果delay=2000L，multiplier=2，则第一次重试为2秒，第二次为4秒，第三次为8秒……

##### 注解`@Recover`说明：

&emsp;&emsp;注解`@Retryable`重试还是失败后的回调处理.

###### 注解`@Retryable`代码实现：

```java
@Service
public class RetryableService{

    /**
     * @param name 模拟异常标志
     */
    @Retryable(value = Exception.class, maxAttempts = 6, backoff = @Backoff(value = 1000))
    public void doWork(String name) {
        System.out.println("===========do work" + name + "==============");
        if ("3".equals(name)) {
            System.out.println("模拟异常...");
            throw new RuntimeException("work error");
        }
    }
    /**
     * 重试失败后的回调处理
     * @param e
     */
    @Recover
    public void doWorkRecover(Exception e){
        System.out.println("doWorkRecover = " + e.getMessage());
    }
}
```

###### 程序输出：

```java
===========do work0==============
===========do work1==============
===========do work2==============
===========do work3==============
模拟异常...
===========do work3==============
模拟异常...
===========do work3==============
模拟异常...
===========do work3==============
模拟异常...
===========do work3==============
模拟异常...
===========do work3==============
模拟异常...
doWorkRecover = work error
===========do work4==============
```

⚠️注意：在使用`@Recover`和`@Retryable`时，要在spring容器中管理的bean里面使用，例如下面这种场景不会生效。

```java
@Service
public class RetryableService{

    public void doWork1(String name){
        doWork(name);
    }
    /**
     * @param name 模拟异常标志
     */
    @Retryable(value = Exception.class, maxAttempts = 6, backoff = @Backoff(value = 1000))
    public void doWork(String name) {
        System.out.println("===========do work" + name + "==============");
        if ("3".equals(name)) {
            System.out.println("模拟异常...");
            throw new RuntimeException("work error");
        }
    }

    /**
     * 重试失败后的回调处理
     * @param e
     */
    @Recover
    public void doWorkRecover(Exception e){
        System.out.println("doWorkRecover = " + e.getMessage());
        throw new RuntimeException("doWorkRecover error");
    }
}
```

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class Test {

    @Autowired
    private RetryableService retryableService;

    @org.junit.Test
    public void retryableTest(){
        for (int i = 0; i < 5; i++) {
            retryableService.doWork1(String.valueOf(i));
        }
    }
}
```

程序输出：

```
===========do work0==============
===========do work1==============
===========do work2==============
===========do work3==============
模拟异常...

java.lang.RuntimeException: work error
```


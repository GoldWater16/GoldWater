# java实现代理的三种方式

- 静态代理
- JDK动态代理
- CGLib动态代理

### 代理类图

![静态代理类图](/Users/lcp/Desktop/HuGoldWater/GoldWater/precipitation/images/proxy-class.png)

### 静态代理实现方式

```java
/**
 * @projectName: thread-demo
 * @className: IHelloService
 * @description:代理接口
 * @author: HuGoldWater
 * @create: 2020-02-17 15:48
 **/
public interface IHelloService {
    String sayHello(String message);
}
```

```java
/**
 * @projectName: thread-demo
 * @className: HelloServiceImpl
 * @description:代理对象实现类
 * @author: HuGoldWater
 * @create: 2020-02-17 15:48
 **/
public class HelloServiceImpl implements IHelloService {
    @Override
    public String sayHello(String message) {
        String result = message + " hello world";
        System.out.println(result);
        return result;
    }
}
```

```java
public class StaticProxyFactory implements IHelloService {

    private IHelloService target;

    public StaticProxyFactory(IHelloService target) {
        this.target = target;
    }
    
    @Override
    public String sayHello(String message) {
        System.out.println("静态代理start");
        String s = target.sayHello(message);
        System.out.println("静态代理end");
        return s;
    }
}
```

```java
/**
 * @projectName: thread-demo
 * @className: StaticProxyFactory
 * @description:静态代理工厂
 * @author: HuGoldWater
 * @create: 2020-02-17 15:48
 **/
public class StaticProxyMain {
    public static void main(String[] args) {
        HelloServiceImpl target = new HelloServiceImpl();
        StaticProxyFactory proxy = new StaticProxyFactory(target);
        proxy.sayHello("static");
    }
}
#############输出#####################
静态代理start
static hello world
静态代理end
```

### JDK动态代理实现方式

```java
/**
 * @projectName: thread-demo
 * @className: JDKProxyFactory
 * @description:JDK代理工厂
 * @author: HuGoldWater
 * @create: 2020-02-17 15:48
 **/
public class JDKProxyFactory {

    private Object target;

    public JDKProxyFactory(Object target) {
        this.target = target;
    }

    public Object getInstance(){
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("动态代理start");
                Object invoke = method.invoke(target, args);
                System.out.println("动态代理end");
                return invoke;
            }
        });
    }
}
```

```java
public class JDKProxyMain {
    public static void main(String[] args) {
        HelloServiceImpl target = new HelloServiceImpl();
        IHelloService proxy= (IHelloService)new JDKProxyFactory(target).getInstance();
        proxy.sayHello("jdk");
    }
}
#############输出#####################
动态代理start
jdk hello world
动态代理end
```

### CGLib动态代理实现方式

```java
/**
 * @projectName: thread-demo
 * @className: ProxyTarget
 * @description: CGLib动态代理目标对象
 * @author: HuGoldWater
 * @create: 2020-02-17 15:48
 **/
public class ProxyTarget {
    public String sayHello(String message) {
        String result = message + " hello world";
        System.out.println(result);
        return result;
    }
}
```

```java
/**
 * @projectName: thread-demo
 * @className: CGLibProxyFactory
 * @description:CGLib代理工厂
 * @author: HuGoldWater
 * @create: 2020-02-17 15:48
 **/
public class CGLibProxyFactory implements MethodInterceptor {
    private Object target;

    public CGLibProxyFactory(Object target) {
        this.target = target;
    }

    /**
     * 创建代理对象
     * @return
     */
    public Object getInstance(){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("cglib 动态代理 start");
        Object invoke = method.invoke(target, objects);
        System.out.println("cglib 动态代理 end");
        return invoke;
    }
}
```

```java
public class CGLibProxyMain {
    public static void main(String[] args) {
//        创建代理目标对象
        ProxyTarget proxyTarget = new ProxyTarget();
        Object instance = new CGLibProxyFactory(proxyTarget).getInstance();
        ProxyTarget proxy = (ProxyTarget)instance;
        proxy.sayHello("cglib");
    }
}
#############输出#####################
cglib 动态代理 start
cglib hello world
cglib 动态代理 end
```




# ThreadLocal面试题汇总

#### 1、ThreadLocal是什么？

答：`ThreadLocal`是一个本地线程副本变量工具类。主要用于将私有线程和该线程存放的副本对象做一个映射，各个线程之间变量互不干扰，在高并发场景下，可以实现无状态的调用，适用于各个线程不共享变量值的操作。

#### 2、ThreadLocal工作原理是什么？

答：`ThreadLocal`原理：每个线程的内部都维护了一个`ThreadLocalMap`，他是一个`Map(key,value)`数据格式，key是一个弱引用，也就是`ThreadLocal`本身，而`value`存的是线程变量的值。也就是说`ThreadLocal`本身并不存储线程的变量值，他只是一个工具，用来维护线程内部的`Map`，帮助存和取变量。

![](/Users/lcp/Desktop/HuGoldWater/GoldWater/precipitation/ThreadLocal.png)

#### 3、ThreadLocal如何解决Hash冲突？

答：它与`HashMap`不同，`ThreadLocalMap`结构非常简单，没有`next`引用，也就是说`ThreadLocalMap`中解决`Hash`冲突的方式并非链表的方式，而是采用线性探测的方式。所谓线性探测，就是根据初始`key`的`hashcode`值确定元素在`table`数组中的位置，如果发现这个位置上已经被其他的`key`值占用，则利用固定的算法寻找一定步长的下个位置，依次判断，直至找到能够存放的位置。

源码如下：

```java
				/**
         * Increment i modulo len.
         */
        private static int nextIndex(int i, int len) {
            return ((i + 1 < len) ? i + 1 : 0);
        }

        /**
         * Decrement i modulo len.
         */
        private static int prevIndex(int i, int len) {
            return ((i - 1 >= 0) ? i - 1 : len - 1);
        }
```

#### 4、ThreadLocal的内存泄漏露是怎么回事？

答：`ThreadLocal`在`ThreadLocalMap`中是以一个弱引用身份被`Entry`中的`key`引用的，因此如果`ThreadLocal`没有外部强引用来引用它，那么`ThreadLocal`会在下次JVM垃圾收集时被回收。这个时候`Entry`中的`key`已经被回收，但是`value`有事一强引用不会被垃圾收集器回收，这样`ThreadLocal`的线程如果一直持续运行，`value`就一直得不到回收，这样就会发生内存泄露。

#### 5、为什么ThreadLocalMap的key是弱引用的？

答：我们知道`ThreadLocalMap`中的`key`是弱引用，而`value`是强引用才会导致内存泄露的问题，至于为什么要这样设计，可以分为两种情况来讨论：

- `key`使用强引用：这样会导致一个问题，引用的`ThreadLocal`的对象被回收了，但是`ThreadLocalMap`还持有`ThreadLocal`的强引用，如果没有手动删除，`ThreadLocal`不会被回收，则会导致内存泄漏。
- `key`使用弱引用：这样的话，引用的`ThreadLocal`的对象被回收了，由于`ThreadLocalMap`持有`ThreadLocal`的弱引用，即使没有手动删除，`ThreadLocal`也会被回收。`value`在下一次`ThreadLocalMap`调用`set`、`get`、`remove`的时候会被清除。

#### 6、ThreadLocal的应用场景有哪些？

答：适用于独立变量副本的情况，比如`Hibernate`的`session`获取场景，`dubbo`框架内的请求缓存的场景等。

#### 7、ThreadLocal的最佳实践？

答：由于线程的生命周期很长，如果我们往`ThreadLocal`里面`set`了一个很大的`Object`对象，当`ThreadLocal`被垃圾回收后，在`ThreadLocalMap`里对应的`Entry`的键值会变成`null`，但是后续在也没有操作`set`、`get`等方法了。

所以最佳实践，应该在我们不使用的时候，主动调用`remove`方法进行清理。

#### 8、在很多源码框架中使用了ThreadLocal，为什么要加private static？

答：在阿里巴巴开发手册中提到，`ThreadLocal`对象建议使用`static`修饰。这个变量是针对一个线程内所有操作共享的，所以设置为静态变量，所有此类实例共享此静态变量，也就是说在类第一次被使用时装载，只分配一块存储空间，所有此类的对象（只要是这个线程内定义的）都可以操控这个变量。

在`ThreadLocal`源码中也有说明，推荐我么能使用`private static`来修饰它，但是不一定要`private`修饰，例如`Struts2`框架中就只用了`static`修饰.

> ```java
> This class provides thread-local variables.  These variables differ from
> their normal counterparts in that each thread that accesses one (via its
> {@code get} or {@code set} method) has its own, independently initialized
> copy of the variable.  {@code ThreadLocal} instances are typically private
> static fields in classes that wish to associate state with a thread (e.g.,
> a user ID or Transaction ID).
> ```



参考：

http://www.sohu.com/a/321815383_684445

https://www.cnblogs.com/over/p/10487273.html




















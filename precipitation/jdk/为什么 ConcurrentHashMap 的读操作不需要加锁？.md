## 为什么 ConcurrentHashMap 的读操作不需要加锁？

我们知道，`ConcurrentHashmap(1.8)`这个并发集合框架是线程安全的，当你看到源码的`get`操作时，会发现`get`操作全程是没有加任何锁的，这也是这篇博文讨论的问题——为什么它不需要加锁呢？



#### **ConcurrentHashMap的简介**

> 我想有基础的同学知道在jdk1.7中是采用`Segment + HashEntry + ReentrantLock`的方式进行实现的，而1.8中放弃了`Segment`臃肿的设计，取而代之的是采用`Node + CAS + Synchronized`来保证并发安全进行实现。

- `JDK1.8`的实现降低锁的粒度，`JDK1.7`版本锁的粒度是基于`Segment`的，包含多个`HashEntry`，而`JDK1.8`锁的粒度就是`HashEntry`（首节点）
- `JDK1.8`版本的数据结构变得更加简单，使得操作也更加清晰流畅，因为已经使用`synchronized`来进行同步，所以不需要分段锁的概念，也就不需要**`Segment`**这种数据结构了，由于粒度的降低，实现的复杂度也增加了
- `JDK1.8`使用红黑树来优化链表，基于长度很长的链表的遍历是一个很漫长的过程，而红黑树的遍历效率是很快的，代替一定阈值的链表，这样形成一个最佳拍档



![为什么ConcurrentHashMap的读操作不需要加锁-images-01](/Users/lcp/Desktop/HuGoldWater/GoldWater/precipitation/images/为什么ConcurrentHashMap的读操作不需要加锁-images/为什么ConcurrentHashMap的读操作不需要加锁-images-01.png)



##### **`get`操作源码**

1. 首先计算`hash`值，定位到该`table`索引位置，如果是首节点符合就返回
2. 如果遇到扩容的时候，会调用标志正在扩容节点`ForwardingNode`的`find`方法，查找该节点，匹配就返回
3. 以上都不符合的话，就往下遍历节点，匹配就返回，否则最后就返回`null`

```java
//会发现源码中没有一处加了锁
public V get(Object key) {
    Node<K,V>[] tab; Node<K,V> e, p; int n, eh; K ek;
    int h = spread(key.hashCode()); //计算hash
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (e = tabAt(tab, (n - 1) & h)) != null) {//读取首节点的Node元素
        if ((eh = e.hash) == h) { //如果该节点就是首节点就返回
            if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                return e.val;
        }
        //hash值为负值表示正在扩容，这个时候查的是ForwardingNode的find方法来定位到nextTable来
        //eh=-1，说明该节点是一个ForwardingNode，正在迁移，此时调用ForwardingNode的find方法去nextTable里找。
        //eh=-2，说明该节点是一个TreeBin，此时调用TreeBin的find方法遍历红黑树，由于红黑树有可能正在旋转变色，所以find里会有读写锁。
        //eh>=0，说明该节点下挂的是一个链表，直接遍历该链表即可。
        else if (eh < 0)
            return (p = e.find(h, key)) != null ? p.val : null;
        while ((e = e.next) != null) {//既不是首节点也不是ForwardingNode，那就往下遍历
            if (e.hash == h &&
                ((ek = e.key) == key || (ek != null && key.equals(ek))))
                return e.val;
        }
    }
    return null;
}
```

`get`没有加锁的话，`ConcurrentHashMap`是如何保证读到的数据不是脏数据的呢?



##### **`volatile`登场**

对于可见性，Java提供了`volatile`关键字来保证可见性、有序性。但不保证原子性。

普通的共享变量不能保证可见性，因为普通共享变量被修改之后，什么时候被写入主存是不确定的，当其他线程去读取时，此时内存中可能还是原来的旧值，因此无法保证可见性。

- `volatile`关键字对于基本类型的修改可以在随后对多个线程的读保持一致，但是对于引用类型如数组，实体`bean`，仅仅保证引用的可见性，但并不保证引用内容的可见性。。
- 禁止进行指令重排序。



背景：为了提高处理速度，处理器不直接和内存进行通信，而是先将系统内存的数据读到内部缓存（L1，L2或其他）后再进行操作，但操作完不知道何时会写到内存。



- 如果对声明了`volatile`的变量进行写操作，`JVM`就会向处理器发送一条指令，将这个变量所在缓存行的数据写回到系统内存。但是，就算写回到内存，如果其他处理器缓存的值还是旧的，再执行计算操作就会有问题。
- 在多处理器下，为了保证各个处理器的缓存是一致的，就会实现缓存一致性协议，当某个`CPU`在写数据时，如果发现操作的变量是共享变量，则会通知其他`CPU`告知该变量的缓存行是无效的，因此其他`CPU`在读取该变量时，发现其无效会重新从主存中加载数据。



![为什么ConcurrentHashMap的读操作不需要加锁-images-02](/Users/lcp/Desktop/HuGoldWater/GoldWater/precipitation/images/为什么ConcurrentHashMap的读操作不需要加锁-images/为什么ConcurrentHashMap的读操作不需要加锁-images-02.png)



总结下来：



- 第一：使用`volatile`关键字会强制将修改的值立即写入主存；
- 第二：使用`volatile`关键字的话，当线程2进行修改时，会导致线程1的工作内存中缓存变量的缓存行无效（反映到硬件层的话，就是`CPU`的L1或者L2缓存中对应的缓存行无效）；
- 第三：由于线程1的工作内存中缓存变量的缓存行无效，所以线程1再次读取变量的值时会去主存读取。



##### **是加在数组上的volatile吗？**

```java
/**
     * The array of bins. Lazily initialized upon first insertion.
     * Size is always a power of two. Accessed directly by iterators.
     */
    transient volatile Node<K,V>[] table;
```

我们知道`volatile`可以修饰数组的，只是意思和它表面上看起来的样子不同。举个栗子，`volatile int array[10]`是指`array`的地址是`volatile`的而不是数组元素的值是volatile的。



##### **用`volatile`修饰的`Node`**



`get`操作可以无锁是由于`Node`的元素`val`和指针`next`是用`volatile`修饰的，在多线程环境下线程A修改结点的`val`或者新增节点的时候是对线程B可见的。



```java
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    //可以看到这些都用了volatile修饰
    volatile V val;
    volatile Node<K,V> next;

    Node(int hash, K key, V val, Node<K,V> next) {
        this.hash = hash;
        this.key = key;
        this.val = val;
        this.next = next;
    }

    public final K getKey()       { return key; }
    public final V getValue()     { return val; }
    public final int hashCode()   { return key.hashCode() ^ val.hashCode(); }
    public final String toString(){ return key + "=" + val; }
    public final V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    public final boolean equals(Object o) {
        Object k, v, u; Map.Entry<?,?> e;
        return ((o instanceof Map.Entry) &&
                (k = (e = (Map.Entry<?,?>)o).getKey()) != null &&
                (v = e.getValue()) != null &&
                (k == key || k.equals(key)) &&
                (v == (u = val) || v.equals(u)));
    }

    /**
     * Virtualized support for map.get(); overridden in subclasses.
     */
    Node<K,V> find(int h, Object k) {
        Node<K,V> e = this;
        if (k != null) {
            do {
                K ek;
                if (e.hash == h &&
                    ((ek = e.key) == k || (ek != null && k.equals(ek))))
                    return e;
            } while ((e = e.next) != null);
        }
        return null;
    }
}
```

##### **既然`volatile`修饰数组对`get`操作没有效果那加在数组上的`volatile`的目的是什么呢？**



> 其实就是为了使得`Node`数组在扩容的时候对其他线程具有可见性而加的`volatile`

**总结**

- 在1.8中`ConcurrentHashMap`的`get`操作全程不需要加锁，这也是它比其他并发集合比如`hashtable`、用`Collections.synchronizedMap()`包装的`hashmap`;安全效率高的原因之一。
- `get`操作全程不需要加锁是因为`Node`的成员`val`是用`volatile`修饰的和数组用`volatile`修饰没有关系。
- 数组用`volatile`修饰主要是保证在数组扩容的时候保证可见性。
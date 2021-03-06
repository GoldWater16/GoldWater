#### 为什么jdk8用元空间数据结构用来替代永久代？

答：永久代即方法区，它是JVM的一种规范，存放类信息、常量、静态变量、即时编译后的代码等；

##### 历史演义：

- 在jdk1.6及以前的版本中，字符串的常量池是放在堆的永久代，它是一个类静态的区域，主要存储一些加载类的信息，常量池，方法片段等内容，默认大小只有4m，一旦常量池中大量使用intern是会直接产生java.lang.OutOfMemoryError:PermGen space错误的。

- 在jdk1.7的版本中，字符串常量池已经从永久代移到堆中。至于为什么要移动，原因是永久代太小了。

- 在jdk1.8的版本中，永久代被移到一个与堆不相连的本地内存区域，即元空间；由于类的元数据分配在本地内存中，元空间的最大可分配空间就是系统可用内存空间。同时，元空间虚拟机采用了组块分配的形式，会导致内存碎片存在。

##### 移除永久代的原因：

1. 字符串存在永久代中，容易出现性能问题和内存溢出。
2. 类及方法的信息等比较难确定其大小，因此对于永久代的大小指定比较困难，太小容易出现永久代溢出，太大则容易导致老年代溢出。
3. 永久代会为GC带来不必要的复杂度，并且回收效率偏低。
4. Oracle可能会将Hotspot与JRockit合二为一。



参考：

1、https://www.jianshu.com/p/c1ac5e7a5f87

2、http://openjdk.java.net/jeps/122
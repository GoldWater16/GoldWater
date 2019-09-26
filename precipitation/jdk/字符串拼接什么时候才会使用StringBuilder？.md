### 字符串拼接什么时候才会使用StringBuilder？

废话不多，先看看两个`demo`：

##### demo1

```java
public class TestDemo {
    public static void main(String[] args) {
        String a = "123";
        String b = "12";
        String c = b + "3";
        System.out.println(a == c);
    }
}
```

**输出结果：false**

##### demo2

```java
public class TestDemo {
    public static void main(String[] args) {
        String a = "123";
        String c = "12" + "3";
        System.out.println(a == c);
    }
}
```

**输出结果：true**

很奇怪，为什么结果会不一样呢？咱们带着这个问题一起来挖掘一下其中的奥秘。咱们把上面两个案例给反编译出来看看，使用`javac TestDemo.java`和`javap -c TestDemo.class`

##### demo1反编译后的结果

```c
HuGoldWaterdeMacBook-Pro:util lcp$ javap -c TestDemo.class 
Compiled from "TestDemo.java"
public class com.tongcaipay.common.log.util.TestDemo {
  public com.tongcaipay.common.log.util.TestDemo();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: return

  public static void main(java.lang.String[]);
    Code:
       0: ldc           #2                  // String 123
       2: astore_1
       3: ldc           #2                  // String 123
       5: astore_2
       6: new           #3                  // class java/lang/StringBuilder
       9: dup
      10: invokespecial #4                  // Method java/lang/StringBuilder."<init>":()V
      13: aload_2
      14: invokevirtual #5                  // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
      17: ldc           #6                  // String
      19: invokevirtual #5                  // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
      22: invokevirtual #7                  // Method java/lang/StringBuilder.toString:()Ljava/lang/String;
      25: astore_3
      26: getstatic     #8                  // Field java/lang/System.out:Ljava/io/PrintStream;
      29: aload_1
      30: aload_3
      31: if_acmpne     38
      34: iconst_1
      35: goto          39
      38: iconst_0
      39: invokevirtual #9                  // Method java/io/PrintStream.println:(Z)V
      42: return
}

```

大家可以看到反编译出来结果，编译器没有对其做优化，而是使用`StringBuilder#append`后再使用`toString`，可以看看`toString`源码（`return new String(value, 0, count);`），他每次`toString`后都会重新创建一个对象，所以结果为`false`不足为奇，反编译后代码如下：

```c
14: invokevirtual #5                  // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
      17: ldc           #6                  // String
      19: invokevirtual #5                  // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
      22: invokevirtual #7                  // Method java/lang/StringBuilder.toString:()Ljava/lang/String;
```

##### demo2反编译后的结果

```c
HuGoldWaterdeMacBook-Pro:util lcp$ javap -c TestDemo.class 
Compiled from "TestDemo.java"
public class com.tongcaipay.common.log.util.TestDemo {
  public com.tongcaipay.common.log.util.TestDemo();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: return

  public static void main(java.lang.String[]);
    Code:
       0: ldc           #2                  // String 123
       2: astore_1
       3: ldc           #2                  // String 123
       5: astore_2
       6: getstatic     #3                  // Field java/lang/System.out:Ljava/io/PrintStream;
       9: aload_1
      10: aload_2
      11: if_acmpne     18
      14: iconst_1
      15: goto          19
      18: iconst_0
      19: invokevirtual #4                  // Method java/io/PrintStream.println:(Z)V
      22: return
}

```

从如上反编译代码中，没有看到`StringBuilder`相关代码，说明编译器直接对其表达式做了优化。

```c
3: ldc           #2                  // String 123
```

ok，来总结一波。

##### 总结

1、从`demo1`中可以看出，通过变量和字符串拼接，`java`是需要先到内存找变量对应的值，才能完成字符串拼接的。所以，这种方式编译器是无法优化，只能使用`StringBuilder`进行字符串拼接，但是它最后使用了`toString`方法，其源码中是创建一个新的字符串，因此结果为`false`。

2、从`demo2`中可以看出，通过表达式的方式进行字符串拼接，`java`不需要去内存找对应的值，所以编译器可以直接对表达式进行优化，从`"12"+"3"`变成`"123"`，`a`和`c`字符串都指向了常量池的`"123"`，因此结果为`true`。


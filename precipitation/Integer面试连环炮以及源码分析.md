# Integer面试连环炮以及源码分析

##### 场景：

&emsp;&emsp;昨天有位朋友去面试，我问他面试问了哪些问题，其中问了Integer相关的问题，以下就是面试官问的问题，还有一些是我对此做了扩展。

##### 问：两个new Integer 128相等吗？

答：不。因为Integer缓存池默认是-127-128；

##### 问：可以修改Integer缓存池范围吗？如何修改？

答：可以。使用`-Djava.lang.Integer.IntegerCache.high=300`设置Integer缓存池大小

##### 问：Integer缓存机制使用了哪种设计模式？

答：亨元模式；

##### 问：Integer是如何获取你设置的缓存池大小？

答：`sun.misc.VM.getSavedProperty("java.lang.Integer.IntegerCache.high");`

##### 问：`sun.misc.VM.getSavedProperty`和`System.getProperty`有啥区别？

答：唯一的区别是，`System.getProperty`只能获取非内部的配置信息；例如`java.lang.Integer.IntegerCache.high`、`sun.zip.disableMemoryMapping`、`sun.java.launcher.diag`、`sun.cds.enableSharedLookupCache`等不能获取，这些只能使用`sun.misc.VM.getSavedProperty`获取

### `Integer`初始化源码分析：

```java
private static class IntegerCache {
    static final int low = -128;
    static final int high;
    static final Integer cache[];

    static {
        // high value may be configured by property
        int h = 127;
        String integerCacheHighPropValue =
            sun.misc.VM.getSavedProperty("java.lang.Integer.IntegerCache.high");
        if (integerCacheHighPropValue != null) {
            try {
                int i = parseInt(integerCacheHighPropValue);
                i = Math.max(i, 127);
                // Maximum array size is Integer.MAX_VALUE
                h = Math.min(i, Integer.MAX_VALUE - (-low) -1);
            } catch( NumberFormatException nfe) {
                // If the property cannot be parsed into an int, ignore it.
            }
        }
        high = h;

        cache = new Integer[(high - low) + 1];
        int j = low;
        for(int k = 0; k < cache.length; k++)
            cache[k] = new Integer(j++);

        // range [-128, 127] must be interned (JLS7 5.1.7)
        assert IntegerCache.high >= 127;
    }

    private IntegerCache() {}
}
```

### `VM.class`源码分析：

#### 初始化：

```java
static {
    allowArraySyntax = defaultAllowArraySyntax;
    savedProps = new Properties();
    finalRefCount = 0;
    peakFinalRefCount = 0;
    initialize();
}
```

#### `getSavedProperty`方法：

```java
public static String getSavedProperty(String var0) {
    if (savedProps.isEmpty()) {
        throw new IllegalStateException("Should be non-empty if initialized");
    } else {
        return savedProps.getProperty(var0);
}    
```

#### `savedProps.getProperty`方法：

```java
public String getProperty(String key) {
    Object oval = super.get(key);
    String sval = (oval instanceof String) ? (String)oval : null;
    return ((sval == null) && (defaults != null)) ? defaults.getProperty(key) : sval;
}
```

#### `System.java`源码分析：

```java
/**
 * 初始化系统类。 线程初始化后调用。
 */
private static void initializeSystemClass() {

    /**
     * VM可能会调用JNU_NewStringPlatform（）来在“props”初始化期间设置那些编码敏感属性（user.home，user.name，boot.class.path等），
     * 它们可能需要通过System.getProperty（）进行访问， 在初始化的早期阶段已经初始化（放入“props”）的相关系统编码属性。
     * 因此，请确保初始化时可以使用“props”，并直接将所有系统属性放入其中。
     */
    props = new Properties();
    initProperties(props);  // initialized by the VM

    /**
     * 某些系统配置可以由VM选项控制，例如用于支持自动装箱的对象标识语义的最大直接内存量和整数高速缓存大小。 通常，库将获得这些值
     * 来自VM设置的属性。 如果属性是
     * 仅限内部实现使用，应从系统属性中删除这些属性。
     *
     *   请参阅java.lang.Integer.IntegerCache和
     *   例如，sun.misc.VM.saveAndRemoveProperties方法。
     *
     *   保存系统属性对象的私有副本，该副本只能由内部实现访问。 去掉
     * 某些不适合公共访问的系统属性。
     */
    sun.misc.VM.saveAndRemoveProperties(props);


    lineSeparator = props.getProperty("line.separator");
    sun.misc.Version.init();

    FileInputStream fdIn = new FileInputStream(FileDescriptor.in);
    FileOutputStream fdOut = new FileOutputStream(FileDescriptor.out);
    FileOutputStream fdErr = new FileOutputStream(FileDescriptor.err);
    setIn0(new BufferedInputStream(fdIn));
    setOut0(newPrintStream(fdOut, props.getProperty("sun.stdout.encoding")));
    setErr0(newPrintStream(fdErr, props.getProperty("sun.stderr.encoding")));

    /**
     * 现在加载zip库，以防止java.util.zip.ZipFile稍后尝试使用它来加载此库。
     */
    loadLibrary("zip");

    // 为HUP，TERM和INT（如果可用）设置Java信号处理程序。
    Terminator.setup();

    /**
     * 初始化需要为类库设置的任何错误的操作系统设置。
     * 目前，除了在使用java.io类之前设置了进程范围错误模式的Windows之外，这在任何地方都是无操作的。
     */
    sun.misc.VM.initializeOSEnvironment();

    /**
     * 主线程没有像其他线程一样添加到其线程组中; 我们必须在这里自己做。
     */
    Thread current = Thread.currentThread();
    current.getThreadGroup().add(current);

    // 注册共享秘密
    setJavaLangAccess();

    /**
     * 在初始化期间调用的子系统可以调用sun.misc.VM.isBooted（），以避免执行应该等到应用程序类加载器设置完毕的事情。
     * 重要信息：确保这仍然是最后一次初始化操作！
     */
    sun.misc.VM.booted();
}
```

重点看这句：`sun.misc.VM.saveAndRemoveProperties(props);`他会移除系统内部使用的配置，咱们来看看源码是如何操作的。

#### `sun.misc.VM.saveAndRemoveProperties`方法：

```java
public static void saveAndRemoveProperties(Properties var0) {
    if (booted) {
        throw new IllegalStateException("System initialization has completed");
    } else {
        savedProps.putAll(var0);
        String var1 = (String)var0.remove("sun.nio.MaxDirectMemorySize");
        if (var1 != null) {
            if (var1.equals("-1")) {
                directMemory = Runtime.getRuntime().maxMemory();
            } else {
                long var2 = Long.parseLong(var1);
                if (var2 > -1L) {
                    directMemory = var2;
                }
            }
        }

        var1 = (String)var0.remove("sun.nio.PageAlignDirectMemory");
        if ("true".equals(var1)) {
            pageAlignDirectMemory = true;
        }

        var1 = var0.getProperty("sun.lang.ClassLoader.allowArraySyntax");
        allowArraySyntax = var1 == null ? defaultAllowArraySyntax : Boolean.parseBoolean(var1);
        //移除内部使用的配置，不应该让看到这些配置信息
        var0.remove("java.lang.Integer.IntegerCache.high");
        var0.remove("sun.zip.disableMemoryMapping");
        var0.remove("sun.java.launcher.diag");
        var0.remove("sun.cds.enableSharedLookupCache");
    }
}
```


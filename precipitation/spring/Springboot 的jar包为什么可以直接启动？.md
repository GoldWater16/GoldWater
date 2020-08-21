## Springboot 的jar包为什么可以直接启动？

&emsp;&emsp;首先，先准备一个`jar`包，我这里准备了一个`demo-0.0.1-SNAPSHOT.jar`;先来看看`jar`包里面的目录结构：

```
├── BOOT-INF
│   ├── classes
│   │   ├── application.properties
│   │   └── com
│   │       └── sf
│   │           └── demo
│   │               └── DemoApplication.class
│   └── lib
│       ├── spring-boot-2.1.3.RELEASE.jar
│       ├── spring-boot-autoconfigure-2.1.3.RELEASE.jar
│       ├── spring-boot-starter-2.1.3.RELEASE.jar
│       ├── 这里省略掉很多jar包
├── META-INF
│   ├── MANIFEST.MF
│   └── maven
│       └── com.sf
│           └── demo
│               ├── pom.properties
│               └── pom.xml
└── org
    └── springframework
        └── boot
            └── loader
                ├── ExecutableArchiveLauncher.class
                ├── JarLauncher.class
                ├── LaunchedURLClassLoader$UseFastConnectionExceptionsEnumeration.class
                ├── LaunchedURLClassLoader.class
                ├── Launcher.class
                ├── 省略class
                ├── archive
                │   ├── Archive$Entry.class
                │   ├── 省略class
                ├── data
                │   ├── RandomAccessData.class
                │   ├── 省略class
                ├── jar
                │   ├── AsciiBytes.class
                │   ├── 省略class
                └── util
                    └── SystemPropertyUtils.class

```

这个文件目录分为`BOOT-INF/classes`、`BOOT-INF/lib`、`META-INF`、`org`：

- `BOOT-INF/classes`：主要存放应用编译后的`class`文件

- `BOOT-INF/lib`：主要存放应用依赖的`jar`包文件

- `META-INF`：主要存放`maven`和`MANIFEST.MF`文件

- `org`：主要存放`springboot`相关的`class`文件

&emsp;&emsp;当你使用命令`java -jar demo-0.0.1-SNAPSHOT.jar`时，它会找到`META-INF`下的`MANIFEST.MF`文件，可以从文件中发现，其内容中的`Main-Class`属性值为`org.springframework.boot.loader.JarLauncher`，并且项目的引导类定义在`Start-Class`属性中，值为`com.sf.demo.DemoApplication`，该属性是由`springboot`引导程序启动需要的，`JarLauncher`就是对应的`jar`文件的启动器.

```
Manifest-Version: 1.0
Spring-Boot-Classpath-Index: BOOT-INF/classpath.idx
Implementation-Title: demo
Implementation-Version: 0.0.1-SNAPSHOT
Start-Class: com.sf.demo.DemoApplication
Spring-Boot-Classes: BOOT-INF/classes/
Spring-Boot-Lib: BOOT-INF/lib/
Build-Jdk-Spec: 1.8
Spring-Boot-Version: 2.3.0.RELEASE
Created-By: Maven Jar Plugin 3.2.0
Implementation-Vendor: Pivotal Software, Inc.
Main-Class: org.springframework.boot.loader.JarLauncher
```

&emsp;&emsp;启动类`org.springframework.boot.loader.JarLauncher`并非是项目中引入的类，而是`spring-boot-maven-plugin`插件`repackage`追加进去的.

#### 探索`JarLauncher`的实现原理

&emsp;&emsp;当执行`java -jar`命令或执行解压后的`org.springframework.boot.loader.JarLauncher`类时，`JarLauncher`会将`BOOT-INF/classes`下的类文件和`BOOT-INF/lib`下依赖的`jar`加入到`classpath`下，最后调用`META-INF`下的`MANIFEST.MF`文件的`Start-Class`属性来完成应用程序的启动，也就是说它是`springboot loader`提供了一套标准用于执行`springboot`打包出来的`JAR`包.

##### JarLauncher重点类的介绍：

- `java.util.jar.JarFile`：`JDK`工具类，用于读取`JAR`文件的内容

- `org.springframework.boot.loader.jar.JarFile`：继承于`JDK`工具类`JarFile`类并扩展了一些嵌套功能

- `java.util.jar.JarEntry`：`JDK`工具类，此类用于表示`JAR`文件条目

- `org.springframework.boot.loader.jar.JarEntry`：也是继承于`JDK`工具类`JarEntry`类

- `org.springframework.boot.loader.archive.Archive`： `spring boot loader`抽象出来的统一访问资源的接口

- `org.springframework.boot.loader.archive.JarFileArchive`：`JAR`文件的实现

- `org.springframework.boot.loader.archive.ExplodedArchive`：文件目录的实现

在项目里面添加一个依赖配置,就可以看`JarLauncher`的源码：

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-loader</artifactId>
  <scope>provided</scope>
</dependency>
```

##### org.springframework.boot.loader.ExecutableArchiveLauncher

```java
public class JarLauncher extends ExecutableArchiveLauncher {

   private static final String DEFAULT_CLASSPATH_INDEX_LOCATION = "BOOT-INF/classpath.idx";

   static final EntryFilter NESTED_ARCHIVE_ENTRY_FILTER = (entry) -> {
      if (entry.isDirectory()) {
         return entry.getName().equals("BOOT-INF/classes/");
      }
      return entry.getName().startsWith("BOOT-INF/lib/");
   };

   public JarLauncher() {
   }

   protected JarLauncher(Archive archive) {
      super(archive);
   }

   @Override
   protected ClassPathIndexFile getClassPathIndex(Archive archive) throws IOException {
      // Only needed for exploded archives, regular ones already have a defined order
      if (archive instanceof ExplodedArchive) {
         String location = getClassPathIndexFileLocation(archive);
         return ClassPathIndexFile.loadIfPossible(archive.getUrl(), location);
      }
      return super.getClassPathIndex(archive);
   }

   private String getClassPathIndexFileLocation(Archive archive) throws IOException {
      Manifest manifest = archive.getManifest();
      Attributes attributes = (manifest != null) ? manifest.getMainAttributes() : null;
      String location = (attributes != null) ? attributes.getValue(BOOT_CLASSPATH_INDEX_ATTRIBUTE) : null;
      return (location != null) ? location : DEFAULT_CLASSPATH_INDEX_LOCATION;
   }

   @Override
   protected boolean isPostProcessingClassPathArchives() {
      return false;
   }

   @Override
   protected boolean isSearchCandidate(Archive.Entry entry) {
      return entry.getName().startsWith("BOOT-INF/");
   }

   @Override
   protected boolean isNestedArchive(Archive.Entry entry) {
      return NESTED_ARCHIVE_ENTRY_FILTER.matches(entry);
   }

  //start
   public static void main(String[] args) throws Exception {
      new JarLauncher().launch(args);
   }

}
```

##### org.springframework.boot.loader.Launcher

```java
/**
 * 
 * 启动程序的基类，该启动程序可以使用一个或多个支持的完全配置的类路径来启动应用程序
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @since 1.0.0
 */
public abstract class Launcher {

   private static final String JAR_MODE_LAUNCHER = "org.springframework.boot.loader.jarmode.JarModeLauncher";

   /**
    * 启动应用程序，此方法是子类方法{@code public static void main(String[] args)}调用的初始入口点
    * @param args the incoming arguments
    * @throws Exception if the application fails to launch
    */
   protected void launch(String[] args) throws Exception {
      if (!isExploded()) {
         //①注册一个自定义URL的jar协议
         JarFile.registerUrlProtocolHandler();
      }
      //②创建指定archive的类加载器
      ClassLoader classLoader = createClassLoader(getClassPathArchivesIterator());
      String jarMode = System.getProperty("jarmode");
      //③获取Start-Class属性对应的com.sf.demo.DemoApplication
      String launchClass = (jarMode != null && !jarMode.isEmpty()) ? JAR_MODE_LAUNCHER : getMainClass();
      //④利用反射调用Start-Class，执行main方法
      launch(args, launchClass, classLoader);
   }
}
```

#### ①注册一个自定义`URL`的`JAR`协议

##### org.springframework.boot.loader.jar.JarFile#registerUrlProtocolHandler

`spring boot loader`扩展了`URL`协议，将包名`org.springframework.boot.loader`追加到`java`系统属性`java.protocol.handler.pkgs`中，该包下存在协议对应的`Handler`类，即`org.springframework.boot.loader.jar.Handler`其实现协议为`JAR`.

```java
/**
 * 注册一个'java.protocol.handler.pkgs'属性，让URLStreamHandler处理jar的URL
 */
public static void registerUrlProtocolHandler() {
   String handlers = System.getProperty(PROTOCOL_HANDLER, "");
   System.setProperty(PROTOCOL_HANDLER,
         ("".equals(handlers) ? HANDLERS_PACKAGE : handlers + "|" + HANDLERS_PACKAGE));
   resetCachedUrlHandlers();
}
```

##### org.springframework.boot.loader.jar.JarFile#resetCachedUrlHandlers

```java
/**
 * 防止已经使用了jar协议，需要重置URLStreamHandlerFactory缓存的处理程序。
 */
private static void resetCachedUrlHandlers() {
   try {
      URL.setURLStreamHandlerFactory(null);
   }
   catch (Error ex) {
      // Ignore
   }
}
```

#### ②创建指定`archive`的类加载器

##### org.springframework.boot.loader.ExecutableArchiveLauncher#getClassPathArchivesIterator

```java
@Override
protected Iterator<Archive> getClassPathArchivesIterator() throws Exception {
   Archive.EntryFilter searchFilter = this::isSearchCandidate;
   Iterator<Archive> archives = this.archive.getNestedArchives(searchFilter,
         (entry) -> isNestedArchive(entry) && !isEntryIndexed(entry));
   if (isPostProcessingClassPathArchives()) {
      archives = applyClassPathArchivePostProcessing(archives);
   }
   return archives;
}
```

##### org.springframework.boot.loader.Launcher#createClassLoader(java.util.Iterator<org.springframework.boot.loader.archive.Archive>)

```java
/**
 * 创建一个指定的archives的类加载器
 * @param archives the archives
 * @return the classloader
 * @throws Exception if the classloader cannot be created
 * @since 2.3.0
 */
protected ClassLoader createClassLoader(Iterator<Archive> archives) throws Exception {
   List<URL> urls = new ArrayList<>(50);
   while (archives.hasNext()) {
      Archive archive = archives.next();
      urls.add(archive.getUrl());
      archive.close();
   }
   return createClassLoader(urls.toArray(new URL[0]));
}
```

##### org.springframework.boot.loader.Launcher#createClassLoader(java.util.Iterator<org.springframework.boot.loader.archive.Archive>)

```java
/**
	 * 创建一个指定的自定义URL的类加载器
	 * @param urls the URLs
	 * @return the classloader
	 * @throws Exception if the classloader cannot be created
	 */
	protected ClassLoader createClassLoader(URL[] urls) throws Exception {
		return new LaunchedURLClassLoader(isExploded(), urls, getClass().getClassLoader());
	}
```

#### ③获取`Start-Class`属性对应的`com.sf.demo.DemoApplication`

`org.springframework.boot.loader.ExecutableArchiveLauncher#getMainClass`

```java
@Override
protected String getMainClass() throws Exception {
   Manifest manifest = this.archive.getManifest();
   String mainClass = null;
   if (manifest != null) {
      //从配置文件获取Start-Class对应的com.sf.demo.DemoApplication
      mainClass = manifest.getMainAttributes().getValue("Start-Class");
   }
   if (mainClass == null) {
      throw new IllegalStateException("No 'Start-Class' manifest entry specified in " + this);
   }
   return mainClass;
}
```

#### ④利用反射调用`Start-Class`，执行`main`方法

```java
/**
	 * 启动应用程序
	 * @param args the incoming arguments
	 * @param launchClass the launch class to run
	 * @param classLoader the classloader
	 * @throws Exception if the launch fails
	 */
	protected void launch(String[] args, String launchClass, ClassLoader classLoader) throws Exception {
    //将当前线程的上下文类加载器设置成LaunchedURLClassLoader
		Thread.currentThread().setContextClassLoader(classLoader);
    //启动应用程序
		createMainMethodRunner(launchClass, args, classLoader).run();
	}
  /**
	 * 构造一个MainMethodRunner类，来启动应用程序
	 * @param mainClass the main class
	 * @param args the incoming arguments
	 * @param classLoader the classloader
	 * @return the main method runner
	 */
	protected MainMethodRunner createMainMethodRunner(String mainClass, String[] args, ClassLoader classLoader) {
		return new MainMethodRunner(mainClass, args);
	}
```

##### org.springframework.boot.loader.MainMethodRunner

```java
/**
 * 用来调用main方法的工具类
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @since 1.0.0
 */
public class MainMethodRunner {

   private final String mainClassName;

   private final String[] args;

   /**
    * Create a new {@link MainMethodRunner} instance.
    * @param mainClass the main class
    * @param args incoming arguments
    */
   public MainMethodRunner(String mainClass, String[] args) {
      this.mainClassName = mainClass;
      this.args = (args != null) ? args.clone() : null;
   }
	//利用反射启动应用程序
   public void run() throws Exception {
      Class<?> mainClass = Class.forName(this.mainClassName, false, Thread.currentThread().getContextClassLoader());
      Method mainMethod = mainClass.getDeclaredMethod("main", String[].class);
      mainMethod.setAccessible(true);
      mainMethod.invoke(null, new Object[] { this.args });
   }

}
```

我们先了解一下类加载机制：

![image-20200610113151859](https://github.com/GoldWater16/GoldWater/blob/master/precipitation/images/%E5%8F%8C%E4%BA%B2%E5%A7%94%E6%B4%BE%E6%A8%A1%E5%9E%8B-images.png?raw=true)

&emsp;&emsp;我们知道双亲委派模型的原则，当一个类加载器收到类加载任务，会先交给其父类加载器去完成，因此最终加载任务都会传递到顶层的启动类加载器，只有当父类加载器无法完成加载任务时，才会尝试加载任务。

&emsp;&emsp;由于`demo-0.0.1-SNAPSHOT.jar`中依赖的各个`JDK`包，并不在程序自己的`classpath`下，它是存放在`JDK`包里的`BOOT-INF/lib`目录下，如果我们采用双亲委派机制的话，根本获取不到我们`JAR`包的依赖，因此我们需要破坏双亲委派模型，使用自定义类加载机制。

&emsp;&emsp;在`springboot2`中，`LaunchedURLClassLoader`自定义类加载器继承`URLClassLoader`，重写了`loadClass`方法；在`JDK`里面，`JAR`的资源分隔符是`!/`，但是`JDK`中只支持一个`!/`，这无法满足`spring boot loader`的需求，so，`springboot`扩展了`JarFile`，从这里可以看到`org.springframework.boot.loader.jar.JarFile#createJarFileFromEntry`,它支持了多个`!/`，表示jar文件嵌套`JAR`文件、`JAR`文件嵌套`Directory`.

##### org.springframework.boot.loader.LaunchedURLClassLoader

```java
public class LaunchedURLClassLoader extends URLClassLoader {
  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
     if (name.startsWith("org.springframework.boot.loader.jarmode.")) {
        ........省略代码
     try {
        try {
           //尝试根据类名去定义类所在的包，即java.lang.Package，确保jar文件嵌套jar包里匹配的manifest能够和package关联起来
           definePackageIfNecessary(name);
        }
        catch (IllegalArgumentException ex) {
           // Tolerate race condition due to being parallel capable
           if (getPackage(name) == null) {
              // This should never happen as the IllegalArgumentException indicates
              // that the package has already been defined and, therefore,
              // getPackage(name) should not return null.
              throw new AssertionError("Package " + name + " has already been defined but it could not be found");
           }
        }
        return super.loadClass(name, resolve);
     }
     finally {
        Handler.setUseFastConnectionExceptions(false);
     }
  }
}
```

##### org.springframework.boot.loader.LaunchedURLClassLoader#definePackageIfNecessary

```java
/**
 * 在进行调用findClass方法之前定义一个包，确保嵌套jar与包关联
 * @param className the class name being found
 */
private void definePackageIfNecessary(String className) {
   int lastDot = className.lastIndexOf('.');
   if (lastDot >= 0) {
      String packageName = className.substring(0, lastDot);
      if (getPackage(packageName) == null) {
         try {
            definePackage(className, packageName);
         }
         catch (IllegalArgumentException ex) {
            // Tolerate race condition due to being parallel capable
            if (getPackage(packageName) == null) {
               // This should never happen as the IllegalArgumentException
               // indicates that the package has already been defined and,
               // therefore, getPackage(name) should not have returned null.
               throw new AssertionError(
                     "Package " + packageName + " has already been defined but it could not be found");
            }
         }
      }
   }
}
```

总结：

1、`springboot` 扩展了`JDK`的`URL`协议；

2、`springboot` 自定义了类加载器`LaunchedURLClassLoader`；

3、`Launcher`利用反射调用`StartClass#main`方法(`org.springframework.boot.loader.MainMethodRunner#run`);

4、`springboot1`和`springboot2`主要区别是在启动应用程序时，`springboot1`会启动一个线程去反射调用，`springboot2`直接调用；



参考资料：

> https://www.yht7.com/news/18153
>
> https://segmentfault.com/a/1190000016192449
>
> https://cloud.tencent.com/developer/article/1469863
>
> https://www.cnblogs.com/xxzhuang/p/11194559.html
>
> http://www.10qianwan.com/articledetail/577937.html
>
> https://blog.csdn.net/shenchaohao12321/article/details/103543446
>
> https://fangjian0423.github.io/2017/05/31/springboot-executable-jar/


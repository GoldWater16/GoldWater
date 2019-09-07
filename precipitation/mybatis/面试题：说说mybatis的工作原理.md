# 面试题：说说mybatis的工作原理

### 开场白：

面试官：说说mybatis的工作原理？

我：emmm......不好意思，说不上来。

面试官：今天就到这里吧，你回家等通知吧。

第二天开始把mybatis撸一遍避免再次被面试官虐，下次面试官还问你的，请把这篇文章发给他。

———————————————————美丽的分割线-----------------------------------------------------------

### `mybatis`的工作原理分为以下六步：

1. 将`mybatis-config.xml`转成`InputStream`流对象；
2. 根据`InputStream`流对象解析出`Configuration`对象，然后创建`SqlSessionFactory`工厂对象；
3. 然后调用`SqlSessionFactory`中的`openSession`创建`SqlSession`对象；
4. 从`SqlSession`中调用`Executor`执行数据库操作并生成具体sql指令；
5. 提交事务；
6. 关闭`SqlSession`；

大概了解下基本的工作原理，接下来会先从使用开始了解它，深入它，解剖它.

### 如何使用`mybatis`：

##### 1.添加依赖包

```xml
<dependency>
   <groupId>org.mybatis.spring.boot</groupId>
   <artifactId>mybatis-spring-boot-starter</artifactId>
   <version>1.3.0</version>
</dependency>
```

##### 2.创建`mybatis-config.xml`配置文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!--环境配置，连接的数据库，这里使用的是MySQL-->
    <environments default="mysql">
        <environment id="mysql">
            <!--指定事务管理的类型，这里简单使用Java的JDBC的提交和回滚设置-->
            <transactionManager type="JDBC"></transactionManager>
            <!--dataSource 指连接源配置，POOLED是JDBC连接对象的数据源连接池的实现-->
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"></property>
                <property name="url" value="jdbc:mysql://xxx:3306/xxx"></property>
                <property name="username" value="root"></property>
                <property name="password" value="xxx"></property>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <mapper resource="Order.xml"></mapper>
    </mappers>
</configuration>
```

##### 3.创建`Order.xml`配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.demo.OrderDAO">
    <select id="selectById" parameterType="int" resultType="com.example.demo.Order">
        select * from t_order where id = #{id}
    </select>
</mapper>
```

##### 4.创建`Order`对象

```java
public class Order {
    private Long id;
    private String orderNo;
    private Long price;
    private Long amount;
    //*********此处省略get、set************
}
```

##### 5.创建`DAO`类

```java
public interface OrderDAO {
    Order selectById(int id);
}
```

##### 6.创建`MybatisTest`测试类

```java
public class MybatisTest {
    public static void main(String[] args) throws IOException {
        String config = "mybatis-config.xml";
        //1.加载配置文件
        InputStream inputStream = Resources.getResourceAsStream(config);
        //2.构建SqlSessionFactory
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(inputStream);
        //3.从SqlSessionFactory中获取SqlSession对象
        SqlSession sqlSession = factory.openSession();
        //4.执行操作
        Object select = sqlSession.selectOne("selectById", 1);
        System.out.println(JSONObject.toJSONString(select));
        //5.提交事务
        sqlSession.commit();
        //6.关闭SqlSession
        sqlSession.close();
    }
}
```

⚠️**温馨提示：复制上面的代码，修改下配置信息就可直接运行**

各位小伙伴，一定要动手体验一下上述的`demo`.

#### 接下来从构建`SqlSessionFactory`开始分析：

> SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(inputStream);

&emsp;&emsp;首先，观察`SqlSessionFactory`的构建方式，是不是觉得有点像设计模式中的建造者模式，没错，就是它！知道他使用了建造者模式，就好办了，开撸。

![](https://github.com/GoldWater16/GoldWater/blob/master/precipitation/images/mybatis%E6%BA%90%E7%A0%81/mybatis-1.png?raw=true)

#### `build`源码：

通过`XMLConfigBuilder`解析`xml`，最后返回`SqlSessionFactory`对象。

```java
public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
  try {
    XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
    return build(parser.parse());
  } catch (Exception e) {
    throw ExceptionFactory.wrapException("Error building SqlSession.", e);
  } finally {
    ErrorContext.instance().reset();
    try {
      inputStream.close();
    } catch (IOException e) {
      // Intentionally ignore. Prefer previous error.
    }
  }
}
public SqlSessionFactory build(Configuration config) {
    return new DefaultSqlSessionFactory(config);
}
```

#### `parseConfiguration`源码：

&emsp;&emsp;看到这段代码是不是很像`mybatis-config.xml`的配置.没错，这个方法就是专门解析`mybatis-config.xml`里面的节点信息，然后将他封装到`Configuration`对象.

```java
private void parseConfiguration(XNode root) {
  try {
    //issue #117 read properties first
    propertiesElement(root.evalNode("properties"));
    Properties settings = settingsAsProperties(root.evalNode("settings"));
    loadCustomVfs(settings);
    typeAliasesElement(root.evalNode("typeAliases"));
    pluginElement(root.evalNode("plugins"));
    objectFactoryElement(root.evalNode("objectFactory"));
    objectWrapperFactoryElement(root.evalNode("objectWrapperFactory"));
    reflectorFactoryElement(root.evalNode("reflectorFactory"));
    settingsElement(settings);
    // read it after objectFactory and objectWrapperFactory issue #631
    environmentsElement(root.evalNode("environments"));
    databaseIdProviderElement(root.evalNode("databaseIdProvider"));
    typeHandlerElement(root.evalNode("typeHandlers"));
    mapperElement(root.evalNode("mappers"));
  } catch (Exception e) {
    throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
  }
}
```

**小结：**

&emsp;&emsp;以上操作主要是为了解析`mybatis-config.xml`配置文件，然后将配置信息封装进`Configuration`对象。

分析如何从`SqlSessionFactory`中获取`SqlSession`对象

#### `openSessionFromDataSource`源码：

```java
private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) {
    Transaction tx = null;
    try {
      //根据配置获取环境
      final Environment environment = configuration.getEnvironment();
      //构建事务工厂
      final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
      //通过事务工厂创建事务Transation
      tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
      //创建执行器Executor对象
      final Executor executor = configuration.newExecutor(tx, execType);
      //根据configuration，executor创建DefaultSqlSession对象
      return new DefaultSqlSession(configuration, executor, autoCommit);
    } catch (Exception e) {
      closeTransaction(tx); // may have fetched a connection so lets call close()
      throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }
public DefaultSqlSession(Configuration configuration, Executor executor, boolean autoCommit) {
    this.configuration = configuration;
    this.executor = executor;
    this.dirty = false;
    this.autoCommit = autoCommit;
}
```

**执行器类型只有三种：**

```
public enum ExecutorType {
  SIMPLE, REUSE, BATCH
}
```

`SIMPLE`：普通的执行器（默认的，见`Configuration.java` 22行 -》 `protected ExecutorType defaultExecutorType = ExecutorType.SIMPLE;`）

`REUSE`：执行器会重用预处理语句

`BATCH`：执行器将重用语句并执行批量更新

**小结：**

&emsp;&emsp;以上操作主要是根据环境创建事务和执行器，然后将其封装成SqlSession返回。

#### 执行操作

接着分析`sqlSession.selectOne()`

```java
DefaultSqlSession.java类

public <T> T selectOne(String statement, Object parameter) {
  // Popular vote was to return null on 0 results and throw exception on too many.
  List<T> list = this.<T>selectList(statement, parameter);
  if (list.size() == 1) {
    return list.get(0);
  } else if (list.size() > 1) {
    throw new TooManyResultsException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
  } else {
    return null;
  }
}
public <E> List<E> selectList(String statement, Object parameter) {
    return this.selectList(statement, parameter, RowBounds.DEFAULT);
  }
  public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
    try {
      //从配置中获取statement信息
      MappedStatement ms = configuration.getMappedStatement(statement);
      //调用执行器
      return executor.query(ms, wrapCollection(parameter), rowBounds, Executor.NO_RESULT_HANDLER);
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error querying database.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }
```

执行器的调用链是：

query->BaseExecutor#query->this.query->this.queryFromDatabase->SimpleExecutor#doQuery->SimpleStatementHandler#query

真正执行`sql`查询的是`doQuery`，执行完之后，在`handleResultSets`方法中将数据封装成对象放到list

**小结：**

&emsp;&emsp;以上操作，主要是获取`statement`信息，执行`sql`，返回结果。

其实执行操作还有另外一种实现方式，就是咱们平时常用的通过接口去调用`selectById`，来看看他是怎么操作的。

```java
public class MybatisTest {
    public static void main(String[] args) throws IOException {
        String config = "mybatis-config.xml";
        //加载配置文件
        InputStream inputStream = Resources.getResourceAsStream(config);
        //构建SqlSessionFactory
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(inputStream);
        //从SqlSessionFactory中获取SqlSession对象
        SqlSession sqlSession = factory.openSession();
        //执行操作
        OrderDAO mapper = sqlSession.getMapper(OrderDAO.class);
        Order order = mapper.selectById(1);
        System.out.println(JSONObject.toJSONString(order));
        //提交事务
        sqlSession.commit();
        //关闭SqlSession
        sqlSession.close();

    }
```

咱们来分析一下这种执行操作跟上面有什么区别？

首先，进入`sqlSession.getMapper();`

```java
public <T> T getMapper(Class<T> type) {
  //①
  return configuration.<T>getMapper(type, this);
}

public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
  	//②
    return mapperRegistry.getMapper(type, sqlSession);
}

public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
    final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
    if (mapperProxyFactory == null) {
      throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
    }
    try {
      // ③
      return mapperProxyFactory.newInstance(sqlSession);
    } catch (Exception e) {
      throw new BindingException("Error getting mapper instance. Cause: " + e, e);
    }
}
public T newInstance(SqlSession sqlSession) {
    // ④
    final MapperProxy<T> mapperProxy = new MapperProxy<T>(sqlSession, mapperInterface, methodCache);
    return newInstance(mapperProxy);
  }
protected T newInstance(MapperProxy<T> mapperProxy) {
    // ⑤
    return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, mapperProxy);
  }
```

从上面代码可以看到通过接口方式，使用了动态代理去调用selectById的；

最后一步，进到`MapperProxy#invoke`方法里面，看最下面

```java
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
  try {
    if (Object.class.equals(method.getDeclaringClass())) {
      return method.invoke(this, args);
    } else if (isDefaultMethod(method)) {
      return invokeDefaultMethod(proxy, method, args);
    }
  } catch (Throwable t) {
    throw ExceptionUtil.unwrapThrowable(t);
  }
  final MapperMethod mapperMethod = cachedMapperMethod(method);
  return mapperMethod.execute(sqlSession, args);
}

public Object execute(SqlSession sqlSession, Object[] args) {
    Object result;
    switch (command.getType()) {
      //***************代码省略************************************
      case SELECT:
        if (method.returnsVoid() && method.hasResultHandler()) {
          executeWithResultHandler(sqlSession, args);
          result = null;
        } else if (method.returnsMany()) {
          result = executeForMany(sqlSession, args);
        } else if (method.returnsMap()) {
          result = executeForMap(sqlSession, args);
        } else if (method.returnsCursor()) {
          result = executeForCursor(sqlSession, args);
        } else {
          Object param = method.convertArgsToSqlCommandParam(args);
   
          //重点！！！其实他也是调用最上面的方法
          result = sqlSession.selectOne(command.getName(), param);
        }
        //********************代码省略********************************
    return result;
  }
```

&emsp;&emsp;大家应该看到这行代码了吧，`result = sqlSession.selectOne(command.getName(), param);`没错，他内部还是调用上面的方法来实现的。ok，`mybatis`查询语句原理分析到此为止，大家一定要动手跟我一起去看看别人是如何写代码的，这里做一个总结，看这个源码能够学到什么东西？

**总结：**

1.设计模式：建造者模式、模板模式、工厂模式、代理模式、装饰器模式(`InputStream`)；

2.代码规范：每个类每个小方法的单一职责；

### 结束语：

面试官：小伙子，知道mybatis吗？

我：！@#￥%……&*（）**……

面试官：嗯，不错，有前途，那说说mybatis原理吧

我：#￥%￥……&……*&……%￥#@！

面试官：恭喜你，你已经被录取了






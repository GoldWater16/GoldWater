##Mybatis面试题

### 1、什么是Mybatis？

答：①Mybatis是一个半ORM(对象关系映射)框架，它内部封装了JDBC，开发时只需要关注SQL语句本身，不需要花费精力去处理加载驱动、创建连接、创建statement等繁杂的过程。程序猿直接编写原生态sql，可以严格控制sql执行性能，灵活度高。

②Mybatis可以使用XML或注解来配置和映射原生信息，将POJO映射成数据库中的记录，避免了几乎所有的JDBC代码和手动设置参数以及获取结果集。

③通过xml文件或注解的方式将要执行的各种statement配置起来，并通过Java对象和statement中sql的动态参数进行映射生成最终执行的sql语句，最后由mybatis框架执行sql并将结果映射为Java对象并返回。

###2、Mybatis有哪些优点？

答：①基于SQL语句编程，相当灵活；

②减少JDBC大量冗余的代码，无需关注连接开关；

③能够很好的兼容各种数据库；

④能够与spring很好的集成；

⑤支持对象与数据库的ORM字段关系映射；

### 3、Mybatis有哪些缺点？

答：①编写sql语句工作量大；

②sql依赖数据库，移植性差；

### 4、Mybatis与Hibernate有哪些区别？

答：

①mybatis是半ORM；hibernate是全ORM；也就是说mybatis需要自己写sql，hibernate不需要；

②mybatis编写sql工作量大，无法做到数据库无关性，但是可以严格控制sql执行性能，灵活度高；

③hibernate是全ORM，对象-关系映射能力强，数据库无关性好，可以节省很多代码，提高效率。

### 5、Mybati是如何进行分页的？分页插件的原理是什么？

答：mybatis使用RowBounds对象进行分页，它是针对ResultSet结果集执行的内存分页，而非物理分页。也可以使用limit进行分页。

######分页插件原理：

&emsp;&emsp;使用mybatis提供的插件接口，实现自定义插件，在插件的拦截方法内拦截待执行的sql，然后重写sql，根据dialect方言，添加对应的物理分页语句和物理分页参数。
# mysql字符集utf8mb4踩坑之路

##### utf8mb4字符集来源

&emsp;&emsp;他是在mysql 5.5.3之后增加的字符编码，它是utf8的超集，能够完全兼容utf8，而且用四个字节存储更多的字符（可存储emoji和一些不常用的汉字）。

##### 主从复制异常

&emsp;&emsp;使用不同的字符集进行主从同步也会导致同步失败，原因是两张表的字符集不同。

##### 使用utf8mb4导致索引键超长问题

&emsp;&emsp;使用了字符集utf8mb4之后，最容易引起索引键超长的问题。InnoDB有单个索引最大字节数768的限制，而字段上定义的数字表示能存储的字符数，例如`varchar(200)`表示能够存20个字符，索引定义是字符集类型最大长度算的，即utf8 最大存储3个字节、utf8mb4 最大存储4个字节，按照`varchar(200)`来算，utf8最大可存储600个字节，utf8mb4最大可存储800字节，utf8mb4超出索引最大字节数的限制，会抛出异常：`Specified key was too long; max key length is 767 bytes`，解决这个异常的办法就是，要么不使用索引，要么长度不能超过800，要么字符集不使用utf8mb4；

&emsp;&emsp;综上所述，①不使用索引显然不太理想；②不使用utf8mb4也不行，如果你要存储4个字节的字符，你就凉凉了。而且使用utf8mb4还会存在一个坑，后面会说；③就剩下减少索引长度了，不过可以合理的减少索引的长度，这是最优的方案，如果你想使用大字段，而且要使用索引，建议思考一下将这个字段进行优化。

##### 使用utf8mb4导致索引失效

&emsp;&emsp;假如在使用两个张表关联时，由于A表的字符集是utf8，B表的字符集是utf8mb4，这里需要做字符集转换，字符集转换遵循由小到大的原则，因为utf8mb4是utf8的超集，所以这里把utf8转换成utf8mb4。

&emsp;&emsp;转换之后，由于A表的关联字段仍然是utf8字符集，所以这个索引就被执行计划忽略了，最后只能选择全表扫描了。

##### 测试：

```sql
CREATE TABLE `t1` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` varchar(20) DEFAULT NULL,
`code` varchar(50) DEFAULT NULL,
PRIMARY KEY (`id`),
KEY `idx_code` (`code`),
KEY `idx_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8

CREATE TABLE `t2` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` varchar(20) DEFAULT NULL,
`code` varchar(50) DEFAULT NULL,
PRIMARY KEY (`id`),
KEY `idx_code` (`code`),
KEY `idx_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4

insert into t1 (`name`,`code`) values('aaa','aaa');
insert into t1 (`name`,`code`) values('bbb','bbb');
insert into t1 (`name`,`code`) values('ddd','ddd');
insert into t1 (`name`,`code`) values('eee','eee');

insert into t2 (`name`,`code`) values('aaa','aaa');
insert into t2 (`name`,`code`) values('bbb','bbb');
insert into t2 (`name`,`code`) values('ddd','ddd');
insert into t2 (`name`,`code`) values('eee','eee');
```

##### 查看执行计划：

```sql
explain select * from t2 left join t1 on t1.code = t2.code where t2.name = 'dddd';
```

```sql
******************* 1. row ****************
id: 1
select_type: SIMPLE
table: t2
partitions: NULL
type: ref
possible_keys: idx_name
key: idx_name
key_len: 83
ref: const
rows: 1
filtered: 100.00
Extra: NULL
****************** 2. row **************
id: 1
select_type: SIMPLE
table: t1
partitions: NULL
type: ALL
possible_keys: NULL
key: NULL
key_len: NULL
ref: NULL
rows: 5
filtered: 100.00
Extra: Using where; Using join buffer (Block Nested Loop)
2 rows in set, 1 warning (0.01 sec)
```



文章来自：https://blog.csdn.net/qq_17555933/article/details/101445526








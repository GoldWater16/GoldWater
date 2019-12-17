## 解释什么叫一致性 Hash 算法

#### 1、数据分片

✔︎ 先让我们看一个例子吧

我们经常会用 Redis 做缓存，把一些数据放在上面，以减少数据的压力。

当数据量少，访问压力不大的时候，通常一台Redis就能搞定，为了高可用，弄个主从也就足够了；

当数据量变大，并发量也增加的时候，把全部的缓存数据放在一台机器上就有些吃力了，毕竟一台机器的资源是有限的

通常我们会搭建集群环境，让数据尽量平均的放到每一台 Redis 中，比如我们的集群中有 4 台Redis。

那么如何把数据尽量平均地放到这 4 台Redis中呢？最简单的就是取模算法：

hash( key ) % N，N 为 Redis 的数量，在这里 N = 4 ;

![一致性Hash算法-images-01](https://github.com/GoldWater16/GoldWater/blob/master/precipitation/images/%E4%B8%80%E8%87%B4%E6%80%A7Hash%E7%AE%97%E6%B3%95-images/%E4%B8%80%E8%87%B4%E6%80%A7Hash%E7%AE%97%E6%B3%95-images-01.png?raw=true)

看起来非常得美好，因为依靠这样的方法，我们可以让数据平均存储到 4 台 Redis 中，当有新的请求过来的时候，我们也可以定位数据会在哪台 Redis 中，这样可以精确地查询到缓存数据。

#### 2、数据分片会遇到的问题

但是 4 台 Redis 不够了，需要再增加 4 台 Redis ；那么这个求余算法就会变成：hash( key ) % 8 ；

![一致性Hash算法-images-02](https://github.com/GoldWater16/GoldWater/blob/master/precipitation/images/%E4%B8%80%E8%87%B4%E6%80%A7Hash%E7%AE%97%E6%B3%95-images/%E4%B8%80%E8%87%B4%E6%80%A7Hash%E7%AE%97%E6%B3%95-images-02.png?raw=true)

那么可以想象一下，当前大部分缓存的位置都会是错误的，极端情况下，就会造成 缓存雪崩。

#### 3、一致性 Hash 算法

一致性 Hash 算法可以很好地解决这个问题，它的大概过程是这样的：

把 0 作为起点，2^32-1 作为终点，画一条直线，再把起点和终点重合，直线变成一个圆，方向是顺时针从小到大。0 的右侧第一个点是 1 ，然后是 2 ，以此类推。

对三台服务器的 IP 或其他关键字进行 hash 后对 2^32 取模，这样势必能落在这个圈上的某个位置，记为 Node1、Node2、Node3。

![一致性Hash算法-images-03](https://github.com/GoldWater16/GoldWater/blob/master/precipitation/images/%E4%B8%80%E8%87%B4%E6%80%A7Hash%E7%AE%97%E6%B3%95-images/%E4%B8%80%E8%87%B4%E6%80%A7Hash%E7%AE%97%E6%B3%95-images-03.png?raw=true)

然后对数据 key 进行相同的操作，势必也会落在圈上的某个位置；然后顺时针行走，可以找到某一个 Node，这就是这个 key 要储存的服务器。

![一致性Hash算法-images-04](https://github.com/GoldWater16/GoldWater/blob/master/precipitation/images/%E4%B8%80%E8%87%B4%E6%80%A7Hash%E7%AE%97%E6%B3%95-images/%E4%B8%80%E8%87%B4%E6%80%A7Hash%E7%AE%97%E6%B3%95-images-04.png?raw=true) 如果增加一台服务器或者删除一台服务器，只会影响 部分数据。

![一致性Hash算法-images-05](https://github.com/GoldWater16/GoldWater/blob/master/precipitation/images/%E4%B8%80%E8%87%B4%E6%80%A7Hash%E7%AE%97%E6%B3%95-images/%E4%B8%80%E8%87%B4%E6%80%A7Hash%E7%AE%97%E6%B3%95-images-05.png?raw=true)



但如果节点太少或分布不均匀的时候，容易造成 数据倾斜，也就是大部分数据会集中在某一台服务器上。



![一致性Hash算法-images-06](https://github.com/GoldWater16/GoldWater/blob/master/precipitation/images/%E4%B8%80%E8%87%B4%E6%80%A7Hash%E7%AE%97%E6%B3%95-images/%E4%B8%80%E8%87%B4%E6%80%A7Hash%E7%AE%97%E6%B3%95-images-06.png?raw=true)

为了解决数据倾斜问题，一致性 Hash 算法提出了【虚拟节点】，会对每一个服务节点计算多个哈希，然后放到圈上的不同位置。



![一致性Hash算法-images-07](https://github.com/GoldWater16/GoldWater/blob/master/precipitation/images/%E4%B8%80%E8%87%B4%E6%80%A7Hash%E7%AE%97%E6%B3%95-images/%E4%B8%80%E8%87%B4%E6%80%A7Hash%E7%AE%97%E6%B3%95-images-07.png?raw=true)

**当然我们也可以发现，一致性 Hash 算法，也只是解决大部分数据的问题。**
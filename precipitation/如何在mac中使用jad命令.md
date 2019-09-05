# 如何在mac中使用jad命令

#### 1、下载`jad`

[下载地址](https://varaneckas.com/jad/)：https://varaneckas.com/jad/

##### 2、解压`jad`

在`/usr/local/`路径下创建相关文件夹，例如：`/usr/local/jad/src`

##### 3、配置`bash_profile`

使用命令：`vim ~/.bash_profile`，添加`export JAD_HOME=/usr/local/jad/src export PATH=$JAD_HOME:$PATH`

##### 4、刷新`bash_profile`

使用命令：`source ~/.bash_profile`

##### 5、使用`jad`反编译`class`

使用命令：`jad T.class`

```shell
-rw-r--r--   1 lcp  staff     927  9  5 16:13 T.class
-rw-r--r--   1 lcp  staff     933  9  5 17:21 T.jad
```

打开`T.jad`

```java
public final class T extends Enum
{

    public static T[] values()
    {
        return (T[])$VALUES.clone();
    }

    public static T valueOf(String s)
    {
        return (T)Enum.valueOf(com/photon/member/T, s);
    }

    private T(String s, int i)
    {
        super(s, i);
    }

    public static final T SPRING;
    public static final T SUMMER;
    public static final T AUTUMN;
    public static final T WINTER;
    private static final T $VALUES[];

    static
    {
        SPRING = new T("SPRING", 0);
        SUMMER = new T("SUMMER", 1);
        AUTUMN = new T("AUTUMN", 2);
        WINTER = new T("WINTER", 3);
        $VALUES = (new T[] {
            SPRING, SUMMER, AUTUMN, WINTER
        });
    }
}
```


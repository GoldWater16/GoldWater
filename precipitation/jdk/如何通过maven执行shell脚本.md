## 如何通过maven执行shell脚本

在maven的pom.xml文件中添加：

```xml
<build>
        <finalName>demo</finalName>
        <plugins>
            <plugin>
                <artifactId>exec-maven-plugin</artifactId>
                <groupId>org.codehaus.mojo</groupId>
                <executions>
                    <execution>
                        <id>uncompress</id>
                        <phase>install</phase>
                        <goals>
                            <goal>exec</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <executable>${basedir}/../check-style/test.sh</executable>
                </configuration>
            </plugin>
        </plugins>
    </build>
```



我的`test.sh`脚本：

```shell
#!/bin/bash
## 获取当前目录路径
check_style_path=$(cd `dirname $0`; pwd)
## 获取上级目录路径
project_path=$(dirname "$PWD")
echo $project_path
echo $check_style_path
cp $check_style_path/pre-commit $project_path/.git/hooks/

```


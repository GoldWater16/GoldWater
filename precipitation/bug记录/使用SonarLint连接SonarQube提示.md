#使用SonarLint连接SonarQube提示

#### 异常提示：

```
Analyzers Not Loaded

The following plugins do not meet the required minimum versions, please upgrade them in SonarQube:javascript(installed:3.2.0.5506,minimum:4.0.0.5862),java(installed:4.15.0.12310,minimum:5.1.0.13090),php(installed:2.11.0.2485,minimum:2.12.0.2871),python(installed:1.8.0.1496,minimum:1.9.1.2080)
```

##### 说明：

&emsp;&emsp;其实你看上面的提示就明白是版本号太低了，但是我一开始看得挺奇怪的，java版本居然会有4.15.0.12310，一脸懵逼，最后去sonarQube官网查了一波资料，发现了新大陆，可以参考这个链接：https://docs.sonarqube.org/latest/instance-administration/plugin-version-matrix/

##### 解决办法：

&emsp;&emsp;升级sonarQube的版本就可以解决这个问题了。哈哈 ~~~
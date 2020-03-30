## 简单实现基于Spring实现责任链(二)

### 场景：

&emsp;&emsp;OA审核；员工提交请假条，需要组长、主管、老板、HR等审批。

话不多说了，直接上代码看吧，

首先，构建一个抽象的审批类->`ProcessChain`

```java
package com.example.springchaindemo.chain.oa;

/**
 * @projectName: spring-chain-demo
 * @className: ProcessChain
 * @description:抽象审批类
 * @author: HuGoldWater
 * @create: 2020-03-30 17:36
 **/
public abstract class ProcessChain {

    private ProcessChain next;

    public void doProcess(){
        String process = process();
        System.out.println(process);
        if (next != null) {
            next.doProcess();
        }
    }

    public void setNext(ProcessChain next) {
        this.next = next;
    }

    public abstract String process();
}
```

然后，再创建项目经理、主管、老板Leader类，->`TeamLeaderChain`、`SupervisorLeaderChain`、`BossLeaderChain`

```java
package com.example.springchaindemo.chain.oa;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @projectName: spring-chain-demo
 * @className: TeamLeaderChain
 * @description:项目经理审批类
 * @author: HuGoldWater
 * @create: 2020-03-30 17:41
 **/
@Component("team")
@Order(1)
public class TeamLeaderChain extends ProcessChain {
    @Override
    public String process() {
        return "项目经理审核通过";
    }
}
```

```java
package com.example.springchaindemo.chain.oa;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @projectName: spring-chain-demo
 * @className: SupervisorLeaderChain
 * @description:主管审批类
 * @author: HuGoldWater
 * @create: 2020-03-30 18:17
 **/
@Component("supervisor")
@Order(2)
public class SupervisorLeaderChain extends ProcessChain {
    @Override
    public String process() {
        return "主管审核通过";
    }
}
```

```java
package com.example.springchaindemo.chain.oa;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @projectName: spring-chain-demo
 * @className: BossLeaderChain
 * @description:老板审批类
 * @author: HuGoldWater
 * @create: 2020-03-30 17:42
 **/
@Component("boss")
@Order(3)
public class BossLeaderChain extends ProcessChain {
    @Override
    public String process() {
        return "boss 审核通过";
    }
}
```

初始化所有Leader类，并组成链式->`InitProcessChain`

```java
package com.example.springchaindemo.chain.oa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @projectName: spring-chain-demo
 * @className: InitProcessChain
 * @description:
 * @author: HuGoldWater
 * @create: 2020-03-30 17:43
 **/
@Configuration
public class InitProcessChain {

    @Autowired
    private List<ProcessChain> processChainsList;

    @PostConstruct
    private void InitProcessChain() {
        int size = processChainsList.size();
        for (int i = 0; i < size; i++) {
            if(i == size -1 ){
                processChainsList.get(i).setNext(null);
            }else{
                processChainsList.get(i).setNext(processChainsList.get(i+1));
            }
        }
    }
}
```

为了方便扩展与维护，需要创建一个枚举类->`LeaderEnum`

```java
package com.example.springchaindemo.chain.oa;

/**
 * @projectName: spring-chain-demo
 * @className: LeaderEnum
 * @description:各位领导的枚举类
 * @author: HuGoldWater
 * @create: 2020-03-30 18:09
 **/
public enum LeaderEnum {
    TEAM_LEADER(0,"组长"),
    SUPERVISOR_LEADER(1,"主管"),
    BOSS_LEADER(2,"老板");

    LeaderEnum(int code, String remark) {
        this.code = code;
        this.remark = remark;
    }

    private int code;
    private String remark;

    public int getCode() {
        return code;
    }

    public String getRemark() {
        return remark;
    }
}
```

创建OA审批工厂类->`OaFactory`

```java
package com.example.springchaindemo.factory;

import com.example.springchaindemo.chain.oa.LeaderEnum;
import com.example.springchaindemo.chain.oa.ProcessChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @projectName: spring-chain-demo
 * @className: OaFactory
 * @description:OA审批工厂
 * @author: HuGoldWater
 * @create: 2020-03-30 18:05
 **/
@Component
public class OaFactory {

    @Autowired
    private List<ProcessChain> processChains;

    public void doProcess(LeaderEnum leaderEnum){
        processChains.get(leaderEnum.getCode()).doProcess();
    }
}
```

OA审批测试类->`ProcessController`

⚠️注意：`oaFactory.doProcess(LeaderEnum.TEAM_LEADER);`如果请假人是主管，这里就需要改成`LeaderEnum.SUPERVISOR_LEADER`

```java
package com.example.springchaindemo.controller;

import com.example.springchaindemo.chain.oa.LeaderEnum;
import com.example.springchaindemo.factory.OaFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @projectName: spring-chain-demo
 * @className: ProcessController
 * @description:OA审批测试类
 * @author: HuGoldWater
 * @create: 2020-03-30 17:52
 **/
@RestController
public class ProcessController {

    @Autowired
    private OaFactory oaFactory;

    @GetMapping("/process")
    public void process(){
        oaFactory.doProcess(LeaderEnum.TEAM_LEADER);
    }
}
```

> 源码地址：https://github.com/GoldWater16/GoldWater/tree/master/project/spring-chain-demo


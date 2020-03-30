## 简单实现基于Spring实现责任链(一)

### 场景：

&emsp;&emsp;**短信发送**；有很多规则，例如：开关、发送次数、黑名单、手机格式校验等等。

&emsp;&emsp;现在，下面基于这种场景来实现过滤式责任链，如果不使用责任链，就会产生很多if else存在，并且下次要添加另外一种规则，需要改动原来的代码，不符合开闭原则。

&emsp;&emsp;这里为了演示，实现起来会比较简单，重点是要理解责任链这个东西的使用方法。

#### 短信发送场景：

##### 短信发送`SmsDTO`实体对象：

```java
package com.example.springchaindemo.entity;

/**
 * @projectName: spring-chain-demo
 * @className: SmsDTO
 * @description: 发送短信DTO
 * @author: HuGoldWater
 * @create: 2020-03-30 15:35
 **/
public class SmsDTO {
    /**手机号**/
    private String mobile;
    /**短信验证码**/
    private String verificationCode;

    public String getMobile() {
        return mobile;
    }

    public SmsDTO setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public SmsDTO setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
        return this;
    }
}
```

##### 构建一个`Filter`接口：

```java
package com.example.springchaindemo.chain.sms;

import com.example.springchaindemo.entity.SmsDTO;

/**
 * @projectName: spring-chain-demo
 * @className: Filter
 * @description:拦截器
 * @author: HuGoldWater
 * @create: 2020-03-30 15:24
 **/
public interface Filter {

    boolean filter(SmsDTO smsDTO);
}
```

##### 构建一个开关拦截器`SwitchFilter`类：

```java
package com.example.springchaindemo.chain.sms;

import com.example.springchaindemo.entity.SmsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @projectName: spring-chain-demo
 * @className: SwitchFilter
 * @description:
 * @author: HuGoldWater
 * @create: 2020-03-30 15:33
 **/
@Component
@Order(1)
public class SwitchFilter implements Filter {

    @Autowired
    private Environment environment;

    //是否发送短信
    private static final String SWITCH_FLAG = "Y";

    @Override
    public boolean filter(SmsDTO smsDTO) {
        if (SWITCH_FLAG.equals(environment.getProperty("switch.flag"))) {
            return true;
        }
        System.out.println("短信发送通道已关闭");
        return false;
    }
}
```

##### 手机号格式校验拦截器`MobileCheckFilter`类：

```java
package com.example.springchaindemo.chain.sms;

import com.example.springchaindemo.entity.SmsDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @projectName: spring-chain-demo
 * @className: MobileCheckFilter
 * @description:手机号格式校验
 * @author: HuGoldWater
 * @create: 2020-03-30 15:43
 **/
@Component
@Order(2)
public class MobileCheckFilter implements Filter {

    //正则表达式
    private static final String REGEX =  "^((13[0-9])|(14[579])|(15([0-3,5-9]))|(16[6])|(17[0135678])|(18[0-9]|19[89]))\\d{8}$";

    @Override
    public boolean filter(SmsDTO smsDTO) {
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(smsDTO.getMobile());
        if (matcher.matches()) {
            return true;
        }
        System.out.println("手机号有误");
        return false;
    }
}
```

##### 手机号黑名单拦截器`BlackListFilter`类：

```java
package com.example.springchaindemo.chain.sms;

import com.example.springchaindemo.entity.SmsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @projectName: spring-chain-demo
 * @className: BlackListFilter
 * @description:手机黑名单过滤
 * @author: HuGoldWater
 * @create: 2020-03-30 15:49
 **/
@Component
@Order(3)
public class BlackListFilter implements Filter {

    @Autowired
    private Environment environment;

    @Override
    public boolean filter(SmsDTO smsDTO) {
        String mobile = environment.getProperty("mobile.black.list");
        if (StringUtils.isEmpty(mobile)) {
            return true;
        }
        if(mobile.equals(smsDTO.getMobile())){
            System.out.println("手机号在黑名单中");
           return false;
        }
        return true;
    }
}
```

##### 短信发送次数限制拦截器`SendLimitFilter`类：

```java
package com.example.springchaindemo.chain.sms;

import com.example.springchaindemo.entity.SmsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @projectName: spring-chain-demo
 * @className: SendLimitFilter
 * @description:短信发送次数限制
 * @author: HuGoldWater
 * @create: 2020-03-30 15:55
 **/
@Component
@Order(4)
public class SendLimitFilter implements Filter {

    @Autowired
    private Environment environment;
    // 模拟短信发送次数缓存
    private Map<String,Integer> smsSendMap = new ConcurrentHashMap<>();

    @Override
    public boolean filter(SmsDTO smsDTO) {
        String num = environment.getProperty("send.frequency.limit");
        if(StringUtils.isEmpty(num)){//没有限制条件
            return true;
        }
        Integer mobileNum = smsSendMap.get(smsDTO.getMobile());

        if(mobileNum != null && Integer.parseInt(num) <= mobileNum){
            System.out.println("手机号已被限制");
            return false;
        }
        if(mobileNum == null){
            smsSendMap.put(smsDTO.getMobile(),new Integer(1));
        }else{
            smsSendMap.put(smsDTO.getMobile(),new Integer(mobileNum) + 1);
        }
        return true;
    }
}
```

##### 短信发送工厂`SmsSendFactory`类：

```java
package com.example.springchaindemo.factory;

import com.example.springchaindemo.chain.sms.Filter;
import com.example.springchaindemo.entity.SmsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @projectName: spring-chain-demo
 * @className: SmsSendFactory
 * @description:
 * @author: HuGoldWater
 * @create: 2020-03-30 16:03
 **/
@Component
public class SmsSendFactory {

    @Autowired
    private List<Filter> filters;

    /**
     * 校验手机号
     *
     * @param smsDTO
     * @return
     */
    public boolean check(SmsDTO smsDTO) {
        for (Filter filter : filters) {
            if (!filter.filter(smsDTO)) {
                return false;
            }
        }
        return true;
    }
}
```

##### 短信发送测试类`SmsSendController`类：

```java
package com.example.springchaindemo.controller;

import com.example.springchaindemo.entity.SmsDTO;
import com.example.springchaindemo.factory.SmsSendFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @projectName: spring-chain-demo
 * @className: SmsSendController
 * @description:
 * @author: HuGoldWater
 * @create: 2020-03-30 16:10
 **/
@RestController
public class SmsSendController {

    @Autowired
    private SmsSendFactory smsSendFactory;

    @GetMapping("/send/{mobile}")
    public String send(@PathVariable("mobile") final String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return "mobile is null";
        }
        String code = Double.toString((Math.random() * 9) * 1000);
        SmsDTO smsDTO = new SmsDTO().setMobile(mobile).setVerificationCode(code);
        if (!smsSendFactory.check(smsDTO)) {
            return "fail";
        }
        return "success";
    }
}
```

> 源码地址：https://github.com/GoldWater16/GoldWater/tree/master/project/spring-chain-demo

---


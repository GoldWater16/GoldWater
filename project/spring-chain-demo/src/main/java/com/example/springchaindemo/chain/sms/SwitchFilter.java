package com.example.springchaindemo.chain.sms;

import com.example.springchaindemo.entity.SmsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @projectName: spring-chain-demo
 * @className: SwitchFilter
 * @description:短信发送开关
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

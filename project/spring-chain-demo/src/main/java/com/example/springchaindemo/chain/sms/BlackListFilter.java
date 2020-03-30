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

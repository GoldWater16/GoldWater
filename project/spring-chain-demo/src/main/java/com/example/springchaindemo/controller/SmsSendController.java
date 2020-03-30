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

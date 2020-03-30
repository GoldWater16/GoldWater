package com.example.springchaindemo.chain;

import com.example.springchaindemo.entity.SmsDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @projectName: spring-chain-demo
 * @className: MobileCheckFilter
 * @description:
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

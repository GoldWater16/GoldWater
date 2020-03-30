package com.example.springchaindemo.factory;

import com.example.springchaindemo.chain.Filter;
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

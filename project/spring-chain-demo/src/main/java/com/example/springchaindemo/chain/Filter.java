package com.example.springchaindemo.chain;

import com.example.springchaindemo.entity.SmsDTO;

/**
 * @projectName: spring-chain-demo
 * @className: Filter
 * @description:
 * @author: HuGoldWater
 * @create: 2020-03-30 15:24
 **/
public interface Filter {

    boolean filter(SmsDTO smsDTO);
}

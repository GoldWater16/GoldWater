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

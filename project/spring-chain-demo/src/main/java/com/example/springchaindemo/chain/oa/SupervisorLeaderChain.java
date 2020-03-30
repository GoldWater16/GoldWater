package com.example.springchaindemo.chain.oa;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @projectName: spring-chain-demo
 * @className: SupervisorLeaderChain
 * @description:
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

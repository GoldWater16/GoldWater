package com.example.springchaindemo.chain.oa;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @projectName: spring-chain-demo
 * @className: TeamLeaderChain
 * @description:
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

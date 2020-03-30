package com.example.springchaindemo.factory;

import com.example.springchaindemo.chain.oa.LeaderEnum;
import com.example.springchaindemo.chain.oa.ProcessChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @projectName: spring-chain-demo
 * @className: OaFactory
 * @description:OA审批工厂
 * @author: HuGoldWater
 * @create: 2020-03-30 18:05
 **/
@Component
public class OaFactory {

    @Autowired
    private List<ProcessChain> processChains;

    public void doProcess(LeaderEnum leaderEnum){
        processChains.get(leaderEnum.getCode()).doProcess();
    }
}

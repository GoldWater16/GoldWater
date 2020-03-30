package com.example.springchaindemo.controller;

import com.example.springchaindemo.chain.oa.LeaderEnum;
import com.example.springchaindemo.factory.OaFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @projectName: spring-chain-demo
 * @className: ProcessController
 * @description:OA审批测试类
 * @author: HuGoldWater
 * @create: 2020-03-30 17:52
 **/
@RestController
public class ProcessController {

    @Autowired
    private OaFactory oaFactory;

    @GetMapping("/process")
    public void process(){
        oaFactory.doProcess(LeaderEnum.SUPERVISOR_LEADER);
    }
}

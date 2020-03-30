package com.example.springchaindemo.chain.oa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @projectName: spring-chain-demo
 * @className: InitProcessChain
 * @description:
 * @author: HuGoldWater
 * @create: 2020-03-30 17:43
 **/
@Configuration
public class InitProcessChain {

    @Autowired
    private List<ProcessChain> processChainsList;

    @PostConstruct
    private void InitProcessChain() {
        int size = processChainsList.size();
        for (int i = 0; i < size; i++) {
            if(i == size -1 ){
                processChainsList.get(i).setNext(null);
            }else{
                processChainsList.get(i).setNext(processChainsList.get(i+1));
            }
        }
    }
}

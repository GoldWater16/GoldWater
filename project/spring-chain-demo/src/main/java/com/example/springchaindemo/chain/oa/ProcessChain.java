package com.example.springchaindemo.chain.oa;

/**
 * @projectName: spring-chain-demo
 * @className: ProcessChain
 * @description:
 * @author: HuGoldWater
 * @create: 2020-03-30 17:36
 **/
public abstract class ProcessChain {

    private ProcessChain next;

    public void doProcess(){
        String process = process();
        System.out.println(process);
        if (next != null) {
            next.doProcess();
        }
    }

    public void setNext(ProcessChain next) {
        this.next = next;
    }

    public abstract String process();
}

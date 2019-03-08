package com.hjs.builder;

/**
 * @author hjs
 * @date 2019/3/72:13
 * @Dec
 */
public class ConcreteBuilderB extends Builder {
    @Override
    public void buildPartA() {
        System.out.print("人是由上半身");
        product.setPartA("上半身");
    }

    @Override
    public void buildPartB() {
        System.out.print("主体");
        product.setPartB("主体");
    }

    @Override
    public void buildPartC() {
        System.out.print("下半身");
        product.setPartC("下半身");
    }
}

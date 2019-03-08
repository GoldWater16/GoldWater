package com.hjs.builder;

/**
 * 子类构建具体的东西
 *
 * @author hjs
 * @date 2019/3/72:04
 * @Dec
 */
public class ConcreteBuilderA extends Builder {
    @Override
    public void buildPartA() {
        System.out.print("汽车是由轮子、");
        product.setPartA("轮子");
    }

    @Override
    public void buildPartB() {
        System.out.print("车身、");
        product.setPartB("车身");
    }

    @Override
    public void buildPartC() {
        System.out.print("方向盘");
        product.setPartC("方向盘");
    }
}

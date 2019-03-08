package com.hjs.abstractFactory;

/**
 * @author hjs
 * @date 2019/3/71:28
 * @Dec
 */
public class ConcreteFactory2 implements AbstractProduct {

    @Override
    public AbstractProductA createProductA() {
        return new ConcreteProductA2();
    }

    @Override
    public AbstractProductB createProductB() {
        return new ConcreteProductB2();
    }
}

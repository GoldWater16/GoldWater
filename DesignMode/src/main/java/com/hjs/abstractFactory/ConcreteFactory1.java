package com.hjs.abstractFactory;

/**
 * @author hjs
 * @date 2019/3/71:27
 * @Dec
 */
public class ConcreteFactory1 implements AbstractProduct {

    @Override
    public AbstractProductA createProductA() {
        return new ConcreteProductA1();
    }

    @Override
    public AbstractProductB createProductB() {
        return new ConcreteProductB1();
    }
}

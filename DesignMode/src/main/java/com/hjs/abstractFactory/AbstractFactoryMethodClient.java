package com.hjs.abstractFactory;

/**
 * @author hjs
 * @date 2019/3/71:29
 * @Dec
 */
public class AbstractFactoryMethodClient {

    public static void main(String[] args) {
        AbstractProduct product = new ConcreteFactory1();
        AbstractProductA productA = product.createProductA();
        AbstractProductB productB = product.createProductB();
        productA.methodDiff();
        productA.methodSame();

        productB.methodDiff();
        productB.methodSame();
    }
}

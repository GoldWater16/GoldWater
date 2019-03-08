package com.hjs.factoryMethod;


/**
 * 产品B
 *
 * @author hjs
 * @date 2019/3/623:20
 * @Dec
 */
public class ConcreteProductB extends FactoryMethodProduct {

    @Override
    public void methodDiff() {
        System.out.println("生产产品B");
    }
}

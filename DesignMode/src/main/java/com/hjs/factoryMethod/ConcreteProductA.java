package com.hjs.factoryMethod;

/**
 * 产品A
 *
 * @author hjs
 * @date 2019/3/623:19
 * @Dec
 */
public class ConcreteProductA extends FactoryMethodProduct {
    @Override
    public void methodDiff() {
        System.out.println("生成产品A");
    }
}

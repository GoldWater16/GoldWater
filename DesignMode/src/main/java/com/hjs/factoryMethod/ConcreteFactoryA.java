package com.hjs.factoryMethod;

/**
 * @author hjs
 * @date 2019/3/70:51
 * @Dec
 */
public class ConcreteFactoryA implements FactoryMethodFactory {

    @Override
    public FactoryMethodProduct getProduct() {
        return new ConcreteProductA();
    }
}

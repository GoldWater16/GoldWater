package com.hjs.factoryMethod;

/**
 * @author hjs
 * @date 2019/3/70:51
 * @Dec
 */
public class ConcreteFactoryB implements FactoryMethodFactory {

    @Override
    public FactoryMethodProduct getProduct() {
        return new ConcreteProductB();
    }
}

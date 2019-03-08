package com.hjs.factoryMethod;

/**
 * @author hjs
 * @date 2019/3/70:52
 * @Dec
 */
public class FactoryMethodClient {

    public static void main(String[] args) {
        //创建A产品工厂
        FactoryMethodFactory factory = new ConcreteFactoryA();
        //获取对应的产品
        FactoryMethodProduct product = factory.getProduct();
        product.methodSame();
        product.methodDiff();


    }
}

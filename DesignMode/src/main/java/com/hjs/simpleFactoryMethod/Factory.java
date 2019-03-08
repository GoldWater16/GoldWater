package com.hjs.simpleFactoryMethod;

/**
 * 生产产品工厂
 *
 * @author hjs
 * @date 2019/3/623:21
 * @Dec
 */
public class Factory {

    public static Product getProduct(String arg) {
        Product product = null;
        if ("A".equalsIgnoreCase(arg)) {
            product = new ConcreteProductA();
        } else if ("B".equalsIgnoreCase(arg)) {
            product = new ConcreteProductB();
        }
        return product;
    }
}

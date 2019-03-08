package com.hjs.simpleFactoryMethod;

/**
 * @author hjs
 * @date 2019/3/623:24
 * @Dec
 */
public class SimpleFactoryMethodClient {

    public static void main(String[] args) {
        Product a = Factory.getProduct("A");
        a.methodSame();
        a.methodDiff();
    }
}

package com.hjs.simpleFactoryMethod;

/**
 * 产品的抽象方法
 *
 * @author hjs
 * @date 2019/3/623:17
 * @Dec
 */
public abstract class Product {
    /**
     * 所有产品的公共业务方法
     */
    public void methodSame() {
        System.out.println("所有产品的公共业务方法");
    }

    public abstract void methodDiff();
}

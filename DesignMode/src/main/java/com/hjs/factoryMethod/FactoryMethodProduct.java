package com.hjs.factoryMethod;

/**
 * @author hjs
 * @date 2019/3/70:47
 * @Dec
 */
public abstract class FactoryMethodProduct {

    /**
     * 所有产品的公共业务方法
     */
    public void methodSame() {
        System.out.println("所有产品的公共业务方法");
    }

    public abstract void methodDiff();
}

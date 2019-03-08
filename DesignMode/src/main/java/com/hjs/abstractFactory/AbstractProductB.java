package com.hjs.abstractFactory;

/**
 * @author hjs
 * @date 2019/3/71:23
 * @Dec
 */
public abstract class AbstractProductB {

    public abstract void methodDiff();

    public void methodSame() {
        System.out.println("公共业务逻辑B");
    }
}

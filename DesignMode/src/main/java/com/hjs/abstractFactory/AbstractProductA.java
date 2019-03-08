package com.hjs.abstractFactory;

/**
 * @author hjs
 * @date 2019/3/71:22
 * @Dec 抽象产品A类
 */
public abstract class AbstractProductA {

    public abstract void methodDiff();

    public void methodSame() {
        System.out.println("公共业务逻辑A");
    }
}

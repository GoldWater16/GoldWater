package com.hjs.proxy;

/**
 * 真正做事的类
 *
 * @author hjs
 * @date 2019/3/622:52
 * @Dec
 */
public class RealSubject implements Subject {

    @Override
    public void request() {
        System.out.println("真正做事的类");
    }
}

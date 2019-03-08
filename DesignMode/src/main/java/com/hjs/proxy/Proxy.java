package com.hjs.proxy;

/**
 * 代理类
 *
 * @author hjs
 * @date 2019/3/622:53
 * @Dec
 */
public class Proxy implements Subject {

    //维持一个真正做事对象的引用
    private RealSubject realSubject = new RealSubject();

    public void preRequest() {
        System.out.println("调用真正做事的方法前置处理");
    }

    @Override
    public void request() {
        preRequest();
        realSubject.request();
        postRequest();
    }

    public void postRequest() {
        System.out.println("调用真正做事的方法后置处理");
    }
}

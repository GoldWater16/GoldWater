package com.hjs.singleton;

/**
 * @author hjs
 * @date 2019/3/722:18
 * @Dec 饿汉式单例模式
 */
public class EagerSingleton {
    public static final EagerSingleton instance = new EagerSingleton();

    public static EagerSingleton getInstance() {
        return instance;
    }
}

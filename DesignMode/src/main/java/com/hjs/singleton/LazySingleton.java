package com.hjs.singleton;

/**
 * @author hjs
 * @date 2019/3/722:19
 * @Dec 懒汉式单例
 */
public class LazySingleton {
    public static LazySingleton instance = null;

    public static LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }
}

package com.hjs.singleton;

/**
 * @author hjs
 * @date 2019/3/722:22
 * @Dec 懒汉式双重检查锁模式
 */
public class LazySynchronizedSingleton {
    public static LazySynchronizedSingleton instance = null;

    public static LazySynchronizedSingleton getInstance() {
        if (instance == null) {
            synchronized (LazySynchronizedSingleton.class) {
                if (instance == null) {
                    instance = new LazySynchronizedSingleton();
                }
            }
        }
        return instance;
    }
}

package com.hjs.proxy;

/**
 * @author hjs
 * @date 2019/3/622:56
 * @Dec
 */
public class ProxyClient {

    public static void main(String[] args) {
        Subject subject = new Proxy();
        subject.request();
    }
}

package com.golden.nettystudy.reactor;

import java.io.IOException;

/**
 * @author: HuGoldWater
 * @description:
 */
public class ReactorMain {

    public static void main(String[] args) throws IOException {
        new Thread(new Reactor(8888),"Main-Thread").start();
    }

}

package com.golden.nettystudy.reactorthread;

import java.io.IOException;

/**
 * @author: HuGoldWater
 * @description:
 */
public class MultiReactorMain {

    public static void main(String[] args) throws IOException {
        new Thread(new MultiReactor(8888),"Multi-Main-Thread").start();
    }

}

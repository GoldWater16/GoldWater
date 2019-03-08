package com.hjs.adapter;

/**
 * @author hjs
 * @date 2019/3/723:04
 * @Dec
 */
public class AdapterClient {
    public static void main(String[] args) {
//        TwoJackTarget twoJackTarget = new ThreeAdapter();
//        twoJackTarget.change();
        TwoJackTarget twoJackTarget = new FourAdapter();
        twoJackTarget.change();
    }
}

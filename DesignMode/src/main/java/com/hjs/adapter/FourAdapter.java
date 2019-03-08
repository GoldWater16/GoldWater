package com.hjs.adapter;

/**
 * @author hjs
 * @date 2019/3/723:12
 * @Dec 适配器类：适配四脚插头类
 */
public class FourAdapter extends TwoJackTarget {
    private FourJackAdapter fourJackAdapter;

    public FourAdapter(){
        fourJackAdapter = new FourJackAdapter();
    }
    @Override
    public void change() {
        fourJackAdapter.doJack();
        System.out.println("已经将四脚插头转成两脚插头");
    }
}

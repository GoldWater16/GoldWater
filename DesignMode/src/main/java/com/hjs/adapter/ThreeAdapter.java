package com.hjs.adapter;

/**
 * @author hjs
 * @date 2019/3/722:58
 * @Dec 三脚插头适配器类
 */
public class ThreeAdapter extends TwoJackTarget{

    private ThreeJackAdaptee jackAdaptee;

    public ThreeAdapter(){
        jackAdaptee = new ThreeJackAdaptee();
    }

    @Override
    public void change() {
        jackAdaptee.doJack();
        System.out.println("已经将三脚插头转成两脚插头");
    }
}

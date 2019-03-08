package com.hjs.builder;

/**
 * @author hjs
 * @date 2019/3/71:53
 * @Dec 抽象类，具体由子类实现
 */
public abstract class Builder {
    protected BuilderProduct product = new BuilderProduct();

    public abstract void buildPartA();

    public abstract void buildPartB();

    public abstract void buildPartC();

    public BuilderProduct getResult() {
        return product;
    }
}

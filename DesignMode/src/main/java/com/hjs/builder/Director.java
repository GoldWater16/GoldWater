package com.hjs.builder;

/**
 * @author hjs
 * @date 2019/3/72:06
 * @Dec
 */
public class Director {

    private Builder builder;

    public Director(Builder builder) {
        this.builder = builder;
    }

    public BuilderProduct construct() {
        builder.buildPartA();
        builder.buildPartB();
        builder.buildPartC();
        return builder.getResult();
    }
}

package com.hjs.builder;

/**
 * @author hjs
 * @date 2019/3/72:09
 * @Dec
 */
public class BuilderClient {

    public static void main(String[] args) {
        Builder builder = new ConcreteBuilderB();
        Director director = new Director(builder);
        BuilderProduct product = director.construct();
    }
}

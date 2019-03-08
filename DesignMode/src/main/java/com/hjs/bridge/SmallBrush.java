package com.hjs.bridge;

/**
 * @author hjs
 * @date 2019/3/80:14
 * @Dec
 */
public class SmallBrush extends AbstractBrush {

    @Override
    public void draw(String name) {
        color.coloring();
        System.out.print(name+"着色");
    }
}

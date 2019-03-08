package com.hjs.bridge;

/**
 * @author hjs
 * @date 2019/3/80:12
 * @Dec
 */
public abstract class AbstractBrush {
    protected Color color;

    public void setColor(Color color) {
        this.color = color;
    }

    public abstract void draw(String name);
}

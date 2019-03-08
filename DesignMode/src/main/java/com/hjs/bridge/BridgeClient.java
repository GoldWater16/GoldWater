package com.hjs.bridge;

/**
 * @author hjs
 * @date 2019/3/80:22
 * @Dec
 */
public class BridgeClient {

    public static void main(String[] args) {
        AbstractBrush brush = new MaxBrush();
        Color color = new GreenColor();
        brush.setColor(color);
        brush.draw("大毛笔");
        //输出：正在使用绿色大毛笔着色
    }
}

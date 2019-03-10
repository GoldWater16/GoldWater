package com.hjs.practice;

import java.util.Stack;

/**
 * @author hjs
 * @date 2019/3/1015:05
 * @Dec 用两个栈实现队列
 */
public class StackQueueDemo {

    static Stack<Integer> in = new Stack<>();
    static Stack<Integer> out = new Stack<>();

    private static void push(int p) {
        in.push(p);
    }

    private static int pop() {
        if (out.isEmpty()) {
            while (!in.isEmpty()) {
                out.push(in.pop());
            }
        }
        if (out.isEmpty()) {
            throw new RuntimeException("out stack is null");
        }
        return out.pop();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            push(i);
        }
        while (true) {
            System.out.println(pop());
        }
    }
}

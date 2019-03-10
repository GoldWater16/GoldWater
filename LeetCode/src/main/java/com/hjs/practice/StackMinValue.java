package com.hjs.practice;

import java.util.Stack;

/**
 * @author hjs
 * @date 2019/3/10 15:36
 * @Dec 定义栈的数据结构，请在该类型中的实现一个能够得到栈最小元素的min值
 * 解题思路：维护两个栈：一个数据栈；一个最小元素栈
 */
public class StackMinValue {

    static Stack<Integer> dataStack = new Stack<>();
    static Stack<Integer> minStack = new Stack<>();

    /**
     * 往栈中存放数据
     *
     * @param data
     */
    private static void push(int data) {
        dataStack.push(data);
        minStack.push(minStack.isEmpty() ? data : Math.min(data, minStack.peek()));
    }

    /**
     * 取出元素
     *
     * @return
     */
    private static int top() {
        return dataStack.pop();
    }

    /**
     * 取出最小元素
     *
     * @return
     */
    private static int min() {
        return minStack.pop();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            push(i);
        }
        System.out.println(min());
    }
}

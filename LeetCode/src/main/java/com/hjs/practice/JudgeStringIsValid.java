package com.hjs.practice;

import java.util.Stack;

/**
 * @author hjs
 * @date 2019/3/1014:44
 * @Dec 给定一个只包括'(',')','[',']','{','}'的字符串，判断字符串是否有效
 */
public class JudgeStringIsValid {
    private static boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();
        char[] chars = s.toCharArray();
        for (char c : chars) {
            if (stack.size() == 0) {
                stack.push(c);//往栈里放元素
                //获取当前的字符和栈里最近的一个字符进行匹配，如果匹配上，则取出
            } else if (isSym(stack.peek(), c)) {
                stack.pop();//从栈中取出数据
            } else {
                stack.push(c);
            }
        }
        //如果栈中没有数据，则客户端给定的字符串是有效的
        return stack.size() == 0;
    }

    private static boolean isSym(char c1, char c2) {
        return (c1 == '(' && c2 == ')') || (c1 == '[' && c2 == ']') || (c1 == '{' && c2 == '}');
    }

    public static void main(String[] args) {
        String string = "])}{([";
        System.out.println(isValid(string));
    }
}

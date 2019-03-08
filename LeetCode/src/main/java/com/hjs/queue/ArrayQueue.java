package com.hjs.queue;

/**
 * 基于数组实现的队列
 */
public class ArrayQueue {

    private Object[] objects;//存放数组
    private int start;//开始下标
    private int end;//结束下标

    public ArrayQueue() {
        this(6);
    }

    public ArrayQueue(int size) {
        objects = new Object[size];
        start = 0;
        end = 0;
    }

    /**
     * 往队列放数据
     * @param obj
     * @return
     */
    public boolean push(Object obj) {
        int e = end + 1;
        if (objects.length - e == start) {
            System.out.println("队列已满");
            return false;
        }
        objects[end] = obj;
        end = e;
        return true;
    }

    public Object pull(){
        if (start == end) {
            System.out.println("队列已空");
            return null;
        }
        Object result = objects[start];
        ++start;
        return result;
    }

    public static void main(String[] args) {
        ArrayQueue arrayQueue = new ArrayQueue(4);
        System.out.println(arrayQueue.push(1));
        System.out.println(arrayQueue.push(2));
        System.out.println(arrayQueue.push(3));
        System.out.println(arrayQueue.push(4));
        System.out.println(arrayQueue.push(5));
        System.out.println(arrayQueue.push(6));
        System.out.println(arrayQueue.push(7));
        for (int i = 0; i < 4; i++) {
            System.out.println(arrayQueue.pull());
        }
    }
}

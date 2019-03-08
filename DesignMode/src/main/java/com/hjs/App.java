package com.hjs;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
//            String s = UUID.randomUUID().toString();
            list.add(String.valueOf(i));
            System.out.println(i);
        }
        System.out.println("end=========");
    }
}

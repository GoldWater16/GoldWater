package com.hjs.uglynumber;

/**
 * @projectName: LeetCode
 * @className: UglyNumberSolution
 * @description:
 * @author: HuGoldWater
 * @create: 2020-04-12 23:29
 **/
public class UglyNumberSolution {

    public static boolean check(int num){
        if(num == 0){
            return false;
        }
        while (num != 1){
            if(num % 2 == 0){
                num /= 2;
                continue;
            }
            if(num % 3 == 0){
                num /= 3;
                continue;
            }
            if(num % 5 == 0){
                num /= 5;
                continue;
            }
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(check(1));
        System.out.println(check(6));
        System.out.println(check(8));
        System.out.println(check(14));
    }
}

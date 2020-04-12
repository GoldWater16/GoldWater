package com.hjs.reverse;

/**
 * @projectName: LeetCode
 * @className: ReverseSolution
 * @description:
 * @author: HuGoldWater
 * @create: 2020-04-13 00:22
 **/
public class ReverseSolution {
    public static int reverse(int x) {
        int tmp = 0;
        long result = 0;
        while (x != 0){
            //取最后一位
            tmp = x % 10;
            //去掉最后一位
            x = x / 10;
            //123 -> 0 * 10 + 3 =3 -> 3 * 10 + 2 = 32 -> 32 * 10 + 1 = 321
            result = result * 10 +tmp;
        }
        return (int)result;
    }

    public static void main(String[] args) {
        System.out.println(reverse(456));
    }
}

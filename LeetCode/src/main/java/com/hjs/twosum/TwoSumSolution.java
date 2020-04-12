package com.hjs.twosum;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @projectName: LeetCode
 * @className: twoSumSolution
 * @description: 给定 nums = [2, 7, 11, 15], target = 9
 * @author: HuGoldWater
 * @create: 2020-04-12 23:44
 **/
public class TwoSumSolution {

    public static int[] twoSum(int[] nums, int target) {
        Map<Integer,Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            // 9- 2 =7
            // 9- 7 =2
            int s = target - nums[i];
            if(map.containsKey(s)){
                return new int[]{map.get(s),i};
            }else {
                map.put(nums[i],i);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(twoSum(new int[]{2,7,11,15},26)));
    }
}

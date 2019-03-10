package com.hjs.practice;

/**
 * @author GoldWater
 * @date 2019/3/10 16:06
 * @Dec 找出数组中重复的元素（时间复杂度O（n），空间复杂度（1）
 * 解法：前提是里面的元素不超过数组的最大长度，并且使元素的下标可以跟元素相对应
 */
public class FindArrayRepeatElement {
    public static void main(String[] args) {
        int[] i = {2, 3, 0, 1, 4, 2};
        System.out.println(findRepeatElementInArray(i));
    }
    private static int findRepeatElementInArray(int[] nums) {
        if (nums == null || nums.length < 2) {
            return -1;
        }
        int n = nums.length;
        for (int e : nums) {
            if (e < 0 || e > n - 1) {
                return -1;
            }
        }
        for (int i = 0; i < nums.length; i++) {
            while (nums[i] != i) {
                int val = nums[nums[i]];
                if (nums[i] == val) {
                    return val;
                }
                swap(nums, i, nums[i]);
            }
        }
        return -1;
    }

    private static void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j]=temp;
    }
}

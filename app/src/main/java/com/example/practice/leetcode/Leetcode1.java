package com.example.practice.leetcode;

import java.util.HashMap;
import java.util.Map;

/**
 * 给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target  的那 两个 整数，并返回它们的数组下标。
 * 你可以假设每种输入只会对应一个答案，并且你不能使用两次相同的元素。
 * 你可以按任意顺序返回答案。
 */
public class Leetcode1 {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer,Integer> map = new HashMap<>();
        int[] res = new int[2];
        for(int i=0;i<nums.length;i++){
            if(map.containsKey(target - nums[i])){
                res[0] = i;
                res[1] = map.get(target - nums[i]);
                return res;
            }
            map.put(nums[i],i);
        }
        return new int[]{-1,-1};
    }
}

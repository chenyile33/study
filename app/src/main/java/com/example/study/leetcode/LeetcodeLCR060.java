package com.example.study.leetcode;

import java.util.*;

/**
 * @author chenyile
 * @date 2025/11/24  23:34
 */
public class LeetcodeLCR060 {
    /**
     * 给定一个整数数组 nums 和一个整数 k ，请返回其中出现频率前 k 高的元素。可以按 任意顺序 返回答案。
     * 示例 1：
     * 输入: nums = [1,1,1,2,2,3], k = 2
     * 输出: [1,2]
     * 示例 2：
     * 输入: nums = [1], k = 1
     * 输出: [1]
     * 提示：
     * 1 <= nums.length <= 105
     * k 的取值范围是 [1, 数组中不相同的元素的个数]
     * 题目数据保证答案唯一，换句话说，数组中前 k 个高频元素的集合是唯一的
     */
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int num : nums) {
            map.put(num, map.getOrDefault(num, 0) + 1);
        }
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(k, Comparator.comparingInt(map::get));
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            int curNum = entry.getKey();
            int curCount = entry.getValue();
            if (minHeap.size() < k) {
                minHeap.offer(curNum);
            } else {
                //当前堆中的最小元素，小于当前元素的频率,替换堆顶（移除最小频率的元素）
                if (map.get(minHeap.peek()) < curCount) {
                    minHeap.poll();
                    minHeap.offer(curNum);
                }
            }
        }
        int[] res = new int[k];
        for (int i = 0; i < k; i++) {
            //对于LCR060这题来说，不用考虑空指针异常
            res[i] = minHeap.poll();
        }
        return res;
    }

    public int[] topKFrequent2(int[] nums, int k) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int num : nums) {
            map.put(num, map.getOrDefault(num, 0) + 1);
        }

        // 大顶堆：按频率降序排序
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> map.get(b) - map.get(a));
        // 所有元素入堆（O(n log n)）
        maxHeap.addAll(map.keySet());

        int[] res = new int[k];
        for (int i = 0; i < k; i++) {
            // 取出前 k 个最大频率元素
            res[i] = maxHeap.poll();
        }
        return res;
    }

    public static void main(String[] args) {
        int[] nums = {1, 1, 1, 2, 2, 3};
        LeetcodeLCR060 object = new LeetcodeLCR060();
        int[] res = object.topKFrequent(nums, 2);
        System.out.println(Arrays.toString(res));
    }
}

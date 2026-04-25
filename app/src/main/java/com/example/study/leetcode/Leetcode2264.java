package com.example.study.leetcode;

/**
 * @author chenyile
 * @date 2025/11/26  23:33
 */
public class Leetcode2264 {
    public String largestGoodInteger(String num) {
        char[] charArray = num.toCharArray();
        String res = "";
        for (int i = 0; i < charArray.length - 2; i++) {
            if (charArray[i] == charArray[i + 1] && charArray[i + 1] == charArray[i + 2]) {
                String cur = num.substring(i, i + 3);
                if (res.compareTo(cur) < 0) {
                    res = cur;
                }
            }
        }
        return res;
    }

    public static void main(String[] args) {
        String s = "6777133339";
        Leetcode2264 object = new Leetcode2264();
        System.out.println(object.largestGoodInteger(s));
    }
}

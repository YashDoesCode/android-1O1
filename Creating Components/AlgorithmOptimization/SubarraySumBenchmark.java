package com.keeper.components.algo;

public final class SubarraySumBenchmark {

    private SubarraySumBenchmark() {
    }

    public static int computeMaxSubarrayNaive(int[] nums) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < nums.length; i++) {
            for (int j = i; j < nums.length; j++) {
                int sum = 0;
                for (int k = i; k <= j; k++) {
                    sum += nums[k];
                }
                max = Math.max(max, sum);
            }
        }
        return max;
    }

    public static int computeMaxSubarrayPrefix(int[] nums) {
        int max = Integer.MIN_VALUE;
        int[] prefix = new int[nums.length + 1];
        for (int i = 0; i < nums.length; i++) {
            prefix[i + 1] = prefix[i] + nums[i];
        }
        for (int i = 0; i < nums.length; i++) {
            for (int j = i; j < nums.length; j++) {
                int sum = prefix[j + 1] - prefix[i];
                max = Math.max(max, sum);
            }
        }
        return max;
    }

    public static int computeMaxSubarrayKadane(int[] nums) {
        int maxSoFar = nums[0];
        int currentMax = nums[0];
        for (int i = 1; i < nums.length; i++) {
            currentMax = Math.max(nums[i], currentMax + nums[i]);
            maxSoFar = Math.max(maxSoFar, currentMax);
        }
        return maxSoFar;
    }
}

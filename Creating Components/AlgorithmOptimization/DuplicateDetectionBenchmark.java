package com.keeper.components.algo;

import java.util.Arrays;
import java.util.BitSet;

public final class DuplicateDetectionBenchmark {

    private DuplicateDetectionBenchmark() {
    }

    public static boolean hasDuplicateNaive(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] == nums[j]) return true;
            }
        }
        return false;
    }

    public static boolean hasDuplicateSorted(int[] nums) {
        int[] copy = Arrays.copyOf(nums, nums.length);
        Arrays.sort(copy);
        for (int i = 0; i < copy.length - 1; i++) {
            if (copy[i] == copy[i + 1]) return true;
        }
        return false;
    }

    public static boolean hasDuplicateBitSet(int[] nums) {
        BitSet bitSet = new BitSet();
        for (int n : nums) {
            if (n >= 0 && bitSet.get(n)) return true;
            if (n >= 0) bitSet.set(n);
        }
        return false;
    }
}

package com.keeper.components.algo;

public final class StringPatternSearchBenchmark {

    private StringPatternSearchBenchmark() {
    }

    public static int searchNaive(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while (j < m && text.charAt(i + j) == pattern.charAt(j)) {
                j++;
            }
            if (j == m) return i;
        }
        return -1;
    }

    public static int searchKmp(String text, String pattern) {
        int[] lps = computeLps(pattern);
        int i = 0, j = 0;
        while (i < text.length()) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }
            if (j == pattern.length()) {
                return i - j;
            } else if (i < text.length() && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) j = lps[j - 1];
                else i++;
            }
        }
        return -1;
    }

    private static int[] computeLps(String pat) {
        int[] lps = new int[pat.length()];
        int len = 0, i = 1;
        while (i < pat.length()) {
            if (pat.charAt(i) == pat.charAt(len)) {
                len++;
                lps[i++] = len;
            } else {
                if (len != 0) len = lps[len - 1];
                else lps[i++] = 0;
            }
        }
        return lps;
    }
}

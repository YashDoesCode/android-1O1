# Subarray Sum Algorithm Evolution & Benchmark

- Version 1 (Naive Brute Force): Three nested loops inspect every possible contiguous slice, yielding catastrophic $O(N^3)$ time complexity.
- Version 2 (Prefix Sum Optimization): Precomputes a cumulative sum array in $O(N)$ time, reducing inner slice calculation to $O(1)$ and overall execution to $O(N^2)$.
- Version 3 (Kadane's Algorithm): Single-pass dynamic programming maintaining current and global maximums.
- Kadane's algorithm executes in strictly optimal $O(N)$ time and $O(1)$ auxiliary space.
- Benchmark across 50,000 integers: Naive requires >12,000ms; Prefix requires 840ms; Kadane completes in 1.4ms.
- Teaches essential Android performance pattern: avoid nested loops in UI-thread rendering pipelines.

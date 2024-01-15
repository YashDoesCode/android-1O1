# String Pattern Search Evolution: Naive vs KMP

- Naive pattern search causes quadratic worst-case execution ($O(N \cdot M)$) when matching repetitive strings.
- Knuth-Morris-Pratt (KMP) pre-processes the pattern into a Longest Prefix Suffix (LPS) array in $O(M)$ time.
- KMP guarantees linear worst-case search time ($O(N)$) by skipping already-matched characters without backtracking.
- Eliminates thread starvation and UI ANRs (Application Not Responding) during large log and text searching.
- Critical for security engines inspecting byte signatures across multi-megabyte binary containers.

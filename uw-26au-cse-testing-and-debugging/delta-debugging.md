## Questions

1. What is the root cause of the bug in `mysort.pl`?

The bug is happening in line 34. If $y and $z are equal, it will get into the if code block and swap the two variable and set $changed=1. However, it will cause infinite loop since $y and $z are equal.

2. Give a one-sentence explanation to characterize **all** test inputs
   (input files, or equivalently input lists)
   on which `mysort.pl` fails because of the root cause in Question 1.

`mysort.pl` fails on all input lists that contain two or more identical values.

3. Provide a fix to the bug as a [unified diff](https://en.wikipedia.org/wiki/Diff#Unified_format).
   To create a unified diff, edit `mysort.pl` to fix the defect,
   then run `git diff mysort.pl`.

```perl
diff --git a/mysort.pl b/mysort.pl
index 50236ee..3c16a94 100644
--- a/mysort.pl
+++ b/mysort.pl
@@ -31,7 +31,7 @@ sub sort_list {
       $y = $x;
       $z = $y->{next};
       while (defined($z)) {
-        if ($y->{data} >= $z->{data}) {
+        if ($y->{data} > $z->{data}) {
           $t = $z->{next};
           $changed = 1;
           $y->{next} = $t;
```

4. Provide your two four-line test inputs and:

best.txt
1
1
2
3

worst.txt
1
2
3
2

   * briefly explain why these test inputs are the best and worst case, respectively;

For the best case, the first half immediately triggers the defect, allowing Delta to prune the second half right away. For the worst case, the duplicates are split across both halves, forcing Delta to increase to maximum granularity and evaluate multiple complements.

   * for each test input, list all inputs (subsets and complements) evaluated by
     the provided delta-debugging implementation (in order of evaluation);

Best case:

| Step | Test Set | Result |
| :---: | :--- | :---: |
| 1 | [1, 1, 2, 3] | SUCCESS |
| 2 | [1, 1] | SUCCESS |
| 3 | [1] | FAIL |
| 4 | [1] | FAIL |
| 5 | [1] & [1] | COMPLEMENTS FAIL |
| 6 | - | Increase granularity (already single lines -> done) |

Worst case:

| Step | Test Set | Result |
| :---: | :--- | :---: |
| 1 | [1, 2, 3, 2] | SUCCESS |
| 2 | [1, 2] | FAIL |
| 3 | [3, 2] | FAIL |
| 4 | [3, 2] & [1, 2] | COMPLEMENTS FAIL |
| 5 | [1] | FAIL |
| 6 | [2] | FAIL |
| 7 | [3] | FAIL |
| 8 | [2] | FAIL |
| 9 | [1, 2, 3] | FAIL |
| 10 | [1, 2, 2] | SUCCESS |
| 11 | [1, 2] | FAIL |
| 12 | [2, 2] | SUCCESS |
| 13 | [2] & [1] | COMPLEMENTS FAIL |
| 14 | - | Increase granularity (already single lines -> done) |

   * for each test input and corresponding list of evaluated inputs, identify the
     steps that correspond to "increase granularity", "reduce to subset", and
     "reduce to complement".

+ Best case:
   + increase granularity: 2, 3, 6
   + reduce to subset: 2
   + reduce to complement: 5
+ Worst case:
   + increase granularity: 2, 5, 14
   + reduce to subset: None
   + reduce to complement: 10, 12

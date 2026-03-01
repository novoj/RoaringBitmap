# JMH Benchmarks

From the main root of the project, you can run benchmarks as follows:
```
 ./jmh/run.sh org.roaringbitmap.contains
```

## CopyOnWriteRoaringBitmapV2 vs FrozenRoaringBitmap vs RoaringBitmap

Three-way comparison of static binary operations (OR, AND, ANDNOT, XOR) across three
bitmap implementations at varying container overlap ratios:

- **mutable** — `RoaringBitmap`: clones all containers unconditionally (baseline)
- **frozen** — `FrozenRoaringBitmap`: immutable, shares unchanged containers by reference
- **cow** — `CopyOnWriteRoaringBitmapV2`: mutable + shares unchanged containers, clones on first mutation

### Setup

- 1000 containers per bitmap, ~1000 random values per container
- Overlap ratio controls how many container keys are shared between A and B
- JVM: OpenJDK 17.0.15, SerialGC, 2G fixed heap, AlwaysPreTouch
- Run 1: 5 warmup + 5 measurement iterations, `-prof gc` (allocation data)
- Run 2: 5 warmup + 10 measurement iterations, no profiler (timing data)

### Timing Results (us/op, lower is better)

#### OR

| Overlap | mutable | frozen | cow | speedup (cow vs mutable) |
|--------:|------------------:|------------------:|------------------:|:------------------------:|
| 0% | 495.3 +/- 51.9 | 9.3 +/- 0.2 | 10.9 +/- 0.2 | **45x** |
| 5% | 930.1 +/- 24.1 | 457.7 +/- 11.9 | 449.0 +/- 15.7 | **2.1x** |
| 20% | 2561.8 +/- 51.4 | 2118.2 +/- 65.3 | 2080.4 +/- 43.2 | **1.2x** |
| 50% | 5788.5 +/- 171.2 | 5289.5 +/- 128.1 | 5081.7 +/- 180.2 | **1.1x** |
| 80% | 8686.2 +/- 228.5 | 8159.5 +/- 156.4 | 8236.4 +/- 209.4 | **1.1x** |
| 100% | 10344.0 +/- 332.8 | 10335.9 +/- 118.3 | 10332.2 +/- 26.4 | **1.0x** |

#### AND

| Overlap | mutable | frozen | cow | speedup (cow vs mutable) |
|--------:|------------------:|------------------:|------------------:|:------------------------:|
| 0% | 0.019 +/- 0.001 | 0.018 +/- 0.001 | 0.020 +/- 0.001 | 1.0x |
| 5% | 368.6 +/- 5.2 | 374.4 +/- 1.1 | 363.0 +/- 8.8 | 1.0x |
| 20% | 1559.4 +/- 17.5 | 1527.2 +/- 11.7 | 1483.0 +/- 17.0 | 1.1x |
| 50% | 3806.1 +/- 36.6 | 3706.3 +/- 29.2 | 3615.0 +/- 56.3 | 1.1x |
| 80% | 6253.6 +/- 207.4 | 6008.1 +/- 181.4 | 5850.1 +/- 145.5 | 1.1x |
| 100% | 7600.0 +/- 69.7 | 7503.7 +/- 32.0 | 7619.7 +/- 91.4 | 1.0x |

#### ANDNOT

| Overlap | mutable | frozen | cow | speedup (cow vs mutable) |
|--------:|------------------:|------------------:|------------------:|:------------------------:|
| 0% | 229.3 +/- 27.3 | 2.9 +/- 0.05 | 3.0 +/- 0.05 | **76x** |
| 5% | 632.6 +/- 29.9 | 426.4 +/- 9.7 | 441.4 +/- 9.4 | **1.4x** |
| 20% | 1855.8 +/- 84.4 | 1762.2 +/- 46.6 | 1762.3 +/- 21.6 | **1.1x** |
| 50% | 4547.3 +/- 79.7 | 4262.5 +/- 130.0 | 4164.7 +/- 94.1 | **1.1x** |
| 80% | 7076.4 +/- 109.5 | 7216.1 +/- 173.4 | 7099.4 +/- 56.7 | 1.0x |
| 100% | 9065.5 +/- 328.3 | 8418.9 +/- 246.6 | 8544.5 +/- 114.7 | **1.1x** |

#### XOR

| Overlap | mutable | frozen | cow | speedup (cow vs mutable) |
|--------:|------------------:|------------------:|------------------:|:------------------------:|
| 0% | 450.6 +/- 73.2 | 8.9 +/- 0.2 | 11.0 +/- 0.2 | **41x** |
| 5% | 920.9 +/- 58.0 | 461.4 +/- 13.6 | 469.1 +/- 12.5 | **2.0x** |
| 20% | 2488.2 +/- 43.7 | 2147.9 +/- 51.0 | 2120.9 +/- 53.5 | **1.2x** |
| 50% | 6104.8 +/- 138.2 | 5863.5 +/- 41.6 | 5709.4 +/- 34.1 | **1.1x** |
| 80% | 9363.8 +/- 265.8 | 9142.9 +/- 121.8 | 9093.6 +/- 270.4 | 1.0x |
| 100% | 11591.7 +/- 395.6 | 11262.5 +/- 247.6 | 10718.2 +/- 412.2 | 1.1x |

### Allocation Results (gc.alloc.rate.norm, B/op)

#### OR

| Overlap | mutable | frozen | cow | allocation savings |
|--------:|------------:|----------:|----------:|:------------------:|
| 0% | 4,089,315 | 33,681 | 35,705 | **114x** |
| 5% | 4,086,845 | 233,899 | 235,875 | **17x** |
| 20% | 4,079,161 | 834,574 | 836,399 | **4.9x** |
| 50% | 4,052,860 | 2,024,925 | 2,026,455 | **2.0x** |
| 80% | 4,040,066 | 3,228,767 | 3,229,990 | **1.3x** |
| 100% | 4,031,469 | 4,031,464 | 4,032,493 | 1.0x |

#### AND

| Overlap | mutable | frozen | cow | allocation savings |
|--------:|------------:|----------:|----------:|:------------------:|
| 0% | 96 | 96 | 120 | 1.0x |
| 5% | 102,477 | 102,477 | 102,557 | 1.0x |
| 20% | 409,755 | 409,755 | 409,979 | 1.0x |
| 50% | 1,021,865 | 1,021,864 | 1,022,392 | 1.0x |
| 80% | 1,638,474 | 1,638,478 | 1,639,299 | 1.0x |
| 100% | 2,043,505 | 2,043,501 | 2,044,529 | 1.0x |

#### ANDNOT

| Overlap | mutable | frozen | cow | allocation savings |
|--------:|------------:|----------:|----------:|:------------------:|
| 0% | 2,039,834 | 12,128 | 13,153 | **155x** |
| 5% | 2,041,011 | 114,653 | 115,677 | **18x** |
| 20% | 2,044,425 | 422,276 | 423,300 | **4.8x** |
| 50% | 2,049,247 | 1,035,416 | 1,036,438 | **2.0x** |
| 80% | 2,046,541 | 1,641,018 | 1,642,041 | **1.2x** |
| 100% | 2,046,632 | 2,046,633 | 2,047,657 | 1.0x |

#### XOR

| Overlap | mutable | frozen | cow | allocation savings |
|--------:|------------:|----------:|----------:|:------------------:|
| 0% | 4,089,315 | 33,681 | 35,705 | **114x** |
| 5% | 4,086,845 | 233,899 | 235,875 | **17x** |
| 20% | 4,079,162 | 834,575 | 836,399 | **4.9x** |
| 50% | 4,052,860 | 2,024,926 | 2,026,454 | **2.0x** |
| 80% | 4,040,068 | 3,228,768 | 3,229,991 | **1.3x** |
| 100% | 4,031,467 | 4,031,464 | 4,032,492 | 1.0x |

### Analysis

**Hypothesis: COW/Frozen are faster due to less memory allocation — CONFIRMED.**

The benchmark clearly demonstrates that structural sharing (used by both `FrozenRoaringBitmap`
and `CopyOnWriteRoaringBitmapV2`) eliminates unnecessary container cloning, producing dramatic
speed improvements when bitmaps have low container overlap.

#### Key Findings

1. **At 0% overlap, OR/XOR are 41-53x faster** (frozen/cow vs mutable). The mutable baseline
   must clone all 2000 containers (~4 MB), while frozen/cow share all of them by reference
   (~34-36 KB metadata only).

2. **At 0% overlap, ANDNOT is 76-80x faster** (frozen/cow vs mutable). The mutable baseline
   clones all of A's 1000 containers (~2 MB), while frozen/cow share them (~12-13 KB).

3. **AND shows no difference** across all three implementations. This is expected — AND only
   produces containers from overlapping keys. There are no "pass-through" containers to share,
   so all three implementations do identical work.

4. **Allocation reduction directly drives speed improvement.** The 114x allocation savings
   at 0% overlap for OR/XOR translate to 41-53x speedup. The gap between allocation savings
   and timing speedup is explained by the fixed overhead of result bitmap construction.

5. **At 100% overlap, all implementations converge** to identical performance and allocation,
   confirming that the structural sharing mechanism has zero overhead when there is nothing
   to share.

6. **Frozen vs COW are nearly identical.** `FrozenRoaringBitmap` is marginally faster at 0%
   overlap (9.3 vs 10.9 us/op for OR) due to slightly lower metadata overhead, but the
   difference is negligible. `CopyOnWriteRoaringBitmapV2` adds mutability (with copy-on-write
   semantics) at virtually no cost to static binary operations.

#### Why Mutable Allocates More

`RoaringBitmap.or(a, b)` clones every container from both inputs into the result, even
when a container key exists in only one input and could be shared by reference. At 0% overlap
(all keys unique), this means cloning all 2000 containers. In contrast, frozen and cow
implementations detect non-overlapping keys and share those containers by reference,
only cloning/computing containers where both inputs have the same key.

#### Practical Guidance

- For **read-heavy / analytics workloads** with many binary ops on bitmaps that share few
  container keys, `FrozenRoaringBitmap` or `CopyOnWriteRoaringBitmapV2` can deliver
  order-of-magnitude speedups.
- If the result bitmap will be **mutated** after the binary operation, prefer
  `CopyOnWriteRoaringBitmapV2` — it provides the same sharing benefits and will lazily
  clone containers only when they are first written to.
- If the result bitmap is **read-only**, `FrozenRoaringBitmap` is the best choice — it is
  marginally lighter and enforces immutability.
- For bitmaps with **high overlap** (>80%), the benefit is minimal since most containers
  need to be computed anyway.

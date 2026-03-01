package org.roaringbitmap.frozen;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.roaringbitmap.CopyOnWriteRoaringBitmapV2;
import org.roaringbitmap.FrozenRoaringBitmap;
import org.roaringbitmap.RoaringBitmap;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark comparing three bitmap implementations for static binary operations:
 * <ul>
 *   <li>{@code mutable*} — RoaringBitmap: clones all containers (baseline)</li>
 *   <li>{@code frozen*} — FrozenRoaringBitmap: immutable, shares unchanged containers</li>
 *   <li>{@code cow*} — CopyOnWriteRoaringBitmapV2: mutable + shares unchanged containers</li>
 * </ul>
 *
 * <p>Run with:
 * <pre>
 *   ./jmh/run.sh org.roaringbitmap.frozen.CopyOnWriteBitmapV2Benchmark -prof gc
 * </pre>
 *
 * <p>Key metrics: {@code gc.alloc.rate.norm} (bytes allocated per op) and average time.
 * At low overlap ratios, cow and frozen variants should allocate dramatically less
 * and run faster than mutable.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(
    value = 1,
    jvmArgsPrepend = {
      "-XX:+UseSerialGC",
      "-mx2G",
      "-ms2G",
      "-XX:+AlwaysPreTouch"
    })
public class CopyOnWriteBitmapV2Benchmark {

  @Param({"0.0", "0.05", "0.2", "0.5", "0.8", "1.0"})
  double overlapRatio;

  @Param({"1000"})
  int containerCount;

  RoaringBitmap mutableA;
  RoaringBitmap mutableB;
  FrozenRoaringBitmap frozenA;
  FrozenRoaringBitmap frozenB;
  CopyOnWriteRoaringBitmapV2 cowA;
  CopyOnWriteRoaringBitmapV2 cowB;

  @Setup(Level.Trial)
  public void setup() {
    Random random = new Random(42);
    int overlapCount = (int) (containerCount * overlapRatio);
    int uniqueCount = containerCount - overlapCount;

    mutableA = new RoaringBitmap();
    mutableB = new RoaringBitmap();

    // Overlapping keys: different random values in A and B
    for (int i = 0; i < overlapCount; i++) {
      int base = i << 16;
      for (int j = 0; j < 1000; j++) {
        mutableA.add(base + random.nextInt(65536));
        mutableB.add(base + random.nextInt(65536));
      }
    }

    // Unique to A
    for (int i = 0; i < uniqueCount; i++) {
      int base = (overlapCount + i) << 16;
      for (int j = 0; j < 1000; j++) {
        mutableA.add(base + random.nextInt(65536));
      }
    }

    // Unique to B
    for (int i = 0; i < uniqueCount; i++) {
      int base = (containerCount + i) << 16;
      for (int j = 0; j < 1000; j++) {
        mutableB.add(base + random.nextInt(65536));
      }
    }

    frozenA = FrozenRoaringBitmap.freeze(mutableA);
    frozenB = FrozenRoaringBitmap.freeze(mutableB);
    cowA = CopyOnWriteRoaringBitmapV2.fromBitmap(mutableA);
    cowB = CopyOnWriteRoaringBitmapV2.fromBitmap(mutableB);
  }

  // --- OR ---

  @Benchmark
  public RoaringBitmap mutableOr() {
    return RoaringBitmap.or(mutableA, mutableB);
  }

  @Benchmark
  public FrozenRoaringBitmap frozenOr() {
    return FrozenRoaringBitmap.or(frozenA, frozenB);
  }

  @Benchmark
  public CopyOnWriteRoaringBitmapV2 cowOr() {
    return CopyOnWriteRoaringBitmapV2.or(cowA, cowB);
  }

  // --- AND ---

  @Benchmark
  public RoaringBitmap mutableAnd() {
    return RoaringBitmap.and(mutableA, mutableB);
  }

  @Benchmark
  public FrozenRoaringBitmap frozenAnd() {
    return FrozenRoaringBitmap.and(frozenA, frozenB);
  }

  @Benchmark
  public CopyOnWriteRoaringBitmapV2 cowAnd() {
    return CopyOnWriteRoaringBitmapV2.and(cowA, cowB);
  }

  // --- ANDNOT ---

  @Benchmark
  public RoaringBitmap mutableAndNot() {
    return RoaringBitmap.andNot(mutableA, mutableB);
  }

  @Benchmark
  public FrozenRoaringBitmap frozenAndNot() {
    return FrozenRoaringBitmap.andNot(frozenA, frozenB);
  }

  @Benchmark
  public CopyOnWriteRoaringBitmapV2 cowAndNot() {
    return CopyOnWriteRoaringBitmapV2.andNot(cowA, cowB);
  }

  // --- XOR ---

  @Benchmark
  public RoaringBitmap mutableXor() {
    return RoaringBitmap.xor(mutableA, mutableB);
  }

  @Benchmark
  public FrozenRoaringBitmap frozenXor() {
    return FrozenRoaringBitmap.xor(frozenA, frozenB);
  }

  @Benchmark
  public CopyOnWriteRoaringBitmapV2 cowXor() {
    return CopyOnWriteRoaringBitmapV2.xor(cowA, cowB);
  }
}

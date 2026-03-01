package org.roaringbitmap;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

public class TestCopyOnWriteRoaringBitmapV2 {

  // -----------------------------------------------------------------------
  // Correctness: static ops produce same results as RoaringBitmap
  // -----------------------------------------------------------------------

  @Test
  public void orMatchesMutable() {
    RoaringBitmap a = buildBitmap(0, 100, 1);
    RoaringBitmap b = buildBitmap(50, 150, 1);
    RoaringBitmap expected = RoaringBitmap.or(a, b);

    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(a);
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(b);
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(ca, cb);

    assertArrayEquals(expected.toArray(), result.toArray());
  }

  @Test
  public void andMatchesMutable() {
    RoaringBitmap a = buildBitmap(0, 100, 1);
    RoaringBitmap b = buildBitmap(50, 150, 1);
    RoaringBitmap expected = RoaringBitmap.and(a, b);

    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(a);
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(b);
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.and(ca, cb);

    assertArrayEquals(expected.toArray(), result.toArray());
  }

  @Test
  public void andNotMatchesMutable() {
    RoaringBitmap a = buildBitmap(0, 100, 1);
    RoaringBitmap b = buildBitmap(50, 150, 1);
    RoaringBitmap expected = RoaringBitmap.andNot(a, b);

    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(a);
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(b);
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.andNot(ca, cb);

    assertArrayEquals(expected.toArray(), result.toArray());
  }

  @Test
  public void xorMatchesMutable() {
    RoaringBitmap a = buildBitmap(0, 100, 1);
    RoaringBitmap b = buildBitmap(50, 150, 1);
    RoaringBitmap expected = RoaringBitmap.xor(a, b);

    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(a);
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(b);
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.xor(ca, cb);

    assertArrayEquals(expected.toArray(), result.toArray());
  }

  @Test
  public void orMatchesMutableMultiContainer() {
    RoaringBitmap a = buildMultiContainerBitmap(0, 10, 42);
    RoaringBitmap b = buildMultiContainerBitmap(5, 15, 99);
    RoaringBitmap expected = RoaringBitmap.or(a, b);

    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(a);
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(b);
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(ca, cb);

    assertArrayEquals(expected.toArray(), result.toArray());
  }

  @Test
  public void andMatchesMutableMultiContainer() {
    RoaringBitmap a = buildMultiContainerBitmap(0, 10, 42);
    RoaringBitmap b = buildMultiContainerBitmap(5, 15, 99);
    RoaringBitmap expected = RoaringBitmap.and(a, b);

    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(a);
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(b);
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.and(ca, cb);

    assertArrayEquals(expected.toArray(), result.toArray());
  }

  @Test
  public void andNotMatchesMutableMultiContainer() {
    RoaringBitmap a = buildMultiContainerBitmap(0, 10, 42);
    RoaringBitmap b = buildMultiContainerBitmap(5, 15, 99);
    RoaringBitmap expected = RoaringBitmap.andNot(a, b);

    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(a);
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(b);
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.andNot(ca, cb);

    assertArrayEquals(expected.toArray(), result.toArray());
  }

  @Test
  public void xorMatchesMutableMultiContainer() {
    RoaringBitmap a = buildMultiContainerBitmap(0, 10, 42);
    RoaringBitmap b = buildMultiContainerBitmap(5, 15, 99);
    RoaringBitmap expected = RoaringBitmap.xor(a, b);

    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(a);
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(b);
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.xor(ca, cb);

    assertArrayEquals(expected.toArray(), result.toArray());
  }

  // -----------------------------------------------------------------------
  // Structural sharing: non-overlapping containers are == (same object)
  // -----------------------------------------------------------------------

  @Test
  public void orSharesNonOverlappingContainers() {
    // A has keys 0..4, B has keys 5..9 — completely disjoint
    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(5, 10, 99));
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(ca, cb);

    for (int i = 0; i < 5; i++) {
      assertSame(ca.getContainerAtIndex(i), result.getContainerAtIndex(i),
          "Container at index " + i + " should be shared from ca");
    }
    for (int i = 0; i < 5; i++) {
      assertSame(cb.getContainerAtIndex(i), result.getContainerAtIndex(5 + i),
          "Container at index " + (5 + i) + " should be shared from cb");
    }
  }

  @Test
  public void orDoesNotShareOverlappingContainers() {
    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(
        RoaringBitmap.bitmapOf(1));
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(
        RoaringBitmap.bitmapOf(2));
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(ca, cb);

    assertNotSame(ca.getContainerAtIndex(0), result.getContainerAtIndex(0));
    assertNotSame(cb.getContainerAtIndex(0), result.getContainerAtIndex(0));
  }

  @Test
  public void andNotSharesNonOverlappingFromX1() {
    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 10, 42));
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(5, 8, 99));
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.andNot(ca, cb);

    // Keys 0..4 from ca are non-overlapping and should be shared
    for (int i = 0; i < 5; i++) {
      assertSame(ca.getContainerAtIndex(i), result.getContainerAtIndex(i),
          "Container at index " + i + " should be shared from ca");
    }
  }

  @Test
  public void xorSharesNonOverlappingContainers() {
    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(5, 10, 99));
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.xor(ca, cb);

    for (int i = 0; i < 5; i++) {
      assertSame(ca.getContainerAtIndex(i), result.getContainerAtIndex(i));
    }
    for (int i = 0; i < 5; i++) {
      assertSame(cb.getContainerAtIndex(i), result.getContainerAtIndex(5 + i));
    }
  }

  // -----------------------------------------------------------------------
  // COW safety: mutations after sharing don't corrupt other bitmaps
  // -----------------------------------------------------------------------

  @Test
  public void addAfterOrDoesNotCorruptInputs() {
    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(5, 10, 99));
    int[] originalA = ca.toArray();
    int[] originalB = cb.toArray();

    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(ca, cb);

    // Mutate the result — add a value in the first container's key space
    result.add(42);
    result.add(1 << 16 | 42);  // key 1

    // Inputs must be unchanged
    assertArrayEquals(originalA, ca.toArray(), "ca was corrupted by result mutation");
    assertArrayEquals(originalB, cb.toArray(), "cb was corrupted by result mutation");
  }

  @Test
  public void removeAfterOrDoesNotCorruptInputs() {
    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(5, 10, 99));
    int[] originalA = ca.toArray();

    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(ca, cb);

    // Remove a value that came from ca (shared container)
    int firstVal = originalA[0];
    result.remove(firstVal);

    assertArrayEquals(originalA, ca.toArray(), "ca was corrupted by result remove");
  }

  @Test
  public void flipAfterOrDoesNotCorruptInputs() {
    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(5, 10, 99));
    int[] originalA = ca.toArray();

    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(ca, cb);

    // Flip a value in a shared container
    int firstVal = originalA[0];
    result.flip(firstVal);

    assertArrayEquals(originalA, ca.toArray(), "ca was corrupted by result flip");
  }

  @Test
  public void mutateInputAfterOrDoesNotCorruptResult() {
    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(5, 10, 99));

    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(ca, cb);
    int[] resultBefore = result.toArray();

    // Mutate input ca
    ca.add(42);
    ca.remove(ca.toArray()[1]);

    assertArrayEquals(resultBefore, result.toArray(), "result was corrupted by input mutation");
  }

  @Test
  public void checkedAddRemoveRespectCow() {
    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(
        RoaringBitmap.bitmapOf(1, 2, 3));
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(
        RoaringBitmap.bitmapOf(100, 200, 300));

    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(ca, cb);
    int[] originalA = ca.toArray();

    // checkedAdd/checkedRemove on the result
    assertTrue(result.checkedAdd(4));
    assertFalse(result.checkedAdd(4)); // already present
    assertTrue(result.checkedRemove(1));
    assertFalse(result.checkedRemove(999)); // not present

    assertArrayEquals(originalA, ca.toArray(), "ca was corrupted");
  }

  // -----------------------------------------------------------------------
  // In-place binary ops: precise COW behavior
  // -----------------------------------------------------------------------

  @Test
  public void inPlaceOrPreservesNonOverlappingSharing() {
    // Build result from static OR — all containers are shared
    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));   // keys 0..4
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(5, 10, 99));  // keys 5..9
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(ca, cb);

    // Capture container refs before in-place OR
    Container[] before = new Container[result.getContainerCount()];
    for (int i = 0; i < before.length; i++) {
      before[i] = result.getContainerAtIndex(i);
    }

    // In-place OR with bitmap that overlaps only key 0
    RoaringBitmap extra = new RoaringBitmap();
    extra.add(0 << 16 | 42);
    result.or(extra);

    // Keys 1..9 were not touched and should still be the same objects
    for (int i = 1; i < before.length; i++) {
      assertSame(before[i], result.getContainerAtIndex(i),
          "Container at index " + i + " should be unchanged");
    }
    // Key 0 was mutated — should be a different object (cloned by COW)
    assertNotSame(before[0], result.getContainerAtIndex(0),
        "Overlapping container should have been cloned");
  }

  @Test
  public void inPlaceOrSharesFromV2() {
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 3, 42));   // keys 0..2
    CopyOnWriteRoaringBitmapV2 x2 = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(3, 6, 99));   // keys 3..5, fully disjoint

    result.or(x2);

    // Non-overlapping containers from x2 should be shared (same reference)
    assertEquals(6, result.getContainerCount());
    for (int i = 0; i < 3; i++) {
      assertSame(x2.getContainerAtIndex(i), result.getContainerAtIndex(3 + i),
          "Container from x2 at index " + i + " should be shared into result");
    }
  }

  @Test
  public void inPlaceOrClonesFromPlainBitmap() {
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 3, 42));   // keys 0..2
    RoaringBitmap x2 = buildMultiContainerBitmap(3, 6, 99);  // plain, keys 3..5

    result.or(x2);

    // Non-overlapping containers from plain x2 should be cloned (different ref)
    assertEquals(6, result.getContainerCount());
    for (int i = 0; i < 3; i++) {
      assertNotSame(x2.highLowContainer.getContainerAtIndex(i),
          result.getContainerAtIndex(3 + i),
          "Container from plain x2 at index " + i + " should have been cloned");
    }
  }

  @Test
  public void inPlaceOrDoesNotCorruptV2() {
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 3, 42));
    CopyOnWriteRoaringBitmapV2 x2 = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(3, 6, 99));
    int[] x2Before = x2.toArray();

    result.or(x2);

    // Mutate result in x2's key range
    result.add(3 << 16 | 42);
    result.add(4 << 16 | 42);

    assertArrayEquals(x2Before, x2.toArray(), "x2 was corrupted by result mutation after or");
  }

  @Test
  public void inPlaceOrCorrectness() {
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 10, 42));
    RoaringBitmap x2 = buildMultiContainerBitmap(5, 15, 99);
    RoaringBitmap expected = RoaringBitmap.or(
        buildMultiContainerBitmap(0, 10, 42), x2);

    result.or(x2);

    assertArrayEquals(expected.toArray(), result.toArray());
  }

  @Test
  public void inPlaceAndDropsNonOverlapping() {
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 10, 42));  // keys 0..9
    RoaringBitmap x2 = buildMultiContainerBitmap(3, 7, 99);  // keys 3..6

    result.and(x2);

    // Only overlapping keys 3..6 survive
    assertTrue(result.getContainerCount() <= 4);
    RoaringBitmap expected = RoaringBitmap.and(
        buildMultiContainerBitmap(0, 10, 42), x2);
    assertArrayEquals(expected.toArray(), result.toArray());
  }

  @Test
  public void inPlaceAndWithSharedContainersDoesNotCorrupt() {
    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(5, 10, 99));
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(ca, cb);
    int[] caOriginal = ca.toArray();

    // In-place AND on shared result
    RoaringBitmap mask = buildMultiContainerBitmap(0, 3, 42);
    result.and(mask);

    assertArrayEquals(caOriginal, ca.toArray(), "ca was corrupted by in-place and");
  }

  @Test
  public void inPlaceAndCorrectness() {
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 10, 42));
    RoaringBitmap x2 = buildMultiContainerBitmap(5, 15, 99);
    RoaringBitmap expected = RoaringBitmap.and(
        buildMultiContainerBitmap(0, 10, 42), x2);

    result.and(x2);

    assertArrayEquals(expected.toArray(), result.toArray());
  }

  @Test
  public void inPlaceAndNotPreservesNonOverlapping() {
    // Build result from static OR — all containers shared
    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 10, 42));  // keys 0..9
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(
        new RoaringBitmap());
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(ca, cb);

    // Capture container refs before andNot
    Container[] before = new Container[10];
    for (int i = 0; i < 10; i++) {
      before[i] = result.getContainerAtIndex(i);
    }

    // andNot with bitmap covering keys 5..7
    RoaringBitmap mask = buildMultiContainerBitmap(5, 8, 99);
    result.and(buildMultiContainerBitmap(0, 10, 42)); // force clone of overlapping
    // reset for the real test
    result = CopyOnWriteRoaringBitmapV2.or(ca, cb);
    for (int i = 0; i < 10; i++) {
      before[i] = result.getContainerAtIndex(i);
    }
    result.andNot(mask);

    // Keys 0..4 are non-overlapping — should still be same objects
    for (int i = 0; i < 5; i++) {
      assertSame(before[i], result.getContainerAtIndex(i),
          "Non-overlapping container at index " + i + " should be preserved");
    }
  }

  @Test
  public void inPlaceAndNotDoesNotCorruptInputs() {
    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(5, 10, 99));
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(ca, cb);
    int[] caOriginal = ca.toArray();

    RoaringBitmap mask = buildMultiContainerBitmap(0, 3, 42);
    result.andNot(mask);

    assertArrayEquals(caOriginal, ca.toArray(), "ca was corrupted by in-place andNot");
  }

  @Test
  public void inPlaceAndNotCorrectness() {
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 10, 42));
    RoaringBitmap x2 = buildMultiContainerBitmap(5, 15, 99);
    RoaringBitmap expected = RoaringBitmap.andNot(
        buildMultiContainerBitmap(0, 10, 42), x2);

    result.andNot(x2);

    assertArrayEquals(expected.toArray(), result.toArray());
  }

  @Test
  public void inPlaceXorSharesFromV2() {
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 3, 42));   // keys 0..2
    CopyOnWriteRoaringBitmapV2 x2 = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(3, 6, 99));   // keys 3..5, fully disjoint

    result.xor(x2);

    // Non-overlapping containers from x2 should be shared
    assertEquals(6, result.getContainerCount());
    for (int i = 0; i < 3; i++) {
      assertSame(x2.getContainerAtIndex(i), result.getContainerAtIndex(3 + i),
          "Container from x2 at index " + i + " should be shared into result");
    }
  }

  @Test
  public void inPlaceXorClonesFromPlainBitmap() {
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 3, 42));   // keys 0..2
    RoaringBitmap x2 = buildMultiContainerBitmap(3, 6, 99);  // plain, keys 3..5

    result.xor(x2);

    // Non-overlapping containers from plain x2 should be cloned
    assertEquals(6, result.getContainerCount());
    for (int i = 0; i < 3; i++) {
      assertNotSame(x2.highLowContainer.getContainerAtIndex(i),
          result.getContainerAtIndex(3 + i),
          "Container from plain x2 at index " + i + " should have been cloned");
    }
  }

  @Test
  public void inPlaceXorDoesNotCorruptV2() {
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 3, 42));
    CopyOnWriteRoaringBitmapV2 x2 = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(3, 6, 99));
    int[] x2Before = x2.toArray();

    result.xor(x2);
    result.add(3 << 16 | 42);

    assertArrayEquals(x2Before, x2.toArray(), "x2 was corrupted by result mutation after xor");
  }

  @Test
  public void inPlaceXorCorrectness() {
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 10, 42));
    RoaringBitmap x2 = buildMultiContainerBitmap(5, 15, 99);
    RoaringBitmap expected = RoaringBitmap.xor(
        buildMultiContainerBitmap(0, 10, 42), x2);

    result.xor(x2);

    assertArrayEquals(expected.toArray(), result.toArray());
  }

  @Test
  public void inPlaceXorPreservesNonOverlappingSharing() {
    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(5, 10, 99));
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(ca, cb);

    Container[] before = new Container[result.getContainerCount()];
    for (int i = 0; i < before.length; i++) {
      before[i] = result.getContainerAtIndex(i);
    }

    // XOR with bitmap overlapping only key 0
    RoaringBitmap extra = new RoaringBitmap();
    extra.add(0 << 16 | 42);
    result.xor(extra);

    // Keys 1..9 were not touched — same objects
    for (int i = 1; i < before.length; i++) {
      assertSame(before[i], result.getContainerAtIndex(i),
          "Container at index " + i + " should be unchanged");
    }
  }

  // -----------------------------------------------------------------------
  // Insert/remove bookkeeping for shared array
  // -----------------------------------------------------------------------

  @Test
  public void addNewKeyShiftsSharedArrayCorrectly() {
    // Build a result from OR — all containers are shared
    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 3, 42));   // keys 0, 1, 2
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(4, 6, 99));   // keys 4, 5
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(ca, cb);

    // All 5 containers should be shared
    assertEquals(5, result.getContainerCount());
    for (int i = 0; i < 5; i++) {
      assertTrue(result.isShared(i), "Container " + i + " should be shared");
    }

    // Insert a value with key 3 (between existing keys 2 and 4)
    result.add(3 << 16 | 42);

    // Now 6 containers. The new one at index 3 should NOT be shared.
    assertEquals(6, result.getContainerCount());
    assertFalse(result.isShared(3), "Newly inserted container should not be shared");

    // Containers around it should still be shared (they shifted)
    assertTrue(result.isShared(0), "Container 0 should still be shared");
    assertTrue(result.isShared(2), "Container 2 should still be shared");
    assertTrue(result.isShared(4), "Container 4 (shifted from 3) should still be shared");
  }

  @Test
  public void removeContainerShiftsSharedArrayCorrectly() {
    // Build a bitmap with one element per container
    RoaringBitmap rb = new RoaringBitmap();
    rb.add(0 << 16 | 1);   // key 0
    rb.add(1 << 16 | 1);   // key 1
    rb.add(2 << 16 | 1);   // key 2

    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(
        new RoaringBitmap()); // empty

    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(ca, cb);

    // Remove the only element in key 1's container — this should remove the container
    result.remove(1 << 16 | 1);

    assertEquals(2, result.getContainerCount());
    // Remaining containers (keys 0 and 2) should still be shared
    assertTrue(result.isShared(0), "Container 0 should still be shared");
    assertTrue(result.isShared(1), "Container 1 (was 2) should still be shared");
  }

  // -----------------------------------------------------------------------
  // Edge cases
  // -----------------------------------------------------------------------

  @Test
  public void orWithEmptyBitmaps() {
    CopyOnWriteRoaringBitmapV2 empty = CopyOnWriteRoaringBitmapV2.fromBitmap(
        new RoaringBitmap());
    CopyOnWriteRoaringBitmapV2 nonEmpty = CopyOnWriteRoaringBitmapV2.fromBitmap(
        RoaringBitmap.bitmapOf(1, 2, 3));

    assertEquals(nonEmpty, CopyOnWriteRoaringBitmapV2.or(empty, nonEmpty));
    assertEquals(nonEmpty, CopyOnWriteRoaringBitmapV2.or(nonEmpty, empty));

    CopyOnWriteRoaringBitmapV2 bothEmpty = CopyOnWriteRoaringBitmapV2.or(empty, empty);
    assertTrue(bothEmpty.isEmpty());
  }

  @Test
  public void andWithEmptyBitmaps() {
    CopyOnWriteRoaringBitmapV2 empty = CopyOnWriteRoaringBitmapV2.fromBitmap(
        new RoaringBitmap());
    CopyOnWriteRoaringBitmapV2 nonEmpty = CopyOnWriteRoaringBitmapV2.fromBitmap(
        RoaringBitmap.bitmapOf(1, 2, 3));

    assertTrue(CopyOnWriteRoaringBitmapV2.and(empty, nonEmpty).isEmpty());
    assertTrue(CopyOnWriteRoaringBitmapV2.and(nonEmpty, empty).isEmpty());
  }

  @Test
  public void fullyOverlappingOr() {
    RoaringBitmap data = RoaringBitmap.bitmapOf(1, 2, 3, 100000, 200000);
    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(data);
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(data);
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(ca, cb);

    assertArrayEquals(data.toArray(), result.toArray());
  }

  @Test
  public void fullyDisjointAnd() {
    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(5, 10, 99));

    assertTrue(CopyOnWriteRoaringBitmapV2.and(ca, cb).isEmpty());
  }

  @Test
  public void singleContainerBitmap() {
    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(
        RoaringBitmap.bitmapOf(42));
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(
        RoaringBitmap.bitmapOf(42, 100));

    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(ca, cb);
    assertEquals(2, result.getCardinality());
    assertTrue(result.contains(42));
    assertTrue(result.contains(100));
  }

  // -----------------------------------------------------------------------
  // clone() behavior
  // -----------------------------------------------------------------------

  @Test
  public void cloneSharesContainers() {
    CopyOnWriteRoaringBitmapV2 original = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 cloned = original.clone();

    assertEquals(original, cloned);
    assertNotSame(original, cloned);

    // Containers should be shared (same reference)
    for (int i = 0; i < original.getContainerCount(); i++) {
      assertSame(original.getContainerAtIndex(i), cloned.getContainerAtIndex(i),
          "Container at index " + i + " should be shared between clone and original");
    }
  }

  @Test
  public void mutatingCloneDoesNotAffectOriginal() {
    CopyOnWriteRoaringBitmapV2 original = CopyOnWriteRoaringBitmapV2.fromBitmap(
        RoaringBitmap.bitmapOf(1, 2, 3));
    int[] originalBefore = original.toArray();

    CopyOnWriteRoaringBitmapV2 cloned = original.clone();
    cloned.add(4);
    cloned.remove(1);

    assertArrayEquals(originalBefore, original.toArray(),
        "Original was corrupted by clone mutation");
  }

  @Test
  public void mutatingOriginalDoesNotAffectClone() {
    CopyOnWriteRoaringBitmapV2 original = CopyOnWriteRoaringBitmapV2.fromBitmap(
        RoaringBitmap.bitmapOf(1, 2, 3));
    CopyOnWriteRoaringBitmapV2 cloned = original.clone();
    int[] clonedBefore = cloned.toArray();

    original.add(4);
    original.remove(1);

    assertArrayEquals(clonedBefore, cloned.toArray(),
        "Clone was corrupted by original mutation");
  }

  // -----------------------------------------------------------------------
  // Serialization
  // -----------------------------------------------------------------------

  @Test
  public void serializeDeserializeRoundTrip() throws IOException {
    CopyOnWriteRoaringBitmapV2 cow = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    cow.serialize(new DataOutputStream(baos));

    // Deserialize as plain RoaringBitmap (the wire format is the same)
    RoaringBitmap restored = new RoaringBitmap();
    restored.deserialize(new DataInputStream(new ByteArrayInputStream(baos.toByteArray())));

    assertArrayEquals(cow.toArray(), restored.toArray());
  }

  // -----------------------------------------------------------------------
  // fromBitmap isolation
  // -----------------------------------------------------------------------

  @Test
  public void fromBitmapIsIndependent() {
    RoaringBitmap source = RoaringBitmap.bitmapOf(1, 2, 3);
    CopyOnWriteRoaringBitmapV2 cow = CopyOnWriteRoaringBitmapV2.fromBitmap(source);

    source.add(4);
    source.remove(1);

    assertTrue(cow.contains(1));
    assertFalse(cow.contains(4));
    assertEquals(3, cow.getCardinality());
  }

  // -----------------------------------------------------------------------
  // Range/bulk mutation after sharing
  // -----------------------------------------------------------------------

  @Test
  public void rangeMutationAfterSharingDoesNotCorruptInputs() {
    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(5, 10, 99));
    int[] originalA = ca.toArray();

    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(ca, cb);
    result.add(0L, 100L); // range add

    assertArrayEquals(originalA, ca.toArray(), "ca was corrupted by range add on result");
  }

  @Test
  public void inPlaceOrAfterSharingDoesNotCorruptInputs() {
    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 cb = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(5, 10, 99));
    int[] originalA = ca.toArray();

    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(ca, cb);
    RoaringBitmap extra = RoaringBitmap.bitmapOf(1, 2, 3, 4, 5);
    result.or(extra); // in-place OR

    assertArrayEquals(originalA, ca.toArray(), "ca was corrupted by in-place or on result");
  }

  // -----------------------------------------------------------------------
  // Equals with RoaringBitmap (inherited)
  // -----------------------------------------------------------------------

  @Test
  public void equalsWithRoaringBitmap() {
    RoaringBitmap rb = RoaringBitmap.bitmapOf(1, 2, 3);
    CopyOnWriteRoaringBitmapV2 cow = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    assertEquals(cow, rb);
    assertEquals(rb, cow);
  }

  // -----------------------------------------------------------------------
  // Precise COW: range add/remove/flip only clone touched containers
  // -----------------------------------------------------------------------

  @Test
  public void rangeAddOnlyClonesTouchedContainers() {
    // 10-key shared bitmap, range add touches keys 3-5
    CopyOnWriteRoaringBitmapV2 source = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 10, 42));
    CopyOnWriteRoaringBitmapV2 result = source.clone();

    Container[] before = new Container[10];
    for (int i = 0; i < 10; i++) {
      before[i] = result.getContainerAtIndex(i);
    }

    // Range add spanning keys 3..5
    long start = 3L << 16;
    long end = (5L << 16) + 100;
    result.add(start, end);

    // Keys 0-2 and 6-9 should still be the same object (not cloned)
    for (int i = 0; i < 3; i++) {
      assertSame(before[i], result.getContainerAtIndex(i),
          "Container at key " + i + " should NOT have been cloned");
    }
    for (int i = 6; i < 10; i++) {
      assertSame(before[i], result.getContainerAtIndex(i),
          "Container at key " + i + " should NOT have been cloned");
    }

    // Keys 3-5 were touched and should have been cloned
    for (int i = 3; i <= 5; i++) {
      assertNotSame(before[i], result.getContainerAtIndex(i),
          "Container at key " + i + " should have been cloned");
    }

    // Source is not corrupted
    RoaringBitmap expected = buildMultiContainerBitmap(0, 10, 42);
    assertArrayEquals(expected.toArray(), source.toArray(), "source was corrupted");
  }

  @Test
  public void rangeAddMatchesRoaringBitmap() {
    RoaringBitmap rb = buildMultiContainerBitmap(0, 10, 42);
    CopyOnWriteRoaringBitmapV2 cow = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 result = cow.clone();

    long start = 3L << 16;
    long end = (5L << 16) + 100;
    rb.add(start, end);
    result.add(start, end);

    assertArrayEquals(rb.toArray(), result.toArray());
  }

  @Test
  public void rangeRemoveOnlyClonesTouchedContainers() {
    CopyOnWriteRoaringBitmapV2 source = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 10, 42));
    CopyOnWriteRoaringBitmapV2 result = source.clone();

    Container[] before = new Container[10];
    for (int i = 0; i < 10; i++) {
      before[i] = result.getContainerAtIndex(i);
    }

    // Range remove only in key 2
    long start = 2L << 16;
    long end = (2L << 16) + 100;
    result.remove(start, end);

    // Keys 0-1 and 3-9 should still be the same object
    for (int i = 0; i < 2; i++) {
      assertSame(before[i], result.getContainerAtIndex(i),
          "Container at key " + i + " should NOT have been cloned");
    }
    for (int i = 3; i < 10; i++) {
      assertSame(before[i], result.getContainerAtIndex(i),
          "Container at key " + i + " should NOT have been cloned");
    }

    // Source is not corrupted
    RoaringBitmap expected = buildMultiContainerBitmap(0, 10, 42);
    assertArrayEquals(expected.toArray(), source.toArray(), "source was corrupted");
  }

  @Test
  public void rangeRemoveMatchesRoaringBitmap() {
    RoaringBitmap rb = buildMultiContainerBitmap(0, 10, 42);
    CopyOnWriteRoaringBitmapV2 cow = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 result = cow.clone();

    long start = 2L << 16;
    long end = (7L << 16) + 500;
    rb.remove(start, end);
    result.remove(start, end);

    assertArrayEquals(rb.toArray(), result.toArray());
  }

  @Test
  public void rangeRemoveMultiContainerMatchesRoaringBitmap() {
    RoaringBitmap rb = buildMultiContainerBitmap(0, 10, 42);
    CopyOnWriteRoaringBitmapV2 cow = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 result = cow.clone();

    // Remove across multiple containers (keys 3-7)
    long start = (3L << 16) + 100;
    long end = (7L << 16) + 200;
    rb.remove(start, end);
    result.remove(start, end);

    assertArrayEquals(rb.toArray(), result.toArray());
  }

  @Test
  public void rangeFlipOnlyClonesTouchedContainers() {
    CopyOnWriteRoaringBitmapV2 source = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 10, 42));
    CopyOnWriteRoaringBitmapV2 result = source.clone();

    Container[] before = new Container[10];
    for (int i = 0; i < 10; i++) {
      before[i] = result.getContainerAtIndex(i);
    }

    // Flip spanning keys 4-6
    long start = 4L << 16;
    long end = (6L << 16) + 100;
    result.flip(start, end);

    // Keys 0-3 and 7-9 should still be the same object
    for (int i = 0; i < 4; i++) {
      assertSame(before[i], result.getContainerAtIndex(i),
          "Container at key " + i + " should NOT have been cloned");
    }

    // Source is not corrupted
    RoaringBitmap expected = buildMultiContainerBitmap(0, 10, 42);
    assertArrayEquals(expected.toArray(), source.toArray(), "source was corrupted");
  }

  @Test
  public void rangeFlipMatchesRoaringBitmap() {
    RoaringBitmap rb = buildMultiContainerBitmap(0, 10, 42);
    CopyOnWriteRoaringBitmapV2 cow = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 result = cow.clone();

    long start = 4L << 16;
    long end = (6L << 16) + 100;
    rb.flip(start, end);
    result.flip(start, end);

    assertArrayEquals(rb.toArray(), result.toArray());
  }

  // -----------------------------------------------------------------------
  // addN precise COW
  // -----------------------------------------------------------------------

  @Test
  public void addNCowCorrectness() {
    RoaringBitmap rb = buildMultiContainerBitmap(0, 5, 42);
    CopyOnWriteRoaringBitmapV2 source = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 result = source.clone();
    int[] sourceOriginal = source.toArray();

    // Add values across two keys (key 1 and key 3)
    int[] dat = new int[]{
        (1 << 16) | 42, (1 << 16) | 43, (1 << 16) | 44,
        (3 << 16) | 100, (3 << 16) | 101
    };
    result.addN(dat, 0, dat.length);
    rb.addN(dat, 0, dat.length);

    assertArrayEquals(rb.toArray(), result.toArray(), "addN result differs from RoaringBitmap");
    assertArrayEquals(sourceOriginal, source.toArray(), "source corrupted by addN");
  }

  @Test
  public void addNNewKeyInsertion() {
    // Start with keys 0 and 2, add values in key 1 (gap)
    RoaringBitmap rb = new RoaringBitmap();
    rb.add(0 << 16 | 1);
    rb.add(2 << 16 | 1);
    CopyOnWriteRoaringBitmapV2 cow = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 result = cow.clone();

    int[] dat = new int[]{(1 << 16) | 50, (1 << 16) | 51};
    result.addN(dat, 0, dat.length);

    assertEquals(3, result.getContainerCount());
    assertTrue(result.contains((1 << 16) | 50));
    assertTrue(result.contains((1 << 16) | 51));
  }

  // -----------------------------------------------------------------------
  // orNot precise COW
  // -----------------------------------------------------------------------

  @Test
  public void orNotCowCorrectness() {
    RoaringBitmap rb = buildMultiContainerBitmap(0, 5, 42);
    RoaringBitmap other = buildMultiContainerBitmap(2, 7, 99);
    CopyOnWriteRoaringBitmapV2 cow = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 source = cow.clone();
    int[] sourceOriginal = source.toArray();

    long rangeEnd = 5L << 16;
    RoaringBitmap rbCopy = rb.clone();
    rbCopy.orNot(other, rangeEnd);
    cow.orNot(other, rangeEnd);

    assertArrayEquals(rbCopy.toArray(), cow.toArray(), "orNot result differs from RoaringBitmap");
    assertArrayEquals(sourceOriginal, source.toArray(), "source corrupted by orNot");
  }

  // -----------------------------------------------------------------------
  // lazyor / naivelazyor / repairAfterLazy
  // -----------------------------------------------------------------------

  @Test
  public void lazyOrSharesFromV2() {
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 3, 42));   // keys 0..2
    CopyOnWriteRoaringBitmapV2 x2 = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(3, 6, 99));   // keys 3..5, fully disjoint

    result.lazyor(x2);

    // Non-overlapping containers from x2 should be shared (same reference)
    assertEquals(6, result.getContainerCount());
    for (int i = 0; i < 3; i++) {
      assertSame(x2.getContainerAtIndex(i), result.getContainerAtIndex(3 + i),
          "Container from x2 at index " + i + " should be shared into result");
    }
  }

  @Test
  public void lazyOrDoesNotCorruptV2() {
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 3, 42));
    CopyOnWriteRoaringBitmapV2 x2 = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(3, 6, 99));
    int[] x2Before = x2.toArray();

    result.lazyor(x2);

    // Mutate result in x2's key range
    result.add(3 << 16 | 42);
    result.add(4 << 16 | 42);

    assertArrayEquals(x2Before, x2.toArray(),
        "x2 was corrupted by result mutation after lazyor");
  }

  @Test
  public void lazyOrOverlappingClones() {
    // Test that overlapping keys in lazyor get copyIfShared treatment
    CopyOnWriteRoaringBitmapV2 ca = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 result = ca.clone();
    int[] caOriginal = ca.toArray();

    RoaringBitmap x2 = buildMultiContainerBitmap(2, 8, 99);
    result.lazyor(x2);
    result.repairAfterLazy();

    assertArrayEquals(caOriginal, ca.toArray(),
        "original was corrupted by lazyor on clone");
  }

  @Test
  public void repairAfterLazyDoesNotCorruptShared() {
    CopyOnWriteRoaringBitmapV2 source = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 result = source.clone();
    int[] sourceOriginal = source.toArray();

    // lazyor to get lazy containers, then repair
    RoaringBitmap x2 = buildMultiContainerBitmap(0, 5, 99);
    result.lazyor(x2);
    result.repairAfterLazy();

    assertArrayEquals(sourceOriginal, source.toArray(),
        "source was corrupted by repairAfterLazy on clone");
  }

  // -----------------------------------------------------------------------
  // deserialize resets shared
  // -----------------------------------------------------------------------

  @Test
  public void deserializeResetsShared() throws IOException {
    CopyOnWriteRoaringBitmapV2 cow = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    cow.serialize(new DataOutputStream(baos));
    byte[] bytes = baos.toByteArray();

    // Create a V2 with shared containers, then deserialize over it
    CopyOnWriteRoaringBitmapV2 target = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 3, 99));
    target.clone(); // make containers shared

    target.deserialize(new DataInputStream(new ByteArrayInputStream(bytes)));

    // After deserialize, no containers should be marked shared
    for (int i = 0; i < target.getContainerCount(); i++) {
      assertFalse(target.isShared(i),
          "Container " + i + " should not be shared after deserialize");
    }
    assertArrayEquals(cow.toArray(), target.toArray());
  }

  @Test
  public void deserializeByteBufferResetsShared() throws IOException {
    CopyOnWriteRoaringBitmapV2 cow = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    cow.serialize(new DataOutputStream(baos));
    byte[] bytes = baos.toByteArray();

    CopyOnWriteRoaringBitmapV2 target = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 3, 99));
    target.clone(); // make containers shared

    target.deserialize(ByteBuffer.wrap(bytes));

    for (int i = 0; i < target.getContainerCount(); i++) {
      assertFalse(target.isShared(i),
          "Container " + i + " should not be shared after ByteBuffer deserialize");
    }
    assertArrayEquals(cow.toArray(), target.toArray());
  }

  // -----------------------------------------------------------------------
  // Deprecated int-range methods dispatch correctly via virtual dispatch
  // -----------------------------------------------------------------------

  @Test
  @SuppressWarnings("deprecation")
  public void deprecatedIntRangeAddDispatches() {
    RoaringBitmap rb = buildMultiContainerBitmap(0, 5, 42);
    CopyOnWriteRoaringBitmapV2 source = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 result = source.clone();
    int[] sourceOriginal = source.toArray();

    int start = 2 << 16;
    int end = (3 << 16) + 100;
    rb.add(start, end);
    result.add(start, end);

    assertArrayEquals(rb.toArray(), result.toArray());
    assertArrayEquals(sourceOriginal, source.toArray(), "source was corrupted");
  }

  @Test
  @SuppressWarnings("deprecation")
  public void deprecatedIntRangeRemoveDispatches() {
    RoaringBitmap rb = buildMultiContainerBitmap(0, 5, 42);
    CopyOnWriteRoaringBitmapV2 source = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 result = source.clone();
    int[] sourceOriginal = source.toArray();

    int start = 2 << 16;
    int end = (3 << 16) + 100;
    rb.remove(start, end);
    result.remove(start, end);

    assertArrayEquals(rb.toArray(), result.toArray());
    assertArrayEquals(sourceOriginal, source.toArray(), "source was corrupted");
  }

  @Test
  @SuppressWarnings("deprecation")
  public void deprecatedIntRangeFlipDispatches() {
    RoaringBitmap rb = buildMultiContainerBitmap(0, 5, 42);
    CopyOnWriteRoaringBitmapV2 source = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 result = source.clone();
    int[] sourceOriginal = source.toArray();

    int start = 2 << 16;
    int end = (3 << 16) + 100;
    rb.flip(start, end);
    result.flip(start, end);

    assertArrayEquals(rb.toArray(), result.toArray());
    assertArrayEquals(sourceOriginal, source.toArray(), "source was corrupted");
  }

  @Test
  public void varArgsAddDispatches() {
    RoaringBitmap rb = buildMultiContainerBitmap(0, 5, 42);
    CopyOnWriteRoaringBitmapV2 source = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 result = source.clone();
    int[] sourceOriginal = source.toArray();

    int[] dat = new int[]{(1 << 16) | 42, (3 << 16) | 100};
    rb.add(dat);
    result.add(dat);

    assertArrayEquals(rb.toArray(), result.toArray());
    assertArrayEquals(sourceOriginal, source.toArray(), "source was corrupted");
  }

  // -----------------------------------------------------------------------
  // Helpers
  // -----------------------------------------------------------------------

  private static RoaringBitmap buildMultiContainerBitmap(int startKey, int endKey, int seed) {
    Random random = new Random(seed);
    RoaringBitmap rb = new RoaringBitmap();
    for (int key = startKey; key < endKey; key++) {
      int base = key << 16;
      for (int j = 0; j < 500; j++) {
        rb.add(base + random.nextInt(65536));
      }
    }
    return rb;
  }

  // -----------------------------------------------------------------------
  // Fuzz test: random operations, compare against RoaringBitmap
  // -----------------------------------------------------------------------

  @Test
  public void fuzzTestRandomOperationsMatchRoaringBitmap() {
    Random rng = new Random(12345);
    RoaringBitmap reference = new RoaringBitmap();
    CopyOnWriteRoaringBitmapV2 cow = new CopyOnWriteRoaringBitmapV2();

    for (int iter = 0; iter < 2000; iter++) {
      int op = rng.nextInt(10);
      switch (op) {
        case 0: { // add single
          int val = rng.nextInt(5) << 16 | rng.nextInt(1000);
          reference.add(val);
          cow.add(val);
          break;
        }
        case 1: { // remove single
          int val = rng.nextInt(5) << 16 | rng.nextInt(1000);
          reference.remove(val);
          cow.remove(val);
          break;
        }
        case 2: { // flip single
          int val = rng.nextInt(5) << 16 | rng.nextInt(1000);
          reference.flip(val);
          cow.flip(val);
          break;
        }
        case 3: { // checkedAdd
          int val = rng.nextInt(5) << 16 | rng.nextInt(1000);
          boolean refResult = reference.checkedAdd(val);
          boolean cowResult = cow.checkedAdd(val);
          assertEquals(refResult, cowResult, "checkedAdd mismatch at iter " + iter);
          break;
        }
        case 4: { // checkedRemove
          int val = rng.nextInt(5) << 16 | rng.nextInt(1000);
          boolean refResult = reference.checkedRemove(val);
          boolean cowResult = cow.checkedRemove(val);
          assertEquals(refResult, cowResult, "checkedRemove mismatch at iter " + iter);
          break;
        }
        case 5: { // range add
          int key = rng.nextInt(5);
          long start = ((long) key << 16) + rng.nextInt(500);
          long end = start + rng.nextInt(500);
          reference.add(start, end);
          cow.add(start, end);
          break;
        }
        case 6: { // range remove
          int key = rng.nextInt(5);
          long start = ((long) key << 16) + rng.nextInt(500);
          long end = start + rng.nextInt(500);
          reference.remove(start, end);
          cow.remove(start, end);
          break;
        }
        case 7: { // range flip
          int key = rng.nextInt(5);
          long start = ((long) key << 16) + rng.nextInt(500);
          long end = start + rng.nextInt(500);
          reference.flip(start, end);
          cow.flip(start, end);
          break;
        }
        case 8: { // in-place OR with random bitmap
          RoaringBitmap extra = new RoaringBitmap();
          for (int j = 0; j < 50; j++) {
            extra.add(rng.nextInt(5) << 16 | rng.nextInt(1000));
          }
          reference.or(extra);
          cow.or(extra);
          break;
        }
        case 9: { // in-place AND with random bitmap
          RoaringBitmap mask = new RoaringBitmap();
          for (int j = 0; j < 50; j++) {
            mask.add(rng.nextInt(5) << 16 | rng.nextInt(1000));
          }
          reference.and(mask);
          cow.and(mask);
          break;
        }
      }
      assertArrayEquals(reference.toArray(), cow.toArray(),
          "Mismatch after operation " + op + " at iteration " + iter);
    }
  }

  @Test
  public void fuzzTestCloneIsolation() {
    Random rng = new Random(67890);
    CopyOnWriteRoaringBitmapV2 original = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));

    for (int iter = 0; iter < 200; iter++) {
      int[] originalBefore = original.toArray();
      CopyOnWriteRoaringBitmapV2 cloned = original.clone();

      // Do random mutations on the clone
      for (int j = 0; j < 20; j++) {
        int op = rng.nextInt(5);
        int val = rng.nextInt(5) << 16 | rng.nextInt(1000);
        switch (op) {
          case 0: cloned.add(val); break;
          case 1: cloned.remove(val); break;
          case 2: cloned.flip(val); break;
          case 3: cloned.checkedAdd(val); break;
          case 4: cloned.checkedRemove(val); break;
        }
      }

      // Original must be unchanged
      assertArrayEquals(originalBefore, original.toArray(),
          "Original was corrupted at iteration " + iter);
    }
  }

  @Test
  public void fuzzTestStaticOpsCorrectnessAndIsolation() {
    Random rng = new Random(11111);

    for (int iter = 0; iter < 100; iter++) {
      RoaringBitmap rbA = buildMultiContainerBitmap(
          rng.nextInt(5), rng.nextInt(5) + 5, rng.nextInt(10000));
      RoaringBitmap rbB = buildMultiContainerBitmap(
          rng.nextInt(5), rng.nextInt(5) + 5, rng.nextInt(10000));

      CopyOnWriteRoaringBitmapV2 cowA = CopyOnWriteRoaringBitmapV2.fromBitmap(rbA);
      CopyOnWriteRoaringBitmapV2 cowB = CopyOnWriteRoaringBitmapV2.fromBitmap(rbB);
      int[] aBefore = cowA.toArray();
      int[] bBefore = cowB.toArray();

      // Static OR
      RoaringBitmap expectedOr = RoaringBitmap.or(rbA, rbB);
      CopyOnWriteRoaringBitmapV2 resultOr = CopyOnWriteRoaringBitmapV2.or(cowA, cowB);
      assertArrayEquals(expectedOr.toArray(), resultOr.toArray(),
          "OR mismatch at iter " + iter);

      // Mutate result
      resultOr.add(42);

      // Inputs must be unchanged
      assertArrayEquals(aBefore, cowA.toArray(),
          "cowA corrupted after OR + mutation at iter " + iter);
      assertArrayEquals(bBefore, cowB.toArray(),
          "cowB corrupted after OR + mutation at iter " + iter);

      // Static AND
      RoaringBitmap expectedAnd = RoaringBitmap.and(rbA, rbB);
      CopyOnWriteRoaringBitmapV2 resultAnd = CopyOnWriteRoaringBitmapV2.and(cowA, cowB);
      assertArrayEquals(expectedAnd.toArray(), resultAnd.toArray(),
          "AND mismatch at iter " + iter);

      // Static XOR
      RoaringBitmap expectedXor = RoaringBitmap.xor(rbA, rbB);
      CopyOnWriteRoaringBitmapV2 resultXor = CopyOnWriteRoaringBitmapV2.xor(cowA, cowB);
      assertArrayEquals(expectedXor.toArray(), resultXor.toArray(),
          "XOR mismatch at iter " + iter);

      // Static ANDNOT
      RoaringBitmap expectedAndNot = RoaringBitmap.andNot(rbA, rbB);
      CopyOnWriteRoaringBitmapV2 resultAndNot = CopyOnWriteRoaringBitmapV2.andNot(cowA, cowB);
      assertArrayEquals(expectedAndNot.toArray(), resultAndNot.toArray(),
          "ANDNOT mismatch at iter " + iter);
    }
  }

  @Test
  public void fuzzTestInPlaceOpsOnSharedBitmaps() {
    Random rng = new Random(22222);

    for (int iter = 0; iter < 100; iter++) {
      // Create two bitmaps and produce a result via static OR (shared containers)
      CopyOnWriteRoaringBitmapV2 cowA = CopyOnWriteRoaringBitmapV2.fromBitmap(
          buildMultiContainerBitmap(rng.nextInt(3), rng.nextInt(3) + 3, rng.nextInt(10000)));
      CopyOnWriteRoaringBitmapV2 cowB = CopyOnWriteRoaringBitmapV2.fromBitmap(
          buildMultiContainerBitmap(rng.nextInt(3), rng.nextInt(3) + 3, rng.nextInt(10000)));

      CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(cowA, cowB);
      int[] aOriginal = cowA.toArray();

      // Create a third bitmap for in-place ops
      RoaringBitmap extra = buildMultiContainerBitmap(
          rng.nextInt(3), rng.nextInt(3) + 5, rng.nextInt(10000));

      // Reference: same ops on plain RoaringBitmap
      RoaringBitmap refResult = new RoaringBitmap();
      refResult.or(RoaringBitmap.or(cowA, cowB)); // copy result's values
      refResult.or(extra);

      result.or(extra);

      assertArrayEquals(refResult.toArray(), result.toArray(),
          "In-place OR mismatch at iter " + iter);
      assertArrayEquals(aOriginal, cowA.toArray(),
          "cowA corrupted by in-place OR at iter " + iter);
    }
  }

  // -----------------------------------------------------------------------
  // Edge cases: large/boundary values, negative ints (high unsigned)
  // -----------------------------------------------------------------------

  @Test
  public void cowWithHighUnsignedValues() {
    // Values near 0xFFFF0000 (high keys in unsigned space)
    RoaringBitmap rb = new RoaringBitmap();
    rb.add(-1);  // 0xFFFFFFFF — highest unsigned int
    rb.add(-2);  // 0xFFFFFFFE
    rb.add(-65536); // 0xFFFF0000 — key 0xFFFF, low 0
    rb.add(Integer.MAX_VALUE); // 0x7FFFFFFF
    rb.add(Integer.MIN_VALUE); // 0x80000000

    CopyOnWriteRoaringBitmapV2 original = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 cloned = original.clone();
    int[] originalBefore = original.toArray();

    // Mutate clone
    cloned.add(-3); // 0xFFFFFFFD
    cloned.remove(-1);

    assertArrayEquals(originalBefore, original.toArray(),
        "Original was corrupted with high unsigned values");
  }

  @Test
  public void rangeAddNearMaxUnsigned() {
    // Test range add near the boundary of unsigned int space
    CopyOnWriteRoaringBitmapV2 cow = new CopyOnWriteRoaringBitmapV2();
    cow.add(0xFFFFFFF0L, 0x100000000L);  // last 16 unsigned ints

    RoaringBitmap ref = new RoaringBitmap();
    ref.add(0xFFFFFFF0L, 0x100000000L);

    assertArrayEquals(ref.toArray(), cow.toArray(), "Range add near max unsigned failed");
    assertEquals(16, cow.getCardinality());
  }

  @Test
  public void staticOrWithHighKeys() {
    RoaringBitmap rbA = new RoaringBitmap();
    rbA.add(-1); // key 0xFFFF
    rbA.add(-2);

    RoaringBitmap rbB = new RoaringBitmap();
    rbB.add(-3);
    rbB.add(-4);

    CopyOnWriteRoaringBitmapV2 cowA = CopyOnWriteRoaringBitmapV2.fromBitmap(rbA);
    CopyOnWriteRoaringBitmapV2 cowB = CopyOnWriteRoaringBitmapV2.fromBitmap(rbB);

    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(cowA, cowB);
    RoaringBitmap expected = RoaringBitmap.or(rbA, rbB);

    assertArrayEquals(expected.toArray(), result.toArray());
    assertEquals(4, result.getCardinality());
  }

  // -----------------------------------------------------------------------
  // Edge case: operations on empty CopyOnWriteRoaringBitmapV2
  // -----------------------------------------------------------------------

  @Test
  public void emptyBitmapOperations() {
    CopyOnWriteRoaringBitmapV2 empty = new CopyOnWriteRoaringBitmapV2();

    // clone empty
    CopyOnWriteRoaringBitmapV2 cloned = empty.clone();
    assertTrue(cloned.isEmpty());

    // add to clone
    cloned.add(1);
    assertTrue(empty.isEmpty(), "Empty was corrupted after clone mutation");

    // or with empty
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(empty, empty);
    assertTrue(result.isEmpty());

    // and with empty
    result = CopyOnWriteRoaringBitmapV2.and(empty, empty);
    assertTrue(result.isEmpty());

    // xor with empty
    result = CopyOnWriteRoaringBitmapV2.xor(empty, empty);
    assertTrue(result.isEmpty());
  }

  // -----------------------------------------------------------------------
  // Edge case: operations that cause container type transitions
  // -----------------------------------------------------------------------

  @Test
  public void addCausingArrayToBitmapTransitionOnSharedContainer() {
    // Create a shared ArrayContainer near the threshold (DEFAULT_MAX_SIZE = 4096)
    RoaringBitmap rb = new RoaringBitmap();
    for (int i = 0; i < 4095; i++) {
      rb.add(i);
    }
    // This should be an ArrayContainer with 4095 elements

    CopyOnWriteRoaringBitmapV2 original = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 cloned = original.clone();
    int[] originalBefore = original.toArray();

    // Add one more element to trigger ArrayContainer -> BitmapContainer transition
    cloned.add(4095);

    assertArrayEquals(originalBefore, original.toArray(),
        "Original corrupted by container type transition on clone");
    assertEquals(4096, cloned.getCardinality());
    assertTrue(cloned.contains(4095));
  }

  @Test
  public void addMultipleTriggeringBitmapTransitionOnSharedContainer() {
    // Create a shared ArrayContainer with 4096 elements (DEFAULT_MAX_SIZE)
    RoaringBitmap rb = new RoaringBitmap();
    for (int i = 0; i < 4096; i++) {
      rb.add(i);
    }
    // At 4096, this is at the boundary; adding one more makes it a BitmapContainer

    CopyOnWriteRoaringBitmapV2 original = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 cloned = original.clone();
    int[] originalBefore = original.toArray();

    // Add element to trigger transition
    cloned.add(4096);

    assertArrayEquals(originalBefore, original.toArray(),
        "Original corrupted by bitmap transition on clone");
    assertTrue(cloned.contains(4096));
  }

  @Test
  public void removeCausingBitmapToArrayTransitionOnSharedContainer() {
    // Create a shared BitmapContainer with exactly DEFAULT_MAX_SIZE + 1 elements
    RoaringBitmap rb = new RoaringBitmap();
    for (int i = 0; i < 4097; i++) {
      rb.add(i);
    }
    // This is a BitmapContainer

    CopyOnWriteRoaringBitmapV2 original = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 cloned = original.clone();
    int[] originalBefore = original.toArray();

    // Remove one element — but this won't trigger the transition
    // Need to use flip to get the right cardinality transition
    // Actually, remove doesn't transition BitmapContainer to ArrayContainer
    // That only happens through flip. Let me use flip instead.
    cloned.flip(0);

    assertArrayEquals(originalBefore, original.toArray(),
        "Original corrupted by bitmap-to-array transition via flip on clone");
    assertEquals(4096, cloned.getCardinality());
    assertFalse(cloned.contains(0));
  }

  // -----------------------------------------------------------------------
  // Bug hunting: in-place xor/andNot that empties ALL containers
  // -----------------------------------------------------------------------

  @Test
  public void inPlaceXorEmptyingAllContainers() {
    CopyOnWriteRoaringBitmapV2 cow = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 source = cow.clone();
    int[] sourceOriginal = source.toArray();

    // XOR with identical bitmap — should empty all containers
    RoaringBitmap identical = buildMultiContainerBitmap(0, 5, 42);
    cow.xor(identical);

    assertTrue(cow.isEmpty(), "XOR with identical should produce empty bitmap");
    assertArrayEquals(sourceOriginal, source.toArray(),
        "Source corrupted by XOR that emptied all containers");
  }

  @Test
  public void inPlaceAndNotEmptyingAllContainers() {
    CopyOnWriteRoaringBitmapV2 cow = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 source = cow.clone();
    int[] sourceOriginal = source.toArray();

    // Use a superset for andNot
    RoaringBitmap superset = buildMultiContainerBitmap(0, 5, 42);
    for (int key = 0; key < 5; key++) {
      superset.add((long)(key << 16), (long)(key << 16) + 65536);
    }
    cow.andNot(superset);

    assertTrue(cow.isEmpty(), "andNot with superset should produce empty bitmap");
    assertArrayEquals(sourceOriginal, source.toArray(),
        "Source corrupted by andNot that emptied all containers");
  }

  // -----------------------------------------------------------------------
  // Bug hunting: mixed in-place ops on shared result
  // -----------------------------------------------------------------------

  @Test
  public void chainedInPlaceOpsOnSharedResult() {
    CopyOnWriteRoaringBitmapV2 a = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 b = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(3, 8, 99));

    // Create shared result
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(a, b);
    int[] aOriginal = a.toArray();
    int[] bOriginal = b.toArray();

    // Chain multiple in-place ops
    RoaringBitmap mask1 = buildMultiContainerBitmap(1, 4, 77);
    RoaringBitmap mask2 = buildMultiContainerBitmap(5, 7, 88);

    // Compute reference
    RoaringBitmap refResult = RoaringBitmap.or(a, b);
    refResult.xor(mask1);
    refResult.andNot(mask2);

    // Apply to COW
    result.xor(mask1);
    result.andNot(mask2);

    assertArrayEquals(refResult.toArray(), result.toArray(),
        "Chained in-place ops produced wrong result");
    assertArrayEquals(aOriginal, a.toArray(),
        "a corrupted by chained in-place ops on shared result");
    assertArrayEquals(bOriginal, b.toArray(),
        "b corrupted by chained in-place ops on shared result");
  }

  // -----------------------------------------------------------------------
  // Polymorphism: CopyOnWriteRoaringBitmapV2 used through RoaringBitmap reference
  // -----------------------------------------------------------------------

  @Test
  public void usedThroughRoaringBitmapReference() {
    RoaringBitmap cow = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 original = (CopyOnWriteRoaringBitmapV2) cow;
    CopyOnWriteRoaringBitmapV2 cloned = original.clone();
    int[] originalBefore = original.toArray();

    // All operations through the RoaringBitmap reference should use COW
    cow.add(42);
    cow.remove(originalBefore[0]);
    cow.flip(originalBefore[1]);

    // Cloned should be unchanged
    assertArrayEquals(originalBefore, cloned.toArray(),
        "Clone was corrupted by mutations through RoaringBitmap reference");
  }

  @Test
  public void inPlaceOrThroughRoaringBitmapReference() {
    RoaringBitmap cow = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 original = (CopyOnWriteRoaringBitmapV2) cow;
    CopyOnWriteRoaringBitmapV2 cloned = original.clone();
    int[] cloneBefore = cloned.toArray();

    RoaringBitmap extra = buildMultiContainerBitmap(3, 8, 99);
    cow.or(extra);

    assertArrayEquals(cloneBefore, cloned.toArray(),
        "Clone was corrupted by in-place OR through RoaringBitmap reference");
  }

  @Test
  public void inPlaceAndXorAndNotThroughRoaringBitmapReference() {
    // Test and
    {
      RoaringBitmap cow = CopyOnWriteRoaringBitmapV2.fromBitmap(
          buildMultiContainerBitmap(0, 5, 42));
      CopyOnWriteRoaringBitmapV2 cloned = ((CopyOnWriteRoaringBitmapV2) cow).clone();
      int[] cloneBefore = cloned.toArray();

      RoaringBitmap mask = buildMultiContainerBitmap(2, 7, 99);
      cow.and(mask);

      assertArrayEquals(cloneBefore, cloned.toArray(),
          "Clone corrupted by in-place AND through RoaringBitmap reference");
    }

    // Test xor
    {
      RoaringBitmap cow = CopyOnWriteRoaringBitmapV2.fromBitmap(
          buildMultiContainerBitmap(0, 5, 42));
      CopyOnWriteRoaringBitmapV2 cloned = ((CopyOnWriteRoaringBitmapV2) cow).clone();
      int[] cloneBefore = cloned.toArray();

      RoaringBitmap mask = buildMultiContainerBitmap(2, 7, 99);
      cow.xor(mask);

      assertArrayEquals(cloneBefore, cloned.toArray(),
          "Clone corrupted by in-place XOR through RoaringBitmap reference");
    }

    // Test andNot
    {
      RoaringBitmap cow = CopyOnWriteRoaringBitmapV2.fromBitmap(
          buildMultiContainerBitmap(0, 5, 42));
      CopyOnWriteRoaringBitmapV2 cloned = ((CopyOnWriteRoaringBitmapV2) cow).clone();
      int[] cloneBefore = cloned.toArray();

      RoaringBitmap mask = buildMultiContainerBitmap(2, 7, 99);
      cow.andNot(mask);

      assertArrayEquals(cloneBefore, cloned.toArray(),
          "Clone corrupted by in-place ANDNOT through RoaringBitmap reference");
    }
  }

  // -----------------------------------------------------------------------
  // Bug hunting: passing COW bitmap to RoaringBitmap.or/and/xor/andNot static methods
  // -----------------------------------------------------------------------

  @Test
  public void cowPassedToRoaringBitmapStaticOr() {
    CopyOnWriteRoaringBitmapV2 cowA = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 cowB = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(3, 8, 99));
    int[] aBefore = cowA.toArray();
    int[] bBefore = cowB.toArray();

    // Use the RoaringBitmap static methods (NOT CopyOnWriteRoaringBitmapV2 static methods)
    RoaringBitmap result = RoaringBitmap.or(cowA, cowB);
    RoaringBitmap expected = RoaringBitmap.or(
        buildMultiContainerBitmap(0, 5, 42),
        buildMultiContainerBitmap(3, 8, 99));

    assertArrayEquals(expected.toArray(), result.toArray(),
        "RoaringBitmap.or with COW inputs produced wrong result");

    // Inputs must be unchanged
    assertArrayEquals(aBefore, cowA.toArray(),
        "cowA corrupted by RoaringBitmap.or");
    assertArrayEquals(bBefore, cowB.toArray(),
        "cowB corrupted by RoaringBitmap.or");
  }

  @Test
  public void cowPassedAsArgumentToPlainBitmapOr() {
    // Plain RoaringBitmap.or(COW) — tests that the parent's or() handles COW arg correctly
    RoaringBitmap plain = buildMultiContainerBitmap(0, 5, 42);
    CopyOnWriteRoaringBitmapV2 cow = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(3, 8, 99));
    CopyOnWriteRoaringBitmapV2 cowClone = cow.clone();
    int[] cowBefore = cow.toArray();

    // plain.or(cow) — plain is NOT a COW bitmap, so its or() is the parent method
    // The parent's or() calls x2.highLowContainer.getContainerAtIndex(pos2).clone()
    // for non-overlapping containers and .ior() for overlapping ones
    plain.or(cow);

    // cow should not be corrupted (parent's or() clones from x2)
    assertArrayEquals(cowBefore, cow.toArray(),
        "COW bitmap corrupted when used as argument to plain bitmap's or()");
  }

  // -----------------------------------------------------------------------
  // Bug hunting: bidirectional mutation after in-place COW sharing
  // -----------------------------------------------------------------------

  @Test
  public void bidirectionalMutationAfterInPlaceOrSharing() {
    // result.or(x2) where x2 is COW — shares containers into result,
    // marks shared in both. Then BOTH are mutated.
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 3, 42));    // keys 0,1,2
    CopyOnWriteRoaringBitmapV2 x2 = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(3, 6, 99));    // keys 3,4,5

    int[] x2Before = x2.toArray();

    // In-place OR: non-overlapping containers from x2 are shared into result
    result.or(x2);

    // Both result and x2 share the same container references for keys 3,4,5
    // Mutate result at a shared key
    result.add(3 << 16 | 42);  // touches key 3 (shared from x2)

    // x2 must be unchanged
    assertArrayEquals(x2Before, x2.toArray(),
        "x2 corrupted by result mutation after in-place OR sharing");

    // Now mutate x2 at a key that's shared with result
    int[] resultBeforeX2Mutation = result.toArray();
    x2.add(4 << 16 | 42);  // touches key 4 (shared with result)

    // result must be unchanged
    assertArrayEquals(resultBeforeX2Mutation, result.toArray(),
        "result corrupted by x2 mutation after in-place OR sharing");
  }

  @Test
  public void bidirectionalMutationAfterInPlaceXorSharing() {
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 3, 42));
    CopyOnWriteRoaringBitmapV2 x2 = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(3, 6, 99));

    int[] x2Before = x2.toArray();

    result.xor(x2);

    result.add(3 << 16 | 42);

    assertArrayEquals(x2Before, x2.toArray(),
        "x2 corrupted by result mutation after in-place XOR sharing");

    int[] resultBeforeX2Mutation = result.toArray();
    x2.add(4 << 16 | 42);

    assertArrayEquals(resultBeforeX2Mutation, result.toArray(),
        "result corrupted by x2 mutation after in-place XOR sharing");
  }

  @Test
  public void bidirectionalMutationAfterLazyOrSharing() {
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 3, 42));
    CopyOnWriteRoaringBitmapV2 x2 = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(3, 6, 99));

    int[] x2Before = x2.toArray();

    result.lazyor(x2);
    result.repairAfterLazy();

    result.add(3 << 16 | 42);

    assertArrayEquals(x2Before, x2.toArray(),
        "x2 corrupted after lazyor sharing + mutation");

    int[] resultBeforeX2Mutation = result.toArray();
    x2.add(4 << 16 | 42);

    assertArrayEquals(resultBeforeX2Mutation, result.toArray(),
        "result corrupted after lazyor sharing + x2 mutation");
  }

  @Test
  public void bidirectionalMutationAfterNaiveLazyOrSharing() {
    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 3, 42));
    CopyOnWriteRoaringBitmapV2 x2 = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(3, 6, 99));

    int[] x2Before = x2.toArray();

    result.naivelazyor(x2);
    result.repairAfterLazy();

    result.add(3 << 16 | 42);

    assertArrayEquals(x2Before, x2.toArray(),
        "x2 corrupted after naivelazyor sharing + mutation");

    int[] resultBeforeX2Mutation = result.toArray();
    x2.add(4 << 16 | 42);

    assertArrayEquals(resultBeforeX2Mutation, result.toArray(),
        "result corrupted after naivelazyor sharing + x2 mutation");
  }

  // -----------------------------------------------------------------------
  // Bug hunting: stress test with many containers
  // -----------------------------------------------------------------------

  @Test
  public void stressTestManyContainers() {
    // Create bitmap with 100 containers
    RoaringBitmap rb = buildMultiContainerBitmap(0, 100, 42);
    CopyOnWriteRoaringBitmapV2 original = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 cloned = original.clone();
    int[] originalBefore = original.toArray();

    // Mutate every other container in the clone
    for (int key = 0; key < 100; key += 2) {
      cloned.add(key << 16 | 60000);
    }

    // Original must be unchanged
    assertArrayEquals(originalBefore, original.toArray(),
        "Original corrupted by mutations on every other container of clone");

    // Verify clone has the new values
    for (int key = 0; key < 100; key += 2) {
      assertTrue(cloned.contains(key << 16 | 60000),
          "Clone missing value at key " + key);
    }
  }

  // -----------------------------------------------------------------------
  // Bug hunting: in-place or where both bitmaps share same container objects
  // -----------------------------------------------------------------------

  @Test
  public void inPlaceOrBetweenClonesOfSameSource() {
    CopyOnWriteRoaringBitmapV2 source = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 a = source.clone();
    CopyOnWriteRoaringBitmapV2 b = source.clone();
    int[] sourceOriginal = source.toArray();

    // a and b share the SAME container objects (both cloned from source)
    // a.or(b) processes overlapping keys: copyIfShared(pos1) clones a's container,
    // then .ior(b's container) reads from b's container (which is the SAME object
    // as what was originally in a before the clone)
    a.or(b);

    // Result should be the same (or of identical = same)
    assertArrayEquals(sourceOriginal, a.toArray(),
        "a.or(b) where both share same containers produced wrong result");

    // source must be unchanged
    assertArrayEquals(sourceOriginal, source.toArray(),
        "source corrupted by or between its clones");

    // b must be unchanged
    assertArrayEquals(sourceOriginal, b.toArray(),
        "b corrupted by a.or(b) where both share same containers");
  }

  @Test
  public void inPlaceXorBetweenClonesOfSameSource() {
    CopyOnWriteRoaringBitmapV2 source = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 a = source.clone();
    CopyOnWriteRoaringBitmapV2 b = source.clone();
    int[] sourceOriginal = source.toArray();

    // a.xor(b) where both share same containers — should produce empty
    a.xor(b);

    assertTrue(a.isEmpty(), "a.xor(b) where both are identical should be empty");
    assertArrayEquals(sourceOriginal, source.toArray(),
        "source corrupted by xor between its clones");
    assertArrayEquals(sourceOriginal, b.toArray(),
        "b corrupted by a.xor(b) where both share same containers");
  }

  @Test
  public void inPlaceAndNotBetweenClonesOfSameSource() {
    CopyOnWriteRoaringBitmapV2 source = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 a = source.clone();
    CopyOnWriteRoaringBitmapV2 b = source.clone();
    int[] sourceOriginal = source.toArray();

    // a.andNot(b) where both are identical — should produce empty
    a.andNot(b);

    assertTrue(a.isEmpty(), "a.andNot(b) where both are identical should be empty");
    assertArrayEquals(sourceOriginal, source.toArray(),
        "source corrupted by andNot between its clones");
    assertArrayEquals(sourceOriginal, b.toArray(),
        "b corrupted by a.andNot(b) where both share same containers");
  }

  @Test
  public void inPlaceAndBetweenClonesOfSameSource() {
    CopyOnWriteRoaringBitmapV2 source = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 a = source.clone();
    CopyOnWriteRoaringBitmapV2 b = source.clone();
    int[] sourceOriginal = source.toArray();

    // a.and(b) where both are identical — should equal source
    a.and(b);

    assertArrayEquals(sourceOriginal, a.toArray(),
        "a.and(b) where both are identical should equal source");
    assertArrayEquals(sourceOriginal, source.toArray(),
        "source corrupted by and between its clones");
    assertArrayEquals(sourceOriginal, b.toArray(),
        "b corrupted by a.and(b) where both share same containers");
  }

  // -----------------------------------------------------------------------
  // Bug hunting: shared[] consistency check after various op sequences
  // -----------------------------------------------------------------------

  @Test
  public void sharedArrayConsistencyAfterComplexSequence() {
    CopyOnWriteRoaringBitmapV2 source = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 10, 42));
    CopyOnWriteRoaringBitmapV2 cow = source.clone();

    // 1. All containers should be shared after clone
    for (int i = 0; i < cow.getContainerCount(); i++) {
      assertTrue(cow.isShared(i), "Initially, container " + i + " should be shared");
    }

    // 2. Add a value to key 5 — should clone that container
    cow.add(5 << 16 | 60000);
    assertFalse(cow.isShared(5), "Container at key 5 should not be shared after mutation");
    for (int i = 0; i < cow.getContainerCount(); i++) {
      if (i != 5) {
        assertTrue(cow.isShared(i), "Container " + i + " should still be shared");
      }
    }

    // 3. Remove entire container at key 3
    for (int val : source.toArray()) {
      if (val >>> 16 == 3) {
        cow.remove(val);
      }
    }
    // Also remove the newly added value at key 3 if any
    // Actually, we only added at key 5. Let me force-remove key 3.
    // The container at key 3 should be removed if it becomes empty.
    // But it won't become empty from removing one value. Let me remove ALL values.

    // Reset for cleaner test
    cow = source.clone();
    int containerCount = cow.getContainerCount();

    // Add new key in the middle
    cow.add(10 << 16 | 1); // key 10, should be at the end since keys are 0-9
    assertEquals(containerCount + 1, cow.getContainerCount());
    assertFalse(cow.isShared(containerCount),
        "New container at the end should not be shared");

    // Add new key between existing keys
    // Create a gap first by removing key 5
    cow = source.clone();
    // Remove ALL values from key 5
    // Use range remove to be sure
    cow.remove(5L << 16, 6L << 16);

    // Now cow has 9 containers (keys 0-4, 6-9)
    assertEquals(9, cow.getContainerCount());

    // Insert new key 5
    cow.add(5 << 16 | 42);
    assertEquals(10, cow.getContainerCount());

    // Key 5 container should not be shared
    // But we need to find which index key 5 is at
    for (int i = 0; i < cow.getContainerCount(); i++) {
      int key = cow.getKeyAtIndex(i);
      if (key == 5) {
        assertFalse(cow.isShared(i),
            "Newly inserted container at key 5 should not be shared");
      }
    }
  }

  @Test
  public void sharedArrayLengthNeverShorterThanContainerCount() {
    CopyOnWriteRoaringBitmapV2 cow = new CopyOnWriteRoaringBitmapV2();

    // Build up a large number of containers
    for (int key = 0; key < 50; key++) {
      cow.add(key << 16 | 1);
    }
    assertEquals(50, cow.getContainerCount());

    // Clone and verify
    CopyOnWriteRoaringBitmapV2 cloned = cow.clone();
    assertEquals(50, cloned.getContainerCount());

    // Remove half the containers
    for (int key = 0; key < 50; key += 2) {
      cloned.remove(key << 16 | 1); // removes the only value, removing the container
    }
    assertEquals(25, cloned.getContainerCount());

    // All remaining containers should have valid shared[] entries (no AIOOBE)
    for (int i = 0; i < cloned.getContainerCount(); i++) {
      // This call should not throw
      boolean isShared = cloned.isShared(i);
      assertTrue(isShared, "Remaining containers should be shared with original");
    }
  }

  private static RoaringBitmap buildBitmap(int start, int end, int step) {
    RoaringBitmap rb = new RoaringBitmap();
    for (int i = start; i < end; i += step) {
      rb.add(i);
    }
    return rb;
  }

  // -----------------------------------------------------------------------
  // Bug hunting: static or with self (or(x, x))
  // -----------------------------------------------------------------------

  @Test
  public void staticOrWithSelfProducesCorrectResult() {
    CopyOnWriteRoaringBitmapV2 x = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    int[] before = x.toArray();

    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(x, x);

    // or(x, x) should equal x
    assertArrayEquals(before, result.toArray(), "or(x, x) should equal x");
  }

  @Test
  public void staticOrWithSelfDoesNotCorruptInput() {
    CopyOnWriteRoaringBitmapV2 x = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    int[] before = x.toArray();

    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(x, x);

    // Mutate result
    result.add(42);
    result.remove(before[0]);

    // Input must be unchanged
    assertArrayEquals(before, x.toArray(), "Input was corrupted after or(x, x)");
  }

  @Test
  public void staticAndWithSelfProducesCorrectResult() {
    CopyOnWriteRoaringBitmapV2 x = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    int[] before = x.toArray();

    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.and(x, x);

    // and(x, x) should equal x
    assertArrayEquals(before, result.toArray(), "and(x, x) should equal x");
  }

  @Test
  public void staticXorWithSelfProducesEmpty() {
    CopyOnWriteRoaringBitmapV2 x = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));

    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.xor(x, x);

    assertTrue(result.isEmpty(), "xor(x, x) should be empty");
  }

  @Test
  public void staticAndNotWithSelfProducesEmpty() {
    CopyOnWriteRoaringBitmapV2 x = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));

    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.andNot(x, x);

    assertTrue(result.isEmpty(), "andNot(x, x) should be empty");
  }

  // -----------------------------------------------------------------------
  // Bug hunting: runOptimize on cloned bitmap
  // -----------------------------------------------------------------------

  @Test
  public void runOptimizeOnCloneDoesNotCorruptOriginal() {
    // Create a bitmap with run-friendly data (dense ranges)
    RoaringBitmap rb = new RoaringBitmap();
    rb.add(0L, 1000L);  // dense range in key 0
    rb.add(1L << 16, (1L << 16) + 1000L);  // dense range in key 1
    rb.runOptimize(); // ensure RunContainers

    CopyOnWriteRoaringBitmapV2 original = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    int[] originalBefore = original.toArray();

    CopyOnWriteRoaringBitmapV2 cloned = original.clone();

    // runOptimize on clone should not affect original
    cloned.runOptimize();

    assertArrayEquals(originalBefore, original.toArray(),
        "Original was corrupted by runOptimize on clone");
  }

  @Test
  public void removeRunCompressionOnCloneDoesNotCorruptOriginal() {
    RoaringBitmap rb = new RoaringBitmap();
    rb.add(0L, 1000L);
    rb.add(1L << 16, (1L << 16) + 1000L);
    rb.runOptimize();

    CopyOnWriteRoaringBitmapV2 original = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    int[] originalBefore = original.toArray();

    CopyOnWriteRoaringBitmapV2 cloned = original.clone();

    // removeRunCompression on clone should not affect original
    cloned.removeRunCompression();

    assertArrayEquals(originalBefore, original.toArray(),
        "Original was corrupted by removeRunCompression on clone");
  }

  // -----------------------------------------------------------------------
  // Bug hunting: xor removing containers from a cloned bitmap
  // -----------------------------------------------------------------------

  @Test
  public void xorRemovingAllOverlappingContainersKeepsSharedArrayConsistent() {
    // Create shared bitmap with keys 0, 1, 2
    CopyOnWriteRoaringBitmapV2 source = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 3, 42));
    CopyOnWriteRoaringBitmapV2 result = source.clone();
    int[] sourceOriginal = source.toArray();

    // XOR with a bitmap that has identical content for keys 0 and 1
    // This should remove containers 0 and 1 (xor of identical = empty)
    RoaringBitmap mask = new RoaringBitmap();
    // Copy exact content of key 0 and 1 from source
    for (int val : sourceOriginal) {
      int key = val >>> 16;
      if (key == 0 || key == 1) {
        mask.add(val);
      }
    }

    result.xor(mask);

    // Only key 2 should remain
    assertEquals(1, result.getContainerCount());
    // Key 2's container should still be shared with source
    assertTrue(result.isShared(0), "Remaining container should still be shared");

    // Source must be unchanged
    assertArrayEquals(sourceOriginal, source.toArray(),
        "Source was corrupted by xor that removed containers");
  }

  // -----------------------------------------------------------------------
  // Bug hunting: range remove emptying middle containers in cloned bitmap
  // -----------------------------------------------------------------------

  @Test
  public void rangeRemoveEmptyingMiddleContainersInClonedBitmap() {
    // Create a bitmap with one element per container for keys 0..4
    RoaringBitmap rb = new RoaringBitmap();
    for (int key = 0; key < 5; key++) {
      rb.add(key << 16 | 1);
    }
    CopyOnWriteRoaringBitmapV2 original = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 cloned = original.clone();
    int[] originalBefore = original.toArray();

    // Remove range that spans keys 1, 2, 3 completely (emptying those containers)
    cloned.remove(1L << 16, 4L << 16);

    // Only keys 0 and 4 should remain
    assertEquals(2, cloned.getContainerCount());
    assertTrue(cloned.contains(0 << 16 | 1));
    assertTrue(cloned.contains(4 << 16 | 1));

    // Both remaining containers should still be shared
    assertTrue(cloned.isShared(0), "Container 0 should still be shared");
    assertTrue(cloned.isShared(1), "Container 1 (was key 4) should still be shared");

    // Original must be unchanged
    assertArrayEquals(originalBefore, original.toArray(),
        "Original was corrupted by range remove on clone");
  }

  // -----------------------------------------------------------------------
  // Bug hunting: sequential insert + remove on cloned bitmap
  // -----------------------------------------------------------------------

  @Test
  public void insertThenRemoveOnClonedBitmapKeepsSharedConsistent() {
    // Create shared bitmap with keys 0, 2, 4 (gaps at 1, 3)
    RoaringBitmap rb = new RoaringBitmap();
    rb.add(0 << 16 | 1);
    rb.add(2 << 16 | 1);
    rb.add(4 << 16 | 1);

    CopyOnWriteRoaringBitmapV2 original = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 cloned = original.clone();
    int[] originalBefore = original.toArray();

    // Insert a new container at key 1 (between 0 and 2)
    cloned.add(1 << 16 | 1);
    assertEquals(4, cloned.getContainerCount());

    // Now remove the container at key 2 (the only element)
    cloned.remove(2 << 16 | 1);
    assertEquals(3, cloned.getContainerCount());

    // Verify containers: keys should be 0, 1, 4
    assertTrue(cloned.contains(0 << 16 | 1));
    assertTrue(cloned.contains(1 << 16 | 1));
    assertTrue(cloned.contains(4 << 16 | 1));

    // Key 0 and 4 containers should still be shared with original
    assertTrue(cloned.isShared(0), "Container at key 0 should still be shared");
    assertFalse(cloned.isShared(1), "Container at key 1 (new) should not be shared");
    assertTrue(cloned.isShared(2), "Container at key 4 should still be shared");

    // Original must be unchanged
    assertArrayEquals(originalBefore, original.toArray(),
        "Original was corrupted by insert+remove on clone");
  }

  // -----------------------------------------------------------------------
  // Bug hunting: in-place or between two COW bitmaps with overlapping shared containers
  // -----------------------------------------------------------------------

  @Test
  public void inPlaceOrBetweenTwoCowBitmapsSharingContainersFromSameSource() {
    // Setup: x is the source, a and b are results of static ops that share x's containers
    CopyOnWriteRoaringBitmapV2 x = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 y = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(5, 10, 99));
    int[] xOriginal = x.toArray();

    CopyOnWriteRoaringBitmapV2 a = CopyOnWriteRoaringBitmapV2.or(x, y);
    // a shares containers from x (keys 0-4) and y (keys 5-9)

    CopyOnWriteRoaringBitmapV2 b = x.clone();
    // b also shares containers from x (keys 0-4)

    // In-place or: a.or(b)
    // For overlapping keys 0-4, a's containers (shared from x) will be cloned before ior
    // b's containers (also shared from x) are passed as argument to ior
    int[] aExpected = a.toArray(); // should not change since b is subset of a

    a.or(b);

    // a should still have the same values (or with subset = no change)
    assertArrayEquals(aExpected, a.toArray(), "a's values changed unexpectedly");

    // x must be unchanged
    assertArrayEquals(xOriginal, x.toArray(),
        "x was corrupted by in-place or between bitmaps sharing its containers");
  }

  // -----------------------------------------------------------------------
  // Bug hunting: addN inserting new keys into cloned bitmap
  // -----------------------------------------------------------------------

  @Test
  public void addNNewKeysInClonedBitmapDoesNotCorruptOriginal() {
    // Create bitmap with keys 0, 2 (gap at key 1)
    RoaringBitmap rb = new RoaringBitmap();
    for (int j = 0; j < 100; j++) {
      rb.add(0 << 16 | j);
      rb.add(2 << 16 | j);
    }
    CopyOnWriteRoaringBitmapV2 original = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 cloned = original.clone();
    int[] originalBefore = original.toArray();

    // addN with values spanning key 0 (existing, shared), key 1 (new), and key 2 (existing, shared)
    int[] dat = new int[]{
        0 << 16 | 999,   // key 0 (existing, shared -> needs COW)
        1 << 16 | 50,    // key 1 (new -> insert)
        1 << 16 | 51,    // key 1 (existing, just added)
        2 << 16 | 999    // key 2 (existing, shared -> needs COW)
    };
    cloned.addN(dat, 0, dat.length);

    // Original must be unchanged
    assertArrayEquals(originalBefore, original.toArray(),
        "Original was corrupted by addN on clone");

    // Clone should have the new values
    assertTrue(cloned.contains(0 << 16 | 999));
    assertTrue(cloned.contains(1 << 16 | 50));
    assertTrue(cloned.contains(1 << 16 | 51));
    assertTrue(cloned.contains(2 << 16 | 999));
  }

  // -----------------------------------------------------------------------
  // Bug hunting: trim after clone
  // -----------------------------------------------------------------------

  @Test
  public void trimAfterClonePreservesSharedTracking() {
    CopyOnWriteRoaringBitmapV2 original = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 3, 42));
    CopyOnWriteRoaringBitmapV2 cloned = original.clone();
    int[] originalBefore = original.toArray();

    cloned.trim();

    // After trim, shared array should be correctly sized
    for (int i = 0; i < cloned.getContainerCount(); i++) {
      assertTrue(cloned.isShared(i),
          "Container " + i + " should still be shared after trim");
    }

    // Mutate cloned — should COW correctly
    cloned.add(42);
    assertArrayEquals(originalBefore, original.toArray(),
        "Original was corrupted after trim + mutation on clone");
  }

  // -----------------------------------------------------------------------
  // Bug hunting: clear after sharing preserves other bitmap
  // -----------------------------------------------------------------------

  @Test
  public void clearAfterSharingDoesNotCorruptOtherBitmap() {
    CopyOnWriteRoaringBitmapV2 a = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 b = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(5, 10, 99));

    CopyOnWriteRoaringBitmapV2 result = CopyOnWriteRoaringBitmapV2.or(a, b);
    int[] resultBefore = result.toArray();

    // Clear one of the inputs
    a.clear();
    assertTrue(a.isEmpty());

    // Result must be unchanged
    assertArrayEquals(resultBefore, result.toArray(),
        "Result was corrupted when input was cleared");
  }

  // -----------------------------------------------------------------------
  // Bug hunting: multiple clones creating a chain of sharing
  // -----------------------------------------------------------------------

  @Test
  public void multipleCloneChainPreservesCow() {
    CopyOnWriteRoaringBitmapV2 a = CopyOnWriteRoaringBitmapV2.fromBitmap(
        RoaringBitmap.bitmapOf(1, 2, 3));
    CopyOnWriteRoaringBitmapV2 b = a.clone();
    CopyOnWriteRoaringBitmapV2 c = b.clone();
    int[] aOriginal = a.toArray();

    // Mutate c — should not affect a or b
    c.add(4);
    c.remove(1);

    assertArrayEquals(aOriginal, a.toArray(), "a was corrupted by c mutation");
    assertArrayEquals(aOriginal, b.toArray(), "b was corrupted by c mutation");
  }

  // -----------------------------------------------------------------------
  // Bug hunting: flip(long, long) inserting new containers in cloned bitmap
  // -----------------------------------------------------------------------

  @Test
  public void rangeFlipInsertingNewContainersInClonedBitmap() {
    // Create bitmap with keys 0 and 4 (gap at keys 1, 2, 3)
    RoaringBitmap rb = new RoaringBitmap();
    rb.add(0 << 16 | 1);
    rb.add(4 << 16 | 1);
    CopyOnWriteRoaringBitmapV2 original = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 cloned = original.clone();
    int[] originalBefore = original.toArray();

    // Flip a range that covers keys 1, 2, 3 (where no containers exist)
    // This should INSERT new containers for those keys
    long start = 1L << 16;
    long end = 4L << 16; // up to but not including key 4
    cloned.flip(start, end);

    // Cloned should now have containers at keys 0, 1, 2, 3, 4
    assertEquals(5, cloned.getContainerCount());

    // Key 0 and 4 should still be shared
    assertTrue(cloned.isShared(0), "Container at key 0 should still be shared");
    // New containers should NOT be shared
    assertFalse(cloned.isShared(1), "New container at key 1 should not be shared");
    assertFalse(cloned.isShared(2), "New container at key 2 should not be shared");
    assertFalse(cloned.isShared(3), "New container at key 3 should not be shared");
    assertTrue(cloned.isShared(4), "Container at key 4 should still be shared");

    // Original must be unchanged
    assertArrayEquals(originalBefore, original.toArray(),
        "Original was corrupted by range flip on clone");
  }

  // -----------------------------------------------------------------------
  // Bug hunting: orNot on cloned bitmap
  // -----------------------------------------------------------------------

  @Test
  public void orNotOnClonedBitmapDoesNotCorruptOriginal() {
    RoaringBitmap rb = buildMultiContainerBitmap(0, 3, 42);
    CopyOnWriteRoaringBitmapV2 original = CopyOnWriteRoaringBitmapV2.fromBitmap(rb);
    CopyOnWriteRoaringBitmapV2 cloned = original.clone();
    int[] originalBefore = original.toArray();

    RoaringBitmap other = buildMultiContainerBitmap(1, 4, 99);
    cloned.orNot(other, 3L << 16);

    assertArrayEquals(originalBefore, original.toArray(),
        "Original was corrupted by orNot on clone");
  }

  // -----------------------------------------------------------------------
  // Bug hunting: deserialization on a bitmap with shared containers
  // -----------------------------------------------------------------------

  @Test
  public void deserializeOverSharedBitmapResetsCorrectly() throws IOException {
    // Create two bitmaps sharing containers
    CopyOnWriteRoaringBitmapV2 a = CopyOnWriteRoaringBitmapV2.fromBitmap(
        buildMultiContainerBitmap(0, 5, 42));
    CopyOnWriteRoaringBitmapV2 b = a.clone();
    int[] aBefore = a.toArray();

    // Serialize a different bitmap
    RoaringBitmap different = buildMultiContainerBitmap(0, 3, 99);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    different.serialize(new DataOutputStream(baos));
    byte[] bytes = baos.toByteArray();

    // Deserialize into b (which shares containers with a)
    b.deserialize(new DataInputStream(new ByteArrayInputStream(bytes)));

    // a must be unchanged
    assertArrayEquals(aBefore, a.toArray(),
        "a was corrupted when its clone was deserialized over");

    // b should have the new content
    assertArrayEquals(different.toArray(), b.toArray(),
        "b does not have the deserialized content");

    // b should have no shared containers
    for (int i = 0; i < b.getContainerCount(); i++) {
      assertFalse(b.isShared(i),
          "Container " + i + " should not be shared after deserialize");
    }
  }
}

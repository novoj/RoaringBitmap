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
import java.util.Random;

public class TestFrozenRoaringBitmap {

  // -----------------------------------------------------------------------
  // Correctness: binary ops produce same results as RoaringBitmap
  // -----------------------------------------------------------------------

  @Test
  public void orMatchesMutable() {
    RoaringBitmap a = buildBitmap(0, 100, 1);
    RoaringBitmap b = buildBitmap(50, 150, 1);
    RoaringBitmap expected = RoaringBitmap.or(a, b);

    FrozenRoaringBitmap fa = FrozenRoaringBitmap.freeze(a);
    FrozenRoaringBitmap fb = FrozenRoaringBitmap.freeze(b);
    FrozenRoaringBitmap result = FrozenRoaringBitmap.or(fa, fb);

    assertArrayEquals(expected.toArray(), result.toArray());
  }

  @Test
  public void andMatchesMutable() {
    RoaringBitmap a = buildBitmap(0, 100, 1);
    RoaringBitmap b = buildBitmap(50, 150, 1);
    RoaringBitmap expected = RoaringBitmap.and(a, b);

    FrozenRoaringBitmap fa = FrozenRoaringBitmap.freeze(a);
    FrozenRoaringBitmap fb = FrozenRoaringBitmap.freeze(b);
    FrozenRoaringBitmap result = FrozenRoaringBitmap.and(fa, fb);

    assertArrayEquals(expected.toArray(), result.toArray());
  }

  @Test
  public void andNotMatchesMutable() {
    RoaringBitmap a = buildBitmap(0, 100, 1);
    RoaringBitmap b = buildBitmap(50, 150, 1);
    RoaringBitmap expected = RoaringBitmap.andNot(a, b);

    FrozenRoaringBitmap fa = FrozenRoaringBitmap.freeze(a);
    FrozenRoaringBitmap fb = FrozenRoaringBitmap.freeze(b);
    FrozenRoaringBitmap result = FrozenRoaringBitmap.andNot(fa, fb);

    assertArrayEquals(expected.toArray(), result.toArray());
  }

  @Test
  public void xorMatchesMutable() {
    RoaringBitmap a = buildBitmap(0, 100, 1);
    RoaringBitmap b = buildBitmap(50, 150, 1);
    RoaringBitmap expected = RoaringBitmap.xor(a, b);

    FrozenRoaringBitmap fa = FrozenRoaringBitmap.freeze(a);
    FrozenRoaringBitmap fb = FrozenRoaringBitmap.freeze(b);
    FrozenRoaringBitmap result = FrozenRoaringBitmap.xor(fa, fb);

    assertArrayEquals(expected.toArray(), result.toArray());
  }

  @Test
  public void orMatchesMutableMultiContainer() {
    RoaringBitmap a = buildMultiContainerBitmap(0, 10, 42);
    RoaringBitmap b = buildMultiContainerBitmap(5, 15, 99);
    RoaringBitmap expected = RoaringBitmap.or(a, b);

    FrozenRoaringBitmap fa = FrozenRoaringBitmap.freeze(a);
    FrozenRoaringBitmap fb = FrozenRoaringBitmap.freeze(b);
    FrozenRoaringBitmap result = FrozenRoaringBitmap.or(fa, fb);

    assertArrayEquals(expected.toArray(), result.toArray());
  }

  @Test
  public void andMatchesMutableMultiContainer() {
    RoaringBitmap a = buildMultiContainerBitmap(0, 10, 42);
    RoaringBitmap b = buildMultiContainerBitmap(5, 15, 99);
    RoaringBitmap expected = RoaringBitmap.and(a, b);

    FrozenRoaringBitmap fa = FrozenRoaringBitmap.freeze(a);
    FrozenRoaringBitmap fb = FrozenRoaringBitmap.freeze(b);
    FrozenRoaringBitmap result = FrozenRoaringBitmap.and(fa, fb);

    assertArrayEquals(expected.toArray(), result.toArray());
  }

  @Test
  public void andNotMatchesMutableMultiContainer() {
    RoaringBitmap a = buildMultiContainerBitmap(0, 10, 42);
    RoaringBitmap b = buildMultiContainerBitmap(5, 15, 99);
    RoaringBitmap expected = RoaringBitmap.andNot(a, b);

    FrozenRoaringBitmap fa = FrozenRoaringBitmap.freeze(a);
    FrozenRoaringBitmap fb = FrozenRoaringBitmap.freeze(b);
    FrozenRoaringBitmap result = FrozenRoaringBitmap.andNot(fa, fb);

    assertArrayEquals(expected.toArray(), result.toArray());
  }

  @Test
  public void xorMatchesMutableMultiContainer() {
    RoaringBitmap a = buildMultiContainerBitmap(0, 10, 42);
    RoaringBitmap b = buildMultiContainerBitmap(5, 15, 99);
    RoaringBitmap expected = RoaringBitmap.xor(a, b);

    FrozenRoaringBitmap fa = FrozenRoaringBitmap.freeze(a);
    FrozenRoaringBitmap fb = FrozenRoaringBitmap.freeze(b);
    FrozenRoaringBitmap result = FrozenRoaringBitmap.xor(fa, fb);

    assertArrayEquals(expected.toArray(), result.toArray());
  }

  // -----------------------------------------------------------------------
  // Structural sharing: non-overlapping containers are == (same object)
  // -----------------------------------------------------------------------

  @Test
  public void orSharesNonOverlappingContainers() {
    // A has keys 0..4, B has keys 5..9 — completely disjoint
    RoaringBitmap a = buildMultiContainerBitmap(0, 5, 42);
    RoaringBitmap b = buildMultiContainerBitmap(5, 10, 99);

    FrozenRoaringBitmap fa = FrozenRoaringBitmap.freeze(a);
    FrozenRoaringBitmap fb = FrozenRoaringBitmap.freeze(b);
    FrozenRoaringBitmap result = FrozenRoaringBitmap.or(fa, fb);

    // Containers from fa should be the same objects in result
    for (int i = 0; i < 5; i++) {
      assertSame(fa.getContainerAtIndex(i), result.getContainerAtIndex(i),
          "Container at index " + i + " should be shared from fa");
    }
    // Containers from fb should be the same objects in result
    for (int i = 0; i < 5; i++) {
      assertSame(fb.getContainerAtIndex(i), result.getContainerAtIndex(5 + i),
          "Container at index " + (5 + i) + " should be shared from fb");
    }
  }

  @Test
  public void orDoesNotShareOverlappingContainers() {
    // A and B both have key 0
    RoaringBitmap a = new RoaringBitmap();
    a.add(1);
    RoaringBitmap b = new RoaringBitmap();
    b.add(2);

    FrozenRoaringBitmap fa = FrozenRoaringBitmap.freeze(a);
    FrozenRoaringBitmap fb = FrozenRoaringBitmap.freeze(b);
    FrozenRoaringBitmap result = FrozenRoaringBitmap.or(fa, fb);

    // Overlapping container should be a new object
    assertNotSame(fa.getContainerAtIndex(0), result.getContainerAtIndex(0));
    assertNotSame(fb.getContainerAtIndex(0), result.getContainerAtIndex(0));
  }

  @Test
  public void andNotSharesNonOverlappingFromX1() {
    RoaringBitmap a = buildMultiContainerBitmap(0, 10, 42);
    RoaringBitmap b = buildMultiContainerBitmap(5, 8, 99);

    FrozenRoaringBitmap fa = FrozenRoaringBitmap.freeze(a);
    FrozenRoaringBitmap fb = FrozenRoaringBitmap.freeze(b);
    FrozenRoaringBitmap result = FrozenRoaringBitmap.andNot(fa, fb);

    // Keys 0..4 from fa are non-overlapping and should be shared
    for (int i = 0; i < 5; i++) {
      assertSame(fa.getContainerAtIndex(i), result.getContainerAtIndex(i),
          "Container at index " + i + " should be shared from fa");
    }
  }

  @Test
  public void xorSharesNonOverlappingContainers() {
    RoaringBitmap a = buildMultiContainerBitmap(0, 5, 42);
    RoaringBitmap b = buildMultiContainerBitmap(5, 10, 99);

    FrozenRoaringBitmap fa = FrozenRoaringBitmap.freeze(a);
    FrozenRoaringBitmap fb = FrozenRoaringBitmap.freeze(b);
    FrozenRoaringBitmap result = FrozenRoaringBitmap.xor(fa, fb);

    // All containers should be shared (completely disjoint)
    for (int i = 0; i < 5; i++) {
      assertSame(fa.getContainerAtIndex(i), result.getContainerAtIndex(i));
    }
    for (int i = 0; i < 5; i++) {
      assertSame(fb.getContainerAtIndex(i), result.getContainerAtIndex(5 + i));
    }
  }

  // -----------------------------------------------------------------------
  // Mutation isolation
  // -----------------------------------------------------------------------

  @Test
  public void freezeIsolatesFromOriginal() {
    RoaringBitmap original = RoaringBitmap.bitmapOf(1, 2, 3);
    FrozenRoaringBitmap frozen = FrozenRoaringBitmap.freeze(original);

    // Mutate original
    original.add(4);
    original.remove(1);

    // Frozen should be unaffected
    assertTrue(frozen.contains(1));
    assertFalse(frozen.contains(4));
    assertEquals(3, frozen.getCardinality());
  }

  @Test
  public void toMutableBitmapIsIndependent() {
    FrozenRoaringBitmap frozen = FrozenRoaringBitmap.freeze(RoaringBitmap.bitmapOf(1, 2, 3));
    RoaringBitmap mutable = frozen.toMutableBitmap();

    mutable.add(4);
    mutable.remove(1);

    // Frozen should be unaffected
    assertTrue(frozen.contains(1));
    assertFalse(frozen.contains(4));
    assertEquals(3, frozen.getCardinality());
  }

  // -----------------------------------------------------------------------
  // Edge cases
  // -----------------------------------------------------------------------

  @Test
  public void orWithEmptyBitmaps() {
    FrozenRoaringBitmap empty = FrozenRoaringBitmap.freeze(new RoaringBitmap());
    FrozenRoaringBitmap nonEmpty = FrozenRoaringBitmap.freeze(RoaringBitmap.bitmapOf(1, 2, 3));

    assertEquals(nonEmpty, FrozenRoaringBitmap.or(empty, nonEmpty));
    assertEquals(nonEmpty, FrozenRoaringBitmap.or(nonEmpty, empty));

    FrozenRoaringBitmap bothEmpty = FrozenRoaringBitmap.or(empty, empty);
    assertTrue(bothEmpty.isEmpty());
  }

  @Test
  public void andWithEmptyBitmaps() {
    FrozenRoaringBitmap empty = FrozenRoaringBitmap.freeze(new RoaringBitmap());
    FrozenRoaringBitmap nonEmpty = FrozenRoaringBitmap.freeze(RoaringBitmap.bitmapOf(1, 2, 3));

    assertTrue(FrozenRoaringBitmap.and(empty, nonEmpty).isEmpty());
    assertTrue(FrozenRoaringBitmap.and(nonEmpty, empty).isEmpty());
  }

  @Test
  public void fullyOverlappingOr() {
    RoaringBitmap data = RoaringBitmap.bitmapOf(1, 2, 3, 100000, 200000);
    FrozenRoaringBitmap fa = FrozenRoaringBitmap.freeze(data);
    FrozenRoaringBitmap fb = FrozenRoaringBitmap.freeze(data);
    FrozenRoaringBitmap result = FrozenRoaringBitmap.or(fa, fb);

    assertArrayEquals(data.toArray(), result.toArray());
  }

  @Test
  public void fullyDisjointAnd() {
    RoaringBitmap a = buildMultiContainerBitmap(0, 5, 42);
    RoaringBitmap b = buildMultiContainerBitmap(5, 10, 99);

    FrozenRoaringBitmap fa = FrozenRoaringBitmap.freeze(a);
    FrozenRoaringBitmap fb = FrozenRoaringBitmap.freeze(b);

    assertTrue(FrozenRoaringBitmap.and(fa, fb).isEmpty());
  }

  @Test
  public void singleContainerBitmap() {
    FrozenRoaringBitmap fa = FrozenRoaringBitmap.freeze(RoaringBitmap.bitmapOf(42));
    FrozenRoaringBitmap fb = FrozenRoaringBitmap.freeze(RoaringBitmap.bitmapOf(42, 100));

    FrozenRoaringBitmap result = FrozenRoaringBitmap.or(fa, fb);
    assertEquals(2, result.getCardinality());
    assertTrue(result.contains(42));
    assertTrue(result.contains(100));
  }

  // -----------------------------------------------------------------------
  // Read API methods
  // -----------------------------------------------------------------------

  @Test
  public void cardinalityAndContains() {
    RoaringBitmap source = buildMultiContainerBitmap(0, 5, 42);
    FrozenRoaringBitmap frozen = FrozenRoaringBitmap.freeze(source);

    assertEquals(source.getCardinality(), frozen.getCardinality());
    assertEquals(source.getLongCardinality(), frozen.getLongCardinality());

    for (int val : source.toArray()) {
      assertTrue(frozen.contains(val));
    }
    assertFalse(frozen.contains(Integer.MAX_VALUE));
  }

  @Test
  public void firstLastSelect() {
    RoaringBitmap source = RoaringBitmap.bitmapOf(10, 20, 30, 100000);
    FrozenRoaringBitmap frozen = FrozenRoaringBitmap.freeze(source);

    assertEquals(source.first(), frozen.first());
    assertEquals(source.last(), frozen.last());
    assertEquals(source.select(0), frozen.select(0));
    assertEquals(source.select(3), frozen.select(3));
  }

  @Test
  public void rankAndRangeCardinality() {
    RoaringBitmap source = RoaringBitmap.bitmapOf(1, 5, 10, 15, 20);
    FrozenRoaringBitmap frozen = FrozenRoaringBitmap.freeze(source);

    assertEquals(source.rankLong(10), frozen.rankLong(10));
    assertEquals(source.rank(15), frozen.rank(15));
    assertEquals(source.rangeCardinality(5, 16), frozen.rangeCardinality(5, 16));
  }

  @Test
  public void iteratorValues() {
    RoaringBitmap source = buildMultiContainerBitmap(0, 3, 42);
    FrozenRoaringBitmap frozen = FrozenRoaringBitmap.freeze(source);

    // Forward iterator
    int[] expected = source.toArray();
    int[] actual = frozen.toArray();
    assertArrayEquals(expected, actual);

    // Reverse iterator
    IntIterator revIter = frozen.getReverseIntIterator();
    for (int i = expected.length - 1; i >= 0; i--) {
      assertTrue(revIter.hasNext());
      assertEquals(expected[i], revIter.next());
    }
    assertFalse(revIter.hasNext());
  }

  @Test
  public void nextValuePreviousValue() {
    RoaringBitmap source = RoaringBitmap.bitmapOf(10, 20, 30);
    FrozenRoaringBitmap frozen = FrozenRoaringBitmap.freeze(source);

    assertEquals(source.nextValue(15), frozen.nextValue(15));
    assertEquals(source.previousValue(25), frozen.previousValue(25));
    assertEquals(source.nextAbsentValue(10), frozen.nextAbsentValue(10));
    assertEquals(source.previousAbsentValue(30), frozen.previousAbsentValue(30));
  }

  // -----------------------------------------------------------------------
  // equals / hashCode / clone / toString
  // -----------------------------------------------------------------------

  @Test
  public void equalsAndHashCode() {
    RoaringBitmap source = RoaringBitmap.bitmapOf(1, 2, 3);
    FrozenRoaringBitmap a = FrozenRoaringBitmap.freeze(source);
    FrozenRoaringBitmap b = FrozenRoaringBitmap.freeze(source);

    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void equalsWithMutableBitmap() {
    RoaringBitmap source = RoaringBitmap.bitmapOf(1, 2, 3);
    FrozenRoaringBitmap frozen = FrozenRoaringBitmap.freeze(source);

    assertEquals(frozen, source);
  }

  @Test
  public void cloneProducesEqualCopy() {
    FrozenRoaringBitmap original = FrozenRoaringBitmap.freeze(
        RoaringBitmap.bitmapOf(1, 2, 3, 100000));
    FrozenRoaringBitmap cloned = original.clone();

    assertEquals(original, cloned);
    assertNotSame(original, cloned);
  }

  @Test
  public void toStringWorks() {
    FrozenRoaringBitmap frozen = FrozenRoaringBitmap.freeze(RoaringBitmap.bitmapOf(1, 2, 3));
    assertEquals("{1,2,3}", frozen.toString());
  }

  // -----------------------------------------------------------------------
  // Serialization round-trip
  // -----------------------------------------------------------------------

  @Test
  public void serializeDeserializeRoundTrip() throws IOException {
    RoaringBitmap source = buildMultiContainerBitmap(0, 5, 42);
    FrozenRoaringBitmap frozen = FrozenRoaringBitmap.freeze(source);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    frozen.serialize(new DataOutputStream(baos));

    FrozenRoaringBitmap restored = FrozenRoaringBitmap.readFrom(
        new DataInputStream(new ByteArrayInputStream(baos.toByteArray())));

    assertEquals(frozen, restored);
    assertArrayEquals(frozen.toArray(), restored.toArray());
  }

  // -----------------------------------------------------------------------
  // intersects / andCardinality
  // -----------------------------------------------------------------------

  @Test
  public void intersectsTest() {
    FrozenRoaringBitmap fa = FrozenRoaringBitmap.freeze(RoaringBitmap.bitmapOf(1, 2, 3));
    FrozenRoaringBitmap fb = FrozenRoaringBitmap.freeze(RoaringBitmap.bitmapOf(3, 4, 5));
    FrozenRoaringBitmap fc = FrozenRoaringBitmap.freeze(RoaringBitmap.bitmapOf(4, 5, 6));

    assertTrue(FrozenRoaringBitmap.intersects(fa, fb));
    assertFalse(FrozenRoaringBitmap.intersects(fa, fc));
  }

  @Test
  public void andCardinalityTest() {
    FrozenRoaringBitmap fa = FrozenRoaringBitmap.freeze(RoaringBitmap.bitmapOf(1, 2, 3, 4, 5));
    FrozenRoaringBitmap fb = FrozenRoaringBitmap.freeze(RoaringBitmap.bitmapOf(3, 4, 5, 6, 7));

    assertEquals(3, FrozenRoaringBitmap.andCardinality(fa, fb));
  }

  // -----------------------------------------------------------------------
  // Helpers
  // -----------------------------------------------------------------------

  /**
   * Build a bitmap with values [startKey*65536+0..startKey*65536+count) for each key
   * in range [startKey, endKey).
   */
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

  /**
   * Build a single-container bitmap with values in [start, end).
   */
  private static RoaringBitmap buildBitmap(int start, int end, int step) {
    RoaringBitmap rb = new RoaringBitmap();
    for (int i = start; i < end; i += step) {
      rb.add(i);
    }
    return rb;
  }
}

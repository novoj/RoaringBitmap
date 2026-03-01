/*
 * (c) the authors Licensed under the Apache License, Version 2.0.
 */

package org.roaringbitmap;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Iterator;


/**
 * A truly immutable RoaringBitmap that supports structural sharing of containers.
 *
 * <p>Binary operations (AND, OR, XOR, ANDNOT) produce new {@code FrozenRoaringBitmap} instances
 * that <b>share unchanged container references</b> with the inputs, rather than cloning every
 * container. Only containers where the operation actually produces new data are freshly allocated.
 * This dramatically reduces GC pressure when bitmaps have low key overlap.
 *
 * <p>Because the class exposes no mutation methods, container sharing is safe: no operation
 * can modify a shared container through this class.
 *
 * <p>Typical usage:
 * <pre>{@code
 *   RoaringBitmap mutable = RoaringBitmap.bitmapOf(1, 2, 3, 1000);
 *   FrozenRoaringBitmap a = FrozenRoaringBitmap.freeze(mutable);
 *   FrozenRoaringBitmap b = FrozenRoaringBitmap.freeze(anotherMutable);
 *   FrozenRoaringBitmap result = FrozenRoaringBitmap.or(a, b);  // shares unchanged containers
 * }</pre>
 */
public final class FrozenRoaringBitmap
    implements Cloneable, Serializable, Iterable<Integer>,
        Externalizable, ImmutableBitmapDataProvider {

  private static final long serialVersionUID = 7L;

  final RoaringArray highLowContainer;

  /**
   * Private constructor — only created via factory methods.
   */
  private FrozenRoaringBitmap(RoaringArray highLowContainer) {
    this.highLowContainer = highLowContainer;
  }

  /**
   * Required for Externalizable. Do not use directly.
   */
  public FrozenRoaringBitmap() {
    this.highLowContainer = new RoaringArray();
  }

  // -----------------------------------------------------------------------
  // Construction
  // -----------------------------------------------------------------------

  /**
   * Create a frozen bitmap from a mutable one. All containers are cloned to decouple
   * from the mutable source.
   *
   * @param source the mutable bitmap to freeze
   * @return a new frozen bitmap with the same data
   */
  public static FrozenRoaringBitmap freeze(RoaringBitmap source) {
    RoaringArray src = source.highLowContainer;
    RoaringArray dst = new RoaringArray(src.size);
    for (int i = 0; i < src.size(); i++) {
      dst.append(src.getKeyAtIndex(i), src.getContainerAtIndex(i).clone());
    }
    return new FrozenRoaringBitmap(dst);
  }

  /**
   * Convert this frozen bitmap back to a mutable one. All containers are cloned so
   * the mutable copy is fully independent.
   *
   * @return a new mutable RoaringBitmap with the same data
   */
  public RoaringBitmap toMutableBitmap() {
    RoaringArray src = this.highLowContainer;
    RoaringArray dst = new RoaringArray(src.size);
    for (int i = 0; i < src.size(); i++) {
      dst.append(src.getKeyAtIndex(i), src.getContainerAtIndex(i).clone());
    }
    return new RoaringBitmap(dst);
  }

  /**
   * Deserialize a frozen bitmap (used after reading from a stream or buffer).
   *
   * @param source the DataInput stream
   * @return a new frozen bitmap
   * @throws IOException if an I/O error occurs
   */
  public static FrozenRoaringBitmap readFrom(DataInput source) throws IOException {
    RoaringBitmap rb = new RoaringBitmap();
    rb.deserialize(source);
    // We take ownership of the deserialized containers directly (no clone needed)
    return new FrozenRoaringBitmap(rb.highLowContainer);
  }

  /**
   * Deserialize a frozen bitmap from a ByteBuffer.
   *
   * @param buffer the ByteBuffer
   * @return a new frozen bitmap
   * @throws IOException if the data is invalid
   */
  public static FrozenRoaringBitmap readFrom(ByteBuffer buffer) throws IOException {
    RoaringBitmap rb = new RoaringBitmap();
    rb.deserialize(buffer);
    return new FrozenRoaringBitmap(rb.highLowContainer);
  }

  // -----------------------------------------------------------------------
  // Binary operations with structural sharing
  // -----------------------------------------------------------------------

  /**
   * Bitwise OR (union) operation. Non-overlapping containers are shared by reference.
   *
   * @param x1 first bitmap
   * @param x2 other bitmap
   * @return result of the operation
   */
  public static FrozenRoaringBitmap or(final FrozenRoaringBitmap x1,
      final FrozenRoaringBitmap x2) {
    final RoaringArray answer = new RoaringArray();
    int pos1 = 0, pos2 = 0;
    final int length1 = x1.highLowContainer.size(), length2 = x2.highLowContainer.size();

    main:
    if (pos1 < length1 && pos2 < length2) {
      char s1 = x1.highLowContainer.getKeyAtIndex(pos1);
      char s2 = x2.highLowContainer.getKeyAtIndex(pos2);

      while (true) {
        if (s1 == s2) {
          // Overlapping: must compute — new container allocated
          answer.append(s1,
              x1.highLowContainer.getContainerAtIndex(pos1)
                  .or(x2.highLowContainer.getContainerAtIndex(pos2)));
          pos1++;
          pos2++;
          if ((pos1 == length1) || (pos2 == length2)) {
            break main;
          }
          s1 = x1.highLowContainer.getKeyAtIndex(pos1);
          s2 = x2.highLowContainer.getKeyAtIndex(pos2);
        } else if (s1 < s2) {
          // Non-overlapping: SHARE REFERENCE — no clone!
          answer.append(s1, x1.highLowContainer.getContainerAtIndex(pos1));
          pos1++;
          if (pos1 == length1) {
            break main;
          }
          s1 = x1.highLowContainer.getKeyAtIndex(pos1);
        } else {
          answer.append(s2, x2.highLowContainer.getContainerAtIndex(pos2));
          pos2++;
          if (pos2 == length2) {
            break main;
          }
          s2 = x2.highLowContainer.getKeyAtIndex(pos2);
        }
      }
    }
    // Tail: share remaining container references directly
    if (pos1 == length1) {
      answer.append(x2.highLowContainer, pos2, length2);
    } else if (pos2 == length2) {
      answer.append(x1.highLowContainer, pos1, length1);
    }
    return new FrozenRoaringBitmap(answer);
  }

  /**
   * Bitwise AND (intersection) operation. Only overlapping keys produce results,
   * so there is nothing to share — this is structurally identical to the mutable version.
   *
   * @param x1 first bitmap
   * @param x2 other bitmap
   * @return result of the operation
   */
  public static FrozenRoaringBitmap and(final FrozenRoaringBitmap x1,
      final FrozenRoaringBitmap x2) {
    final RoaringArray answer = new RoaringArray();
    final int length1 = x1.highLowContainer.size(), length2 = x2.highLowContainer.size();
    int pos1 = 0, pos2 = 0;

    while (pos1 < length1 && pos2 < length2) {
      final char s1 = x1.highLowContainer.getKeyAtIndex(pos1);
      final char s2 = x2.highLowContainer.getKeyAtIndex(pos2);
      if (s1 == s2) {
        final Container c1 = x1.highLowContainer.getContainerAtIndex(pos1);
        final Container c2 = x2.highLowContainer.getContainerAtIndex(pos2);
        final Container c = c1.and(c2);
        if (!c.isEmpty()) {
          answer.append(s1, c);
        }
        ++pos1;
        ++pos2;
      } else if (s1 < s2) {
        pos1 = x1.highLowContainer.advanceUntil(s2, pos1);
      } else {
        pos2 = x2.highLowContainer.advanceUntil(s1, pos2);
      }
    }
    return new FrozenRoaringBitmap(answer);
  }

  /**
   * Bitwise ANDNOT (difference) operation. Non-overlapping containers from x1
   * are shared by reference.
   *
   * @param x1 first bitmap
   * @param x2 other bitmap
   * @return result of the operation
   */
  public static FrozenRoaringBitmap andNot(final FrozenRoaringBitmap x1,
      final FrozenRoaringBitmap x2) {
    final RoaringArray answer = new RoaringArray();
    int pos1 = 0, pos2 = 0;
    final int length1 = x1.highLowContainer.size(), length2 = x2.highLowContainer.size();

    while (pos1 < length1 && pos2 < length2) {
      final char s1 = x1.highLowContainer.getKeyAtIndex(pos1);
      final char s2 = x2.highLowContainer.getKeyAtIndex(pos2);
      if (s1 == s2) {
        final Container c1 = x1.highLowContainer.getContainerAtIndex(pos1);
        final Container c2 = x2.highLowContainer.getContainerAtIndex(pos2);
        final Container c = c1.andNot(c2);
        if (!c.isEmpty()) {
          answer.append(s1, c);
        }
        ++pos1;
        ++pos2;
      } else if (s1 < s2) {
        // Non-overlapping from x1: SHARE REFERENCE
        final int nextPos1 = x1.highLowContainer.advanceUntil(s2, pos1);
        answer.append(x1.highLowContainer, pos1, nextPos1);
        pos1 = nextPos1;
      } else {
        pos2 = x2.highLowContainer.advanceUntil(s1, pos2);
      }
    }
    if (pos2 == length2) {
      // Tail from x1: share remaining container references
      answer.append(x1.highLowContainer, pos1, length1);
    }
    return new FrozenRoaringBitmap(answer);
  }

  /**
   * Bitwise XOR (symmetric difference) operation. Non-overlapping containers
   * from both inputs are shared by reference.
   *
   * @param x1 first bitmap
   * @param x2 other bitmap
   * @return result of the operation
   */
  public static FrozenRoaringBitmap xor(final FrozenRoaringBitmap x1,
      final FrozenRoaringBitmap x2) {
    final RoaringArray answer = new RoaringArray();
    int pos1 = 0, pos2 = 0;
    final int length1 = x1.highLowContainer.size(), length2 = x2.highLowContainer.size();

    main:
    if (pos1 < length1 && pos2 < length2) {
      char s1 = x1.highLowContainer.getKeyAtIndex(pos1);
      char s2 = x2.highLowContainer.getKeyAtIndex(pos2);

      while (true) {
        if (s1 == s2) {
          final Container c =
              x1.highLowContainer.getContainerAtIndex(pos1)
                  .xor(x2.highLowContainer.getContainerAtIndex(pos2));
          if (!c.isEmpty()) {
            answer.append(s1, c);
          }
          pos1++;
          pos2++;
          if ((pos1 == length1) || (pos2 == length2)) {
            break main;
          }
          s1 = x1.highLowContainer.getKeyAtIndex(pos1);
          s2 = x2.highLowContainer.getKeyAtIndex(pos2);
        } else if (s1 < s2) {
          // Non-overlapping: SHARE REFERENCE
          answer.append(s1, x1.highLowContainer.getContainerAtIndex(pos1));
          pos1++;
          if (pos1 == length1) {
            break main;
          }
          s1 = x1.highLowContainer.getKeyAtIndex(pos1);
        } else {
          answer.append(s2, x2.highLowContainer.getContainerAtIndex(pos2));
          pos2++;
          if (pos2 == length2) {
            break main;
          }
          s2 = x2.highLowContainer.getKeyAtIndex(pos2);
        }
      }
    }
    // Tail: share remaining container references directly
    if (pos1 == length1) {
      answer.append(x2.highLowContainer, pos2, length2);
    } else if (pos2 == length2) {
      answer.append(x1.highLowContainer, pos1, length1);
    }
    return new FrozenRoaringBitmap(answer);
  }

  /**
   * Whether x1 intersects x2, without materializing the intersection.
   *
   * @param x1 first bitmap
   * @param x2 second bitmap
   * @return true if they share at least one value
   */
  public static boolean intersects(final FrozenRoaringBitmap x1,
      final FrozenRoaringBitmap x2) {
    final int length1 = x1.highLowContainer.size(), length2 = x2.highLowContainer.size();
    int pos1 = 0, pos2 = 0;
    while (pos1 < length1 && pos2 < length2) {
      final char s1 = x1.highLowContainer.getKeyAtIndex(pos1);
      final char s2 = x2.highLowContainer.getKeyAtIndex(pos2);
      if (s1 == s2) {
        final Container c1 = x1.highLowContainer.getContainerAtIndex(pos1);
        final Container c2 = x2.highLowContainer.getContainerAtIndex(pos2);
        if (c1.intersects(c2)) {
          return true;
        }
        ++pos1;
        ++pos2;
      } else if (s1 < s2) {
        pos1 = x1.highLowContainer.advanceUntil(s2, pos1);
      } else {
        pos2 = x2.highLowContainer.advanceUntil(s1, pos2);
      }
    }
    return false;
  }

  /**
   * Cardinality of the AND (intersection) of two frozen bitmaps.
   *
   * @param x1 first bitmap
   * @param x2 second bitmap
   * @return the cardinality of the intersection
   */
  public static int andCardinality(final FrozenRoaringBitmap x1,
      final FrozenRoaringBitmap x2) {
    int answer = 0;
    final int length1 = x1.highLowContainer.size(), length2 = x2.highLowContainer.size();
    int pos1 = 0, pos2 = 0;
    while (pos1 < length1 && pos2 < length2) {
      final char s1 = x1.highLowContainer.getKeyAtIndex(pos1);
      final char s2 = x2.highLowContainer.getKeyAtIndex(pos2);
      if (s1 == s2) {
        answer += x1.highLowContainer.getContainerAtIndex(pos1)
            .andCardinality(x2.highLowContainer.getContainerAtIndex(pos2));
        ++pos1;
        ++pos2;
      } else if (s1 < s2) {
        pos1 = x1.highLowContainer.advanceUntil(s2, pos1);
      } else {
        pos2 = x2.highLowContainer.advanceUntil(s1, pos2);
      }
    }
    return answer;
  }

  // -----------------------------------------------------------------------
  // ImmutableBitmapDataProvider implementation (read-only API)
  // -----------------------------------------------------------------------

  @Override
  public boolean contains(int x) {
    final char hb = Util.highbits(x);
    int index = highLowContainer.getContainerIndex(hb);
    if (index < 0) {
      return false;
    }
    return highLowContainer.getContainerAtIndex(index).contains(Util.lowbits(x));
  }

  @Override
  public int getCardinality() {
    return (int) getLongCardinality();
  }

  @Override
  public long getLongCardinality() {
    long size = 0;
    for (int i = 0; i < highLowContainer.size(); i++) {
      size += highLowContainer.getContainerAtIndex(i).getCardinality();
    }
    return size;
  }

  @Override
  public void forEach(IntConsumer ic) {
    for (int i = 0; i < highLowContainer.size(); i++) {
      highLowContainer.getContainerAtIndex(i).forEach(highLowContainer.keys[i], ic);
    }
  }

  @Override
  public PeekableIntIterator getIntIterator() {
    return new FrozenIntIterator();
  }

  @Override
  public PeekableIntIterator getSignedIntIterator() {
    return new FrozenSignedIntIterator();
  }

  @Override
  public IntIterator getReverseIntIterator() {
    return new FrozenReverseIntIterator();
  }

  @Override
  public RoaringBatchIterator getBatchIterator() {
    return new RoaringBatchIterator(highLowContainer);
  }

  @Override
  public long getLongSizeInBytes() {
    long size = 8;
    for (int i = 0; i < highLowContainer.size(); i++) {
      size += 2 + highLowContainer.getContainerAtIndex(i).getSizeInBytes();
    }
    return size;
  }

  @Override
  public int getSizeInBytes() {
    return (int) getLongSizeInBytes();
  }

  @Override
  public boolean isEmpty() {
    return highLowContainer.size() == 0;
  }

  @Override
  public FrozenRoaringBitmap limit(int maxcardinality) {
    RoaringArray answer = new RoaringArray();
    int currentcardinality = 0;
    for (int i = 0;
        (currentcardinality < maxcardinality) && (i < highLowContainer.size());
        i++) {
      Container c = highLowContainer.getContainerAtIndex(i);
      if (c.getCardinality() + currentcardinality <= maxcardinality) {
        // Share reference — frozen bitmap, so this is safe
        answer.append(highLowContainer.getKeyAtIndex(i), c);
        currentcardinality += c.getCardinality();
      } else {
        int leftover = maxcardinality - currentcardinality;
        Container limited = c.limit(leftover);
        answer.append(highLowContainer.getKeyAtIndex(i), limited);
        break;
      }
    }
    return new FrozenRoaringBitmap(answer);
  }

  @Override
  public int rank(int x) {
    return (int) rankLong(x);
  }

  @Override
  public long rankLong(int x) {
    long size = 0;
    char xhigh = Util.highbits(x);
    for (int i = 0; i < highLowContainer.size(); i++) {
      char key = highLowContainer.getKeyAtIndex(i);
      if (key < xhigh) {
        size += highLowContainer.getContainerAtIndex(i).getCardinality();
      } else if (key == xhigh) {
        return size + highLowContainer.getContainerAtIndex(i).rank(Util.lowbits(x));
      } else {
        break;
      }
    }
    return size;
  }

  @Override
  public long rangeCardinality(long start, long end) {
    if (Long.compareUnsigned(start, end) >= 0) {
      return 0;
    }
    long size = 0;
    int startIndex = highLowContainer.getIndex(Util.highbits(start));
    if (startIndex < 0) {
      startIndex = -startIndex - 1;
    } else {
      int inContainerStart = (Util.lowbits(start));
      if (inContainerStart != 0) {
        size -= highLowContainer.getContainerAtIndex(startIndex)
            .rank((char) (inContainerStart - 1));
      }
    }
    char xhigh = Util.highbits(end - 1);
    for (int i = startIndex; i < highLowContainer.size(); i++) {
      char key = highLowContainer.getKeyAtIndex(i);
      if (key < xhigh) {
        size += highLowContainer.getContainerAtIndex(i).getCardinality();
      } else if (key == xhigh) {
        return size
            + highLowContainer.getContainerAtIndex(i).rank(Util.lowbits((int) (end - 1)));
      } else {
        break;
      }
    }
    return size;
  }

  @Override
  public int select(int j) {
    long leftover = Util.toUnsignedLong(j);
    for (int i = 0; i < highLowContainer.size(); i++) {
      Container c = highLowContainer.getContainerAtIndex(i);
      int thiscard = c.getCardinality();
      if (thiscard > leftover) {
        int keycontrib = highLowContainer.getKeyAtIndex(i) << 16;
        int lowcontrib = c.select((int) leftover);
        return lowcontrib + keycontrib;
      }
      leftover -= thiscard;
    }
    throw new IllegalArgumentException(
        "select " + j + " when cardinality is " + getCardinality());
  }

  @Override
  public int first() {
    return highLowContainer.first();
  }

  @Override
  public int last() {
    return highLowContainer.last();
  }

  @Override
  public int firstSigned() {
    return highLowContainer.firstSigned();
  }

  @Override
  public int lastSigned() {
    return highLowContainer.lastSigned();
  }

  @Override
  public long nextValue(int fromValue) {
    char key = Util.highbits(fromValue);
    int containerIndex = highLowContainer.advanceUntil(key, -1);
    long nextSetBit = -1L;
    while (containerIndex < highLowContainer.size() && nextSetBit == -1L) {
      char containerKey = highLowContainer.getKeyAtIndex(containerIndex);
      Container container = highLowContainer.getContainerAtIndex(containerIndex);
      int bit =
          (containerKey - key > 0
              ? container.first()
              : container.nextValue(Util.lowbits(fromValue)));
      nextSetBit = bit == -1 ? -1L : Util.toUnsignedLong((containerKey << 16) | bit);
      ++containerIndex;
    }
    return nextSetBit;
  }

  @Override
  public long previousValue(int fromValue) {
    if (isEmpty()) {
      return -1L;
    }
    char key = Util.highbits(fromValue);
    int containerIndex = highLowContainer.advanceUntil(key, -1);
    if (containerIndex == highLowContainer.size()) {
      return Util.toUnsignedLong(last());
    }
    if (highLowContainer.getKeyAtIndex(containerIndex) > key) {
      --containerIndex;
    }
    long prevSetBit = -1L;
    while (containerIndex != -1 && prevSetBit == -1L) {
      char containerKey = highLowContainer.getKeyAtIndex(containerIndex);
      Container container = highLowContainer.getContainerAtIndex(containerIndex);
      int bit =
          (containerKey < key
              ? container.last()
              : container.previousValue(Util.lowbits(fromValue)));
      prevSetBit = bit == -1 ? -1L : Util.toUnsignedLong((containerKey << 16) | bit);
      --containerIndex;
    }
    return prevSetBit;
  }

  @Override
  public long nextAbsentValue(int fromValue) {
    long nextAbsentBit = computeNextAbsentValue(fromValue);
    if (nextAbsentBit == 0x100000000L) {
      return -1L;
    }
    return nextAbsentBit;
  }

  private long computeNextAbsentValue(int fromValue) {
    char key = Util.highbits(fromValue);
    int containerIndex = highLowContainer.advanceUntil(key, -1);
    int size = highLowContainer.size();
    if (containerIndex == size) {
      return Util.toUnsignedLong(fromValue);
    }
    char containerKey = highLowContainer.getKeyAtIndex(containerIndex);
    if (fromValue < containerKey << 16) {
      return Util.toUnsignedLong(fromValue);
    }
    Container container = highLowContainer.getContainerAtIndex(containerIndex);
    int bit = container.nextAbsentValue(Util.lowbits(fromValue));
    while (true) {
      if (bit != 1 << 16) {
        return Util.toUnsignedLong((containerKey << 16) | bit);
      }
      if (containerIndex == size - 1) {
        return Util.toUnsignedLong(highLowContainer.last()) + 1;
      }
      containerIndex += 1;
      char nextContainerKey = highLowContainer.getKeyAtIndex(containerIndex);
      if (containerKey + 1 < nextContainerKey) {
        return Util.toUnsignedLong((containerKey + 1) << 16);
      }
      containerKey = nextContainerKey;
      container = highLowContainer.getContainerAtIndex(containerIndex);
      bit = container.nextAbsentValue((char) 0);
    }
  }

  @Override
  public long previousAbsentValue(int fromValue) {
    long prevAbsentBit = computePreviousAbsentValue(fromValue);
    return prevAbsentBit;
  }

  private long computePreviousAbsentValue(int fromValue) {
    char key = Util.highbits(fromValue);
    int containerIndex = highLowContainer.advanceUntil(key, -1);
    if (containerIndex == highLowContainer.size()) {
      return Util.toUnsignedLong(fromValue);
    }
    char containerKey = highLowContainer.getKeyAtIndex(containerIndex);
    if (fromValue < containerKey << 16) {
      return Util.toUnsignedLong(fromValue);
    }
    Container container = highLowContainer.getContainerAtIndex(containerIndex);
    int bit = container.previousAbsentValue(Util.lowbits(fromValue));
    while (true) {
      if (bit != -1) {
        return Util.toUnsignedLong((containerKey << 16) | bit);
      }
      if (containerIndex == 0) {
        return Util.toUnsignedLong(highLowContainer.first()) - 1;
      }
      containerIndex -= 1;
      char nextContainerKey = highLowContainer.getKeyAtIndex(containerIndex);
      if (nextContainerKey < containerKey - 1) {
        return Util.toUnsignedLong((containerKey << 16)) - 1;
      }
      containerKey = nextContainerKey;
      container = highLowContainer.getContainerAtIndex(containerIndex);
      bit = container.previousAbsentValue((char) ((1 << 16) - 1));
    }
  }

  @Override
  public void serialize(DataOutput out) throws IOException {
    highLowContainer.serialize(out);
  }

  @Override
  public void serialize(ByteBuffer buffer) {
    highLowContainer.serialize(buffer);
  }

  @Override
  public int serializedSizeInBytes() {
    return highLowContainer.serializedSizeInBytes();
  }

  @Override
  public int[] toArray() {
    final int[] array = new int[getCardinality()];
    int pos = 0, pos2 = 0;
    while (pos < highLowContainer.size()) {
      final int hs = highLowContainer.getKeyAtIndex(pos) << 16;
      Container c = highLowContainer.getContainerAtIndex(pos++);
      c.fillLeastSignificant16bits(array, pos2, hs);
      pos2 += c.getCardinality();
    }
    return array;
  }

  @Override
  public int getContainerCount() {
    return highLowContainer.size();
  }

  // -----------------------------------------------------------------------
  // Iterable<Integer>
  // -----------------------------------------------------------------------

  @Override
  public Iterator<Integer> iterator() {
    return new Iterator<Integer>() {
      private int pos = 0;
      private int hs = 0;
      private PeekableCharIterator iter;

      {
        nextContainer();
      }

      private void nextContainer() {
        if (pos < highLowContainer.size()) {
          iter = highLowContainer.getContainerAtIndex(pos).getCharIterator();
          hs = highLowContainer.getKeyAtIndex(pos) << 16;
        }
      }

      @Override
      public boolean hasNext() {
        return pos < highLowContainer.size();
      }

      @Override
      public Integer next() {
        final int x = iter.nextAsInt() | hs;
        if (!iter.hasNext()) {
          ++pos;
          nextContainer();
        }
        return x;
      }
    };
  }

  // -----------------------------------------------------------------------
  // Cloneable
  // -----------------------------------------------------------------------

  @Override
  public FrozenRoaringBitmap clone() {
    // Shallow clone is safe: containers are never mutated through FrozenRoaringBitmap
    try {
      return new FrozenRoaringBitmap(highLowContainer.clone());
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException("clone not supported", e);
    }
  }

  // -----------------------------------------------------------------------
  // equals / hashCode / toString
  // -----------------------------------------------------------------------

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof FrozenRoaringBitmap) {
      return highLowContainer.equals(((FrozenRoaringBitmap) o).highLowContainer);
    }
    if (o instanceof RoaringBitmap) {
      return highLowContainer.equals(((RoaringBitmap) o).highLowContainer);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return highLowContainer.hashCode();
  }

  @Override
  public String toString() {
    final StringBuilder answer = new StringBuilder("{");
    final IntIterator i = getIntIterator();
    boolean first = true;
    while (i.hasNext()) {
      if (!first) {
        answer.append(",");
      }
      first = false;
      answer.append(i.next());
      if (answer.length() > 0x80000) {
        answer.append("...");
        break;
      }
    }
    answer.append("}");
    return answer.toString();
  }

  // -----------------------------------------------------------------------
  // Externalizable
  // -----------------------------------------------------------------------

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    highLowContainer.writeExternal(out);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException {
    highLowContainer.readExternal(in);
  }

  // -----------------------------------------------------------------------
  // Package-private access for tests
  // -----------------------------------------------------------------------

  /**
   * Returns the container at the given index. Package-private for testing structural sharing.
   */
  Container getContainerAtIndex(int i) {
    return highLowContainer.getContainerAtIndex(i);
  }

  /**
   * Returns the key at the given index. Package-private for testing.
   */
  char getKeyAtIndex(int i) {
    return highLowContainer.getKeyAtIndex(i);
  }

  // -----------------------------------------------------------------------
  // Inner iterator classes
  // -----------------------------------------------------------------------

  private class FrozenIntIterator implements PeekableIntIterator {
    private final char startingContainerIndex;
    int hs = 0;
    PeekableCharIterator iter;
    int pos = 0;

    FrozenIntIterator() {
      this.startingContainerIndex = findStartingContainerIndex();
      nextContainer();
    }

    char findStartingContainerIndex() {
      return 0;
    }

    @Override
    public PeekableIntIterator clone() {
      try {
        FrozenIntIterator x = (FrozenIntIterator) super.clone();
        if (this.iter != null) {
          x.iter = this.iter.clone();
        }
        return x;
      } catch (CloneNotSupportedException e) {
        return null;
      }
    }

    @Override
    public boolean hasNext() {
      return pos < highLowContainer.size();
    }

    @Override
    public int next() {
      final int x = iter.nextAsInt() | hs;
      if (!iter.hasNext()) {
        ++pos;
        nextContainer();
      }
      return x;
    }

    private void nextContainer() {
      final int containerSize = highLowContainer.size();
      if (pos < containerSize) {
        final int index = (pos + startingContainerIndex) % containerSize;
        iter = highLowContainer.getContainerAtIndex(index).getCharIterator();
        hs = highLowContainer.getKeyAtIndex(index) << 16;
      }
    }

    @Override
    public void advanceIfNeeded(int minval) {
      while (hasNext() && shouldAdvanceContainer(hs, minval)) {
        ++pos;
        nextContainer();
      }
      if (hasNext() && ((hs >>> 16) == (minval >>> 16))) {
        iter.advanceIfNeeded(Util.lowbits(minval));
        if (!iter.hasNext()) {
          ++pos;
          nextContainer();
        }
      }
    }

    boolean shouldAdvanceContainer(final int hs, final int minval) {
      return (hs >>> 16) < (minval >>> 16);
    }

    @Override
    public int peekNext() {
      return (iter.peekNext()) | hs;
    }
  }

  private final class FrozenSignedIntIterator extends FrozenIntIterator {
    @Override
    char findStartingContainerIndex() {
      char index =
          (char) FrozenRoaringBitmap.this.highLowContainer.advanceUntil((char) (1 << 15), -1);
      if (index == FrozenRoaringBitmap.this.highLowContainer.size()) {
        index = 0;
      }
      return index;
    }

    @Override
    boolean shouldAdvanceContainer(final int hs, final int minval) {
      return (hs >> 16) < (minval >> 16);
    }
  }

  private final class FrozenReverseIntIterator implements IntIterator {
    int hs = 0;
    CharIterator iter;
    int pos = highLowContainer.size() - 1;

    FrozenReverseIntIterator() {
      nextContainer();
    }

    @Override
    public IntIterator clone() {
      try {
        FrozenReverseIntIterator clone = (FrozenReverseIntIterator) super.clone();
        if (this.iter != null) {
          clone.iter = this.iter.clone();
        }
        return clone;
      } catch (CloneNotSupportedException e) {
        return null;
      }
    }

    @Override
    public boolean hasNext() {
      return pos >= 0;
    }

    @Override
    public int next() {
      final int x = iter.nextAsInt() | hs;
      if (!iter.hasNext()) {
        --pos;
        nextContainer();
      }
      return x;
    }

    private void nextContainer() {
      if (pos >= 0) {
        iter = highLowContainer.getContainerAtIndex(pos).getReverseCharIterator();
        hs = highLowContainer.getKeyAtIndex(pos) << 16;
      }
    }
  }
}

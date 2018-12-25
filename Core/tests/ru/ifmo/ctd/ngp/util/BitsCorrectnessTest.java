package ru.ifmo.ctd.ngp.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Unit tests for {@link Bits}.
 *
 * @author Maxim Buzdalov
 */
public class BitsCorrectnessTest {
    @Test
    public void testShort() {
        int i = 0;
        int mask = (1 << 16) - 1;
        do {
            Assert.assertEquals(Integer.bitCount(i & mask), Bits.bitCount((short) i));
        } while ((++i & mask) != 0);
    }

    @Test
    public void testInt() {
        int[] masks = { 0xAAAAAAAA, 0x55555555 };
        for (int mask : masks) {
            int i = mask;
            do {
                Assert.assertEquals(Integer.bitCount(i), Bits.bitCount(i));
            } while ((i = (i - 1) & mask) != mask);
        }
        Assert.assertEquals(Integer.bitCount(-1), Bits.bitCount(-1));
    }

    @Test
    public void testLong() {
        long[] masks = { 0xA0A0A0A0A0A0A0A0L, 0x0A0A0A0A0A0A0A0AL, 0x0505050505050505L, 0x5050505050505050L };
        for (long mask : masks) {
            long i = mask;
            do {
                Assert.assertEquals(Long.bitCount(i), Bits.bitCount(i));
            } while ((i = (i - 1) & mask) != mask);
        }
        Assert.assertEquals(Long.bitCount(-1L), Bits.bitCount(-1L));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(expected = IllegalArgumentException.class)
    public void nBitMaskLongThrows1() {
        Bits.nBitMaskLong(-1);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(expected = IllegalArgumentException.class)
    public void nBitMaskLongThrows2() {
        Bits.nBitMaskLong(65);
    }

    @Test
    public void nBitMaskLong() {
        Assert.assertEquals(0, Bits.nBitMaskLong(0));
        Assert.assertEquals(1, Bits.nBitMaskLong(1));
        Assert.assertEquals(3, Bits.nBitMaskLong(2));
        Assert.assertEquals(7, Bits.nBitMaskLong(3));
        Assert.assertEquals(-1L, Bits.nBitMaskLong(64));
        Assert.assertEquals(Long.MAX_VALUE, Bits.nBitMaskLong(63));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(expected = IllegalArgumentException.class)
    public void nBitMaskThrows1() {
        Bits.nBitMask(-1);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(expected = IllegalArgumentException.class)
    public void nBitMaskThrows2() {
        Bits.nBitMask(33);
    }

    @Test
    public void nBitMask() {
        Assert.assertEquals(0, Bits.nBitMask(0));
        Assert.assertEquals(1, Bits.nBitMask(1));
        Assert.assertEquals(3, Bits.nBitMask(2));
        Assert.assertEquals(7, Bits.nBitMask(3));
        Assert.assertEquals(-1, Bits.nBitMask(32));
        Assert.assertEquals(Integer.MAX_VALUE, Bits.nBitMaskLong(31));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(expected = IllegalArgumentException.class)
    public void extractBitsNTooSmall() {
        Bits.extractBits(new long[1], 0, -1);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(expected = IllegalArgumentException.class)
    public void extractBitsNTooLarge() {
        Bits.extractBits(new long[1], 0, 65);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(expected = IllegalArgumentException.class)
    public void extractBitsOffsetTooLarge() {
        Bits.extractBits(new long[1], 64, 1);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(expected = IllegalArgumentException.class)
    public void extractBitsOffsetPlusLengthTooLarge() {
        Bits.extractBits(new long[1], 62, 3);
    }

    @Test
    public void extractBitsStrange1() {
        long[] bits = {-1};
        Assert.assertEquals(-1L, Bits.extractBits(bits, 0, 64));
    }

    @Test
    public void extractBitsStrange2() {
        long[] bits = {-1, -1};
        Assert.assertEquals(-1L, Bits.extractBits(bits, 64, 64));
    }

    @Test
    public void extractBitsRandomTest() {
        Random r = new Random(32454465);
        long[] bits = new long[11];
        StringBuilder zeroOnesBuilder = new StringBuilder();
        for (int i = 0; i < bits.length * 64; ++i) {
            boolean x = r.nextBoolean();
            if (x) {
                bits[i >>> 6] |= 1L << i;
            }
            zeroOnesBuilder.append(x ? '1' : '0');
        }
        String zeroOnes = zeroOnesBuilder.toString();

        for (int off = 0; off < bits.length * 64; ++off) {
            for (int len = 0; len <= 64 && off + len <= bits.length * 64; ++len) {
                long exp = 0;
                for (int i = len - 1; i >= 0; --i) {
                    exp <<= 1;
                    exp += zeroOnes.charAt(i + off) - '0';
                }
                Assert.assertEquals(exp, Bits.extractBits(bits, off, len));
            }
        }
    }

    @Test
    public void copyBitsRandomTest() {
        Random r = new Random(241234);
        final int L = 150;
        long[] src = new long[(L + 63) / 64];
        for (int i = 0; i < L; ++i) {
            if (r.nextInt(4) != 0) {
                src[i >>> 6] |= 1L << i;
            }
        }

        long[] dst = new long[(L + 63) / 64];
        long[] dst2 = new long[(L + 63) / 64];

        Set<Integer> lengths = new HashSet<>();
        for (int x = 1; x <= 128; x <<= 4) {
            lengths.add(x);
        }
        lengths.add(0);
        for (int j = 3; j <= L; j += 35) {
            lengths.add(j);
        }

        for (int length : lengths) {
            for (int srcOffset = 0; srcOffset + length <= L; ++srcOffset) {
                for (int dstOffset = 0; dstOffset + length <= L; ++dstOffset) {
                    long x = r.nextLong();
                    Arrays.fill(dst, x);
                    Arrays.fill(dst2, x);
                    Bits.copyBits(src, srcOffset, dst, dstOffset, length);
                    for (int i = 0; i < length; ++i) {
                        if (((src[(srcOffset + i) >>> 6] >> (srcOffset + i)) & 1) == 1) {
                            dst2[(dstOffset + i) >>> 6] |= 1L << (dstOffset + i);
                        } else {
                            dst2[(dstOffset + i) >>> 6] &= ~(1L << (dstOffset + i));
                        }
                    }
                    Assert.assertArrayEquals(dst2, dst);
                }
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyBitsBoundsTest_NegativeLength() {
        Bits.copyBits(new long[1], 0, new long[1], 0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyBitsBoundsTest_LengthExceededSrc() {
        Bits.copyBits(new long[1], 0, new long[2], 0, 65);
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyBitsBoundsTest_LengthExceededDst() {
        Bits.copyBits(new long[2], 0, new long[1], 0, 65);
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyBitsBoundsTest_OffsetAndLengthExceededSrc() {
        Bits.copyBits(new long[1], 5, new long[2], 5, 60);
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyBitsBoundsTest_OffsetAndLengthExceededDst() {
        Bits.copyBits(new long[2], 5, new long[1], 5, 60);
    }

    @Test
    public void copyToSameArrayForwardDistant() {
        copyToSameArray(0, 100, 35);
    }

    @Test
    public void copyToSameArrayForwardNeighbouringLongs() {
        copyToSameArray(47, 64, 17);
    }

    @Test
    public void copyToSameArrayForwardNonOverlappingSameLong() {
        copyToSameArray(45, 62, 17);
    }

    @Test
    public void copyToSameArrayForwardOverlappingOneLong() {
        copyToSameArray(3, 7, 8);
    }

    @Test
    public void copyToSameArrayForwardOverlappingManyLongs() {
        copyToSameArray(3, 7, 80);
    }

    @Test
    public void copyToSameArrayForwardOverlappingManyLongsAligned() {
        copyToSameArray(0, 64, 128);
    }

    @Test
    public void copyToSameArrayBackwardDistant() {
        copyToSameArray(100, 0, 35);
    }

    @Test
    public void copyToSameArrayBackwardNeighbouringLongs() {
        copyToSameArray(64, 47, 17);
    }

    @Test
    public void copyToSameArrayBackwardNonOverlappingSameLong() {
        copyToSameArray(62, 45, 17);
    }

    @Test
    public void copyToSameArrayBackwardOverlappingOneLong() {
        copyToSameArray(7, 3, 8);
    }

    @Test
    public void copyToSameArrayBackwardOverlappingManyLongs() {
        copyToSameArray(7, 3, 80);
    }

    @Test
    public void copyToSameArrayBackwardOverlappingManyLongsAligned() {
        copyToSameArray(64, 0, 128);
    }

    private static final Random r = new Random(2454235);
    private static void copyToSameArray(int from, int to, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 200; ++i) {
            sb.append(r.nextInt(2));
        }
        String s = sb.toString();
        long[] ar = of(s);

        copy(s, from, sb, to, length);
        Bits.copyBits(ar, from, ar, to, length);
        equal(ar, sb);
    }

    private static void equal(long[] bits, CharSequence sequence) {
        for (int i = 0; i < sequence.length(); ++i) {
            long bit = (bits[i >>> 6] >>> i) & 1;
            Assert.assertEquals(bit, sequence.charAt(i) - '0');
        }
    }

    private static void copy(String src, int srcOffset, StringBuilder dst, int dstOffset, int length) {
        for (int i = 0; i < length; ++i) {
            dst.setCharAt(dstOffset + i, src.charAt(srcOffset + i));
        }
    }

    private static long[] of(String s) {
        long[] rv = new long[(s.length() + 63) >>> 6];
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '1') {
                rv[i >>> 6] |= 1L << i;
            }
        }
        return rv;
    }

    @Test
    public void compareBitRangesSmall() {
        long[] arrayOfZeros = new long[2];
        long[] arrayOfOnes = new long[2];
        int bitLength = arrayOfOnes.length * 64;
        Arrays.fill(arrayOfOnes, -1);

        for (int length = 1; length <= bitLength; length += 4) {
            for (int l = 0; l + length <= bitLength; l += 4) {
                for (int r = 0; r + length <= bitLength; r += 4) {
                    Assert.assertEquals(0, Bits.compareBitRanges(arrayOfOnes, l, arrayOfOnes, r, length));
                    Assert.assertEquals(0, Bits.compareBitRanges(arrayOfZeros, l, arrayOfZeros, r, length));
                    Assert.assertEquals(-1, Bits.compareBitRanges(arrayOfZeros, l, arrayOfOnes, r, length));
                    Assert.assertEquals(1, Bits.compareBitRanges(arrayOfOnes, l, arrayOfZeros, r, length));
                }
            }
        }

        long[] la = {7744, 6};
        long[] ra = {7744, 1};
        Assert.assertEquals(-1, Bits.compareBitRanges(la, 64, ra, 64, 3));
        Assert.assertEquals(1, Bits.compareBitRanges(la, 65, ra, 65, 2));
        Assert.assertEquals(-1, Bits.compareBitRanges(la, 0, ra, 0, 128));
        Assert.assertEquals(0, Bits.compareBitRanges(la, 0, ra, 0, 64));
    }

    @Test
    public void compareBitRangesRandom() {
        long[] left = new long[3];
        long[] right = new long[3];
        Random rnd = new Random(24352);
        long df = rnd.nextLong();
        Arrays.fill(left, df);
        Arrays.fill(right, Long.rotateLeft(df, 17));
        left[2] = rnd.nextLong();
        right[2] = rnd.nextLong();

        int len = left.length * 64;

        for (int length = 0; length <= len; ++length) {
            for (int l = 0; l + length <= len; ++l) {
                for (int r = 0; r + length <= len; ++r) {
                    int sign = 0;
                    for (int i = 0; i < length; ++i) {
                        int atLeft = (int) (left[(l + i) >>> 6] >>> (l + i)) & 1;
                        int atRight = (int) (right[(r + i) >>> 6] >>> (r + i)) & 1;
                        if (atLeft != atRight) {
                            sign = atLeft > atRight ? 1 : -1;
                            break;
                        }
                    }
                    Assert.assertEquals(sign, Bits.compareBitRanges(left, l, right, r, length));
                }
            }
        }
    }

    @Test
    public void countBitsRandomTest() {
        Random r = new Random(32454465);
        long[] bits = new long[3];
        for (int i = 0; i < bits.length; ++i) {
            bits[i] = r.nextLong();
        }

        for (int off = 0; off < bits.length * 64; ++off) {
            for (int len = 0; off + len <= bits.length * 64; ++len) {
                int bc = 0;
                for (int i = 0, j = off; i < len; ++i, ++j) {
                    if ((bits[j >>> 6] & (1L << j)) != 0) {
                        ++bc;
                    }
                }
                Assert.assertEquals(bc, Bits.bitCount(bits, off, len));
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void compareBitsBoundsTest_NegativeLength() {
        Bits.compareBitRanges(new long[1], 0, new long[1], 0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void compareBitsBoundsTest_LengthExceededSrc() {
        Bits.compareBitRanges(new long[1], 0, new long[2], 0, 65);
    }

    @Test(expected = IllegalArgumentException.class)
    public void compareBitsBoundsTest_LengthExceededDst() {
        Bits.compareBitRanges(new long[2], 0, new long[1], 0, 65);
    }

    @Test(expected = IllegalArgumentException.class)
    public void compareBitsBoundsTest_OffsetAndLengthExceededSrc() {
        Bits.compareBitRanges(new long[1], 5, new long[2], 5, 60);
    }

    @Test(expected = IllegalArgumentException.class)
    public void compareBitsBoundsTest_OffsetAndLengthExceededDst() {
        Bits.compareBitRanges(new long[2], 5, new long[1], 5, 60);
    }

    @Test
    public void fillBitsTest() {
        long[] array = new long[10];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 64 * array.length; ++i) {
            sb.append('0');
        }
        Random r = new Random(245346364);
        for (int i = 0; i < 20; ++i) {
            int right = r.nextInt(sb.length() + 1);
            int left = r.nextInt(right + 1);
            boolean set = r.nextBoolean();
            Bits.fillBits(array, left, right - left, set);
            for (int j = left; j < right; ++j) {
                sb.setCharAt(j, set ? '1' : '0');
            }
            equal(array, sb);
        }
    }
}

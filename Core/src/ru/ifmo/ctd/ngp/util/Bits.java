package ru.ifmo.ctd.ngp.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * The utility class for bit operations.
 *
 * @author Maxim Buzdalov
 */
public final class Bits {
    private Bits() {
        Static.doNotCreateInstancesOf(Bits.class);
    }

    private static final int MASK_16_BIT = (1 << 16) - 1;

    /**
     * Returns the number of bits set to 1 in the given argument.
     * As for the current version, just calls {@link Integer#bitCount(int)}.
     * @param a the argument to count set bits in.
     * @return the number of bits set.
     */
    public static int bitCount(short a) {
        return Integer.bitCount(a & MASK_16_BIT);
    }

    /**
     * Returns the number of bits set to 1 in the given argument.
     * As for the current version, just calls {@link Integer#bitCount(int)}.
     * @param a the argument to count set bits in.
     * @return the number of bits set.
     */
    public static int bitCount(int a) {
        return Integer.bitCount(a);
    }

    /**
     * Returns the number of bits set to 1 in the given argument.
     * As for the current version, just calls {@link Long#bitCount(long)}.
     * @param a the argument to count set bits in.
     * @return the number of bits set.
     */
    public static int bitCount(long a) {
        return Long.bitCount(a);
    }

    /**
     * Counts the number of bits set to 1 in a portion of the given array of longs,
     * starting with the given bit offset and having the given bit length.
     * @param array the array of longs representing the bits.
     * @param offset the bit offset of the range of which to count bits.
     * @param nBits the bit length of the range of which to count bits.
     * @return the number of bits set to 1 in the specified range.
     */
    public static int bitCount(long[] array, int offset, int nBits) {
        if (0 > offset || 0 > nBits || offset + nBits > array.length * 64) {
            throw new IllegalArgumentException("bits in array: " + array.length * 64 + ", offset: " +
                    offset + ", nBits: " + nBits);
        }
        if (nBits == 0) {
            return 0;
        }
        int srcS = offset >>> 6;
        int dstS = (offset + nBits - 1) >>> 6;
        if (srcS == dstS) {
            return bitCount(extractBits(array, offset, nBits));
        } else {
            int rv = bitCount(extractBits(array, offset, 64 - (offset & 63)));
            rv += bitCount(extractBits(array, dstS << 6, (offset + nBits) - (dstS << 6)));
            for (int i = srcS + 1; i < dstS; ++i) {
                rv += bitCount(array[i]);
            }
            return rv;
        }
    }

    /**
     * Extracts a number of bits from the specified array, starting at the specified offset,
     * and returns these bits as a long.
     *
     * The returned number contains the bit with the smallest index as a smallest degree bit.
     *
     * The number of bits to extract must not exceed 64.
     *
     * @param array the array to extract bits from.
     * @param offset the index of the first bit to extract.
     * @param nBits the number of bits to extract.
     * @return the extracted bits, packed in a long.
     */
    public static long extractBits(@NotNull long[] array, int offset, int nBits) {
        if (0 > offset || 0 > nBits || nBits > 64 || offset + nBits > array.length * 64) {
            throw new IllegalArgumentException("bits in array: " + array.length * 64 + ", offset: " +
                    offset + ", nBits: " + nBits);
        }
        if (nBits == 0) {
            return 0;
        }
        int lastBit = offset + nBits - 1;
        int smallIndex = offset >>> 6;
        long rv = array[smallIndex] >>> offset;
        int largeIndex = lastBit >>> 6;
        if (largeIndex != smallIndex) {
            //This works because we extract a long from the long array
            //and if lastBit is 63 modulo 64, then this branch can not be executed.
            rv |= (array[largeIndex] & (1L << (lastBit + 1)) - 1) << (64 - offset);
        } else {
            rv &= NBITMASK_LONG[nBits];
        }
        return rv;
    }

    private static final long[] NBITMASK_LONG;
    private static final int[] NBITMASK_INT;
    static {
        NBITMASK_INT = new int[33];
        for (int i = 0; i < 32; ++i) {
            NBITMASK_INT[i] = (1 << i) - 1;
        }
        NBITMASK_INT[32] = -1;

        NBITMASK_LONG = new long[65];
        for (int i = 0; i < 64; ++i) {
            NBITMASK_LONG[i] = (1L << i) - 1;
        }
        NBITMASK_LONG[64] = -1L;
    }

    /**
     * Returns an int-valued bit mask with {@code n} lowest bits set.}
     *
     * If {@code n} is negative or exceeds 32, an {@link IllegalArgumentException} is thrown.
     *
     * @param n the number of bits.
     * @return the bit mask with {@code n} bits set.
     */
    public static int nBitMask(int n) {
        if (n < 0 || n > 32) {
            throw new IllegalArgumentException("Number of bits: " + n);
        }
        return NBITMASK_INT[n];
    }

    /**
     * Returns a long-valued bit mask with {@code n} lowest bits set.}
     *
     * If {@code n} is negative or exceeds 64, an {@link IllegalArgumentException} is thrown.
     *
     * @param n the number of bits.
     * @return the bit mask with {@code n} bits set.
     */
    public static long nBitMaskLong(int n) {
        if (n < 0 || n > 64) {
            throw new IllegalArgumentException("Number of bits: " + n);
        }
        return NBITMASK_LONG[n];
    }

    /**
     * Copies a number of bits from the source array at the source offset
     * to the destination array at the destination offset.
     *
     * @param src the array to copy bits from.
     * @param srcOffset the bit index to start copying from.
     * @param dst the array to copy bits to.
     * @param dstOffset the bit index to start copying to.
     * @param length the number of bits to copy.
     */
    public static void copyBits(@NotNull long[] src, int srcOffset, @NotNull long[] dst, int dstOffset, int length) {
        if (0 > length || srcOffset + length > 64 * src.length || dstOffset + length > 64 * dst.length) {
            throw new IllegalArgumentException(String.format(
                    "src.length in bits = %d, srcOffset = %d, dst.length in bits = %d, dstOffset = %d, length = %d",
                    src.length * 64, srcOffset, dst.length * 64, dstOffset, length));
        }
        if (length > 0) {
            if (src == dst) {
                //In this section, a Q&D workaround is introduced for overlapped regions.
                if (srcOffset < dstOffset) {
                    if (srcOffset + length > dstOffset) {
                        long[] tmp = new long[(length + 63) >>> 6];
                        copyBits(src, srcOffset, tmp, 0, length);
                        copyBits(tmp, 0, dst, dstOffset, length);
                        return;
                    }
                } else if (srcOffset > dstOffset) {
                    if (dstOffset + length > srcOffset) {
                        long[] tmp = new long[(length + 63) >>> 6];
                        copyBits(src, srcOffset, tmp, 0, length);
                        copyBits(tmp, 0, dst, dstOffset, length);
                        return;
                    }
                } else {
                    return;
                }
            }
            int dstS = dstOffset >>> 6;
            int dstF = (dstOffset + length - 1) >>> 6;
            if (dstS == dstF) {
                dst[dstS] &= ~(NBITMASK_LONG[length] << dstOffset);
                dst[dstS] |= extractBits(src, srcOffset, length) << dstOffset;
            } else {
                int lBits = 1 + ((63 - dstOffset) & 63);
                int rBits = 1 + ((dstOffset + length - 1) & 63);

                dst[dstS] = dst[dstS] & ~(NBITMASK_LONG[lBits] << dstOffset) |
                        (extractBits(src, srcOffset, lBits) << dstOffset);

                if (((srcOffset + lBits) & 63) == 0) {
                    System.arraycopy(src, (srcOffset + lBits) >>> 6, dst, dstS + 1, dstF - (dstS + 1));
                } else {
                    int fb = srcOffset + lBits;
                    int pj = fb & 63;
                    int nj = (64 - fb) & 63;
                    long nm = ((1L << fb) - 1);
                    for (int i = dstS + 1, j = (srcOffset + lBits) >>> 6; i < dstF; ++i, ++j) {
                        dst[i] = (src[j] >>> pj) | ((src[j + 1] & nm) << nj);
                    }
                }

                dst[dstF] = dst[dstF] & ~NBITMASK_LONG[rBits] | extractBits(src, srcOffset + length - rBits, rBits);
            }
        }
    }

    /**
     * Compares two bit ranges of the same length lexicographically.
     *
     * In this method, the least significant bits are compared first. That is,
     * <code>compareBitRanges(new long[] {1}, 0, new long[] {2}, 0, 2)</code>
     * returns 1 (the first array is considered to be greater) because the first array
     * has the set bit in the very first position.
     *
     * Returns -1 if the first range is smaller than the second one, 1 if the
     * first range is greater than the second one, 0 if they are equal.
     *
     *
     * @param first the first array of bits packed as longs.
     * @param firstOffset the bit offset in the first array.
     * @param second the second array of bits packed as longs.
     * @param secondOffset the bit offset in the second array.
     * @param length the length of ranges to compare.
     * @return -1 if the first range is smaller, 1 if the first range is greater,
     *          0 if the ranges are equal.
     */
    @SuppressWarnings("HtmlTagCanBeJavadocTag")
    public static int compareBitRanges(@NotNull long[] first, int firstOffset,
                                       @NotNull long[] second, int secondOffset, int length) {
        if (0 > length || firstOffset + length > 64 * first.length || secondOffset + length > 64 * second.length) {
            throw new IllegalArgumentException(String.format(
                    "first.length in bits = %d, firstOffset = %d, " +
                            "second.length in bits = %d, secondOffset = %d, length = %d",
                    first.length * 64, firstOffset, second.length * 64, secondOffset, length));
        }
        if (length == 0 || first == second && firstOffset == secondOffset) {
            return 0;
        }
        int dstS = secondOffset >>> 6;
        int dstF = (secondOffset + length - 1) >>> 6;
        if (dstS == dstF) {
            long s = (second[dstS] >>> secondOffset) & NBITMASK_LONG[length];
            long f = extractBits(first, firstOffset, length);
            return s == f ? 0 : compareUnequalLongsLexicographically(f, s);
        } else {
            int lBits = 1 + ((63 - secondOffset) & 63);
            int rBits = 1 + ((secondOffset + length - 1) & 63);

            long s1 = (second[dstS] >>> secondOffset) & NBITMASK_LONG[lBits];
            long f1 = extractBits(first, firstOffset, lBits);
            if (s1 != f1) {
                return compareUnequalLongsLexicographically(f1, s1);
            }

            if (((firstOffset + lBits) & 63) == 0) {
                for (int f = (secondOffset + lBits) >>> 6, s = dstS + 1; s < dstF; ++f, ++s) {
                    if (first[f] != second[s]) {
                        return compareUnequalLongsLexicographically(first[f], second[s]);
                    }
                }
            } else {
                int fb = firstOffset + lBits;
                int pj = fb & 63;
                int nj = (64 - fb) & 63;
                long nm = ((1L << fb) - 1);
                for (int i = dstS + 1, j = (firstOffset + lBits) >>> 6; i < dstF; ++i, ++j) {
                    long f = (first[j] >>> pj) | ((first[j + 1] & nm) << nj);
                    if (second[i] != f) {
                        return compareUnequalLongsLexicographically(f, second[i]);
                    }
                }
            }

            long sL = second[dstF] & NBITMASK_LONG[rBits];
            long fL = extractBits(first, firstOffset + length - rBits, rBits);
            return sL == fL ? 0 : compareUnequalLongsLexicographically(fL, sL);
        }
    }

    private static int compareUnequalLongsLexicographically(long l, long r) {
        return (l & Long.lowestOneBit(l ^ r)) == 0 ? -1 : 1;
    }

    /**
     * Computes hash code of the given bit range.
     * @param array the array of longs containing the bits.
     * @param offset the bit offset of the beginning of the range.
     * @param length the bit length of the range.
     * @return the hash code.
     */
    public static int hashCode(@NotNull long[] array, int offset, int length) {
        int fromIdx = offset >>> 6;
        int toIdx = (offset + length - 1) >>> 6;
        int hashCode = 43;
        if (fromIdx == toIdx) {
            long extracted = (array[fromIdx] >>> offset) & Bits.nBitMaskLong(length);
            hashCode = 31 * hashCode + (int) (extracted ^ (extracted >>> 32));
        } else {
            long firstLong = array[fromIdx] >>> offset;
            hashCode = 31 * hashCode + (int) (firstLong ^ (firstLong >>> 32));
            for (int i = fromIdx + 1; i < toIdx; ++i) {
                long v = array[i];
                hashCode = 31 * hashCode + (int) (v ^ (v >>> 32));
            }
            long lastLong = array[toIdx] & Bits.nBitMaskLong(1 + (63 & (offset + length - 1)));
            hashCode = 31 * hashCode + (int) (lastLong ^ (lastLong >>> 32));
        }

        //To overcome the effect of x.hashCode() == (x + "0").hashCode()
        hashCode = 31 * hashCode + length;

        return hashCode;
    }
    
    /**
     * Fills the given bit range with the specified bit value.
     *
     * @param array the bit array packed in longs.
     * @param offset the first bit to fill.
     * @param length the number of bits to fill.
     * @param value {@code true} if the bits are to be set,
     * {@code false} if to be cleared.
     */
    public static void fillBits(@NotNull long[] array, int offset, int length, boolean value) {
        if (0 > length || offset + length > 64 * array.length) {
            throw new IllegalArgumentException(String.format(
                    "array.length in bits = %d, offset = %d, length = %d",
                    array.length * 64, offset, length)
            );
        }
        if (length > 0) {
            int dstS = offset >>> 6;
            int dstF = (offset + length - 1) >>> 6;
            if (dstS == dstF) {
                if (value) {
                    array[dstS] |= (NBITMASK_LONG[length] << offset);
                } else {
                    array[dstS] &= ~(NBITMASK_LONG[length] << offset);
                }
            } else {
                int lBits = 1 + ((63 - offset) & 63);
                int rBits = 1 + ((offset + length - 1) & 63);

                if (value) {
                    array[dstS] |= (NBITMASK_LONG[lBits] << offset);
                    Arrays.fill(array, dstS + 1, dstF, -1L);
                    array[dstF] |= NBITMASK_LONG[rBits];
                } else {
                    array[dstS] &= ~(NBITMASK_LONG[lBits] << offset);
                    Arrays.fill(array, dstS + 1, dstF, 0);
                    array[dstF] &= ~NBITMASK_LONG[rBits];
                }
            }
        }
    }
}

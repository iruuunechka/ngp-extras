package ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.BitSequence;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.GCharSequence;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.GStringBuilderX;
import ru.ifmo.ctd.ngp.util.Bits;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;

/**
 * A bit string builder for strings with additional operations for bulk access to bits.
 *
 * Is dual to {@link BitString}.
 *
 * @author Maxim Buzdalov
 */
public final class BitStringBuilder implements GStringBuilderX<Boolean, BitStringBuilder, BitString>, BitSequence {
    @NotNull
    private static final long[] EMPTY_ARRAY = new long[0];
    private static final long serialVersionUID = -7794748208295238098L;

    @NotNull
    private long[] value = EMPTY_ARRAY;
    private int length = 0;

    public BitStringBuilder() {}

    public BitStringBuilder(@NotNull boolean... bits) {
        length = bits.length;
        value = new long[(length + 63) >>> 6];
        for (int i = 0; i < length; ++i) {
            if (bits[i]) {
                value[i >>> 6] |= 1L << i;
            }
        }
    }

    @SuppressWarnings("CopyConstructorMissesField") // copy constructors here go the other way :)
    public BitStringBuilder(@NotNull BitStringBuilder builder) {
        append(builder);
    }

    public BitStringBuilder(@NotNull BitString string) {
        append(string);
    }

    public BitStringBuilder(@NotNull Iterable<Boolean> iterable) {
        if (iterable instanceof Collection) {
            ensureCapacity(((Collection<Boolean>) (iterable)).size());
        }
        for (boolean b : iterable) {
            appendBit(b);
        }
    }

    @NotNull
    @Override
    public BitStringBuilder setCharAt(int index, @NotNull Boolean newValue) throws IllegalArgumentException {
        return setBitAt(index, newValue);
    }

    @NotNull
    public BitStringBuilder setBitAt(int index, boolean bit) throws IllegalArgumentException {
        checkIndex(index);
        long mask = 1L << index;
        if (bit) {
            value[index >>> 6] |= mask;
        } else {
            value[index >>> 6] &= ~mask;
        }
        return this;
    }

    @NotNull
    @Override
    public BitStringBuilder append(@NotNull Boolean character) {
        return appendBit(character);
    }

    @NotNull
    public BitStringBuilder appendBit(boolean bit) {
        ensureCapacity(length + 1);
        if (bit) {
            value[length >>> 6] |= 1L << length;
        } else {
            value[length >>> 6] &= ~(1L << length);
        }
        ++length;
        return this;
    }

    @NotNull
    public BitStringBuilder appendBits(long v, int nBits) {
        checkNBits(nBits);
        if (nBits > 0) {
            ensureCapacity(length + nBits);

            int dstS = length >>> 6;
            int dstF = (length + nBits - 1) >>> 6;
            if (dstS == dstF) {
                long bm = Bits.nBitMaskLong(nBits);
                value[dstS] &= ~(bm << length);
                value[dstS] |= (v & bm) << length;
            } else {
                int lBits = 1 + ((63 - length) & 63);
                int rBits = 1 + ((length + nBits - 1) & 63);

                long lBm = Bits.nBitMaskLong(lBits);
                long rBm = Bits.nBitMaskLong(rBits);
                value[dstS] = value[dstS] & ~(lBm << length) | ((v & lBm) << length);
                value[dstF] = value[dstF] & ~rBm | (v >>> (nBits - rBits)) & rBm;
            }

            length += nBits;
        }
        return this;
    }

    @NotNull
    @Override
    public BitStringBuilder append(@NotNull GCharSequence<? extends Boolean> sequence) {
        if (sequence instanceof BitString) {
            BitString string = (BitString) (sequence);
            ensureCapacity(length + string.length);
            Bits.copyBits(string.value, string.offset, value, length, string.length);
            length += string.length;
        } else if (sequence instanceof BitStringBuilder) {
            BitStringBuilder builder = (BitStringBuilder) (sequence);
            ensureCapacity(length + builder.length);
            Bits.copyBits(builder.value, 0, value, length, builder.length);
            length += builder.length;
        } else {
            for (int i = 0, j = sequence.length(); i < j; ++i) {
                appendBit(sequence.charAt(i));
            }
        }
        return this;
    }

    @NotNull
    @Override
    public BitStringBuilder decreaseLength(int newLength) throws IllegalArgumentException {
        if (0 > newLength || newLength > length()) {
            throw new IllegalArgumentException("length = " + length() + ", newLength = " + newLength);
        }
        length = newLength;
        return this;
    }

    @NotNull
    @Override
    public BitStringBuilder setLength(int newLength, @NotNull Boolean fillCharacter) throws IllegalArgumentException {
        if (newLength <= length()) {
            return decreaseLength(newLength);
        } else {
            ensureCapacity(newLength);
            Bits.fillBits(value, length, newLength - length, fillCharacter);
            length = newLength;
            return this;
        }
    }

    @NotNull
    @Override
    public BitString toGString() {
        if (isEmpty()) {
            return BitString.empty();
        }
        return new BitString(value.clone(), 0, length);
    }

    @Override
    public boolean isEmpty() {
        return length() == 0;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public void trimToSize() {
        value = Arrays.copyOf(value, (length + 63) >>> 6);
    }

    @Override
    public long bitsAt(int offset, int nBits) throws IllegalArgumentException {
        checkNBits(nBits);
        if (0 > offset || offset + nBits > length()) {
            throw new IllegalArgumentException("offset = " + offset + ", nBits = " + nBits + ", length = " + length());
        }
        return Bits.extractBits(value, offset, nBits);
    }

    @Override
    public int bitCount(int offset, int nBits) throws IllegalArgumentException {
        if (0 > offset || offset + nBits > length()) {
            throw new IllegalArgumentException("offset = " + offset + ", nBits = " + nBits + ", length = " + length());
        }
        return Bits.bitCount(value, offset, nBits);
    }

    @NotNull
    @Override
    public Boolean charAt(int index) throws IllegalArgumentException {
        return bitAt(index);
    }

    /**
     * Flips the bit at the specified index.
     * @param index the index of the bit to flip.
     * @return itself.
     */
    @NotNull
    public BitStringBuilder flipBitAt(int index) {
        checkIndex(index);
        value[index >>> 6] ^= 1L << index;
        return this;
    }

    @Override
    public boolean bitAt(int index) {
        checkIndex(index);
        return ((value[index >>> 6] >>> index) & 1) == 1;
    }

    @NotNull
    public static BitStringBuilder of(@NotNull boolean... bits) {
        return new BitStringBuilder(bits);
    }

    @NotNull
    public static BitStringBuilder empty() {
        return new BitStringBuilder();
    }

    @NotNull
    public static BitStringBuilder of(@NotNull Iterable<Boolean> iterable) {
        return new BitStringBuilder(iterable);
    }

    @NotNull
    public static BitStringBuilder of(@NotNull BitString string) {
        return new BitStringBuilder(string);
    }

    private void ensureCapacity(int capacity) {
        if ((value.length << 6) < capacity) {
            int newCapacity = Math.max(2 * value.length, (capacity + 63) >>> 6);
            value = Arrays.copyOf(value, newCapacity);
        }
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= length()) {
            throw new IllegalArgumentException("index = " + index + ", length = " + length());
        }
    }

    private void checkNBits(int nBits) {
        if (0 > nBits || nBits > 64) {
            throw new IllegalArgumentException("illegal nBits " + nBits + ", must be in [0; 64]");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BitStringBuilder that = (BitStringBuilder) o;

        return length == that.length && Bits.compareBitRanges(value, 0, that.value, 0, length) == 0;
    }

    @Override
    public int hashCode() {
        return Bits.hashCode(value, 0, length);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        int length = length();
        stream.writeInt(length);
        int offset = 0;
        while (length >= 64) {
            stream.writeLong(bitsAt(offset, 64));
            offset += 64;
            length -= 64;
        }
        stream.writeLong(bitsAt(offset, length));
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.length = stream.readInt();
        this.value = new long[(length + 64) >>> 6];
        for (int i = 0; i < value.length; ++i) {
            value[i] = stream.readLong();
        }
    }
}

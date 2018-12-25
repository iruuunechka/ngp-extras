package ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.BitSequence;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.GStringX;
import ru.ifmo.ctd.ngp.util.Bits;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * A bit string with additional operations for bulk access to bits.
 *
 * Is dual to {@link BitStringBuilder}.
 *
 * @author Maxim Buzdalov
 */
public final class BitString implements GStringX<Boolean, BitString, BitStringBuilder>,
        BitSequence, Comparable<BitString> {
    @NotNull
    private static final BitString EMPTY_STRING = new BitString(new long[0], 0, 0);
    private static final long serialVersionUID = 4798445894312535330L;

    @NotNull
    protected transient long[] value;
    protected transient int offset;
    protected transient int length;
    private transient int hashCode = 0;

    @Nullable
    private transient List<Boolean> view = null;

    protected BitString(@NotNull long[] value, int offset, int length) {
        this.value = value;
        this.offset = offset;
        this.length = length;
    }

    protected BitString(boolean... bits) {
        length = bits.length;
        value = new long[(length + 63) >>> 6];
        offset = 0;
        for (int i = 0; i < length; ++i) {
            if (bits[i]) {
                value[i >>> 6] |= 1L << i;
            }
        }
    }

    protected BitString(Iterable<Boolean> iterable) {
        Collection<Boolean> collection;
        if (iterable instanceof Collection) {
            collection = (Collection<Boolean>) (iterable);
        } else {
            collection = new ArrayList<>();
            for (Boolean b : iterable) {
                collection.add(b);
            }
        }
        length = collection.size();
        value = new long[(length + 63) >>> 6];
        offset = 0;
        Iterator<Boolean> it = collection.iterator();
        for (int i = 0; i < length; ++i) {
            if (it.next()) {
                value[i >>> 6] |= 1L << i;
            }
        }
    }

    @NotNull
    @Override
    public List<Boolean> asList() {
        if (view == null) {
            List<Boolean> rv = new AbstractList<Boolean>() {
                @Override
                public Boolean get(int index) {
                    if (index < 0 || index >= length) {
                        throw new IndexOutOfBoundsException("Index " + index + ", size " + length());
                    }
                    index += offset;
                    return ((value[index >>> 6] >>> index) & 1) == 1;
                }

                @Override
                public int size() {
                    return length;
                }
            };
            view = rv;
            return rv;
        }
        return view;
    }

    @NotNull
    @Override
    public BitString substring(int beginIndex) throws IllegalArgumentException {
        return substring(beginIndex, length);
    }

    @NotNull
    @Override
    public BitString substring(int beginIndex, int endIndex) throws IllegalArgumentException {
        if (0 > beginIndex || beginIndex > endIndex || endIndex > length) {
            throw new IllegalArgumentException("beginIndex = " + beginIndex +
                    ", endIndex = " + endIndex + ", length = " + length);
        }
        if (beginIndex == 0 && endIndex == length) {
            return this;
        }
        if (beginIndex == endIndex) {
            return emptyString();
        }
        return new BitString(value, offset + beginIndex, endIndex - beginIndex);
    }

    @NotNull
    @Override
    public BitStringBuilder toGStringBuilder() {
        return new BitStringBuilder(this);
    }

    @NotNull
    @Override
    public BitString emptyString() {
        return EMPTY_STRING;
    }

    @Override
    public boolean isEmpty() {
        return length() == 0;
    }

    @Override
    public int length() {
        return length;
    }

    @NotNull
    @Override
    public Boolean charAt(int index) throws IllegalArgumentException {
        return bitAt(index);
    }

    @Override
    public boolean bitAt(int index) throws IllegalArgumentException {
        if (0 > index || index >= length) {
            throw new IllegalArgumentException("index = " + index + ", length = " + length);
        }
        index += offset;
        return ((value[index >>> 6] >>> index) & 1) == 1;
    }

    @Override
    public long bitsAt(int offset, int nBits) throws IllegalArgumentException {
        if (0 > nBits || nBits > 64) {
            throw new IllegalArgumentException("illegal nBits " + nBits + ", must be in [0; 64]");
        }
        if (0 > offset || offset + nBits > length) {
            throw new IllegalArgumentException("offset = " + offset + ", nBits = " + nBits + ", length = " + length);
        }
        return Bits.extractBits(value, this.offset + offset, nBits);
    }

    @Override
    public int bitCount(int offset, int nBits) throws IllegalArgumentException {
        if (0 > nBits) {
            throw new IllegalArgumentException("illegal nBits " + nBits + ", must be non-negative");
        }
        if (0 > offset || offset + nBits > length) {
            throw new IllegalArgumentException("offset = " + offset + ", nBits = " + nBits + ", length = " + length);
        }
        return Bits.bitCount(value, this.offset + offset, nBits);
    }

    @Override
    public int hashCode() {
        if (length == 0) {
            return 0;
        }
        if (hashCode != 0) {
            return hashCode;
        }

        int hashCode = Bits.hashCode(value, offset, length);

        if (hashCode == 0) {
            hashCode = 1;
        }

        return this.hashCode = hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof BitString) {
            BitString that = (BitString) o;
            return length == that.length &&
                    (offset == that.offset && value == that.value ||
                            Bits.compareBitRanges(value, offset, that.value, that.offset, length) == 0);
        }
        return false;
    }

    @NotNull
    public static BitString empty() {
        return EMPTY_STRING;
    }

    @NotNull
    public static BitString of(boolean... bits) {
        return new BitString(bits);
    }

    @NotNull
    public static BitString of(Iterable<Boolean> iterable) {
        return new BitString(iterable);
    }

    @Override
    public int compareTo(@NotNull BitString o) {
        if (this == o) {
            return 0;
        }
        int minLength = Math.min(length, o.length);
        int cmp = Bits.compareBitRanges(value, offset, o.value, o.offset, minLength);
        if (cmp != 0) {
            return cmp;
        }
        if (length != o.length) {
            return length < o.length ? -1 : 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length(); ++i) {
            sb.append(bitAt(i) ? '1' : '0');
        }
        return sb.toString();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        int length = length();
        stream.writeInt(length);
        int offset = this.offset;
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
        this.offset = 0;
        this.value = new long[(length + 64) >>> 6];
        for (int i = 0; i < value.length; ++i) {
            value[i] = stream.readLong();
        }
        hashCode = 0; //must be called before the first call to hashCode().
        view = null;
    }
}

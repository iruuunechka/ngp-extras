package ru.ifmo.ctd.ngp.demo.legacy_ds.string;

/**
 * An interface for a bit sequence.
 *
 * @author Maxim Buzdalov
 */
public interface BitSequence extends GCharSequence<Boolean> {
    /**
     * Returns the bit at the specified index. The index should be in the range
     * from 0 to {@link #length()} - 1, otherwise, a {@link StringIndexOutOfBoundsException} is thrown.
     * @param index the index.
     * @return the character at the specified index.
     * @throws IllegalArgumentException if the index is out of bounds.
     */
    boolean bitAt(int index) throws IllegalArgumentException;

    /**
     * Returns the bits at the specified index, packed in a long. The bit with
     * the index {@code offset} is returned as the bit 0, the bit with
     * the index {@code offset + 1} is returned as the bit 1, and so on.
     *
     * The value of {@code length} must not be negative and must not exceed 64.
     *
     * @param offset the smallest index of the bits to extract.
     * @param length the number of bits to extract.
     * @return the extracted bits packed as a long.
     * @throws IllegalArgumentException if the offset and/or length are illegal.
     */
    long bitsAt(int offset, int length) throws IllegalArgumentException;

    /**
     * Counts the number of bits set to 1 at the bit range specified by its offset and length.
     *
     * @param offset the smallest index of the bit range.
     * @param nBits the number of bits in the bit range.
     * @return the number of bits set to 1 in the bit range.
     * @throws IllegalArgumentException if the offset and/or length are illegal.
     */
    int bitCount(int offset, int nBits) throws IllegalArgumentException;
}

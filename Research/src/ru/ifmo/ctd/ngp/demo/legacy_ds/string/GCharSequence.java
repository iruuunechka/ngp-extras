package ru.ifmo.ctd.ngp.demo.legacy_ds.string;

import org.jetbrains.annotations.NotNull;

/**
 * A generic equivalent for {@link CharSequence} with characters of type T.
 *
 * @author Maxim Buzdalov
 */
public interface GCharSequence<T> {
    /**
     * Returns whether the char sequence is empty.
     * @return whether the char sequence is empty.
     */
    boolean isEmpty();

    /**
     * Returns the length of the generic char sequence, that is, the nummber of characters.
     * @return the length of the sequence.
     */
    int length();

    /**
     * Returns the character at the specified index. The index should be in the range
     * from 0 to {@link #length()} - 1, otherwise, a {@link StringIndexOutOfBoundsException} is thrown.
     * @param index the index.
     * @return the character at the specified index.
     * @throws IllegalArgumentException if the index is out of bounds.
     */
    @NotNull
    T charAt(int index) throws IllegalArgumentException;
}

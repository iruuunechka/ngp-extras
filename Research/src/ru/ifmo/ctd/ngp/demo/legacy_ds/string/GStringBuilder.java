package ru.ifmo.ctd.ngp.demo.legacy_ds.string;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * A "simplified" version of {@link GStringBuilderX}.
 *
 * @author Maxim Buzdalov
 */
public interface GStringBuilder<T> extends GCharSequence<T>, Serializable {
    /**
     * Sets a new character at the specified index. If the index is out of bounds,
     * an {@link StringIndexOutOfBoundsException} is thrown.
     *
     * Returns itself.
     *
     * @param index the index.
     * @param newValue the new character value.
     * @throws StringIndexOutOfBoundsException if the index is out of bounds.
     * @return itself.
     */
    @NotNull
    GStringBuilder<T> setCharAt(int index, @NotNull T newValue) throws StringIndexOutOfBoundsException;

    /**
     * Appends the given character.
     * Returns itself.
     *
     * @param character the character to be appended.
     * @return itself.
     */
    @NotNull
    GStringBuilder<T> append(@NotNull T character);

    /**
     * Appends the contents of the given sequence.
     * Returns itself.
     *
     * @param sequence the sequence which contents are to be appended.
     * @return itself.
     */
    @NotNull
    GStringBuilder<T> append(@NotNull GCharSequence<? extends T> sequence);

    /**
     * Decreases the length of the builder to the specified one.
     * The new length must non-negative and be less or equal to the current length,
     * otherwise, an {@link IllegalArgumentException} is thrown.
     *
     * Returns itself.
     *
     * @param newLength the new length.
     * @return itself.
     * @throws IllegalArgumentException if the length is negative or greater than the current one.
     */
    @NotNull
    GStringBuilder<T> decreaseLength(int newLength) throws IllegalArgumentException;

    /**
     * Sets the length to the specified one. If the length is to be increased, the specified character
     * will be used to fill the empty space.
     *
     * Returns itself.
     *
     * @param newLength the new length.
     * @param fillCharacter the character to be used to fill the space.
     * @return itself.
     * @throws IllegalArgumentException if the length is negative.
     */
    @NotNull
    GStringBuilder<T> setLength(int newLength, @NotNull T fillCharacter) throws IllegalArgumentException;

    /**
     * Decreases the underlying storage size to reduce the memory usage.
     */
    void trimToSize();
}

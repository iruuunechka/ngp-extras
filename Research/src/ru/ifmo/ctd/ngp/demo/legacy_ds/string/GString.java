package ru.ifmo.ctd.ngp.demo.legacy_ds.string;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

/**
 * A "simple" version of {@link GStringX}.
 *
 * @author Maxim Buzdalov
 */
public interface GString<T> extends GCharSequence<T>, Serializable {
    /**
     * Returns the unmodifiable view on this string as a list of characters.
     * @return the unmodifiable view on this string.
     */
    @NotNull
    List<T> asList();

    /**
     * Returns a substring of this string from the given {@code beginIndex} (inclusively) to the end of the string.
     * The return is the same as of {@code substring(beginIndex, length())}.
     *
     * The returned string should have the same memory and performance characteristics as this string,
     * in other words, they should have the same type. Sharing of memory is recommended.
     *
     * @param beginIndex the index of the beginning character of the substring.
     * @return the substring.
     * @throws IllegalArgumentException if the index is out of bounds.
     */
    @NotNull
    GString<T> substring(int beginIndex) throws IllegalArgumentException;

    /**
     * Returns a substring of this string from the given {@code beginIndex} (inclusively)
     * to the given {@code endIndex} (exclusively) of the string. The length of the string thus
     * equals to endIndex - beginIndex.
     *
     * The returned string should have the same memory and performance characteristics as this string,
     * in other words, they should have the same type. Sharing of memory is recommended.
     *
     * @param beginIndex the index of the beginning character of the substring.
     * @param endIndex the index of a character next to the ending character of the substring.
     * @return the substring.
     * @throws IllegalArgumentException if the index is out of bounds.
     */
    @NotNull
    GString<T> substring(int beginIndex, int endIndex) throws IllegalArgumentException;

    /**
     * Returns an empty string of the same type that this string. This should be the same object every time.
     * @return the empty string of the same type.
     */
    @NotNull
    GString<T> emptyString();
}

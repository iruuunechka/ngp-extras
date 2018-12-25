package ru.ifmo.ctd.ngp.demo.legacy_ds.string;

import org.jetbrains.annotations.NotNull;

/**
 * A generic equivalent of {@link String} with characters of type T.
 *
 * TODO: fully describe the concept.
 *
 * @author Maxim Buzdalov
 */
public interface GStringX<T, S extends GStringX<T, S, B>, B extends GStringBuilderX<T, B, S>> extends GString<T> {
    /**
     * Returns a substring of this string from the given <code>beginIndex</code> (inclusively) to the end of the string.
     * The return is the same as of <code>substring(beginIndex, length())</code>.
     *
     * The returned string should have the same memory and performance characteristics as this string,
     * in other words, they should have the same type. Sharing of memory is recommended.
     *
     * @param beginIndex the index of the beginning character of the substring.
     * @return the substring.
     * @throws IllegalArgumentException if the index is out of bounds.
     */
    @NotNull
    S substring(int beginIndex) throws IllegalArgumentException;

    /**
     * Returns a substring of this string from the given <code>beginIndex</code> (inclusively)
     * to the given <code>endIndex</code> (exclusively) of the string. The length of the string thus
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
    S substring(int beginIndex, int endIndex) throws IllegalArgumentException;

    /**
     * Returns an empty string of the same type that this string. This should be the same object every time.
     * @return the empty string of the same type.
     */
    @NotNull
    S emptyString();

    /**
     * Returns a generic string builder containing the same elements in the same order
     * as in this string. The storage concepts of the builder must be the same as in this
     * string, so if invoke <code>this.toGStringBuilder().toGString()</code>, the resulting
     * string must have the same memory and performance characteristics as this string.
     *
     * @return the generic string builder.
     */
    @NotNull
    B toGStringBuilder();
}

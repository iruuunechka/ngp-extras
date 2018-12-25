package ru.ifmo.ctd.ngp.learning.util;

/**
 * A simple formatter for arbitrary values.
 *
 * @author Maxim Buzdalov
 */
public interface PrintFormatter<T> {
    /**
     * A formatter that converts Boolean values to 0 for {@code false}, 1 for {@code true}.
     */
    PrintFormatter<Boolean> BOOLEAN_01 = value -> value ? "1" : "0";

    /**
     * Converts the given value to a string.
     * @param value the value.
     * @return its string representation.
     */
    String format(T value);
}

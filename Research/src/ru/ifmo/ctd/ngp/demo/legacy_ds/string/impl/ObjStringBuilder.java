package ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.GCharSequence;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.GStringBuilderX;

import java.util.ArrayList;

/**
 * A generic implementation of {@link ru.ifmo.ctd.ngp.demo.legacy_ds.string.GStringX} based on a list of objects.
 * Is dual to {@link ObjString}.
 *
 * @author Maxim Buzdalov
 */
public final class ObjStringBuilder<T> implements GStringBuilderX<T, ObjStringBuilder<T>, ObjString<T>> {
    private static final long serialVersionUID = -2812322804721426467L;

    @NotNull
    private final ArrayList<T> contents = new ArrayList<>();

    /**
     * Creates an empty builder.
     */
    public ObjStringBuilder() {}

    /**
     * Creates a builder with the characters of the given char sequence.
     * @param sequence the sequence to be copied.
     */
    public ObjStringBuilder(@NotNull GCharSequence<? extends T> sequence) {
        append(sequence);
    }

    /**
     * Creates a builder with the characters contained in the specified iterable.
     * @param iterable the iterable.
     */
    public ObjStringBuilder(@NotNull Iterable<? extends T> iterable) {
        for (T character : iterable) {
            append(character);
        }
    }

    /**
     * Creates a builder from the given characters.
     * @param characters the characters.
     */
    @SafeVarargs
    public ObjStringBuilder(@NotNull T... characters) {
        for (T character : characters) {
            append(character);
        }
    }

    /**
     * Returns a new empty builder.
     * @return the new empty builder.
     */
    @NotNull
    public static <T> ObjStringBuilder<T> empty() {
        return new ObjStringBuilder<>();
    }

    /**
     * Returns a new builder with the characters of the given char sequence.
     * @param sequence the sequence to be copied.
     * @return the new builder.
     */
    @NotNull
    public static <T> ObjStringBuilder<T> of(@NotNull GCharSequence<? extends T> sequence) {
        return new ObjStringBuilder<>(sequence);
    }

    /**
     * Returns a new builder with the characters from the given iterable.
     * @param iterable the iterable to be read.
     * @return the new builder.
     */
    @NotNull
    public static <T> ObjStringBuilder<T> of(@NotNull Iterable<? extends T> iterable) {
        return new ObjStringBuilder<>(iterable);
    }

    /**
     * Returns a new builder with the given characters.
     * @param characters the characters to consist of.
     * @return the new builder.
     */
    @SafeVarargs
    @NotNull
    public static <T> ObjStringBuilder<T> of(@NotNull T... characters) {
        return new ObjStringBuilder<>(characters);
    }

    @NotNull
    @Override
    public ObjStringBuilder<T> setCharAt(int index, @NotNull T newValue) throws IllegalArgumentException {
        if (index < 0 || index >= length()) {
            throw new IllegalArgumentException("index = " + index + ", length = " + length());
        }
        contents.set(index, newValue);
        return this;
    }

    @NotNull
    @Override
    public ObjStringBuilder<T> append(@NotNull T character) {
        contents.add(character);
        return this;
    }

    @NotNull
    @Override
    public ObjStringBuilder<T> append(@NotNull GCharSequence<? extends T> charSequence) {
        for (int i = 0, j = charSequence.length(); i < j; ++i) {
            contents.add(charSequence.charAt(i));
        }
        return this;
    }

    @NotNull
    @Override
    public ObjStringBuilder<T> decreaseLength(int newLength) throws IllegalArgumentException {
        if (newLength < 0 || newLength > length()) {
            throw new IllegalArgumentException("length = " + length() + ", newLength = " + newLength);
        }
        int cs = contents.size();
        while (cs > newLength) {
            contents.remove(--cs);
        }
        return this;
    }

    @NotNull
    @Override
    public ObjStringBuilder<T> setLength(int newLength, @NotNull T fillCharacter) throws IllegalArgumentException {
        if (newLength <= length()) {
            return decreaseLength(newLength);
        } else {
            int cs = contents.size();
            while (cs < newLength) {
                contents.add(fillCharacter);
                ++cs;
            }
            return this;
        }
    }

    @NotNull
    @Override
    @SuppressWarnings({"unchecked"})
    public ObjString<T> toGString() {
        if (contents.isEmpty()) {
            return ObjString.empty();
        }
        Object[] ar = contents.toArray();
        return new ObjString<>((T[])ar, 0, ar.length);
    }

    @Override
    public boolean isEmpty() {
        return length() == 0;
    }

    @Override
    public int length() {
        return contents.size();
    }

    @NotNull
    @Override
    public T charAt(int index) throws IllegalArgumentException {
        if (index < 0 || index >= length()) {
            throw new IllegalArgumentException("index = " + index + ", length = " + length());
        }
        return contents.get(index);
    }

    @Override
    public void trimToSize() {
        contents.trimToSize();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ObjStringBuilder<?> that = (ObjStringBuilder<?>) o;
        return contents.equals(that.contents);
    }

    @Override
    public int hashCode() {
        return contents.hashCode();
    }
}

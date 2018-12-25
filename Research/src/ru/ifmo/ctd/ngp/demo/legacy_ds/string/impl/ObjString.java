package ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.GStringX;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * A generic implementation of {@link ru.ifmo.ctd.ngp.demo.legacy_ds.string.GStringX} based on an array of objects.
 * Is dual to {@link ObjStringBuilder}.
 *
 * @author Maxim Buzdalov
 */
public final class ObjString<T> implements GStringX<T, ObjString<T>, ObjStringBuilder<T>> {
    @SuppressWarnings({"RawUseOfParameterizedType"})
    private static final ObjString EMPTY_STRING = new ObjString<>(new Object[0], 0, 0);
    private static final long serialVersionUID = -6596050318636458147L;

    @NotNull
    private transient T[] value;
    private transient int offset;
    private transient int length;
    private transient int hashCode = 0;

    @Nullable
    private transient List<T> listView = null;

    /**
     * Creates a string from the iterable of characters.
     * @param iterable the iterable of characters.
     */
    @SuppressWarnings({"unchecked"})
    protected ObjString(@NotNull Iterable<? extends T> iterable) {
        Collection<? extends T> collection;
        if (iterable instanceof Collection) {
            collection = (Collection<? extends T>) (iterable);
        } else {
            List<T> list = new ArrayList<>();
            for (T t : iterable) {
                list.add(t);
            }
            collection = list;
        }
        this.value = (T[]) collection.toArray();
        this.offset = 0;
        this.length = value.length;
    }

    /**
     * Creates a string from the given characters.
     * @param characters the characters.
     */
    @SafeVarargs
    protected ObjString(@NotNull T... characters) {
        this.value = characters.clone();
        this.offset = 0;
        this.length = this.value.length;
    }

    /**
     * Returns a new string constructed from the iterable of characters.
     * @param iterable the iterable of characters.
     * @return the new string.
     */
    @NotNull
    public static <T> ObjString<T> of(@NotNull Iterable<? extends T> iterable) {
        return new ObjString<>(iterable);
    }

    /**
     * Returns a new string constructed from the given characters.
     * @param characters the characters.
     * @return the new string.
     */
    @SafeVarargs
    @NotNull
    public static <T> ObjString<T> of(@NotNull T... characters) {
        return new ObjString<>(characters);
    }

    protected ObjString(@NotNull T[] value, int offset, int length) {
        this.value = value;
        this.offset = offset;
        this.length = length;
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public List<T> asList() {
        @NotNull List<T> rv;
        if (listView == null) {
            if (length == 0) {
                listView = rv = Collections.emptyList();
            } else {
                listView = rv = Collections.unmodifiableList(Arrays.asList(value).subList(offset, offset + length));
            }
        } else {
            rv = listView;
        }
        return rv;
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public ObjString<T> substring(int beginIndex) throws IllegalArgumentException {
        return substring(beginIndex, length);
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public ObjString<T> substring(int beginIndex, int endIndex) throws IllegalArgumentException {
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
        return new ObjString<>(value, offset + beginIndex, endIndex - beginIndex);
    }

    /**
     * {@inheritDoc}
     *
     * The returned builder will be of {@link ObjStringBuilder} class.
     */
    @NotNull
    @Override
    public ObjStringBuilder<T> toGStringBuilder() {
        return new ObjStringBuilder<>(this);
    }

    /** {@inheritDoc} */
    @SuppressWarnings({"unchecked"})
    @NotNull
    @Override
    public ObjString<T> emptyString() {
        return EMPTY_STRING;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return length == 0;
    }

    /** {@inheritDoc} */
    @Override
    public int length() {
        return length;
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public T charAt(int index) throws IllegalArgumentException {
        if (index < 0 || index >= length) {
            throw new IllegalArgumentException("index = " + index + ", length = " + length);
        }
        return value[offset + index];
    }

    /**
     * Returns <tt>true</tt> if the given object is a ObjString
     * and the objects contained in the string are equal in order of presence.
     *
     * @param obj the object to be tested for equality with.
     * @return <tt>true</tt> if the given object is equal to this one, <tt>false</tt> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() == this.getClass()) {
            ObjString<?> that = (ObjString<?>) obj;
            if (length != that.length) {
                return false;
            }
            if (value == that.value && offset == that.offset) {
                return true;
            }
            for (int l = offset, r = that.offset, i = 0; i < length; ++i, ++l, ++r) {
                if (!value[l].equals(that.value[r])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0, j = offset; i < length; ) {
            sb.append(value[j++]);
            if (++i != length) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        if (hashCode != 0) {
            return hashCode;
        }
        int rv = 0;
        for (int i = 0, j = offset; i < length; ++i, ++j) {
            rv = 31 * rv + value[j].hashCode();
        }
        if (rv == 0) {
            rv = 1;
        }
        return hashCode = rv;
    }

    /**
     * Returns the empty {@link ObjString} with the characters of type T.
     *
     * This string will always be the same one.
     *
     * @param <T> the type of characters.
     * @return the empty string.
     */
    @NotNull
    @SuppressWarnings({"unchecked"})
    public static <T> ObjString<T> empty() {
        return EMPTY_STRING;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        int length = length();
        stream.writeInt(length);
        for (int i = 0; i < length; ++i) {
            //Java type system does not give us the possibility of defining ObjString<T> serializable
            //iff T is serializable.
            //noinspection NonSerializableObjectPassedToObjectStream
            stream.writeObject(charAt(i));
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.length = stream.readInt();
        this.offset = 0;
        //noinspection unchecked
        this.value = (T[]) new Object[length];
        for (int i = 0; i < value.length; ++i) {
            //noinspection unchecked
            value[i] = (T) stream.readObject();
        }
        hashCode = 0; //must be called before the first call to hashCode().
        listView = null;
    }
}

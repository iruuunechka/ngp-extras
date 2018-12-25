package ru.ifmo.ctd.ngp.util;

import java.util.*;

/**
 * An utility class for collections containing methods not found in {@link java.util.Collections}.
 *
 * @author Maxim Buzdalov
 */
public final class CollectionsEx {
    private CollectionsEx() {
        Static.doNotCreateInstancesOf(CollectionsEx.class);
    }

    private interface ImmutableList {}
    private static final List<Class<?>> knownImmutableLists = new ArrayList<>();
    private static void tryRegisterImmutableList(String name) {
        try {
            Class<?> clazz = Class.forName(name);
            knownImmutableLists.add(clazz);
        } catch (Throwable ignore) {
            System.err.println("Unable to register immutable list '" + name + "'. Sorry for slow performance.");
        }
    }
    static {
        tryRegisterImmutableList("java.util.Collections$UnmodifiableList");
        tryRegisterImmutableList("java.util.Collections$EmptyList");
        tryRegisterImmutableList("java.util.Collections$SingletonList");
        tryRegisterImmutableList("java.util.Collections$CopiesList");
    }

    /**
     * Returns an unmodifiable list containing no elements.
     *
     * @param <T> the type of elements in the resulting list.
     * @return the list containing the given elements.
     */
    public static <T> List<T> listOf() {
        return Collections.emptyList();
    }


    /**
     * Returns an unmodifiable list containing the given elements.
     *
     * @param e0 the element with index 0.
     * @param <T> the type of elements in the resulting list.
     * @return the list containing the given elements.
     */
    public static <T> List<T> listOf(T e0) {
        return new FixedSizeArrayList1<>(e0);
    }

    /**
     * Returns an unmodifiable list containing the given elements.
     *
     * @param e0 the element with index 0.
     * @param e1 the element with index 1.
     * @param <T> the type of elements in the resulting list.
     * @return the list containing the given elements.
     */
    public static <T> List<T> listOf(T e0, T e1) {
        return new FixedSizeArrayList2<>(e0, e1);
    }

    /**
     * Returns an unmodifiable list containing the given elements.
     *
     * @param e0 the element with index 0.
     * @param e1 the element with index 1.
     * @param e2 the element with index 2.
     * @param <T> the type of elements in the resulting list.
     * @return the list containing the given elements.
     */
    public static <T> List<T> listOf(T e0, T e1, T e2) {
        return new FixedSizeArrayList<>(e0, e1, e2);
    }

    /**
     * Returns an unmodifiable list containing the given elements.
     *
     * @param e0 the element with index 0.
     * @param e1 the element with index 1.
     * @param e2 the element with index 2.
     * @param e3 the element with index 3.
     * @param <T> the type of elements in the resulting list.
     * @return the list containing the given elements.
     */
    public static <T> List<T> listOf(T e0, T e1, T e2, T e3) {
        return new FixedSizeArrayList<>(e0, e1, e2, e3);
    }

    /**
     * Returns an unmodifiable list containing the given elements.
     *
     * @param e0 the element with index 0.
     * @param e1 the element with index 1.
     * @param e2 the element with index 2.
     * @param e3 the element with index 3.
     * @param e4 the element with index 4.
     * @param <T> the type of elements in the resulting list.
     * @return the list containing the given elements.
     */
    public static <T> List<T> listOf(T e0, T e1, T e2, T e3, T e4) {
        return new FixedSizeArrayList<>(e0, e1, e2, e3, e4);
    }

    /**
     * Returns an unmodifiable list containing the given elements.
     *
     * @param e0 the element with index 0.
     * @param e1 the element with index 1.
     * @param e2 the element with index 2.
     * @param e3 the element with index 3.
     * @param e4 the element with index 4.
     * @param e5 the element with index 5.
     * @param <T> the type of elements in the resulting list.
     * @return the list containing the given elements.
     */
    public static <T> List<T> listOf(T e0, T e1, T e2, T e3, T e4, T e5) {
        return new FixedSizeArrayList<>(e0, e1, e2, e3, e4, e5);
    }

    /**
     * Returns an unmodifiable list containing the given elements.
     *
     * @param e0 the element with index 0.
     * @param e1 the element with index 1.
     * @param e2 the element with index 2.
     * @param e3 the element with index 3.
     * @param e4 the element with index 4.
     * @param e5 the element with index 5.
     * @param e6 the element with index 6.
     * @param <T> the type of elements in the resulting list.
     * @return the list containing the given elements.
     */
    public static <T> List<T> listOf(T e0, T e1, T e2, T e3, T e4, T e5, T e6) {
        return new FixedSizeArrayList<>(e0, e1, e2, e3, e4, e5, e6);
    }

    /**
     * Returns an unmodifiable list containing the given elements.
     *
     * @param elements the elements of the list to be created.
     * @param <T> the type of elements in the resulting list.
     * @return the list containing the given elements.
     */
    @SafeVarargs
    public static <T> List<T> listOf(T... elements) {
        return new FixedSizeArrayList<>(elements);
    }

    /**
     * Returns an unmodifiable list containing the elements in the given collection.
     *
     * @param elements the elements of the list to be created.
     * @param <T> the type of elements in the resulting list.
     * @return the list containing the given elements.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> listFrom(Collection<? extends T> elements) {
        if (elements instanceof ImmutableList) {
            return (List<T>) (elements);
        }
        for (Class<?> immutableClass : knownImmutableLists) {
            if (immutableClass.isInstance(elements)) {
                return (List<T>) (elements);
            }
        }
        return new FixedSizeArrayList<>(elements);
    }

    private static final class FixedSizeArrayList1<E> extends AbstractList<E> implements RandomAccess, ImmutableList {
        private final E e;

        private FixedSizeArrayList1(E e) {
            this.e = e;
        }

        @Override
        public E get(int index) {
            if (index != 0) {
                throw new IndexOutOfBoundsException("Index " + index + ", size 2");
            }
            return e;
        }

        @Override
        public int size() {
            return 1;
        }
    }


    private static final class FixedSizeArrayList2<E> extends AbstractList<E> implements RandomAccess, ImmutableList {
        private final E e0, e1;

        private FixedSizeArrayList2(E e0, E e1) {
            this.e0 = e0;
            this.e1 = e1;
        }

        @Override
        public E get(int index) {
            if (index < 0 || index > 1) {
                throw new IndexOutOfBoundsException("Index " + index + ", size 2");
            }
            return index == 0 ? e0 : e1;
        }

        @Override
        public int size() {
            return 2;
        }
    }

    @SuppressWarnings({"unchecked"})
    private static final class FixedSizeArrayList<E> extends AbstractList<E> implements RandomAccess, ImmutableList {
        private final E[] elements;

        FixedSizeArrayList(E e0, E e1, E e2) {
            elements = (E[]) new Object[] { e0, e1, e2 };
        }

        FixedSizeArrayList(E e0, E e1, E e2, E e3) {
            elements = (E[]) new Object[] { e0, e1, e2, e3 };
        }

        FixedSizeArrayList(E e0, E e1, E e2, E e3, E e4) {
            elements = (E[]) new Object[] { e0, e1, e2, e3, e4 };
        }

        FixedSizeArrayList(E e0, E e1, E e2, E e3, E e4, E e5) {
            elements = (E[]) new Object[] { e0, e1, e2, e3, e4, e5 };
        }

        FixedSizeArrayList(E e0, E e1, E e2, E e3, E e4, E e5, E e6) {
            elements = (E[]) new Object[] { e0, e1, e2, e3, e4, e5, e6 };
        }

        FixedSizeArrayList(E... elements) {
            this.elements = elements.clone();
        }

        FixedSizeArrayList(Collection<? extends E> collection) {
            this.elements = (E[]) collection.toArray();
        }

        @Override
        public E get(int index) {
            return elements[index];
        }

        @Override
        public int size() {
            return elements.length;
        }
    }

}

package ru.ifmo.ctd.ngp.theory.moearl;

/**
 * @author Irene Petrova
 */
public class Pair<T1, T2>{
    private final T1 key;
    private final T2 value;

    public Pair(T1 key, T2 value) {

        this.key = key;
        this.value = value;
    }

    public T1 getKey() {
        return key;
    }

    public T2 getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        return key.equals(pair.key) && value.equals(pair.value);
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}

package ru.ifmo.ctd.ngp.demo.util;

/**
 * A meaningless singleton intended to be used as an argument or return value
 * for visitors where no argument or return value is needed.
 *
 * @author Maxim Buzdalov
 */
public final class Nil {
    private Nil() {}
    private static final Nil VALUE = new Nil();

    public static Nil value() {
        return VALUE;
    }
}

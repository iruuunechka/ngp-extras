package ru.ifmo.ctd.ngp.util;

/**
 * An utility class for "static" classes (e.g. containing only static methods).
 *
 * @author Maxim Buzdalov
 */
public final class Static {
    private Static() {
        Static.doNotCreateInstancesOf(Static.class);
    }

    /**
     * Must be called from within the private constructor of a "static" class.
     *
     * @param clazz the class.
     */
    public static void doNotCreateInstancesOf(Class<?> clazz) {
        throw new AssertionError("Do not create instances of class " + clazz.getName());
    }
}

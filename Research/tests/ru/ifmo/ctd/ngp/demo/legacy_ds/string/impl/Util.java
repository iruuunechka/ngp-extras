package ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl;

import org.junit.Assert;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.BitSequence;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.GCharSequence;

/**
 * Utility methods for testing of ObjString and ObjStringBuilder.
 *
 * @author Maxim Buzdalov
 */
public final class Util {
    private Util() {}

    public static void testBits(String expected, BitSequence actual) {
        Assert.assertEquals(expected.length(), actual.length());
        for (int i = 0; i < expected.length(); ++i) {
            Assert.assertEquals(expected.charAt(i), actual.charAt(i) ? '1' : '0');
            Assert.assertEquals(expected.charAt(i), actual.bitAt(i) ? '1' : '0');
        }
        Assert.assertEquals(expected.isEmpty(), actual.isEmpty());
    }

    public static void testBitBuilder(String expected, BitStringBuilder actual) {
        testBits(expected, actual);
        testBits(expected, actual.toGString());
    }

    public static void testBitString(String expected, BitString actual) {
        testBits(expected, actual);
        testBits(expected, actual.toGStringBuilder());
    }

    public static void test(String expected, GCharSequence<Character> actual) {
        Assert.assertEquals(expected.length(), actual.length());
        for (int i = 0; i < expected.length(); ++i) {
            Assert.assertEquals(expected.charAt(i), (char) actual.charAt(i));
        }
        Assert.assertEquals(expected.isEmpty(), actual.isEmpty());
    }

    public static void testBuilder(String expected, ObjStringBuilder<Character> actual) {
        test(expected, actual);
        test(expected, actual.toGString());
    }

    public static void testString(String expected, ObjString<Character> actual) {
        test(expected, actual);
        test(expected, actual.toGStringBuilder());
    }

}

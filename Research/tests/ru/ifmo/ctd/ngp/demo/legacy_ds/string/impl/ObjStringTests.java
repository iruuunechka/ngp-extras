package ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Random;

/**
 * Some tests for {@link ObjString}.
 *
 * @author Maxim Buzdalov
 */
public class ObjStringTests {
    @Test
    public void emptyString() {
        ObjString<Character> str = ObjString.empty();
        Util.testString("", str);

        Assert.assertSame(str, str.substring(0));
        Assert.assertSame(str, str.substring(0, 0));
        Assert.assertEquals(0, str.asList().size());
        Assert.assertTrue(str.isEmpty());
    }

    @Test
    public void equalsHashCodeTest() {
        ObjStringBuilder<Character> builder = ObjStringBuilder.of('a', 'b', 'c');
        ObjString<Character> s1 = builder.toGString();
        builder.append('d');
        builder.decreaseLength(2);
        builder.append('c');
        ObjString<Character> s2 = builder.toGString();
        builder.append('d');
        ObjString<Character> s3 = builder.toGString();

        Assert.assertEquals(s1, s2);
        Assert.assertEquals(s1.hashCode(), s2.hashCode());

        //noinspection SimplifiableJUnitAssertion
        Assert.assertFalse(s1.equals(s3));
        Assert.assertFalse(s1.hashCode() == s3.hashCode());
    }

    @Test
    public void oneCharacterString() {
        ObjString<Character> str = ObjString.of('a');
        Util.testString("a", str);

        Assert.assertEquals(0, str.substring(1).length());
        Assert.assertEquals(0, str.substring(0, 0).length());
        Assert.assertSame(str, str.substring(0));
        Assert.assertSame(str, str.substring(0, 1));
        Assert.assertFalse(str.isEmpty());

        List<Character> list = str.asList();

        Assert.assertEquals(1, list.size());
        Assert.assertEquals('a', (char) list.get(0));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(expected = IllegalArgumentException.class)
    public void negativeIndex() {
        ObjString.empty().charAt(-1);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(expected = IllegalArgumentException.class)
    public void tooLargeIndex() {
        ObjString.of('a').charAt(1);
    }

    @Test(expected =  IllegalArgumentException.class)
    public void negativeSizeSubstring() {
        ObjString.of('a', 'b', 'c').substring(2, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeStartSubstring() {
        ObjString.of('a', 'b', 'c').substring(-1, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceedingSizeSubstring() {
        ObjString.of('a', 'b', 'c').substring(2, 4);
    }

    @Test
    public void exactlyAtTheEndSubstring() {
        Util.testString("c", ObjString.of('a', 'b', 'c').substring(2, 3));
    }

    @Test
    public void testCharAt() {
        Character[] chars = new Character[300];
        StringBuilder tsb = new StringBuilder();
        for (int i = 0; i < chars.length; ++i) {
            chars[i] = (char) ('a' + i % 26);
            tsb.append(chars[i]);
        }
        String ts = tsb.toString();
        ObjString<Character> string = ObjString.of(chars);
        Util.testString(ts, string);
        for (int l = 0; l <= string.length(); ++l) {
            for (int r = l; r <= string.length(); ++r) {
                Util.testString(ts.substring(l, r), string.substring(l, r));
            }
        }
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void asListIsImmutable() {
        ObjString<Character> os = ObjString.of('a', 'b');
        List<Character> list = os.asList();
        list.set(0, 'b');
        Assert.assertEquals('a', (long)os.charAt(0));
    }

    @Test
    public void serialization() throws Exception {
        Integer[] ar = new Integer[10];
        Random r = new Random(234235);
        for (int times = 0; times < 100; ++times) {
            for (int i = 0; i < ar.length; ++i) {
                ar[i] = r.nextInt();
            }
            ObjString<Integer> str = ObjString.of(ar);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
                oos.writeObject(str);
            }
            out.close();

            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            try (ObjectInputStream ois = new ObjectInputStream(in)) {
                Object str2 = ois.readObject();
                Assert.assertEquals(str, str2);
            }
            in.close();
        }
    }
}

package ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

/**
 * Some tests for {@link ObjStringBuilder}.
 *
 * @author Maxim Buzdalov
 */
public class ObjStringBuilderTests {
    @Test
    public void empty() {
        ObjStringBuilder<Character> b = ObjStringBuilder.empty();
        Assert.assertEquals(0, b.length());
        Assert.assertEquals(0, b.toGString().length());
        Assert.assertSame(ObjString.empty(), b.toGString());
    }

    @Test
    public void someActions() {
        ObjStringBuilder<Character> builder = ObjStringBuilder.empty();
        Util.testBuilder("", builder);
        builder.append('a');
        Util.testBuilder("a", builder);
        builder.append('b');
        Util.testBuilder("ab", builder);
        builder.decreaseLength(1);
        Util.testBuilder("a", builder);
        builder.setLength(10, '1');
        Util.testBuilder("a111111111", builder);
        builder.setCharAt(1, '2');
        Util.testBuilder("a211111111", builder);
        builder.setLength(3, 'y');
        Util.testBuilder("a21", builder);
        builder.append(ObjString.of('0', '2', '4'));
        Util.testBuilder("a21024", builder);
    }

    @Test
    public void appendItself() {
        ObjStringBuilder<Character> builder = ObjStringBuilder.of('a', 'b', 'c');
        builder.append(builder);
        Util.testBuilder("abcabc", builder);
    }

    @Test(expected = IllegalArgumentException.class)
    public void charAtTooSmall() {
        ObjStringBuilder.of('a', 'b', 'c').charAt(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void charAtTooLarge() {
        ObjStringBuilder.of('a', 'b', 'c').charAt(3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setCharAtTooSmall() {
        ObjStringBuilder.of('a', 'b', 'c').setCharAt(-1, 'd');
    }

    @Test(expected = IllegalArgumentException.class)
    public void setCharAtTooLarge() {
        ObjStringBuilder.of('a', 'b', 'c').setCharAt(3, 'd');
    }

    @Test(expected = IllegalArgumentException.class)
    public void decreaseLengthIncreased() {
        ObjStringBuilder.of('a', 'b', 'c').decreaseLength(4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decreaseLengthNegative() {
        ObjStringBuilder.of('a', 'b', 'c').decreaseLength(-1);
    }

    @Test
    public void decreaseLengthSame() {
        Util.testBuilder("abc", ObjStringBuilder.of('a', 'b', 'c').decreaseLength(3));
    }

    @Test
    public void decreaseLengthZero() {
        Util.testBuilder("", ObjStringBuilder.of('a', 'b', 'c').decreaseLength(0));
    }

    @Test
    public void testTrim() {
        ObjStringBuilder<Boolean> builder = ObjStringBuilder.empty();
        for (int i = 0; i < 100; ++i) {
            builder.append((i * 2323435) % 2 == 0);
        }
        boolean x = builder.charAt(0);
        boolean y = builder.charAt(30);
        boolean z = builder.charAt(60);

        builder.decreaseLength(61);
        Assert.assertEquals(x, builder.charAt(0));
        Assert.assertEquals(y, builder.charAt(30));
        Assert.assertEquals(z, builder.charAt(60));

        builder.trimToSize();
        Assert.assertEquals(x, builder.charAt(0));
        Assert.assertEquals(y, builder.charAt(30));
        Assert.assertEquals(z, builder.charAt(60));
    }

    @Test
    public void serialization() throws Exception {
        Integer[] ar = new Integer[10];
        Random r = new Random(234235);
        for (int times = 0; times < 100; ++times) {
            for (int i = 0; i < ar.length; ++i) {
                ar[i] = r.nextInt();
            }
            ObjStringBuilder<Integer> str = ObjStringBuilder.of(ar);

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

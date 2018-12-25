package ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Random;

/**
 * Some tests for {@link ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitStringBuilder}.
 *
 * @author Maxim Buzdalov
 */
public class BitStringBuilderTests {
    @Test
    public void empty() {
        BitStringBuilder b = BitStringBuilder.empty();
        Assert.assertEquals(0, b.length());
        Assert.assertEquals(0, b.toGString().length());
        Assert.assertSame(BitString.empty(), b.toGString());
    }

    @Test
    public void someActions() {
        BitStringBuilder builder = BitStringBuilder.empty();
        Util.testBitBuilder("", builder);
        builder.append(true);
        Util.testBitBuilder("1", builder);
        builder.append(false);
        Util.testBitBuilder("10", builder);
        builder.decreaseLength(1);
        Util.testBitBuilder("1", builder);
        builder.setLength(10, false);
        Util.testBitBuilder("1000000000", builder);
        builder.setCharAt(1, true);
        Util.testBitBuilder("1100000000", builder);
        builder.setLength(3, false);
        Util.testBitBuilder("110", builder);
        builder.append(BitString.of(false, false, true, false));
        Util.testBitBuilder("1100010", builder);

        builder.append(ObjString.of(true, false));
        Util.testBitBuilder("110001010", builder);

        builder.setLength(15, true);
        Util.testBitBuilder("110001010111111", builder);
    }

    @Test
    public void someBitActions() {
        BitStringBuilder builder = BitStringBuilder.empty();
        Util.testBitBuilder("", builder);
        builder.appendBit(true);
        Util.testBitBuilder("1", builder);
        builder.appendBit(false);
        Util.testBitBuilder("10", builder);
        builder.decreaseLength(1);
        Util.testBitBuilder("1", builder);
        builder.setLength(10, false);
        Util.testBitBuilder("1000000000", builder);
        builder.setCharAt(1, true);
        Util.testBitBuilder("1100000000", builder);
        builder.setLength(3, false);
        Util.testBitBuilder("110", builder);
        builder.append(BitString.of(false, false, true, false));
        Util.testBitBuilder("1100010", builder);

        builder.append(ObjString.of(true, false));
        Util.testBitBuilder("110001010", builder);

        builder.append(BitString.of(Collections.nCopies(100, true)));
        StringBuilder nb = new StringBuilder("110001010");
        for (int i = 0; i < 100; ++i) {
            nb.append('1');
        }
        Util.testBitBuilder(nb.toString(), builder);

        builder.append(BitString.of(Collections.nCopies(100, false)));
        for (int i = 0; i < 100; ++i) {
            nb.append('0');
        }
        Util.testBitBuilder(nb.toString(), builder);
    }

    @Test
    public void testForZeroingOut() {
        BitStringBuilder b = BitStringBuilder.empty();
        b.appendBit(true);
        Assert.assertTrue("b[0] must be true", b.bitAt(0));
        b.decreaseLength(0);
        b.appendBit(false);
        Assert.assertFalse("Positive values are not cleaned", b.bitAt(0));
        b.decreaseLength(0);
        b.appendBit(true);
        Assert.assertTrue("Negative values are not cleaned", b.bitAt(0));
    }

    @Test
    public void appendBits() {
        BitStringBuilder builder = BitStringBuilder.empty();
        Util.testBitBuilder("", builder);
        Util.testBitBuilder("1111111111", builder.appendBits(-1L, 10));
        Util.testBitBuilder("1", builder.decreaseLength(1));
        Util.testBitBuilder("1000000000", builder.appendBits(0, 9));
        Util.testBitBuilder("10", builder.decreaseLength(2));
        Util.testBitBuilder("1011111111", builder.appendBits(-1L, 8));
        Util.testBitBuilder("10111111110100000000000000000000000000000000000000000000000000000000000000", builder.appendBits(2L, 64));
    }

    @Test(expected = IllegalArgumentException.class)
    public void appendNegativeNumberOfBits() {
        BitStringBuilder.empty().appendBits(0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void appendLotsOfBits() {
        BitStringBuilder.empty().appendBits(0, 65);
    }

    @Test
    public void appendItself() {
        BitStringBuilder builder = BitStringBuilder.of(true, false);
        builder.append(builder);
        Util.testBitBuilder("1010", builder);
    }

    @Test(expected = IllegalArgumentException.class)
    public void charAtTooSmall() {
        BitStringBuilder.of(true, false, false).charAt(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void charAtTooLarge() {
        BitStringBuilder.of(true, false, false).charAt(3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void bitAtTooSmall() {
        BitStringBuilder.of(true, false, false).bitAt(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void bitAtTooLarge() {
        BitStringBuilder.of(true, false, false).bitAt(3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setCharAtTooSmall() {
        BitStringBuilder.of(true, false, false).setCharAt(-1, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setCharAtTooLarge() {
        BitStringBuilder.of(true, false, false).setCharAt(3, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setBitAtTooSmall() {
        BitStringBuilder.of(true, false, false).setBitAt(-1, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setBitAtTooLarge() {
        BitStringBuilder.of(true, false, false).setBitAt(3, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decreaseLengthIncreased() {
        BitStringBuilder.of(true, false, false).decreaseLength(4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decreaseLengthNegative() {
        BitStringBuilder.of(true, false, false).decreaseLength(-1);
    }

    @Test
    public void decreaseLengthSame() {
        Util.testBitBuilder("010", BitStringBuilder.of(false, true, false).decreaseLength(3));
    }

    @Test
    public void decreaseLengthZero() {
        Util.testBitBuilder("", BitStringBuilder.of(true, false, false).decreaseLength(0));
    }

    @Test
    public void testTrim() {
        BitStringBuilder builder = BitStringBuilder.empty();
        for (int i = 0; i < 100; ++i) {
            builder.appendBit(((i * 2323435) / 443) % 2 == 0);
        }
        boolean x = builder.bitAt(0);
        boolean y = builder.bitAt(30);
        boolean z = builder.bitAt(60);

        builder.decreaseLength(61);
        Assert.assertEquals(x, builder.bitAt(0));
        Assert.assertEquals(y, builder.bitAt(30));
        Assert.assertEquals(z, builder.bitAt(60));

        builder.trimToSize();
        Assert.assertEquals(x, builder.bitAt(0));
        Assert.assertEquals(y, builder.bitAt(30));
        Assert.assertEquals(z, builder.bitAt(60));
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeBits() {
        BitStringBuilder.of(true).bitsAt(0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void bitsOutOfString() {
        BitStringBuilder.of(true).bitsAt(0, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooManyBits() {
        BitStringBuilder.of(Collections.nCopies(100, true)).bitsAt(10, 65);
    }

    @Test
    public void testBits() {
        Random r = new Random(6356);
        boolean[] bits = new boolean[150];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bits.length; ++i) {
            bits[i] = r.nextBoolean();
            sb.append(bits[i] ? 1 : 0);
        }
        String rs = sb.reverse().toString();

        BitStringBuilder bs = BitStringBuilder.of(bits);

        for (int i = 0; i <= bs.length(); ++i) {
            Assert.assertEquals(0, bs.bitsAt(i, 0));
        }

        for (int nBits = 1; nBits <= 64; ++nBits) {
            for (int i = 0; i + nBits <= bs.length(); ++i) {
                int last = rs.length() - (i + nBits);
                long expected;
                if (nBits == 64) {
                    expected = Long.parseLong(rs.substring(last + 1, last + nBits), 2);
                    if (rs.charAt(last) == '1') {
                        expected ^= Long.MIN_VALUE;
                    }
                } else {
                    expected = Long.parseLong(rs.substring(last, last + nBits), 2);
                }
                Assert.assertEquals(expected, bs.bitsAt(i, nBits));
            }
        }
    }

    @Test
    public void cloneTest() {
        BitStringBuilder b = BitStringBuilder.of(true, false, true);
        BitStringBuilder c = new BitStringBuilder(b);
        b.appendBit(true);
        c.appendBit(false);
        Util.testBitBuilder("1011", b);
        Util.testBitBuilder("1010", c);
    }

    @Test
    public void ensureCapacityBug() {
        BitStringBuilder b = BitStringBuilder.of(true);
        b.append(BitString.of(Collections.nCopies(1000, true)));
    }

    @Test
    public void flipBit() {
        BitStringBuilder s1 = BitStringBuilder.empty();
        for (int i = 0; i < 128; ++i) {
            s1.appendBit((((i * 76252351) / 6151) & 1) == 0);
        }

        BitStringBuilder s2 = new BitStringBuilder(s1);

        Random r = new Random(2465246);
        for (int i = 0; i < 100; ++i) {
            int idx = r.nextInt(s1.length());
            s1.setBitAt(idx, !s1.bitAt(idx));
            Assert.assertEquals(s1.toGString(), s2.flipBitAt(idx).toGString());
        }
    }

    @Test
    public void serialization() throws Exception {
        boolean[] ar = new boolean[120];
        Random r = new Random(234235);
        for (int times = 0; times < 100; ++times) {
            for (int i = 0; i < ar.length; ++i) {
                ar[i] = r.nextBoolean();
            }
            BitStringBuilder str = BitStringBuilder.of(ar);

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

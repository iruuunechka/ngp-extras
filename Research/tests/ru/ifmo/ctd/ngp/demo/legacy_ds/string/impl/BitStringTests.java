package ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Some tests for {@link ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.ObjString}.
 *
 * @author Maxim Buzdalov
 */
public class BitStringTests {
    @Test
    public void emptyString() {
        BitString str = BitString.empty();
        Util.testBitString("", str);

        Assert.assertSame(str, str.substring(0));
        Assert.assertSame(str, str.substring(0, 0));
        Assert.assertEquals(0, str.asList().size());
        Assert.assertTrue(str.isEmpty());
    }

    @Test
    public void equalsHashCodeTest() {
        BitStringBuilder builder = BitStringBuilder.of(true, true, false);
        BitString s1 = builder.toGString();
        builder.append(false);
        builder.decreaseLength(2);
        builder.append(false);
        BitString s2 = builder.toGString();
        builder.append(false);
        BitString s3 = builder.toGString();

        Assert.assertEquals(s1, s2);
        Assert.assertEquals(s1.hashCode(), s2.hashCode());

        //noinspection SimplifiableJUnitAssertion
        Assert.assertFalse(s1.equals(s3));
        Assert.assertFalse(s1.hashCode() == s3.hashCode());
    }

    @Test
    public void oneCharacterString() {
        BitString str = BitString.of(true);
        Util.testBitString("1", str);

        Assert.assertEquals(0, str.substring(1).length());
        Assert.assertEquals(0, str.substring(0, 0).length());
        Assert.assertSame(str, str.substring(0));
        Assert.assertSame(str, str.substring(0, 1));
        Assert.assertFalse(str.isEmpty());

        List<Boolean> list = str.asList();

        Assert.assertEquals(1, list.size());
        Assert.assertEquals(true, list.get(0));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(expected = IllegalArgumentException.class)
    public void negativeIndex() {
        BitString.empty().charAt(-1);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(expected = IllegalArgumentException.class)
    public void tooLargeIndex() {
        BitString.of(true).charAt(1);
    }

    @Test(expected =  IllegalArgumentException.class)
    public void negativeSizeSubstring() {
        BitString.of(true, false, true).substring(2, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeStartSubstring() {
        BitString.of(true, false, true).substring(-1, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceedingSizeSubstring() {
        BitString.of(true, false, true).substring(2, 4);
    }

    @Test
    public void exactlyAtTheEndSubstring() {
        Util.testBitString("1", BitString.of(true, false, true).substring(2, 3));
    }

    @Test
    public void testCharAt() {
        boolean[] bits = new boolean[150];
        StringBuilder tsb = new StringBuilder();
        for (int i = 0; i < bits.length; ++i) {
            bits[i] = Integer.bitCount(i) % 2 == 0;
            tsb.append(bits[i] ? '1' : '0');
        }
        String ts = tsb.toString();
        BitString string = BitString.of(bits);
        Util.testBitString(ts, string);
        for (int l = 0; l <= string.length(); ++l) {
            for (int r = l; r <= string.length(); ++r) {
                Util.testBitString(ts.substring(l, r), string.substring(l, r));
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeBits() {
        BitString.of(true).bitsAt(0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void bitsOutOfString() {
        BitString.of(true).bitsAt(0, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooManyBits() {
        BitString.of(Collections.nCopies(100, true)).bitsAt(10, 65);
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

        BitString bs = BitString.of(bits);

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
    public void advancedHashCodeTest() {
        BitString s1 = BitString.of(true, false, false, true);
        BitString s2 = BitString.of(true, false, false, false);
        BitString s3 = BitString.of(false, false, false, true);

        Assert.assertEquals(s1.substring(0, 3).hashCode(), s2.substring(0, 3).hashCode());
        Assert.assertFalse(s1.substring(0, 3).hashCode() == s3.substring(0, 3).hashCode());
        Assert.assertEquals(s1.substring(1, 4).hashCode(), s3.substring(1, 4).hashCode());
        Assert.assertFalse(s2.substring(1, 4).hashCode() == s3.substring(1, 4).hashCode());
        Assert.assertEquals(s1.substring(1, 3).hashCode(), s2.substring(1, 3).hashCode());
        Assert.assertEquals(s1.substring(1, 3).hashCode(), s3.substring(1, 3).hashCode());
    }

    @Test
    public void moreAdvancedHashCodeTest() {
        BitStringBuilder builder = BitStringBuilder.empty();
        for (int i = 0; i < 140; ++i) {
            builder.appendBit((((i * 872347251) / 4151) & 1) == 0);
        }
        BitString s1 = builder.toGString();
        builder.flipBitAt(43);
        builder.flipBitAt(117);
        BitString s2 = builder.toGString();

        for (int l = 0; l <= s1.length(); ++l) {
            for (int r = l; r <= s1.length(); ++r) {
                BitString ss1 = s1.substring(l, r);
                BitString ss2 = s2.substring(l, r);
                Assert.assertEquals(ss1.equals(ss2), ss1.hashCode() == ss2.hashCode());
            }
        }
    }

    @Test
    public void testCompare() {
        //noinspection EqualsWithItself: this is intentional in unit tests :)
        Assert.assertEquals(0, BitString.empty().compareTo(BitString.empty()));
        Assert.assertEquals(1, BitString.of(false).compareTo(BitString.empty()));
        Assert.assertEquals(1, BitString.of(true).compareTo(BitString.empty()));
        Assert.assertEquals(-1, BitString.empty().compareTo(BitString.of(false)));
        Assert.assertEquals(-1, BitString.empty().compareTo(BitString.of(true)));
        Assert.assertEquals(1, BitString.of(true).compareTo(BitString.of(false)));
        Assert.assertEquals(-1, BitString.of(false).compareTo(BitString.of(true)));
        Assert.assertEquals(-1, BitString.of(false, false).compareTo(BitString.of(true)));
        Assert.assertEquals(1, BitString.of(true).compareTo(BitString.of(false, false)));
        Assert.assertEquals(1, BitString.of(true, true).compareTo(BitString.of(false)));
        Assert.assertEquals(-1, BitString.of(false).compareTo(BitString.of(true, true)));
        Assert.assertEquals(1, BitString.of(true, true).compareTo(BitString.of(true)));
        Assert.assertEquals(-1, BitString.of(true).compareTo(BitString.of(true, true)));
    }


    @Test(expected = UnsupportedOperationException.class)
    public void asListIsImmutable() {
        BitString bs = BitString.of(false, true);
        List<Boolean> list = bs.asList();
        list.set(0, true);
        Assert.assertFalse(bs.charAt(0));
    }
    
    @Test
    public void serialization() throws Exception {
        boolean[] ar = new boolean[120];
        Random r = new Random(234235);
        for (int times = 0; times < 100; ++times) {
            for (int i = 0; i < ar.length; ++i) {
                ar[i] = r.nextBoolean();
            }
            BitString str = BitString.of(ar);

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

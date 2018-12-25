package ru.ifmo.ctd.ngp.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.*;

/**
 * Some sanity checks for {@link FastRandom}.
 *
 * @author Maxim Buzdalov
 */
public class FastRandomTests {
    @Test
    public void checkRandomness() {
        boolean[] used = new boolean[1024];
        FastRandom rnd = new FastRandom(41242434224234L);
        for (int i = 0; i < 10240; ++i) {
            used[rnd.nextInt(used.length)] = true;
        }
        for (boolean u : used) {
            Assert.assertTrue(u);
        }
    }

    @Test
    public void setSeed() {
        long seed = 41242434224234L;
        FastRandom rnd = new FastRandom(seed);
        for (int i = 0; i < 10; ++i) {
            rnd.nextInt();
        }
        FastRandom rnd2 = new FastRandom(seed);
        rnd.setSeed(seed);
        for (int i = 0; i < 10; ++i) {
            Assert.assertEquals(rnd2.nextInt(), rnd.nextInt());
        }
    }
    
    @Test
    public void serialization() throws IOException, ClassNotFoundException {
        long seed = 824985243525L;
        FastRandom rnd1 = new FastRandom(seed);
        for (int i = 0; i < 10; ++i) {
            rnd1.nextInt();
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(rnd1);
        }

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        try (ObjectInputStream ois = new ObjectInputStream(in)) {
            rnd1 = (FastRandom) (ois.readObject());
        }

        FastRandom rnd2 = new FastRandom(seed);
        for (int i = 0; i < 10; ++i) {
            rnd2.nextInt();
        }
        
        for (int i = 0; i < 10; ++i) {
            Assert.assertEquals(rnd2.nextInt(), rnd1.nextInt());
        }
    }

    @Test
    public void emptyConstructor() {
        FastRandom rnd1 = new FastRandom();
        FastRandom rnd2 = new FastRandom();
        for (int i = 0; i < 10; ++i) {
            Assert.assertTrue(rnd1.nextInt() != rnd2.nextInt());
        }
    }

    @Test
    public void zigguratCorrectness() {
        int size = 100;
        int[] nxg = new int[size];
        int[] zig = new int[size];
        FastRandom fr = new FastRandom(1234234144);
        for (int i = 0; i < 1000000; ++i) {
            int nxgV = (int) (size / 2 + fr.nextGaussianSlow() * size / 2);
            int zigV = (int) (size / 2 + fr.nextGaussian() * size / 2);
            nxg[Math.max(0, Math.min(nxg.length - 1, nxgV))]++;
            zig[Math.max(0, Math.min(zig.length - 1, zigV))]++;
        }
        for (int i = 0; i < size; ++i) {
            if (Math.abs(nxg[i] - zig[i]) > 2 && Math.abs(nxg[i] - zig[i]) * 10 > Math.max(nxg[i], zig[i])) {
                System.err.println(Arrays.toString(nxg));
                System.err.println(Arrays.toString(zig));
                throw new AssertionError("Ziggurat does bad things");
            }
        }
    }
}

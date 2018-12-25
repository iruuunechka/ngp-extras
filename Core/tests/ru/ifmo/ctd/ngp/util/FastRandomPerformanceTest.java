package ru.ifmo.ctd.ngp.util;

import java.util.Random;

/**
 * Dumb performance test {@link FastRandom} vs {@link java.util.Random}.
 *
 * @author Maxim Buzdalov
 */
public class FastRandomPerformanceTest {
    private static void test(Random random, String name) {
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < 100000000; ++i) {
            random.nextInt();
        }
        System.out.println(name + ": " + (System.currentTimeMillis() - t0));
    }

    public static void main(String[] args) {
        Random r = new Random();
        FastRandom fr = new FastRandom();

        for (int i = 0; i < 3; ++i) {
            test(r, "java.util.Random");
        }
        for (int i = 0; i < 3; ++i) {
            test(fr, "FastRandom");
        }
    }
}

package ru.ifmo.ctd.ngp.util;

import java.lang.management.*;

/**
 * Performance tests for {@link ru.ifmo.ctd.ngp.util.Bits#bitCount}.
 *
 * @author Maxim Buzdalov
 */
public class BitCountPerformanceTest {
    private static final int ITERATIONS = 1000;

    private static final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
    static {
        bean.setThreadCpuTimeEnabled(true);
    }

    private static long time() {
        return bean.getCurrentThreadCpuTime();
    }

    private static long bitCountShort_Java() {
        long t0 = time();
        int sum = 0;
        int mask = (1 << 16) - 1;
        for (int times = 0; times <= ITERATIONS; ++times) {
            for (int i = Short.MIN_VALUE; i <= Short.MAX_VALUE; ++i) {
                sum += Integer.bitCount(i & mask);
                sum += Integer.bitCount((i + 1) & mask);
                sum += Integer.bitCount((i + 2) & mask);
                sum += Integer.bitCount((i + 3) & mask);
                sum += Integer.bitCount((i + 4) & mask);
            }
        }
        t0 += sum;
        return (time() + sum) - t0;
    }

    private static long bitCountInt_Java() {
        long t0 = time();
        int sum = 0;
        int mask = 0xAAAAAAAA;
        for (int times = 0; times <= ITERATIONS; ++times) {
            for (int i = mask; i != 0; i = (i - 1) & mask) {
                sum += Integer.bitCount(i);
                sum += Integer.bitCount(i + 1);
                sum += Integer.bitCount(i + 2);
                sum += Integer.bitCount(i + 3);
                sum += Integer.bitCount(i + 4);
            }
        }
        t0 += sum;
        return (time() + sum) - t0;
    }

    private static long bitCountLong_Java() {
        long t0 = time();
        long mask = 0xA00A0AAA000AA00AL;
        int sum = 0;
        for (int times = 0; times <= ITERATIONS; ++times) {
            long i = mask;
            do {
                sum += Long.bitCount(i);
                sum += Long.bitCount(i + 1);
                sum += Long.bitCount(i + 2);
                sum += Long.bitCount(i + 3);
                sum += Long.bitCount(i + 4);
            } while ((i = ((i - 1) & mask)) != 0);
        }
        t0 += sum;
        return (time() + sum) - t0;
    }

    private static long bitCountShort_Bits() {
        long t0 = time();
        int sum = 0;
        for (int times = 0; times <= ITERATIONS; ++times) {
            for (int i = Short.MIN_VALUE; i <= Short.MAX_VALUE; ++i) {
                sum += Bits.bitCount((short) (i));
                sum += Bits.bitCount((short) (i + 1));
                sum += Bits.bitCount((short) (i + 2));
                sum += Bits.bitCount((short) (i + 3));
                sum += Bits.bitCount((short) (i + 4));
            }
        }
        t0 += sum;
        return (time() + sum) - t0;
    }

    private static long bitCountInt_Bits() {
        long t0 = time();
        int mask = 0xAAAAAAAA;
        int sum = 0;
        for (int times = 0; times <= ITERATIONS; ++times) {
            for (int i = mask; i != 0; i = (i - 1) & mask) {
                sum += Bits.bitCount(i);
                sum += Bits.bitCount(i + 1);
                sum += Bits.bitCount(i + 2);
                sum += Bits.bitCount(i + 3);
                sum += Bits.bitCount(i + 4);
            }
        }
        t0 += sum;
        return (time() + sum) - t0;
    }

    private static long bitCountLong_Bits() {
        long t0 = time();
        long mask = 0xA00A0AAA000AA00AL;
        int sum = 0;
        for (int times = 0; times <= ITERATIONS; ++times) {
            long i = mask;
            do {
                sum += Bits.bitCount(i);
                sum += Bits.bitCount(i + 1);
                sum += Bits.bitCount(i + 2);
                sum += Bits.bitCount(i + 3);
                sum += Bits.bitCount(i + 4);
            } while ((i = ((i - 1) & mask)) != 0);
        }
        t0 += sum;
        return (time() + sum) - t0;
    }

    public static void main(String[] args) {
        System.out.println("Bit Count Performance Test");
        for (int i = 0; i < 4; ++i) {
            System.out.print("Short:");
            System.out.print(" Bits: " + bitCountShort_Bits() / 1000000);
            System.out.print(" Java: " + bitCountShort_Java() / 1000000);
            System.out.println();
            System.out.print("Int:  ");
            System.out.print(" Bits: " + bitCountInt_Bits() / 1000000);
            System.out.print(" Java: " + bitCountInt_Java() / 1000000);
            System.out.println();
            System.out.print("Long: ");
            System.out.print(" Bits: " + bitCountLong_Bits() / 1000000);
            System.out.print(" Java: " + bitCountLong_Java() / 1000000);
            System.out.println("\n");
        }
    }
}

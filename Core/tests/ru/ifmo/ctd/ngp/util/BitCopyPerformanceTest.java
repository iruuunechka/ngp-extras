package ru.ifmo.ctd.ngp.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Random;

/**
 * Performance tests for {@link Bits#copyBits(long[], int, long[], int, int)}.
 *
 * @author Maxim Buzdalov
 */
public class BitCopyPerformanceTest {
    private static final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
    static {
        bean.setThreadCpuTimeEnabled(true);
    }

    private interface BitCopier {
        void copy(long[] src, int srcOffset, long[] dst, int dstOffset, int length);
    }

    private static final BitCopier direct = (src, s, dst, d, length) -> {
        if (length > 0) {
            for (int i = d, j = 0, k = s; j < length; ++i, ++j, ++k) {
                long m = 1L << i;
                dst[i >>> 6] = (dst[i >>> 6] & ~m) | ((src[k >>> 6] >>> k) << i) & m;
            }
        }
    };

    private static final BitCopier bitsImplementation = Bits::copyBits;

    private static long time() {
        return bean.getCurrentThreadCpuTime();
    }

    private static long bitCopyTime(BitCopier copier) {
        long t0 = time();
        Random r = new Random(42523456);
        long[] src = new long[25];
        long[] dst = new long[30];
        for (int i = 0; i < src.length; ++i) {
            src[i] = r.nextLong();
        }

        for (int length = 0; length <= src.length * 64; length += 17) {
            for (int s = 0; s + length <= src.length * 64; s += 5) {
                for (int d = 0; d + length <= dst.length * 64; d += 7) {
                    copier.copy(src, s, dst, d, length);
                }
            }
        }

        return (time() - t0) / 1000000;
    }

    public static void main(String[] args) {
        System.out.println("Bit Copying Performance Test");
        for (int i = 0; i < 5; ++i) {
            long d, b;
            System.out.print("Direct: " + (d = bitCopyTime(direct)));
            System.out.print(" Bits: " + (b = bitCopyTime(bitsImplementation)));
            System.out.println(" Ratio: " + (double) (d) / b);
        }
    }
}

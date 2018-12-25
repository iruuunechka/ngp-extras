package ru.ifmo.ctd.ngp.theory.moearl;

import ru.ifmo.ctd.ngp.util.FastRandom;

import java.util.Arrays;
import java.util.Random;

/**
 * @author Irene Petrova
 */
public class BitMaskFitness implements BitFitness {
    private final boolean[] mask;
    private final int n;

    private boolean[] generateRandomMask(int n, int d) {
        boolean[] mask = new boolean[n];
        Random rand = FastRandom.threadLocal();
        if (d < n/2) {
            Arrays.fill(mask, true);
            for (int i = 0; i < d; i++) {
                int pos = rand.nextInt(n);
                while (!mask[pos]) {
                    pos = rand.nextInt(n);
                }
                mask[pos] = false;
            }
        } else {
            Arrays.fill(mask, false);
            for (int i = 0; i < n - d; i++) {
                int pos = rand.nextInt(n);
                while (mask[pos]) {
                    pos = rand.nextInt(n);
                }
                mask[pos] = true;
            }
        }
        return mask;
    }

    @Override
    public int calculate(boolean[] individual) {
        if (individual.length != n) {
            throw new RuntimeException("Length of individual does not correspond to mask length");
        }
        int count = 0;
        for (int i = 0; i < n; i++) {
            if (individual[i] == mask[i]) {
                count++;
            }
        }
        return count;
    }

    public BitMaskFitness(int n, int d) {
        this.mask = generateRandomMask(n, d);
        this.n = n;
    }
}

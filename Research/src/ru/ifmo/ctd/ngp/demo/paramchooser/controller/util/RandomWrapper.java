package ru.ifmo.ctd.ngp.demo.paramchooser.controller.util;

import org.apache.commons.math3.random.AbstractRandomGenerator;

import java.util.Random;

/**
 * @author Arkadii Rost
 */
public class RandomWrapper extends AbstractRandomGenerator {
    private final Random random;

    public RandomWrapper(Random random) {
        this.random = random;
    }

    @Override
    public void setSeed(int seed) {
        random.setSeed(seed);
    }

    @Override
    public void setSeed(long seed) {
        random.setSeed(seed);
    }

    @Override
    public void nextBytes(byte[] bytes) {
        random.nextBytes(bytes);
    }

    @Override
    public int nextInt() {
        return random.nextInt();
    }

    @Override
    public int nextInt(int n) {
        return random.nextInt(n);
    }

    @Override
    public long nextLong() {
        return random.nextLong();
    }

    @Override
    public boolean nextBoolean() {
        return random.nextBoolean();
    }

    @Override
    public float nextFloat() {
        return random.nextFloat();
    }

    @Override
    public double nextDouble() {
        return random.nextDouble();
    }

    @Override
    public double nextGaussian() {
        return random.nextGaussian();
    }
}

package ru.ifmo.ctd.ngp.demo.paramchooser.controller;

import java.util.Random;

/**
 * @author Arkadii Rost
 */
public interface Bounded {
    double getLowerBound();
    double getUpperBound();

    default boolean contains(double x) {
        return getLowerBound() <= x && x <= getUpperBound();
    }

	default double getRandomValue(Random rand) {
		return getLowerBound() + rand.nextDouble() * (getUpperBound() - getLowerBound());
	}
}

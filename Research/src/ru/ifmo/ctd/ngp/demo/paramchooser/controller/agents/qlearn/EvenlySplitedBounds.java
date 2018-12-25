package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Bounded;

import java.util.Random;

/**
 * @author Arkadii Rost
 */
public interface EvenlySplitedBounds extends Partition, Bounded {
    default double chooseParameterValue(Random rand, int split) {
	    double splitSize = (getUpperBound() - getLowerBound()) / getSplitCount();
        return getLowerBound() + splitSize * (split + rand.nextDouble());
    }
}

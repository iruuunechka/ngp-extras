package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn;

import java.util.Random;

/**
 * @author Arkadii Rost
 */
public interface Partition {
    int getSplitCount();

    double chooseParameterValue(Random rand, int split);
}

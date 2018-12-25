package ru.ifmo.ctd.ngp.theory.moearl;

import ru.ifmo.ctd.ngp.util.FastRandom;

import java.util.Random;

/**
 * @author Irene Petrova
 */
public class RandomAgent implements Agent {
    private final Random rand;
    private final int actionsCount;

    public RandomAgent(int actionsCount) {
        this.actionsCount = actionsCount;
        rand = FastRandom.threadLocal();
    }

    @Override
    public void updateExperience(int state, int newState, int action, int reward) {

    }

    @Override
    public int selectAction(int state) {
        return rand.nextInt(actionsCount);
    }
}

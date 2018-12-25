package ru.ifmo.ctd.ngp.theory.moearl;

import ru.ifmo.ctd.ngp.util.FastRandom;

import java.util.Random;

/**
 * @author Irene Petrova
 */
class BitStringMutation implements BitMutation {
    @Override
    public boolean[] mutate(boolean[] individual) {
        Random rand = FastRandom.threadLocal();
        boolean[] newIndividual = individual.clone();
        for (int i = 0; i < individual.length; ++i) {
            if (rand.nextInt(individual.length) == 0) {
                newIndividual[i] = !newIndividual[i];
            }
        }
        return newIndividual;
    }
}

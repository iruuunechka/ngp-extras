package ru.ifmo.ctd.ngp.theory.moearl;

import ru.ifmo.ctd.ngp.util.FastRandom;

import java.util.Random;

/**
 * @author Irene Petrova
 */
class OneBitMutation implements BitMutation {
    @Override
    public boolean[] mutate(boolean[] individual) {
        Random rand = FastRandom.threadLocal();
        boolean[] newIndividual = individual.clone();
        int pos = rand.nextInt(newIndividual.length);
        newIndividual[pos] = !newIndividual[pos];
        return newIndividual;
    }
}

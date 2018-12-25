package ru.ifmo.ctd.ngp.theory.moearl;

/**
 * @author Irene Petrova
 */
public class Individual {
    public final boolean[] individual;
    public final int[] fitness;

    public Individual(boolean[] individual, int[] fitness) {
        this.individual = individual;
        this.fitness = fitness;
    }
}

package ru.ifmo.ctd.ngp.theory.moearl;

import java.util.List;

/**
 * @author Irene Petrova
 */
public class SingleState implements State {
    @Override
    public int getCurrentState(List<Individual> population, int targetIndex) {
        return 1;
    }
}

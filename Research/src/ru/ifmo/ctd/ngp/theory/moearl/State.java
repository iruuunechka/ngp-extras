package ru.ifmo.ctd.ngp.theory.moearl;

import java.util.List;

/**
 * @author Irene Petrova
 */
public interface State {
    int getCurrentState(List<Individual> population, int targetIndex);
}

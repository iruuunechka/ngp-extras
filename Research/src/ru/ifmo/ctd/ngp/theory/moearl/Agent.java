package ru.ifmo.ctd.ngp.theory.moearl;

/**
 * @author Irene Petrova
 */
public interface Agent {
    void updateExperience(int state, int newState, int action, int reward);
    int selectAction(int state);
}

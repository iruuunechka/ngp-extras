package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.strategy;

/**
 * @author Irene Petrova
 */
public interface Strategy {
    double changeRandProbability(double probability, int step);

    Strategy make_clone();
    void refresh();
}

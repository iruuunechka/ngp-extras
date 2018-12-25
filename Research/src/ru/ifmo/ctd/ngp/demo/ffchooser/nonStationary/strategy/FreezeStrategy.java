package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.strategy;

import ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDef;

/**
 * @author Irene Petrova
 */
@SuppressWarnings({"unused"})
public class FreezeStrategy implements Strategy {

    private final double freeze;
    private final int freezeCou;
    private final int firstChange;
    private int change;
    private int curFreezeCou;
    private int curInterval;

    /**
     * Strategy of reducing a probability of choosing random action. Changes logarithmically.
     * @param freeze decreasing percent of probability(between 0 and 1)
     * @param firstChange first time when decreasing occurs
     * @param freezeCou number of decreasing
     */
    public FreezeStrategy(@ParamDef(name = "freeze") double freeze, @ParamDef(name = "firstChange") int firstChange, @ParamDef(name = "freezeCou") int freezeCou) {
        this.freeze = freeze;
        this.firstChange = firstChange;
        this.change = firstChange;
        this.curInterval = firstChange;
        this.freezeCou = freezeCou;
        this.curFreezeCou = freezeCou;
    }

    @Override
    public double changeRandProbability(double probability, int step) {
        if ((step != change) || (freezeCou <= 0)) {
            return probability;
        }
        curFreezeCou--;
        change += curInterval / 2;
        curInterval /= 2;
        double newProb = probability * (1 - freeze);
        return newProb >= 0 ? newProb : 0;
    }

    @Override
    public Strategy make_clone() {
        return new FreezeStrategy(freeze, firstChange, freezeCou);
    }

    @Override
    public String toString() {
        return "freeze strategy";
    }

    @Override
    public void refresh() {
        this.change = firstChange;
        this.curInterval = firstChange;
        this.curFreezeCou = freezeCou;
    }
}

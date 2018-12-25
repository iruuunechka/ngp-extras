package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.gecco;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Action;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.State;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.BaseTransition;

/**
 * @author Arkadii Rost
 */
public class ETransition<A extends Action, S extends State>
        extends BaseTransition<A, S>
{
    private double eligibilityRate;

    public ETransition(double reward, S fromState, A action, S toState) {
        super(reward, fromState, action, toState);
        eligibilityRate = 1;
    }

    public double getEligibilityRate() {
        return eligibilityRate;
    }

    public void setEligibilityRate(double eligibilityRate) {
        this.eligibilityRate = eligibilityRate;
    }
}

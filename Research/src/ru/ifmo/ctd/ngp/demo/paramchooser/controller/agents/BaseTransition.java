package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Action;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.State;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Transition;

/**
 * @author Arkadii Rost
 */
public class BaseTransition<A extends Action, S extends State>
        implements Transition<A, S>
{
    private final double reward;
    private final S fromState;
    private final A action;
    private final S toState;

    public BaseTransition(double reward, S fromState, A action, S toState) {
        this.fromState = fromState;
        this.action = action;
        this.toState = toState;
        this.reward = reward;
    }

    @Override
    public double getReward() {
        return reward;
    }

    @Override
    public S getFromState() {
        return fromState;
    }

    @Override
    public A getAction() {
        return action;
    }

    @Override
    public S getToState() {
        return toState;
    }
}

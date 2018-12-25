package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary;

import ru.ifmo.ctd.ngp.learning.reinforce.AbstractAgent;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.util.Map2;
import ru.ifmo.ctd.ngp.learning.util.Map3;

import java.util.List;

/**
 * /**
 * Implementation of the {@link AbstractAgent} that constructs the model of the environment
 *
 * @author Irene Petrova
 *
 * @param <S> the type of a state
 * @param <A> the type of an action
 */
public abstract class ModelAgent<S, A> extends AbstractAgent<S, A, ModelAgent<S, A>> {

    protected Map3<S, A, S, Double> T;
    protected Map2<S, A, Double> R;

    @Override
    protected ModelAgent<S, A> self() {
        return null;
    }

    @Override
    public void refresh() {

    }

    @Override
    public Agent<S, A> makeClone() {
        return null;
    }

    /**
     * Update model parameters
     * @param s the previous state
     * @param a the action in state s
     * @param s1 the current state
     * @param r the reward for action a
     */
    public abstract void updateModel(S s, A a, S s1, double r);

    /**
     * Update strategy of the {@link ModelAgent}
     * @param actions all possible actions
     */
    public abstract void updateStrategy(List<A> actions);

    /**
     * Choose the action in the current state
     * @param actions all possible actions
     * @param state the current state
     * @return the chosen action
     */
    public abstract A chooseAction(List<A> actions, S state);
}

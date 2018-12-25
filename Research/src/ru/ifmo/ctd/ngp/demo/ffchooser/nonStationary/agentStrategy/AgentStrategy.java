package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.agentStrategy;

import java.util.List;

/**
 * Learning strategy for model-based algorithm
 * @author Irene Petrova
 */
public interface AgentStrategy<S, A> {
    /**
     * Update strategy of the {@link ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.ModelAgent}
     * @param actions all possible actions
     */
    void updateStrategy(S s, A a, List<A> actions);

    /**
     * Choose the action in the current state
     * @param actions all possible actions
     * @param state the current state
     * @return the chosen action
     */
    A chooseAction(List<A> actions, S state);

    /**
     *
     * @return the number of steps of the algorithm
     */
    int getSteps();

    void refresh();
}

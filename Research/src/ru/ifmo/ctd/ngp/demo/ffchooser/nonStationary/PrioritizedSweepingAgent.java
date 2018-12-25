package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary;

import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.agentStrategy.PSStrategy;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.model.DynaModel;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.model.Model;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.strategy.Strategy;
import ru.ifmo.ctd.ngp.learning.reinforce.AbstractAgent;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.Environment;

import java.util.*;

/**
 * @author Irene Petrova
 */
public class PrioritizedSweepingAgent<S, A> extends AbstractAgent<S, A, PrioritizedSweepingAgent<S, A>> {

    private final Model<S, A> model;
    private final PSStrategy<S, A> agentStrategy;
    /**
     * Constructs {@link PrioritizedSweepingAgent} with the specified parameters
     * @param probability the probability of exploration
     * @param discount the discount factor
     * @param k the number of random state-action updates
     */
    public PrioritizedSweepingAgent(double probability, double discount, int k, Strategy strategy) {

        model = new DynaModel<>();
        agentStrategy = new PSStrategy<>(model, probability, discount, k, strategy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int learn(Environment<S, A> environment) {
        List<A> actions = environment.getActions();

        A a = environment.firstAction();

        while (!environment.isInTerminalState()) {
            S s = environment.getCurrentState();
            double r = environment.applyAction(a);
            S ss = environment.getCurrentState();

            model.updateModel(s, a, ss, r);
            agentStrategy.updateStrategy(s, a, actions);
            updatePrinters(environment);
            a = agentStrategy.chooseAction(actions, ss);
        }
        return agentStrategy.getSteps();
    }


    @Override
    public Agent<S, A> makeClone() {
        return new PrioritizedSweepingAgent<>(agentStrategy.getProbability(), agentStrategy.getDiscount(), agentStrategy.getK(), agentStrategy.getStrategy().make_clone());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("prioritizedSweeping-p%sd%sk%d", agentStrategy.getProbability(), agentStrategy.getDiscount(), agentStrategy.getK());
    }

    @Override
    protected PrioritizedSweepingAgent<S, A> self() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh() {
        model.refresh();
        agentStrategy.refresh();
    }

}

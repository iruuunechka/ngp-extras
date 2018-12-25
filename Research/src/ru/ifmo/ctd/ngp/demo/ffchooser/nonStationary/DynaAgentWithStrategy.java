package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary;

import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.agentStrategy.DynaStrategy;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.model.DynaModel;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.model.Model;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.strategy.Strategy;
import ru.ifmo.ctd.ngp.learning.reinforce.AbstractAgent;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.Environment;
import java.util.List;

/**
 * @author Irene Petrova
 */
public class DynaAgentWithStrategy<S, A> extends AbstractAgent<S, A, DynaAgentWithStrategy<S, A>> {

    private final Model<S, A> model;
    private final DynaStrategy<S, A> agentStrategy;


    /**
     * Constructs {@link DynaAgentWithStrategy} with the specified parameters
     * @param probability the probability of exploration
     * @param discount the discount factor
     * @param k the number of random state-action updates
     */
    public DynaAgentWithStrategy(double probability, double discount, int k, Strategy strategy) {
        model = new DynaModel<>();
        agentStrategy = new DynaStrategy<>(model, probability, discount, k, strategy);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh() {
        model.refresh();
        agentStrategy.refresh();
    }

    @Override
    public Agent<S, A> makeClone() {
        return new DynaAgentWithStrategy<>(agentStrategy.getProbability(), agentStrategy.getDiscount(), agentStrategy.getK(), agentStrategy.getStrategy().make_clone());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("dynaWithStrategy-p%sd%sk%d", agentStrategy.getProbability(), agentStrategy.getDiscount(), agentStrategy.getK());
    }

    @Override
    protected DynaAgentWithStrategy<S, A> self() {
        return this;
    }
}


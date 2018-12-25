package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary;

import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.agentStrategy.AgentStrategy;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.agentStrategy.DynaStrategy;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.model.RLCDModel;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.strategy.Strategy;
import ru.ifmo.ctd.ngp.learning.reinforce.AbstractAgent;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 @author Irene Petrova
 */

public class RLCD<S, A> extends AbstractAgent<S, A, RLCD<S, A>> {


    private RLCDModel<S, A> active;
    private final Map<RLCDModel<S, A>, AgentStrategy<S, A>> availableModels;
    private final double omega;
    private final double Emin;
    private final double rho;
    private final int M;
    private final double probability;
    private final double discount;
    private final int k;
    private final Strategy strategy;


    public RLCD(double probability, double discount, int k, Strategy strategy, double omega, double emin, double rho, int m) {
        this.omega = omega;
        Emin = emin;
        this.M = m;
        this.rho = rho;
        this.probability = probability;
        this.discount = discount;
        this.k = k;
        this.strategy = strategy;
        availableModels = new HashMap<>();
        active = new RLCDModel<>(omega, rho, M, availableModels.size());
        availableModels.put(active, new DynaStrategy<>(active, probability, discount, k, strategy.make_clone()));
    }

    private void updateActive() {
        for (RLCDModel<S, A> m : availableModels.keySet()) {
            if (m.getE() > active.getE()) {
                active = m;
            }
            //System.out.println("Updated to " + m.toString());
        }
        if (active.getE() < Emin) {
            //availableModels.remove(active);
            active = new RLCDModel<>(omega, rho, M, availableModels.size());
            availableModels.put(active, new DynaStrategy<>(active, probability, discount, k, strategy.make_clone()));
        }
    }

    @Override
    public int learn(Environment<S, A> environment) {
        A a = environment.firstAction();
        int steps = 0;
        while (!environment.isInTerminalState()) {
            S s = environment.getCurrentState();
            double r = environment.applyAction(a);
            S s1 = environment.getCurrentState();
            for (RLCDModel<S, A> m : availableModels.keySet()) {
                m.updateE(s, a, r, s1);
            }
            int oldSize = availableModels.size();
            updateActive();
            if (availableModels.size() > oldSize) {
                System.out.println("New models count:" + availableModels.size() + " " + steps);
            }
            active.updateModel(s, a, s1, r);
            System.out.println(active.getE());
//            if (steps == 200 || steps == 1800) {
//                System.out.println(availableModels.size());
//            }
            steps++;
            updatePrinters(environment);
            a = availableModels.get(active).chooseAction(environment.getActions(), s1);
        }
        return steps;
    }

    @Override
    public void refresh() {
//        for (RLCDModel m : availableModels.keySet()) {
//            m.refresh();
//            availableModels.get(m).refresh();
//        }
        availableModels.clear();
        active = new RLCDModel<>(omega, rho, M, availableModels.size());
        availableModels.put(active, new DynaStrategy<>(active, probability, discount, k, strategy.make_clone()));
    }

    @Override
    protected RLCD<S, A> self() {
        return this;
    }

    @Override
    public Agent<S, A> makeClone() {
        return new RLCD<>(probability, discount, k, strategy, omega, Emin, rho, M);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("rlcd-p%sd%sk%somega%srho%s", probability, discount, k, omega, rho);
    }

}

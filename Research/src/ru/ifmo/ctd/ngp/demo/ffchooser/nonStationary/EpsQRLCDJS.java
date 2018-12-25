package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary;

import java.util.List;
import java.util.Random;

import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.strategy.Strategy;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.OptAlgEnvironment;
import ru.ifmo.ctd.ngp.learning.reinforce.AbstractAgent;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.Environment;
import ru.ifmo.ctd.ngp.learning.util.Map2;
import ru.ifmo.ctd.ngp.learning.util.Maps;
import ru.ifmo.ctd.ngp.util.FastRandom;

/**
 * @author Irene Petrova
 */
public class EpsQRLCDJS<S, A> extends AbstractAgent<S, A, EpsQRLCDJS<S, A>> {

    private final double alpha;
    private final double gamma;
    private final double epsilon;
    private final Random rand;
    private final double minDiff;
    private final int localTimes;
    private int badLocalTimes;
    private double probability;
    private final Strategy strategy;
    private Map2<S, A, Double> activeQ;
    private int steps;
    private final int times;
    private int badTimes;
    private final int optimal;
    private final double minAlpha = 0.4;

    private final int maxSteps;
    private double curAlpha;
    private A startAction;

    public EpsQRLCDJS(double alpha, double gamma, double epsilon, double minDiff, Strategy strategy, int optimal, int maxSteps) {
        this.maxSteps = maxSteps;
        this.activeQ = new Map2<>(0.0);
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.steps = 0;
        this.rand = FastRandom.threadLocal();
        this.minDiff = minDiff;
        this.probability = epsilon;
        this.strategy = strategy;
        this.optimal = optimal;
        times = 10;
        localTimes = 6;
        badTimes = 0;
        badLocalTimes = 0;
        curAlpha = minAlpha;
    }
    @Override
    protected EpsQRLCDJS<S, A> self() {
        return this;
    }

    @Override
    public int learn(Environment<S, A> environment) {
        refresh();
        steps = 0;
        startAction = environment.firstAction();
        while(!environment.isInTerminalState()) {
            S state = environment.getCurrentState();
            A action = chooseAction(state, environment.getActions());
            updatePrinters(environment);

            double reward = environment.applyAction(action);

            S newState = environment.getCurrentState();
            double old = activeQ.get(state, action);
            activeQ.put(state, action, old + curAlpha * (reward +
                    gamma * Maps.max(activeQ, newState, environment.getActions()) - old));
            probability = strategy.changeRandProbability(probability, steps);
            updateActive(reward, environment);
            steps++;
            updateAlpha();
        }
        return steps;
    }

    private void updateAlpha() {
        curAlpha = Math.min(alpha, Math.max((double) steps / maxSteps, curAlpha));
    }

    private void updateActive(double reward, Environment<S, A> environment) {
        if (reward < 0) {
            badTimes++;
        } else if (reward > 0) {
            badLocalTimes = 0;
        } else if (reward == 0) {
            badLocalTimes++;
        }

        if (badTimes == times) {
            activeQ = new Map2<>(0.0);
            System.out.println(steps);
            curAlpha = minAlpha;
            badTimes = 0;
            badLocalTimes = 0;
        }

        if (badLocalTimes == localTimes ) {
            badLocalTimes = 0;
            if (environment instanceof OptAlgEnvironment) {
                if (rand.nextDouble() <= 0.5) {
                    curAlpha = minAlpha;
                    activeQ = new Map2<>(0.0);
                    System.out.println(steps);
                    badTimes = 0;
                }
            }
        }
    }

    protected A chooseAction(S state, List<A> actions) {
        if (startAction != null) {
            A a = startAction;
            startAction = null;
            return a;
        }
        if (Math.random() < probability) {
            return actions.get(rand.nextInt(actions.size()));
        } else {
            return Maps.argMax(activeQ, state, actions);
        }
    }

    @Override
    public void refresh() {
        this.activeQ = new Map2<>(0.0);
        strategy.refresh();
        badTimes = 0;
        badLocalTimes = 0;
        probability = epsilon;
        steps = 0;
        curAlpha = minAlpha;
    }

    @Override
    public Agent<S, A> makeClone() {
        return new EpsQRLCD<>(alpha, gamma, epsilon, minDiff, strategy.make_clone(), optimal);
    }

    @Override
    public String toString() {
        return "EpsQRLCDJSAgent" + localTimes + "gamma" + gamma + "alpha" + alpha;
    }

}

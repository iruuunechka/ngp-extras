package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.TSP;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MultiOptAlgEnvironment;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.strategy.Strategy;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.OptAlgEnvironment;
import ru.ifmo.ctd.ngp.learning.reinforce.AbstractAgent;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.Environment;
import ru.ifmo.ctd.ngp.learning.util.Map2;
import ru.ifmo.ctd.ngp.learning.util.Maps;
import ru.ifmo.ctd.ngp.util.FastRandom;

import java.util.List;
import java.util.Random;

/**
 * @author Irene Petrova
 */
public class EpsQRLCDTSP<S, A> extends AbstractAgent<S, A, EpsQRLCDTSP<S, A>> {

    private final double alpha;
    private final double gamma;
    private final double epsilon;
    private final Random rand;
    private final double minDiff;
    private final int localTimes;
    private final double problemMax;
    private int badLocalTimes;
    private double probability;
    private final Strategy strategy;
    private Map2<S, A, Double> activeQ;
    private int steps;
    private final int times;
    private int badTimes;
    private final double optimal;

    private final TSPProblem problem;

    public EpsQRLCDTSP(double alpha, double gamma, double epsilon, double minDiff, Strategy strategy, TSPProblem problem) {
        this.problem = problem;
        this.activeQ = new Map2<>(0.0);
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.steps = 0;
        this.rand = FastRandom.threadLocal();
        this.minDiff = minDiff;
        this.probability = epsilon;
        this.strategy = strategy;
        this.problemMax = problem.getMax();
        optimal = problem.solution;
        times = 10;
        localTimes = 10;
        badTimes = 0;
        badLocalTimes = 0;
    }

    @Override
    protected EpsQRLCDTSP<S, A> self() {
        return this;
    }

    @Override
    public int learn(Environment<S, A> environment) {
        refresh();
        steps = 0;
        while(!environment.isInTerminalState()) {
            S state = environment.getCurrentState();
            A action = chooseAction(state, environment.getActions());
            updatePrinters(environment);

            double reward = environment.applyAction(action);

            S newState = environment.getCurrentState();
            if (!newState.equals(state)) {
                if (environment instanceof OptAlgEnvironment) {
                    OptAlgEnvironment oaEnv = (OptAlgEnvironment) environment;
                    System.out.println("state: " + newState + " steps: " + steps + "fitness: " + oaEnv.getAlgorithm().getBestTargetValue());
                }
            }
            double old = activeQ.get(state, action);
            activeQ.put(state, action, old + alpha * (reward +
                    gamma * Maps.max(activeQ, newState, environment.getActions()) - old));
            probability = strategy.changeRandProbability(probability, steps);
            updateActive(reward, environment);
            steps++;
        }
        return steps;
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
            badTimes = 0;
            badLocalTimes = 0;
        }

        if (badLocalTimes == localTimes ) {
            badLocalTimes = 0;
            if (environment instanceof MultiOptAlgEnvironment) {
                MultiOptAlgEnvironment oaEnv = (MultiOptAlgEnvironment) environment;
                if (rand.nextDouble() <= (problemMax - oaEnv.getAlgorithm().getBestTargetValue()) / optimal - 1.0) {
                    activeQ = new Map2<>(0.0);
                    System.out.println(steps);
                    badTimes = 0;
                }
            }
        }
    }

    protected A chooseAction(S state, List<A> actions) {
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
    }

    @Override
    public Agent<S, A> makeClone() {
        return new EpsQRLCDTSP<>(alpha, gamma, epsilon, minDiff, strategy.make_clone(), problem);
    }

    @Override
    public String toString() {
        return "EpsQRLCDTSPAgent" + localTimes + "gamma" + gamma + "alpha" + alpha;
    }

}


package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.agent;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MultiOptAlgEnvironment;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.OptAlgEnvironment;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.q.QAgent;
import ru.ifmo.ctd.ngp.learning.util.Map2;
import ru.ifmo.ctd.ngp.learning.util.Maps;
import ru.ifmo.ctd.ngp.util.FastRandom;

import java.util.Random;

/**
 * @author Irene Petrova
 */
public class NonstationaryAgent<S, A> extends QAgent<S, A, NonstationaryAgent<S, A>> {

    private final double alpha;
    private final double gamma;
    private final double epsilon;
    private final Random rand;
    private int badTimes;
    private int badLocalTimes;      
    private final int times;
    private final int localTimes;
    private final double relearningEpsilon;
    private boolean relearned;
    private A curAction;


    public NonstationaryAgent(double alpha, double gamma, double epsilon, double relearningEpsilon) {

        super();

        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.relearningEpsilon = relearningEpsilon;

        this.Q = new Map2<>(0.0);
        this.rand = FastRandom.threadLocal();
        this.times = 10;
        this.localTimes = 10;
        this.badTimes = 0;
        this.badLocalTimes = 0;
        this.relearned = false;
        this.curAction = null;
    }

    @Override
    protected NonstationaryAgent<S, A> self() {
        return this;
    }

    @Override
    public Agent<S, A> makeClone() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected double makeStep(A action) {
        S state = environment.getCurrentState();
        double reward = environment.applyAction(action);
        S newState = environment.getCurrentState();
        if (!newState.equals(state)) {
            if (environment instanceof OptAlgEnvironment) {
                OptAlgEnvironment oaEnv = (OptAlgEnvironment) environment;
                System.out.println("state: " + newState + " steps: " + steps + "fitness: " + oaEnv.getAlgorithm().getBestTargetValue());
            }
        }
        double old = Q.get(state, action);
        Q.put(state, action, old + alpha * (reward +
                gamma * Maps.max(Q, newState, environment.getActions()) - old));
        updateActive(reward);

        return reward;
    }

    private void updateActive(double reward) {
        if (reward < 0) {
            badTimes++;
        } else if (reward > 0) {
            badLocalTimes = 0;
        } else if (reward == 0) {
            badLocalTimes++;
        }

        if (badTimes == times) {
            Q = new Map2<>(0.0);
            //System.out.println(steps);
            badTimes = 0;
            badLocalTimes = 0;
        }

        if (badLocalTimes == localTimes ) {
            badLocalTimes = 0;
            if (environment instanceof MultiOptAlgEnvironment) {
                MultiOptAlgEnvironment oaEnv = (MultiOptAlgEnvironment) environment;
                if (rand.nextDouble() <= relearningEpsilon) {//(problemMax - oaEnv.getAlgorithm().getBestTargetValue()) / optimal) {
                    Q = new Map2<>(0.0);
                    System.out.println(steps);
                    badTimes = 0;
                    relearned = true;
                }
            }
        }
    }

    @Override
    protected A chooseAction(S state) {
        if (relearned) {
            relearned = false;
            return curAction;
        }

        if (rand.nextDouble() < epsilon) {
            curAction = actions.get(rand.nextInt(actions.size()));
            return curAction;
        } else {
            curAction = Maps.argMax(Q, state, actions);
            return curAction;
        }
    }

    @Override
    public void refresh() {
        Q = new Map2<>(0.0);
    }

    @Override
    public String toString() {
        return "NonstationaryAgent"  + "gamma" + gamma + "alpha" + alpha + "eps" + epsilon;
    }
}

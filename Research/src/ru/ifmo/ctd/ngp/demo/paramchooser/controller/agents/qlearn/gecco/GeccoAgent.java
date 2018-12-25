package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.gecco;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.Partition;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.State;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.QAction;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.QTreeAgent;

import java.util.*;

/**
 * @author Arkadii Rost
 */
public class GeccoAgent<S extends State> extends QTreeAgent<S, ETransition<QAction, S>, GeccoState<S>> {
    private final Partition[] splits;
    private final double alpha;
    private final double threshold;
    private final double lambda;

    private final Set<GeccoState<S>> eligibleStates = new HashSet<>();

    public GeccoAgent(Partition[] splits, double threshold, double lambda, double eps, double alpha, double gamma,
		double actionScale, double obsResolution, double minSplitPercent, int splitLimit)
    {
        super(eps, alpha, gamma, actionScale, obsResolution, minSplitPercent, splitLimit);
        this.splits = splits;
        this.alpha = alpha;
        this.threshold = threshold;
        this.lambda = lambda;
    }

    @Override
    protected ETransition<QAction, S> createTransition(double reward, S lastState, QAction lastAction, S state) {
        return new ETransition<>(reward, lastState, lastAction, state);
    }

    @Override
    protected void updateTransitions(Random rand, double reward, S lastState, QAction lastAction, S state, GeccoState<S> uState)
    {
	    Collection<GeccoState<S>> oldStates = new ArrayList<>();
        for (GeccoState<S> eState : eligibleStates) {
            eState.decayTraces();
            if (!eState.isEligible())
                oldStates.add(eState);
        }
	    eligibleStates.removeAll(oldStates);
        eligibleStates.add(uState);
        super.updateTransitions(rand, reward, lastState, lastAction, state, uState);
    }

    @Override
    protected void updateQ(GeccoState<S> lastUState, QAction lastAction, double actionDelta) {
        for (GeccoState<S> state : eligibleStates) {
            for (ETransition<QAction, S> transition : state.getEligibleTransitions())
                state.updateQ(transition.getAction(), transition.getEligibilityRate() * actionDelta);
        }
    }

    @Override
    protected GeccoState<S> createUState(Random rand, GeccoState<S> prev, List<ETransition<QAction, S>> transitions) {
        if (prev == null)
            return new GeccoState<>(threshold, lambda, alpha, eps, splits);
        return new GeccoState<>(prev, transitions);
    }

    @Override
    protected void registerState(GeccoState<S> state) {
        if (state.isEligible())
            eligibleStates.add(state);
    }

    @Override
    protected void deregisterState(GeccoState<S> state) {
        eligibleStates.remove(state);
    }
}

package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.gecco;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.Partition;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.State;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.QAction;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.QState;

import java.util.*;

/**
 * @author Arkadii Rost
 */
public class GeccoState<S extends State> extends QState<S, ETransition<QAction, S>> {
    private final List<ETransition<QAction, S>> eligibleTransitions;
    private final double decayThreshold;
    private final double lambda;

    public GeccoState(double decayThreshold, double lambda, double alpha, double eps, Partition[] parameters) {
        super(alpha, eps, parameters);
        this.decayThreshold = decayThreshold;
        this.lambda = lambda;
        eligibleTransitions = new ArrayList<>();
    }

    public GeccoState(GeccoState<S> state, List<ETransition<QAction, S>> transitions) {
        this(state.decayThreshold, state.lambda, state.alpha, state.eps, state.splits);
        this.transitions.addAll(transitions);
        System.arraycopy(state.q, 0, q, 0, state.q.length);
        maxQ = state.maxQ;
        transitions.stream().filter(t -> t.getEligibilityRate() > decayThreshold).forEach(eligibleTransitions::add);
    }

    @Override
    public void updateTransitions(Random rand, ETransition<QAction, S> newTransition) {
        transitions.add(newTransition);
        eligibleTransitions.add(newTransition);
    }

    public void decayTraces() {
	    Collection<ETransition<QAction, S>> oldTransitions = new ArrayList<>();
        for (ETransition<QAction, S> transition : eligibleTransitions) {
            transition.setEligibilityRate(lambda * transition.getEligibilityRate());
            if (transition.getEligibilityRate() <= decayThreshold)
                oldTransitions.add(transition);
        }
	    eligibleTransitions.removeAll(oldTransitions);
    }

    public boolean isEligible() {
        return !eligibleTransitions.isEmpty();
    }

    public Collection<ETransition<QAction, S>> getEligibleTransitions() {
        return Collections.unmodifiableCollection(eligibleTransitions);
    }
}

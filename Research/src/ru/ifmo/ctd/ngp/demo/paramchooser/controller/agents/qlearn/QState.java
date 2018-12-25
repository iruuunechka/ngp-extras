package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.*;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.utree.UState;

import java.util.*;

/**
 * @author Arkadii Rost
 */
public class QState<S extends State, T extends Transition<QAction, S>> extends QLearner implements UState<QAction, S, T> {
	protected final List<T> transitions;

	public QState(double alpha, double eps, Partition[] splits) {
		super(alpha, eps, splits);
		transitions = new ArrayList<>();
	}

	@Override
	public double getEffectiveQ() {
		return maxQ;
	}

	@Override
	public Collection<T> getTransitions() {
		return Collections.unmodifiableList(transitions);
	}

	@Override
	public void updateTransitions(Random rand, T newTransition) {
		transitions.add(newTransition);
	}
}
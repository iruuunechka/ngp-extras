package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.utree;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Action;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Agent;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.State;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Transition;

import java.util.*;

/**
 * @author Arkadii Rost
 */
public abstract class SingleNodeAgent<A extends Action, S extends State, T extends Transition<A, S>,
	  U extends UState<A, S, T>, L> implements Agent<A, S, L>
{

	private U uState;

	@Override
	public void init(Random rand) {
		this.uState = createUState(rand);
	}

	protected abstract U createUState(Random rand);

	protected U getUState() {
		return uState;
	}

	@Override
	public A getParameterValues(Random rand, S state) {
		return uState.get(rand);
	}
}

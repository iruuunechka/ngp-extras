package ru.ifmo.ctd.ngp.demo.paramchooser.controller.watchmaker;

import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.PopulationData;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Action;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Agent;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.util.SimpleState;

import java.util.Random;

/**
 * @author Arkadii Rost
 */
public class WatchmakerController<G, A extends Action, L> implements EvolutionObserver<G> {
    private final Random rand;
    private final WatchmakerEnvironment<G, A> environment;
    private final Agent<A, SimpleState, L> agent;
	private SimpleState lastState;
    private A lastAction;

    public WatchmakerController(Random rand, WatchmakerEnvironment<G, A> environment, Agent<A, SimpleState, L> agent) {
        this.rand = rand;
        this.environment = environment;
        this.agent = agent;
    }

    @Override
    public void populationUpdate(PopulationData<? extends G> populationData) {
	    environment.setPopulationData(populationData);
        adjustParameters();
    }

	//todo serialize parameters here
	protected void adjustParameters() {
		SimpleState currentState = environment.getState();
		if (lastState == null) {
			agent.init(rand);
		} else {
			double reward = environment.getReward();
			agent.update(rand, reward, lastState, lastAction, currentState);
		}
		A currentAction = agent.getParameterValues(rand, currentState);
		environment.apply(currentAction);
		lastAction = currentAction;
		lastState = currentState;
	}
}

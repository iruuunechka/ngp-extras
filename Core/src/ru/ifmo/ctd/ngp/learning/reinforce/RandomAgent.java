package ru.ifmo.ctd.ngp.learning.reinforce;

import java.util.List;
import java.util.Random;

import ru.ifmo.ctd.ngp.util.FastRandom;


/**
 * Agent that chooses one of the possible actions with equal probability. 
 * 
 * @author Arina Buzdalova
 * 
 * @param <S> type of a state
 * @param <A> type of an action
 */
public class RandomAgent<S, A> extends AbstractAgent<S, A, RandomAgent<S, A>> {
    public RandomAgent() {}
	/**
	 * {@inheritDoc}
	 * Randomly chooses action at each generation.
	 */
	@Override
	public int learn(Environment<S, A> environment) {
		Random r = FastRandom.threadLocal();
		List<A> actions = environment.getActions();
		int times = 0;		
		while (!environment.isInTerminalState()) {
			int choice = r.nextInt(actions.size());
			environment.applyAction(actions.get(choice));
			times++;
		}		
		return times;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
	}

    @Override
    public Agent<S, A> makeClone() {
        return this;
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "random";
	}

	@Override
	protected RandomAgent<S, A> self() {
		return this;
	}
}

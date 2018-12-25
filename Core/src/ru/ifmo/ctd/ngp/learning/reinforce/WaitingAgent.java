package ru.ifmo.ctd.ngp.learning.reinforce;

import ru.ifmo.ctd.ngp.learning.util.Map2;
import ru.ifmo.ctd.ngp.learning.util.Maps;
import ru.ifmo.ctd.ngp.util.FastRandom;

import java.util.List;
import java.util.Random;

/**
 * Agent that implements the "waiting-for-the-change" algorithm for two objectives.
 * 
 * @author Arina Buzdalova
 *
 * @param <S> the type of states
 * @param <A> the type of actions
 */
public class WaitingAgent<S, A> extends AbstractAgent<S, A, WaitingAgent<S, A>> {
	private final Map2<S, A, Double> Q;

	public WaitingAgent() {
		Q = new Map2<>(0.0);
	}

	/**
	 * {@inheritDoc}
	 * Each execution doesn't consider previous experience. 
	 */
	@Override
	public int learn(Environment<S, A> environment) {
		refresh();
		S ss = environment.getCurrentState();		
        
		List<A> actions = environment.getActions();        
        for (A a : actions) {
        	Q.put(ss, a, 0.0);
        }

        Random rand = FastRandom.threadLocal();

        int helperCount = actions.size();
        int sinceChange = 0;
        int currentHelper = rand.nextInt(helperCount);
        int steps = 0;
        double prevReward = 0;

        int lastUpdate = 0;
        
		while (!environment.isInTerminalState()) {
			double curReward = environment.applyAction(actions.get(currentHelper));
			if (curReward != prevReward) {
				double update = (curReward - prevReward) /*/ (sinceChange + 1)*/;
				S s = environment.getCurrentState();
				prevReward = curReward;
				
				double oldSum = Q.get(s, actions.get(currentHelper))/*updatesNum[currentHelper]*/;
				double newSum = oldSum + update;
				Q.put(s, actions.get(currentHelper), newSum /*/ (double) ++updatesNum[currentHelper]*/);
				
				sinceChange = 0;
				currentHelper = actions.indexOf(Maps.argMax(Q, s, actions));
				lastUpdate = steps;
			} else {
				if (sinceChange >= lastUpdate) {
                    int old = currentHelper;
    			    currentHelper = rand.nextInt(helperCount - 1);
                    if (currentHelper >= old) {
                        currentHelper += 1;
                    }
					sinceChange = 1;
				} else {
					sinceChange++;
				}
			}
			
			steps++;
		}
		
		return steps;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
		Q.clear();
	}

    @Override
    public Agent<S, A> makeClone() {
        return new WaitingAgent<>();
    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "waitingAgent";
	}

	@Override
	protected WaitingAgent<S, A> self() {
		return this;
	}
}
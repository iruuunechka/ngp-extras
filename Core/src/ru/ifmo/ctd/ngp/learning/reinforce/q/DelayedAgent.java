package ru.ifmo.ctd.ngp.learning.reinforce.q;

import ru.ifmo.ctd.ngp.learning.reinforce.*;
import ru.ifmo.ctd.ngp.learning.util.Map2;
import ru.ifmo.ctd.ngp.learning.util.Maps;

/**
 * Implementation of the Delayed Q-learning algorithm
 * described in the <a href = "http://hunch.net/~jl/projects/RL/Delayed_Q/icml06.pdf">
 * "PAC model-free reinforcement learning"
 * by Alexander L. Strehl ,  Lihong Li ,  Eric Wiewiora and others</a>.
 * 
 * @author Arina Buzdalova
 *
 * @param <S> the type of a state
 * @param <A> the type of an action
 */
public class DelayedAgent<S, A> extends QAgent<S, A, DelayedAgent<S, A>> {
	private final double gamma;
	private final int m;
	private final double eps;
	private final int refresh;
	
	private Map2<S, A, Double> U;
	private Map2<S, A, Integer> counter;
	private Map2<S, A, Integer> time;
	private Map2<S, A, Boolean> learn;
	
	private int recentUpdate;
	private int t;
	
	/**
     * Constructs {@link DelayedAgent} with the specified parameters.
     * See {@link DelayedParameters} documentation for the explanation of the
     * parameters' meaning.
     *
     * @param reset the reset period, use {@code 0} for no-reset mode
     * @param gamma the discount factor
     * @param m the update period
     * @param eps the bonus reward
     *
     * @see DelayedParameters
     */
	public DelayedAgent(int reset, double gamma, int m, double eps) {
		this.refresh = reset;
		this.gamma = gamma;
		this.m = m;
		this.eps = eps;
	}
	
	/**
     * Constructs {@link DelayedAgent} with the parameters
     * taken from the specified {@link DelayedParameters}.
     *
     * @param reset the reset period, use {@code 0} for no-reset mode
     * @param parameters the specified {@link DelayedParameters}
     */
	public DelayedAgent(int reset, DelayedParameters parameters) {
		this.refresh = reset;
		this.gamma = parameters.getGamma();
		this.m = (int) (parameters.calcM() + 0.5);
		this.eps = parameters.getEps();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
		this.Q = new Map2<>(1 / (1 - gamma));
		this.U = new Map2<>(0.0);
		this.counter = new Map2<>(0);
		this.time = new Map2<>(0);
		this.learn = new Map2<>(true);
		this.recentUpdate = 0;
		this.t = 0;
	}

    @Override
    public Agent<S, A> makeClone() {
        return new DelayedAgent<>(refresh, gamma, m, eps);
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
	protected A chooseAction(S state) {
		return Maps.argMax(Q, state, actions);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double makeStep(A action) {
		t++;
		
		if (refresh != 0 && t % refresh == 0) {
			refresh();
		}
        S state = environment.getCurrentState();
		double reward = environment.applyAction(action);
        S sPrime = environment.getCurrentState();
		
		if (learn.get(state, action)) {
			double uSA = U.get(state, action);
			U.put(state, action, uSA + reward + gamma * Maps.max(Q, sPrime, environment.getActions()));
			int cSA = counter.get(state, action);
			counter.put(state, action, cSA + 1);
			
			if (counter.get(state, action) == m) {
				if (Q.get(state, action) - U.get(state, action) / m >= 2 * eps) {
					Q.put(state, action, U.get(state, action) / m + eps);
					recentUpdate = t;
				} else {
					if (time.get(state, action) >= recentUpdate) {
						learn.put(state, action, false);
					}
				}
				time.put(state, action, t);
				U.put(state, action, 0.0);
				counter.put(state, action, 0);
			}
		} else {
			if (time.get(state, action) < recentUpdate) {
				learn.put(state, action, true);
			}
		}	
		return reward;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "delayed-gamma" + gamma + "m" + m + "eps" + eps;
	}
	
	/**
	 * @return the gamma
	 */
	public double getGamma() {
		return gamma;
	}

	/**
	 * @return the update period m
	 */
	public int getM() {
		return m;
	}

	/**
	 * @return the epsilon
	 */
	public double getEps() {
		return eps;
	}
	
	/**
	 * @return the refresh period
	 */
	public int getRefresh() {
		return refresh;
	}

	protected Map2<S, A, Double> getU() {
		return U;
	}

	protected Map2<S, A, Integer> getCounter() {
		return counter;
	}

	protected Map2<S, A, Integer> getTime() {
		return time;
	}

	protected Map2<S, A, Boolean> getLearn() {
		return learn;
	}

	/**
	 * @return the "time" of the recent update
	 */
	public int getRecentUpdate() {
		return recentUpdate;
	}

	/**
	 * @return the "time"
	 */
	public int getT() {
		return t;
	}

	@Override
	protected DelayedAgent<S, A> self() {
		return this;
	}
}

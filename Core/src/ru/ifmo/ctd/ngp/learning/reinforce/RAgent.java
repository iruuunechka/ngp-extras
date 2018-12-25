package ru.ifmo.ctd.ngp.learning.reinforce;

import ru.ifmo.ctd.ngp.learning.util.Map2;
import ru.ifmo.ctd.ngp.learning.util.Maps;

import java.util.List;
import java.util.Random;

/**
 * Agent that implements R-learning -- model-free algorithm that maximizes average reward 
 * (unlike Q-learning that maximizes discounted reward).
 * The epsilon-greedy exploration strategy is used.
 * 
 * @author Arina Buzdalova
 *
 * @param <S> the type of states
 * @param <A> the type of actions
 */
public class RAgent<S, A> extends AbstractAgent<S, A, RAgent<S, A>> {
	private Map2<S, A, Double> R;
	private double rho;	
	
	private final double alpha;
	private final double beta;
	private final double eps;
	
	private final Random rand;
	
	/**
	 * 
	 * @param alpha the learning rate for the average reward
	 * @param beta the learning rate for the (state, action) quality
	 * @param eps the exploration probability
	 */
	public RAgent(double alpha, double beta, double eps) {
		this.alpha = alpha;
		this.beta = beta;
		this.eps = eps;
		this.rand = new Random();
	}

	/**
	 * {@inheritDoc}
	 * Each execution doesn't consider previous experience. 
	 */
	@Override
	public int learn(Environment<S, A> environment) {
		R = new Map2<>(0.0);
		rho = 0;
		S s1 = environment.getCurrentState();
        List<A> actions = environment.getActions();
		int steps = 0;
		
		while (!environment.isInTerminalState()) {
			A a = chooseAction(actions, s1);
			Double r = environment.applyAction(a);
			S s2 = environment.getCurrentState();
			
			Double R1 = R.get(s1, a);
			Double maxR1 = Maps.max(R, s1, actions);
			Double maxR2 = Maps.max(R, s2, actions);
			
			R.put(s1, a, (1 - beta) * R1 + beta * (r - rho + maxR2));
			rho = (1 - alpha) * rho + alpha * (r + maxR2 - maxR1);	

            s1 = s2;

			steps++;
		}
		
		return steps;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
		if (R != null) {
			R.clear();
		}
		rho = 0;
	}

    @Override
    public Agent<S, A> makeClone() {
        return new RAgent<>(alpha, beta, eps);
    }

    private A chooseAction(List<A> actions, S state) {
		if (rand.nextDouble() < eps) {
			return actions.get(rand.nextInt(actions.size()));
		} else {
			return Maps.argMax(R, state, actions);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "rLearning-alpha" + alpha + "beta" + beta + "eps" + eps;
	}

	@Override
	protected RAgent<S, A> self() {
		return this;
	}
}

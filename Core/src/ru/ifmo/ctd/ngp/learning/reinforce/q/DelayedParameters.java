package ru.ifmo.ctd.ngp.learning.reinforce.q;

/**
 * <p>
 * Class for calculating parameters and characteristics of 
 * Delayed Q-Learning algorithm described in the <a href = "http://hunch.net/~jl/projects/RL/Delayed_Q/icml06.pdf">
 * "PAC model-free reinforcement learning" by Alexander L. Strehl ,  Lihong Li ,  Eric Wiewiora and others</a>.
 * </p><p>
 * Parameters of the Delayed Q-Learning:
 * <ul>
 * <li><b>Discount factor gamma.</b> The discount factor determines the importance of future rewards. 
 * A factor of 0 will make the agent "opportunistic" by only considering current rewards, 
 * while a factor approaching 1 will make it strive for a long-term high reward. 
 * If the discount factor meets or exceeds 1, the Q values will diverge.</li>
 * 
 * <li><b>Bonus reward epsilon.</b> The bonus reward is added to Q at each update. 
 * It provides the property of optimism, which is useful for safe exploration.</li>
 * 
 * <li><b>Failure probability delta.</b> The probability of inefficient performance. 
 * The amount of learning experience is less than some polynomial in
 * the relevant quantities (S, A, 1 / epsilon, 1 / delta, 1 / (1 - gamma)), 
 * with probability at least 1 - delta.</li>
 * 
 * <li><b>Update period m.</b> The number of steps, during which the update value for Q is collected.
 * Update period can be calculated using the previous parameters.</li>
 * </ul>
 * </p>
 * @author Arina Buzdalova
 * @see DelayedAgent
 */
public class DelayedParameters {
	private final int s;
	private final int a;
	private double gamma;
	private double eps;
	private double delta;
	
	/**
	 * Constructs {@link DelayedParameters} with the
	 * specified number of states and actions
	 * 
	 * @param states the number of states
	 * @param actions the number of actions
	 */
	public DelayedParameters(int states, int actions) {
		this.s = states;
		this.a = actions;
	}
	
	/**
	 * Calculates the update period m basing
	 * on the current values of the discount factor gamma,
	 * the bonus reward epsilon and the failure probability delta.
	 * 
	 * These parameters should be set before calculating m.
	 * 
	 * @return the update period m
	 */
	public double calcM() {
		return 
			Math.log(3 * s * a *(1 + s * a * calcK()) / delta) / 
			(2 * eps * eps * (1 - gamma) * (1 - gamma));
	}
	
	/**
	 * Estimates the number of learning steps 
	 * needed to reach near-optimal behavior.
	 * 
	 * The estimation is polynomial in the relevant quantities 
	 * <code>(S, A, 1 / epsilon, 1 / delta, 1 / (1 - gamma))</code>.
	 * 
	 * All parameters should be set before its calculating.
	 * 
	 * @return estimation of the sample complexity
	 */
	public double learningEstimate() {
		return 
			s * a / (Math.pow(1 - gamma, 8) * Math.pow(eps, 4)) *
			Math.log(1.0 / delta) * 
			Math.log(1.0 / (eps * (1 - gamma))) *
			Math.log(s * a / (delta * eps * (1.0 - gamma)));
	}

	/**
	 * Calculates maximum number of successful updates
	 * of a fixed state-action pair. 
	 * 
	 * Gamma and epsilon should be set.
	 * 
	 * @return maximum number of successful updates
	 * of a fixed state-action pair
	 */
	public double calcK() {
		return 1.0 / ((1.0 - gamma) * eps);
	}
	/**
	 * @return the discount factor gamma
	 */
	public double getGamma() {
		return gamma;
	}

	/**
	 * @param gamma the discount factor gamma to set
	 */
	public void setGamma(double gamma) {
		this.gamma = gamma;
	}

	/**
	 * @return the bonus reward epsilon
	 */
	public double getEps() {
		return eps;
	}

	/**
	 * @param eps the bonus reward epsilon to set
	 */
	public void setEps(double eps) {
		this.eps = eps;
	}

	/**
	 * @return the failure probability delta
	 */
	public double getDelta() {
		return delta;
	}

	/**
	 * @param delta the failure probability delta to set
	 */
	public void setDelta(double delta) {
		this.delta = delta;
	}
	
	/**
	 * Test method used to calculate parameters
	 * @param args are not used
	 */
	public static void main(String[] args) {
		DelayedParameters param = new DelayedParameters(2, 3);
		param.setDelta(0.2);
		param.setGamma(0.05);
		param.setEps(0.9);
		
		System.out.println("Update period: " + param.calcM() + " steps");
		System.out.println("Steps for learning estimate: " + param.learningEstimate());
	}
}

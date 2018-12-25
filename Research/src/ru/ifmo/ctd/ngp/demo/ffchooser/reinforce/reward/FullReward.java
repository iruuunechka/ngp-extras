package ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward;

import ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDef;

/**
 * {@link RewardCalculator} that calculates difference between total fitness of entire generations
 * 
 * All criteria, continuous reward, all individuals, global.
 * 
 * @author Arina Buzdalova
 */
public class FullReward extends PositiveFullReward {
	private static final long serialVersionUID = 7664925525501710364L;

	/**
	 * Constructs {@link PositiveFullReward} with the factor for the supporting fitness evaluators
	 * @param discount the factor for the supporting fitness evaluators
	 */
	public FullReward(@ParamDef(name = "discount") double discount) {
		super(discount);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double diff(double[] prevSum, double[] newSum, int target, int eval) {
		double diff = 0;
		//System.out.print("D: ");
		for (int i = 0; i < eval; i++) {
			double value = (newSum[i] - prevSum[i]);// / Math.max(1, Math.abs(prevSum[i]));
			
			double added = i == target ? value : discount * value;
			//System.out.print(i + ") " + added + " ");
			diff += added;
		}
		//System.out.println(" S: " + diff);
		return diff;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return String.format("full-negative%.3f", discount);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(discount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PositiveFullReward other = (FullReward) obj;
        return Double.doubleToLongBits(discount) == Double.doubleToLongBits(other.discount);
    }
}

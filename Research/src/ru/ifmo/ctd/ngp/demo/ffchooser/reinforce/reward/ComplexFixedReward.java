package ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward;

import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.OptAlgEnvironment;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDef;

/**
 * Like {@link FixedBestReward}, but takes into account all the parameters.
 * 
 * All criteria, discrete reward, best individual, global.
 * 
 * @author Arina Buzdalova
 *
 */
public class ComplexFixedReward implements RewardCalculator {
	private static final long serialVersionUID = -682989392757684905L;
	private final double negative;
	private final double zero;
	private final double positive;
	private final double discount;

	/**
	 * Constructs {@link FixedBestReward} reward calculator with the
	 * specified rewards for different kind of a parameter change
	 * 
	 * @param negative reward for a negative change of a parameter
	 * @param zero reward for a zero change of a parameter
	 * @param positive reward for a positive change of a parameter
	 * @param discount discount factor for subordinate parameters
	 */
	public ComplexFixedReward(@ParamDef(name = "negative") double negative, @ParamDef(name = "zero") double zero, 
			@ParamDef(name = "positive") double positive, @ParamDef(name = "discount") double discount) {
		this.negative = negative;
		this.zero = zero;
		this.positive = positive;
		this.discount = discount;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculate(OptAlgEnvironment<?, ?> environment) {
		int target = environment.getAlgorithm().getTargetParameter();
		List<Double> prevValues = environment.getAlgorithm().getCurrentBest();
		List<Double> newValues = environment.getAlgorithm().computeValues();	
		double delta = 0;
		for (int i = 0, size = environment.actionsCount(); i < size; i++) {
			double value = newValues.get(i) -	prevValues.get(i);
			delta += 
					i == target 
					? value
					: discount * value;
		}
		
		if (delta < 0) {
			return negative;
		}
		
		if (delta == 0) {
			return zero;
		}
		
		if (delta > 0) {
			return positive;
		}
		
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return String.format("fixed%.2fn%.2fz%.2fp%.3fd", negative, zero, positive, discount);
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
		temp = Double.doubleToLongBits(negative);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(positive);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(zero);
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
		ComplexFixedReward other = (ComplexFixedReward) obj;
        return Double.doubleToLongBits(discount) == Double.doubleToLongBits(other.discount)
                && Double.doubleToLongBits(negative) == Double.doubleToLongBits(other.negative)
                && Double.doubleToLongBits(positive) == Double.doubleToLongBits(other.positive)
                && Double.doubleToLongBits(zero) == Double.doubleToLongBits(other.zero);
    }
}

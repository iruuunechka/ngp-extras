package ru.ifmo.ctd.ngp.demo.paramchooser.controller.watchmaker;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.EvenlySplitedBounds;

/**
 * @author Arkadii Rost
 */
public class SplittedParameter extends ParameterNumberGenerator implements EvenlySplitedBounds {
	private final int splitCount;

	public SplittedParameter(String description, double lowerBound, double upperBound, double initialValue, int splitCount) {
		super(description, lowerBound, upperBound, initialValue);
		this.splitCount = splitCount;
	}

	@Override
	public int getSplitCount() {
		return splitCount;
	}
}

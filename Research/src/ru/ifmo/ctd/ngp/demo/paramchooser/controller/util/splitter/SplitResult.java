package ru.ifmo.ctd.ngp.demo.paramchooser.controller.util.splitter;

/**
 * @author Arkadii Rost
 */
public class SplitResult {
	private final double splitPoint;
	private final double pValue;

	public SplitResult(double splitPoint, double pValue) {
		this.splitPoint = splitPoint;
		this.pValue = pValue;
	}

	public double getSplitPoint() {
		return splitPoint;
	}

	public double getpValue() {
		return pValue;
	}
}
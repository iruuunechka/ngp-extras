package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.adaptive.earpc;

/**
 * @author Arkadii Rost
 */
public class EarpcLogState {
	private final double[] splitPoints;
	private final double[] parameters;

	public EarpcLogState(double[] splitPoints, double[] parameters) {
		this.splitPoints = splitPoints;
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < splitPoints.length; i++) {
			sb.append(parameters[i]).append("\t");
			sb.append(splitPoints[i]).append("\t");
		}

		return sb.toString();
	}
}

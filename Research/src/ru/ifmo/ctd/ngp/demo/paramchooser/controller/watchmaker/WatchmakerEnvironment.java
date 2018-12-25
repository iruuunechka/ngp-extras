package ru.ifmo.ctd.ngp.demo.paramchooser.controller.watchmaker;

import org.uncommons.watchmaker.framework.PopulationData;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Action;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Environment;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Parameter;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.util.SimpleState;

/**
 * @author Arkadii Rost
 */
public class WatchmakerEnvironment<G, A extends Action> implements Environment<A, SimpleState>
{
	private final Parameter[] parameters;
	private final double c;
	private final boolean isNatural;
	private int stagnation;
	private double fitnessDeviation;
	private double fitnessDelta;
	private double reward;
	private PopulationData<? extends G> prevPopulationData;

	public WatchmakerEnvironment(Parameter[] parameters, double c, boolean isNatural) {
		this.parameters = parameters;
		this.c = c;
		this.isNatural = isNatural;
	}

	@Override
	public Parameter[] getParameters() {
		return parameters;
	}

	@Override
	public double getReward() {
		return reward;
	}

	@Override
	public SimpleState getState() {
		double[] paramValues = new double[parameters.length];
		for (int i = 0; i < paramValues.length; i++)
			paramValues[i] = parameters[i].getValue();

		double[] observables = new double[] {
			  fitnessDelta,
			  fitnessDeviation,
			  stagnation,
		};
		return new SimpleState(observables, paramValues);
	}

	public void setPopulationData(PopulationData<? extends G> data) {
		if (prevPopulationData != null) {
			int elapsedIterationNumber = data.getGenerationNumber() - prevPopulationData.getGenerationNumber();
			// reward
			double fitnessRatio = isNatural ? data.getMeanFitness() / prevPopulationData.getMeanFitness()
				  : prevPopulationData.getMeanFitness() / data.getMeanFitness();
			reward = c * (fitnessRatio - 1) / elapsedIterationNumber;
			// fitness change
			fitnessDelta = data.getMeanFitness() - prevPopulationData.getMeanFitness();
			// stagnation
			if (data.getBestCandidateFitness() == prevPopulationData.getBestCandidateFitness()) {
				stagnation += elapsedIterationNumber;
			} else {
				stagnation = 0;
			}
			// double diversity
			fitnessDeviation = data.getFitnessStandardDeviation();
		}
		prevPopulationData = data;
	}
}

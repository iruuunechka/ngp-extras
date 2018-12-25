package ru.ifmo.ctd.ngp.demo.ffchooser.genetic;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

public class AuxCriterionEvolutionStrategy implements EvolutionaryAlgorithm<BitString>{

	@Override
	public void changeCriterion(int index) {
		// TODO Auto-generated method stub
    }

	@Override
	public List<Double> computeValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTargetParameter() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCurrentCriterion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int parametersCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getBestTargetValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIterationsNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Double> getCurrentBest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<List<Double>> getCurrentPoints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPrinter(Printer<? super BitString> printer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePrinter(Printer<? super BitString> printer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStartPopulation(List<BitString> seedPopulation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getTargetValueInCurrentBest() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getFinalBestTargetValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setLength(int length) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEvaluator(int index, FitnessEvaluator<? super BitString> evaluator) {
		// TODO Auto-generated method stub
		
	}

}

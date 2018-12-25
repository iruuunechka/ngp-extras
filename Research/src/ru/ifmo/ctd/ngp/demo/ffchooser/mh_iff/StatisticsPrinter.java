package ru.ifmo.ctd.ngp.demo.ffchooser.mh_iff;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.Printer;

public class StatisticsPrinter<T> implements Printer<T> {
	private final int target;
	private final Set<T> bests;
	private double bestFitness;
	
	public StatisticsPrinter(int target) {
		this.target = target;
		this.bests = new HashSet<>();
		this.bestFitness = 0;
	}

	@Override
	public void print(List<Double> values, T bestIndividual, int iterations,
			int curEvaluatorIndex) {
		double fitness = values.get(target);
		
		//System.err.print(curEvaluatorIndex);
		
		//System.out.println(bestIndividual);
		//System.out.println(values);
		
		if (fitness < bestFitness) {
			return;
		}
		
		if (fitness > bestFitness) {
			//System.out.println(bestIndividual);
			bestFitness = fitness;
			bests.clear();
		}
		
		bests.add(bestIndividual);
	}

	@Override
	public void println(String info) {
		throw new UnsupportedOperationException();
	}
	
	public double getBestFitness() {
		return bestFitness;
	}
	
	public Set<T> getBestsIndividuals() {
		return bests;
	}
	
	public void refresh() {
		bests.clear();
		bestFitness = 0;
	}

}

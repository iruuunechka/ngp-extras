package ru.ifmo.ctd.ngp.demo.ffchooser.genetic;

import org.uncommons.watchmaker.framework.FitnessEvaluator;
import ru.ifmo.ctd.ngp.demo.ffchooser.Utils;
import ru.ifmo.ctd.ngp.demo.ffchooser.xvector.BitCountFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.xvector.ExpFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.xvector.IntFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.xvector.PowerFitness;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitStringBuilder;

import java.util.Collections;

/**
 * Measures performance of genetic algorithm
 * solving bit count problem with different fitness functions
 * 
 * @author Arina Buzdalova
 */
public class BitCountConvergence {
	
	public static void main(String[] args) {
		int times = 20;
		int length = 100;
		
		BitStringBuilder maxStrBuilder = new BitStringBuilder();
		for (int i = 0; i < length; i++) {
			maxStrBuilder.append(true);
		}
		
		BitString maxString = maxStrBuilder.toGString();
		
		System.out.println("count bits: " + average(times, length, new BitCountFitness(1.0, 0), length) + " steps");
		
		double cross = 80.0;
		double k = 10000;
		double divider = 5;
		
		FitnessEvaluator<? super BitString> exp = new IntFitness(new ExpFitness(k, 2.0, 1.0 / cross, -k));
		System.out.println("(int) exp: " + average(times, length, exp, exp.getFitness(maxString, null)) + " steps");
		
		FitnessEvaluator<? super BitString> power = new IntFitness(new PowerFitness(k * k * (1.0 / cross), 1.0 / 2, 0));
		System.out.println("(int) power: " + average(times, length, power, power.getFitness(maxString, null)) + " steps");
		
		FitnessEvaluator<? super BitString> intBits = new IntFitness(new BitCountFitness(1.0 / divider, 0));
		System.out.println("(int) bits /" + divider + ": " + average(times, length, intBits, length / divider) + " steps");
	}
	
	/**
     * Counts average number of steps used by {@link EvolutionaryAlgImpl}
     * to evolve individual with the specified ideal fitness
     * @param times the number of times the number of steps is measured
     * @param length the length of the string being evolved
     * @param evaluator the fitness function evaluator
     * @param maxFitness the ideal fitness
     * @return number of steps taken to evolve individual with {@code maxFitness}
     */
	private static int average(int times, int length, FitnessEvaluator<? super BitString> evaluator, double maxFitness) {
		int sum = 0;
		int genSize = 100;
		
		EvolutionaryAlgImpl<BitString> ga = StringGeneticAlgorithm.newStringGA(length, 0, 0,
                Collections.singletonList(evaluator));
		ga.setGenerationSize(genSize);
		
		for (int i = 0; i < times; i++) {
			sum += Utils.countSteps(ga, maxFitness, 10000);
		}
		
		return sum / times;
	}	
}

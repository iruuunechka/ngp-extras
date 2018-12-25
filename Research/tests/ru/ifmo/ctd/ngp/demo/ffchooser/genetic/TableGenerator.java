package ru.ifmo.ctd.ngp.demo.ffchooser.genetic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.uncommons.maths.random.Probability;
import org.uncommons.maths.statistics.EmptyDataSetException;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;

import ru.ifmo.ctd.ngp.demo.util.strings.GStringXMutation;
import ru.ifmo.ctd.ngp.demo.util.strings.ShiftCrossover;
import ru.ifmo.ctd.ngp.demo.ffchooser.GenerationsCounter;
import ru.ifmo.ctd.ngp.demo.ffchooser.StatisticsData;
import ru.ifmo.ctd.ngp.demo.ffchooser.TablePrinter;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print.CompactPrinter;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.ReinforcementCounter;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.MultiDiffReward;
import ru.ifmo.ctd.ngp.demo.ffchooser.utils.GeneticUtils;
import ru.ifmo.ctd.ngp.demo.ffchooser.xvector.BitCountFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.xvector.ExpFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.xvector.IntFitness;
import ru.ifmo.ctd.ngp.demo.generators.SetMemberGenerator;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;
import ru.ifmo.ctd.ngp.demo.util.ResultViewer;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.q.DelayedAgent;
import ru.ifmo.ctd.ngp.util.CollectionsEx;

public class TableGenerator {
	
	private final static int maxSteps = 100000;
	private final static int times = 500;
	private final static int generationSize = 100;
	private final static int eliteCount = 5;
	private final static double mutationProbab = 0.03;
	private final static double crossoverProbab = 0.7;
	private final static boolean zeroStart = true;
	
	private static boolean learning = true;
	private final static double epsilon = 0.4;
	private final static double cross = 80.0;
	private final static double k = 10000;
	
	private final static int[] len = {100};
	private final static int[] div = {10};
	
	/**
	 * Uses {@link GACounter} with normal, non-incremental genetic algorithms inside
	 * and {@link ReinforcementCounter} with incremental genetic algorithm.
	 * 
	 * Generates tables (N = string length) x (K = divider) -> (value),
	 * where value can be 
	 * 
	 * <ul>
	 * <li>average number of steps;</li>
	 * <li>expectation of number of steps;</li>
	 * <li>maximal (minimal) number of steps;</li>
	 * <li>median.</li>
	 * </ul>
	 * 
	 * If the number of steps is greater than some constant, 
	 * then the calculation of the current value is interrupted.
	 * 
	 * @param args are not used
	 */
	public static void main(String[] args) {
		learning = true;
		run();
//		learning = false;
//		run();
	}
	
	private static void run() {
		String[] names = {"Average", "Expectation", "Maximum", "Minimum", "Median"};
		
		String comment = learning ? "($\\epsilon$-greedy Q-learning; $\\epsilon$ = " + epsilon + "; " : "(no learing; ";

		if (zeroStart) {
			comment += "\"0\" start population; ";
		}

		comment += 	"times = " + times + 
					"; tournament selecion" +
					"; elite count = " + eliteCount +
					"; generation size = " + generationSize + 
					"; steps limit = " + maxSteps +
					"; mutation probability: " + mutationProbab * 100 + "\\%" +
					"; shift crossover with " + crossoverProbab * 100 + "\\% probability";
		
		List<EvolutionaryOperator<BitString>> operators =
			new ArrayList<>();
		
		operators.add(new ShiftCrossover<>(
				new Probability(crossoverProbab)));
		
		operators.add(new GStringXMutation<>(
				SetMemberGenerator.newGen(CollectionsEx.listOf(false, true)), new Probability(mutationProbab)));
		
		String[] head = makeHead("N, K", div);
		
		List<TablePrinter> printers = makePrinters(names, head, comment, div.length + 1);
		
		GenerationsCounter<FitnessEvaluator<? super BitString>, BitString> counter =
			learning ?
					makeQCounter(maxSteps, generationSize, epsilon, cross, k)
					:
					makeGACounter(maxSteps, generationSize, eliteCount, operators);
			
		
		generateTables(times, generationSize, zeroStart, len, div, counter, printers);
	}
	
	/**
     * Creates {@link GACounter} with the specified steps limit,
     * size of generation, elite count and genetic operators.
     * If the steps limit is reached, counting stops.
     *
     * @param stepsLimit the maximal number of generations that can be evolved
     * @param generationSize the size of a generation
     * @param eliteCount the number of candidates kept by elitism
     * @param operators the genetic operators
     * @return GACounter with the specified {@code stepsLimit, generationSize, eliteCount,} and {@code operators}
     */
	private static GACounter makeGACounter(
			int stepsLimit, 
			int generationSize, 
			int eliteCount, 
			List<EvolutionaryOperator<BitString>> operators) {
		return new GACounter(stepsLimit, generationSize, eliteCount, new EvolutionPipeline<>(operators));
	}
	
	/**
     * <p>
     * Creates {@link ReinforcementCounter} with the specified parameters,
     * which allows to measure reinforcement learning performance.
     * </p><p>
     * Learning algorithm chooses between {@code (int) {@link ExpFitness}} and
     * {@code (int) {@link BitCountFitness}}.
     * </p><p>
     * If the specified steps limit is reached, counting stops.
     * </p>
     * @param stepsLimit the maximal number of generations that can be evolved
     * @param generationSize the size of a generation
     * @param epsilon probability of exploring the area by the learning agent
     * @param cross {@link ExpFitness} divider
     * @param k {@link ExpFitness} multiplier
     * @return {@link ReinforcementCounter} with the specified parameters
     */
	private static ReinforcementCounter<BitString> makeQCounter(
			int stepsLimit,
			int generationSize,
			double epsilon,
			double cross,
			double k) {
		
		List<FitnessEvaluator<? super BitString>> evaluators = new ArrayList<>();

		evaluators.add(new IntFitness(new BitCountFitness(1.0 / 5, 0)));
		evaluators.add(new IntFitness(new ExpFitness(k, 2.0, 1.0 / cross, -k)));
		
		EvolutionaryAlgImpl<BitString> ga = StringGeneticAlgorithm.newStringGA(0, 0, 0, evaluators);
		ga.setGenerationSize(generationSize);
		ga.addPrinter(new CompactPrinter<>());
		
		Agent<String, Integer> agent = 
			//new EGreedyAgent<String, Integer>("TourElite_" + eliteCount + "_EGreedy_" + epsilon, epsilon, 1.0, 0.05, 0.8);
			//new SoftmaxAgent<String, Integer>("TourEliteSoftmax-1.0-1.01.learn", 4.0, 1.01, 0.05, 0.8);
			new DelayedAgent<>(maxSteps, 0.005, 3, 0.001);
		
		return new ReinforcementCounter<>(
				stepsLimit, 
				agent, 
				new MultiDiffReward(1), 
				ga);
	}
	
	/**
     * Calculates the number of generations the genetic algorithm needs
     * to evaluate {@code len[i] / div[j]} fitness
     *
     * @param times number of times the calculation is repeated
     * @param generationSize size of a generation
     * @param zeroStart does the start population consist of zeros
     * @param len array of lengths
     * @param div array of dividers
     * @param gc calculator of generations evolved by genetic algorithm
     * @param printers table printers
     */
	private static void generateTables(
            int times,
            int generationSize,
            boolean zeroStart,
            int[] len,
            int[] div,
            GenerationsCounter<FitnessEvaluator<? super BitString>, BitString> gc,
            List<TablePrinter> printers) {

        int kNum = div.length;
		int pNum = printers.size();

        for (int currentLength : len) {
            List<String[]> lines = makeLines(pNum, kNum + 1, currentLength + "");

            gc.setLength(currentLength);

            if (zeroStart) {
                gc.setStartPopulation(GeneticUtils.zeroPopulation(currentLength, generationSize));
            }

            for (int j = 0; j < kNum; j++) {
                StatisticsData data = new StatisticsData();
                ResultViewer viewer = new ResultViewer();

                StringBuilder nameBuilder = new StringBuilder("N=");
                nameBuilder.append(currentLength).append(", K=").append(div[j]);
                if (learning) {
                    nameBuilder.append("; eGreedy learning, epsilon = ").append(epsilon);
                } else {
                    nameBuilder.append("; no learning");
                }
                nameBuilder.append("; tournament selection; elite count = ").append(eliteCount);
                String name = nameBuilder.toString();

                PrintWriter printer;
                for (int t = 0; ; t++) {
                    File f = new File(String.format("%s-%02d", name, t));
                    if (!f.exists()) {
                        try {
                            printer = new PrintWriter(f);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                }
                printer.println(name);

                int currentDivider = div[j];

                FitnessEvaluator<? super BitString> evaluator =
                        new IntFitness(new BitCountFitness(1.0 / currentDivider, 0));

				//noinspection IntegerDivisionInFloatingPointContext: length % divider === 0
				gc.setEvaluator(evaluator, currentLength / currentDivider);

                boolean allTimes = true;

                for (int k = 0; k < times; k++) {
                    int steps = gc.countGenerations();
                    if (steps == -1) {
                        allTimes = false;
                        break;
                    }
                    data.addValue(steps);
                    viewer.add(name, steps);
                    printer.println(steps);
                    printer.flush();
                }
                fillLineElement(lines, data, allTimes, j + 1);
                viewer.showViewer("Comparison of run results", "Fitness function calls", "Runs");
                printer.close();
            }

            for (int k = 0; k < pNum; k++) {
                printers.get(k).addLine(lines.get(k));
            }
        }

        printers.forEach(TablePrinter::close);
	}
	
	private static void fillLineElement(List<String[]> lines, StatisticsData data, boolean trust, int index) {
		try {
			lines.get(0)[index] = String.format("%.2f", data.getAverage());
			lines.get(1)[index] = String.format("%.2f", data.getExpectation());
			lines.get(2)[index] = String.format("%.2f", data.getMax());
			lines.get(3)[index] = String.format("%.2f", data.getMin());
			lines.get(4)[index] = String.format("%.2f", data.getMedian());
			
			if (!trust) {
                for (String[] line : lines) {
                    line[index] += "?";
                }
			}
			
		} catch (EmptyDataSetException e) {
            for (String[] line : lines) {
                line[index] = "-";
            }
		}
	}
	
	private static List<String[]> makeLines(int printersNum, int columns, String firstElem) {
		List<String[]> lines = new ArrayList<>();
		
		for (int t = 0; t < printersNum; t++) {
			String[] line = new String[columns];
			line[0] = firstElem;
			lines.add(line);
		}
		
		return lines;
	}
	
	private static String[] makeHead(String firstElement, int[] columnNames) {
		int columns = columnNames.length + 1;
		
		String[] head = new String[columns];
		
		head[0] = firstElement;
		
		for (int i = 1; i < columns; i++) {
			head[i] = columnNames[i - 1] + "";
		}
		return head;
	}
	
	private static List<TablePrinter> makePrinters(String[] filenames, String[] head, String comment, int columns) {
		List<TablePrinter> printers = new ArrayList<>();

        for (String filename : filenames) {
            TablePrinter printer = new TablePrinter(filename, columns);
            printer.setComment(filename + " " + comment);
            printer.addLine(head);
            printers.add(printer);
        }
		return printers;
	}
}

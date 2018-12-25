package ru.ifmo.ctd.ngp.demo.ffchooser.genetic;

import org.uncommons.watchmaker.framework.FitnessEvaluator;
import ru.ifmo.ctd.ngp.demo.ffchooser.StatisticsData;
import ru.ifmo.ctd.ngp.demo.ffchooser.TablePrinter;
import ru.ifmo.ctd.ngp.demo.ffchooser.Utils;
import ru.ifmo.ctd.ngp.demo.ffchooser.utils.GeneticUtils;
import ru.ifmo.ctd.ngp.demo.ffchooser.xvector.BitCountFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.xvector.IntFitness;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IncrementalTableGenerator {
	
	/**
	 * Uses incremental {@link EvolutionaryAlgImpl}.
	 * 
	 * Generates tables (N = string length) x (K = divider) -> (value),
	 * where value can be 
	 * 
	 * <ul>
	 * <li>average number of steps;
	 * <li>expectation of number of steps;
	 * <li>maximal (minimal) number of steps;
	 * <li>median
	 * </ul>
	 * 
	 * If the number of steps is greater than some constant, 
	 * then the calculation of the current value is interrupted.
	 * 
	 * @param args are not used
	 */
	public static void main(String[] args) {
		int maxSteps = 30000;
		int times = 10;
		int generationSize = 100;
		int[] len = {50, 100, 150, 200, 250};
		int[] div = {1, 2, 5, 10};
        int dlen = div.length;
		
		String[] names = {"Average", "Expectation", "Maximum", "Minimum", "Median"};
		String comment = 	"(times = " + times + 
							"; generation size = " + generationSize + 
							"; steps limit = " + maxSteps +
							"; mutation probability: 3\\%" +
							"; shift crossover with 70\\% probability)";
		
		String[] head = new String[dlen + 1];
		head[0] = "N, K";
		for (int i = 1; i < dlen + 1; i++) {
			head[i] = div[i - 1] + "";
		}
		
		List<TablePrinter> printers = new ArrayList<>();

        for (String name : names) {
            TablePrinter printer = new TablePrinter(name, dlen + 1);
            printer.setComment(name + " " + comment);
            printer.addLine(head);
            printers.add(printer);
        }

        for (int l : len) {
            List<String[]> lines = new ArrayList<>();

            for (String ignored : names) {
                String[] line = new String[dlen + 1];
                line[0] = l + "";
                lines.add(line);
            }

            for (int j = 0; j < dlen; j++) {
                StatisticsData data = new StatisticsData();

                FitnessEvaluator<? super BitString> evaluator =
                        new IntFitness(new BitCountFitness(1.0 / div[j], 0));

                EvolutionaryAlgImpl<BitString> ga =
                        StringGeneticAlgorithm.newStringGA(l, 0, 0, Collections.singletonList(evaluator));

                ga.setGenerationSize(generationSize);

                ga.setStartPopulation(GeneticUtils.zeroPopulation(l, generationSize));

                boolean allTimes = true;

                for (int k = 0; k < times; k++) {
                    //noinspection IntegerDivisionInFloatingPointContext: l % div[j] === 0
                    int steps = Utils.countSteps(ga, l / div[j], maxSteps);
                    if (steps == -1) {
                        data.addValue(0);
                        allTimes = false;
                        break;
                    }
                    data.addValue(steps);
                    ga.refresh();
                }

                lines.get(0)[j + 1] = String.format("%.2f", data.getAverage());
                lines.get(1)[j + 1] = String.format("%.2f", data.getExpectation());
                lines.get(2)[j + 1] = String.format("%.2f", data.getMax());
                lines.get(3)[j + 1] = String.format("%.2f", data.getMin());
                lines.get(4)[j + 1] = String.format("%.2f", data.getMedian());

                if (!allTimes) {
                    for (int t = 0; t < names.length; t++) {
                        lines.get(t)[j + 1] += "?";
                    }
                }
            }

            for (int k = 0; k < names.length; k++) {
                printers.get(k).addLine(lines.get(k));
            }
        }
		
		for (TablePrinter p : printers) {
			p.close();
		}
	}
}

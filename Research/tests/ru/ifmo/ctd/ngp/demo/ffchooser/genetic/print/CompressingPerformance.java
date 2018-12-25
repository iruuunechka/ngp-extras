package ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print;

import java.io.IOException;
import java.io.StringWriter;

import ru.ifmo.ctd.ngp.demo.ffchooser.Utils;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.stat.StatisticsCollector;
import ru.ifmo.ctd.ngp.util.CollectionsEx;

/**
 * Measures the efficiency of the {@link CompressingPrinter}.
 * 
 * @author Arina Buzdalova
 */
public class CompressingPerformance {
	
	public static void main(String[] args) throws IOException {
		int stepsLimit = 3000;
		int times = 1;	
		runNoLearning(stepsLimit, times);
		runGreedy(stepsLimit, times);
		runDelayed(stepsLimit, times);
	}
	
	private static void runNoLearning(int stepsLimit, int times) throws IOException {
		System.out.println("No Learning: ");
		run(Utils.makeNoLearnConfig(stepsLimit), times);
	}
	
	private static void runGreedy(int stepsLimit, int times) throws IOException {
		System.out.println("Eps-Greedy Q-Learning: ");
		run(Utils.makeGreedyConfig(stepsLimit), times);
	}

	private static void runDelayed(int stepsLimit, int times) throws IOException {
		System.out.println("Delayed Q-Learning: ");
		run(Utils.makeDelayedConfig(stepsLimit), times);
	}

	private static void run(Configuration config, int times) throws IOException {				
		StringWriter cWriter = new StringWriter();
		StringWriter fullWriter = new StringWriter();
		
		StatisticsCollector.run(
				config, 
				times,
				cWriter,
                CollectionsEx.listOf(new GeneralPrinter<>(fullWriter)));
		
		fullWriter.close();
		cWriter.close();
		
		int full = fullWriter.toString().length();
		int comp = cWriter.toString().length();		
		
		System.out.println("\nFull log length: " + full);
		System.out.println("Compressed log length: " + comp);
		System.out.println("Full / compressed = " + (double) full / comp);
	}
}

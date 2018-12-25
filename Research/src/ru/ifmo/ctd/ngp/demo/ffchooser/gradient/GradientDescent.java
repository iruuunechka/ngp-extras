package ru.ifmo.ctd.ngp.demo.ffchooser.gradient;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;

/**
 * Gradient descent algorithm that searches for argmin of 
 * a real valued multivariable function.
 * 
 * @author Arina Buzdalova
 */
public class GradientDescent {
	
	/**
	 * Runs gradient descent 
	 * @param args args[0] -- path to the properties with the basic configuration
	 * @throws IOException if an I/O exception occurs
	 */
	public static void main(String[] args) throws IOException {
		Locale.setDefault(Locale.US);
		
		Properties props = new Properties();
        try (FileReader in = new FileReader(args[0])) {
            props.load(in);
        }
		int times = 50;
		props.setProperty("steps", "1000");
		props.setProperty("crossover", "0.7");
		props.setProperty("mutation", "0.003");
		props.setProperty("length", "400");
		props.setProperty("point", "395");
		
		//Delayed:
//		run(
//			new LearningFunction(props, new GradientDelayedFactory()),
//			new Vector(new double[] {100.0 / 1000, 0.2, 0.01}),
//			0.01,
//			0.001,
//			0.01
//		);
		
		//Greedy:
		run(
			new LearningFunction(props, new GradientGreedyFactory(), 0.01, times),
			new Vector(new double[] {0.3, 0.5, 0.02}),
			0.01,
			0.001
			);
	}
	
	/**
	 * Runs gradient descent with the specified function
	 * @param f the specified function
	 * @param start the start point
	 * @param step the step size
	 * @param precision precision
	 */
	public static void run(ImpreciseFunction f, Vector start, double step, 
			double precision) {
		double[] zero = new double[start.length()];
		Arrays.fill(zero, 0);
		Vector oldV = new Vector(zero);
		Vector newV = start;
		
		double oldAvg = 0;

		while (newV.minus(oldV).maxNorm() > precision) {
			
			System.out.println("\n\nCalculating current average generation: ");
			double newAvg = f.value(newV);
			System.out.println("Current result: " + newAvg);	
			System.out.println("(Old average generation) - (new average generation): " + (oldAvg - newAvg));
			oldAvg = newAvg;
			System.out.println("Error: " + f.error(newV));
			
			System.out.println("\nCalculating gradient:");
			
			oldV = newV;
			Vector gradient = f.gradient(newV);
			newV = oldV.minus(gradient.mult(step / gradient.maxNorm()));
		}
	}
}

package ru.ifmo.ctd.ngp.demo.ffchooser.gradient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.lang.Integer.parseInt;

import org.uncommons.maths.statistics.DataSet;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.LearnConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.stat.StatisticsCollector;
import ru.ifmo.ctd.ngp.util.CollectionsEx;

/**
 * Function based on a learning algorithm that solves the "switch point" problem.
 * It takes parameters of the learning algorithm and returns
 * the average number of generation in which the ideal individual was evolved.
 * 
 * @author Arina Buzdalova
 */
public class LearningFunction implements ImpreciseFunction {
	private final Properties basicConfig;
	private final GradientConfigFactory factory;
	private final Map<String, DataSet> cache;
	private final double delta;
	private final int times;
	
	/**
	 * Constructs the {@link LearningFunction} with the basic configuration
	 * that specifies all the parameters except learning ones
	 * @param basicConfig the basic configuration
	 * @param factory the factory for creating configurations
	 * @param delta the delta value that is used to calculate discrete gradient
	 * @param times the number of configuration runs
	 */
	public LearningFunction(Properties basicConfig, GradientConfigFactory factory, double delta, int times) {
		this.basicConfig = basicConfig;
		this.factory = factory;
		this.cache = new HashMap<>();
		this.delta = delta;
		this.times = times;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Vector gradient(Vector v) {
		int len = v.length();
		double[] rv = new double[len];
		double fv = value(v);
		for (int i = 0; i < len; i++) {
			rv[i] = (value(v.iPlus(delta, i)) - fv) / delta;
		}		
		return new Vector(rv);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double error(Vector v) {
		return getStatistics(v).getSampleStandardDeviation();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double value(Vector v) {
		return getStatistics(v).getArithmeticMean();
	}
	
	private DataSet getStatistics(Vector v) {
		String key = v.toString();
		if (cache.containsKey(key)) {
			return cache.get(key);
		}
		
		LearnConfiguration config = factory.create(basicConfig, v);

		//noinspection IntegerDivisionInFloatingPointContext: length % divider === 0
		BestStatisticsPrinter printer = new BestStatisticsPrinter(
            parseInt(basicConfig.getProperty("length")) / parseInt(basicConfig.getProperty("divider")), 2
        );
		try {
			StatisticsCollector.run(config, times, "../gradient", CollectionsEx.listOf(printer));
			printer.println(" average: " + printer.getStatistics().getArithmeticMean());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DataSet dataSet = printer.getStatistics();
		cache.put(key, dataSet);
		return dataSet;
	}

}

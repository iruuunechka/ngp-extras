package ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print;

import java.io.Serializable;
import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.Printer;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.stat.StatCollObserver;

/**
 * Printer that passes information to {@link StatCollObserver}.
 * It also can be used to count the percentage of evolved generations directly,
 * without any observer.
 * 
 * @author Arina Buzdalova
 * @param <T> the type of an individual
 */
public class StatObserverPrinter<T> implements Printer<T>, Serializable {
    private static final long serialVersionUID = -8805203773561134809L;

    private final StatCollObserver observer;
	private final Configuration config;
	private final int period;
	private final int total;
	private int completed;
	
	/**
	 * Constructs {@link StatObserverPrinter} with the specified parameters
	 * @param observer the specified observer
	 * @param config the specified configuration
	 * @param period the period of update 
	 * @param times the number of runs
	 */
	public StatObserverPrinter(StatCollObserver observer, Configuration config, int period, int times) {
		this.observer = observer;
		this.config = config;
		this.period = period;
		this.total = config.getSteps() * times;
		this.completed = 0;
	}

    /**
	 * Adds the specified value to the number of completed generations
	 * @param generations the value to be added
	 */
	public void addCompleted(int generations) {
		completed += generations;
		updateObserver();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(List<Double> values, T bestIndividual, int iterations, int curEvaluatorIndex) {
		completed++;		
		if (iterations % period == 0) {
			updateObserver();
		}
	}
	
	/**
	 * Gets the percentage of currently evolved generations
	 * @return the percentage of currently evolved generations
	 */
	private double getPercentage() {
		return (double) completed / total;
	}
	
	private void updateObserver() {
		if (observer != null) {
			observer.update(config, getPercentage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void println(String info) {

	}
}

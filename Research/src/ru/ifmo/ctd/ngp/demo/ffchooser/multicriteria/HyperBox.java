package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The hyper-box -- element of a hyper-grid that stores evaluated individuals.
 * All the individuals in the hyper-box have the same squeeze factor,
 * which is equal to the number of individuals in the hyper-box.
 * 
 * @author Arina Buzdalova
 *
 * @param <T> the type of an individual
 */
public class HyperBox<T> implements Comparable<HyperBox<? extends T>> {
	private final List<EvaluatedIndividual<T>> individuals;

	/**
	 * Constructs {@link HyperBox} without any parameters.
	 */
	public HyperBox() {
		individuals = new ArrayList<>();
	}
	
	/**
	 * Returns an unmodifiable view on all the individuals contained in this hyper-box.
	 * @return an unmodifiable view on all the individuals contained in this hyper-box
	 */
	public List<EvaluatedIndividual<T>> getAll() {
		return Collections.unmodifiableList(individuals);
	}
	
	/**
	 * Adds the specified individual to this hyper-box.
	 * @param individual the individual to be added
	 */
	public void add(EvaluatedIndividual<T> individual) {
		individual.setSqueezeFactor(getSqueezeFactor());
		individuals.add(individual);
		updateFactors();
	}
	
	private void updateFactors() {
		individuals.forEach(EvaluatedIndividual::incSqueezeFactor);
	}
	
	/**
	 * Checks whether this hyper-box is empty.
	 * @return {@code true}, if this hyper-box is empty, {@code false} otherwise
	 */
	public boolean isEmpty() {
		return individuals.size() == 0;
	}
	
	/**
	 * Removes a random individual from this hyper-box and returns it.
	 * @param rng the source of randomness
	 * @return {@code null} if the box is empty, the removed individual otherwise
	 */
	public EvaluatedIndividual<T> extractRandom(Random rng) {
		if (isEmpty()) {
			return null;
		}
		EvaluatedIndividual<T> ind = individuals.get(rng.nextInt(individuals.size()));
		individuals.remove(ind);		
		return ind;
	}
	
	/**
	 * Gets a random individual from this hyper-box, the individual stays in the hyper-box
	 * @param rng the source of randomness
	 * @return a random individual form this hyper-box
	 */
	public EvaluatedIndividual<T> getRandom(Random rng) {
		return individuals.get(rng.nextInt(individuals.size()));
	}
	
	/**
	 * Gets the current squeeze factor of this hyper-box. 
	 * Squeeze factor is the number of individuals consisted in a hyper-box.
	 * @return the current squeeze factor of this hyper-box
	 */
	private int getSqueezeFactor() {
		return individuals.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(@NotNull HyperBox<? extends T> o) {
		int sf = getSqueezeFactor();
		int osf = o.getSqueezeFactor();
		return Integer.compare(sf, osf);
	}
}

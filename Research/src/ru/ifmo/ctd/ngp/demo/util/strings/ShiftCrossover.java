package ru.ifmo.ctd.ngp.demo.util.strings;

import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.operators.AbstractCrossover;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.GStringBuilderX;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.GStringX;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.ObjString;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 2 points crossover with shift, which is performed on {@link ObjString}.
 * 
 * Takes random part of the first parent and exchanges it with the
 * part of the same length with random start taken from the second parent.
 * 
 * @author Arina Buzdalova
 * @param <T> type of string elements
 * @param <S> type of strings
 * @param <B> type of string builders
 */
public class ShiftCrossover<T, S extends GStringX<T, S, B>, B extends GStringBuilderX<T, B, S>>
        extends AbstractCrossover<S>{
	private final static int crossoverPoints = 2;

	/**
	 * Constructs {@link ShiftCrossover} with the specified crossover probability
	 * @param crossoverProbability the specified probability of performing crossover
	 */
	public ShiftCrossover(Probability crossoverProbability) {
		super(crossoverPoints, crossoverProbability);
	}

	/**
	 * {@inheritDoc}
	 * Performs 2 points crossover with shift on the two specified parents.
	 * @throws IllegalArgumentException if the parents are of different length
	 */
	@Override
	protected List<S> mate(S parent1, S parent2, int numberOfCrossoverPoints, Random rng) {
		return crossTwoParents(parent1, parent2, rng);
	}
	
	/**
	 * Performs 2 points crossover with shift on the two specified parents. 
	 * @param <T> the type of the elements in the string
	 * @param parent1 the first specified parent
	 * @param parent2 the second specified parent
	 * @param rng the source of randomness used to determine the location of cross-over points
	 * @return a list containing two evolved offsprings
	 * @throws IllegalArgumentException if the parents are of different length, or
	 * 									if the number of crossover points is out of the range [1, (parent's length)/2].
	 */
	private static <
            T, S extends GStringX<T, S, B>, B extends GStringBuilderX<T, B, S>
    > List<S> crossTwoParents(S parent1, S parent2, Random rng) {
		int length = parent1.length();
		
		if (parent1.length() != parent2.length()) {
			throw new IllegalArgumentException("Cannot perform crossover with different length parents.");
		}
		
		List<S> children = new ArrayList<>(2);
		
		if (parent1.isEmpty() || length == 1) {
			for (int i = 0; i < 2; i++) {
				children.add(parent1);
			}
			return children;
		}
		
		B builder1 = parent1.emptyString().toGStringBuilder();
		B builder2 = parent2.emptyString().toGStringBuilder();
		
		int shiftLen = 1 + rng.nextInt(length - 1);
		int start1 = rng.nextInt(length - shiftLen);
		int start2 = rng.nextInt(length - shiftLen);		
		
		S newPart1 = parent2.substring(start2, start2 + shiftLen);
		S newPart2 = parent1.substring(start1, start1 + shiftLen);
		
		builder1.append(parent1.substring(0, start1));
		builder1.append(newPart1);
		builder1.append(parent1.substring(start1 + shiftLen));
		
		builder2.append(parent2.substring(0, start2));
		builder2.append(newPart2);
		builder2.append(parent2.substring(start2 + shiftLen));
		
		children.add(builder1.toGString());
		children.add(builder2.toGString());
		return children;
	}
}

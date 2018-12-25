package ru.ifmo.ctd.ngp.demo.util.strings;

import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import ru.ifmo.ctd.ngp.demo.generators.MutationalGenerator;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.GStringBuilderX;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.GStringX;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.ObjString;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Mutation of individual elements in a {@link GStringX} according to some probability.
 * 
 * @param <T> the type of elements in a {@link GStringX}
 * @param <S> the actual type of string in a {@link GStringX}
 * @param <B> the actual type of a builder for a {@link GStringX}
 *
 * @author Arina Buzdalova
 * @author Maxim Buzdalov
 */
public class GStringXMutation<T, S extends GStringX<T, S, B>, B extends GStringBuilderX<T, B, S>>
        implements EvolutionaryOperator<S> {
    private final MutationalGenerator<T> generator;
    private final Probability mutationProbability;

    /**
     * Creates a mutation operator that is applied with the given
     * probability and gets the possible elements of mutated {@link ObjString} 
     * from the specified {@link ru.ifmo.ctd.ngp.demo.generators.RandomObjGenerator}.
     * @param generator the specified generator of random string elements
     * @param mutationProbability the probability that a given element is changed
     * incompatible probability pictures.
     */
    public GStringXMutation(MutationalGenerator<T> generator, Probability mutationProbability) {
        this.generator = generator;
        this.mutationProbability = mutationProbability;
    }

    /**
     * Mutates a single individual according to the operator's settings.
     * @param individual the individual to mutate.
     * @param rng the random number generator.
     * @return the mutated (possibly the same) individual.
     */
    private S applySingle(S individual, Random rng) {
        if (individual.isEmpty()) {
        	return individual;
        }

    	B builder = individual.toGStringBuilder();

        int length = builder.length();
        double logComplementP = Math.log(1 - mutationProbability.doubleValue());
        int currIndex = -1;
        while (currIndex < length) {
            if (currIndex >= 0) {
                builder.setCharAt(currIndex, generator.generate(builder.charAt(currIndex), rng));
            }
            currIndex += 1 + (int) (Math.log(rng.nextDouble()) / logComplementP);
        }

        return builder.toGString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<S> apply(List<S> selectedCandidates, Random rng) {
        return selectedCandidates.stream().map(s -> applySingle(s, rng)).collect(Collectors.toList());
    }

    /**
     * Creates the same mutation operator as {@link GStringXMutation#GStringXMutation(
     * ru.ifmo.ctd.ngp.demo.generators.MutationalGenerator, org.uncommons.maths.random.Probability)},
     * but with type parameter inference.
     *
     * @param generator the specified generator of random string elements
     * @param probability the probability that a given element is changed
     * @param <T> the type of string's character.
     * @param <S> the type of string itself.
     * @param <B> the type of string builder.
     * @return the newly created mutation operator.
     */
    public static <
            T,
            S extends GStringX<T, S, B>,
            B extends GStringBuilderX<T, B, S>
    > GStringXMutation<T, S, B> create(MutationalGenerator<T> generator, Probability probability) {
        return new GStringXMutation<>(generator, probability);
    }
}

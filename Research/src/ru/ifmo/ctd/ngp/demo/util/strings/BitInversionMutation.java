package ru.ifmo.ctd.ngp.demo.util.strings;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitStringBuilder;

/**
 * Mutation of individual elements in a {@link ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString}.
 * It inverses a specified number of randomly chosen bits.
 * 
 * @author Arina Buzdalova
 */
public class BitInversionMutation implements EvolutionaryOperator<BitString> {
	private final int x;
	
	/**
	 * Constructs {@link BitInversionMutation} 
	 * with the specified number of bits to be inverted.
	 * @param x number of bits to be inverted
	 */
	public BitInversionMutation(int x) {
		this.x = x;
	}

	private BitString singleMutation(BitString individual, Random rand) {
		int len = individual.length();
		BitStringBuilder builder = individual.toGStringBuilder();
		for (int i = 0; i < x; i++) {
			int index = rand.nextInt(len);
			builder.setCharAt(index, !builder.charAt(index));
		}
		return builder.toGString();
	}
	
	/**
	 * {@inheritDoc}
	 * Inverts randomly chosen bits.
	 */
	@Override
	public List<BitString> apply(List<BitString> population, Random rand) {
		return population.stream().map(individual -> singleMutation(individual, rand)).collect(Collectors.toList());
	}
}


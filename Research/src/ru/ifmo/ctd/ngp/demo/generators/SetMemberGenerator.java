package ru.ifmo.ctd.ngp.demo.generators;

import java.util.List;
import java.util.Random;

/**
 * Generator of objects, which are randomly taken from some set 
 * specified by an array.
 *
 * @param <T> the type of objects being generated
 * 
 * @author Arina Buzdalova
 */
public class SetMemberGenerator<T> implements MutationalGenerator<T>, RandomObjGenerator<T> {
	private final List<T> values;
	
	/**
     * Constructs the {@link SetMemberGenerator} with the array which
     * specifies the set of possible generated values.
     *
     * The specified array is copied, so changes made to the {@code values} don't reflect
     * on the generator.
     *
     * @param values the specified list of possible generated values
     */
	private SetMemberGenerator(List<T> values) {
		this.values = values;
	}

	/**
     * <p>Constructs the {@link SetMemberGenerator} with the array which
     * specifies the set of possible generated values.</p>
     *
     * <p>The specified array is copied, so changes made to the {@code values} don't reflect
     * on the generator.</p>
     *
     * <p>If the specified array is empty, {@code null} value will be returned.</p>
     *
     * @param <T> the type of generated values
     * @param values the specified array of possible generated values
     * @return new {@link SetMemberGenerator} if the length of {@code values} is greater than zero;
     * otherwise, return {@code null}
     */
	public static <T> SetMemberGenerator<T> newGen(List<T> values) {
		return new SetMemberGenerator<>(values);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public T generate(Random rng) {
		return values.get(rng.nextInt(values.size()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T generate(T assistant, Random rng) {
		return values.get(rng.nextInt(values.size()));
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isNull() {
		return values.isEmpty();
	}
}

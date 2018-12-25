package ru.ifmo.ctd.ngp.demo.generators;

import java.util.Random;

/**
 * Interface for classes which generate objects of the specified type using the given 
 * assistant of the same type.
 * This interface is used in mutation operators to unify their actions.
 * 
 * @param <T> the type of objects being generated
 * 
 * @author Arina Buzdalova
 */
public interface MutationalGenerator<T> {

	/**
	 * Randomly generates an object of the specified type using the specified source of randomness.
	 * Some specified assistant can be used to generate the result.
	 * @param assistant some object which can be used to generate the result
	 * @param rng the source of randomness
	 * @return randomly generated object
	 */
    T generate(T assistant, Random rng);
}

package ru.ifmo.ctd.ngp.demo.generators;

import java.util.Random;

/**
 * Interface for classes which generate random objects of the specified type.
 * 
 * @param <T> the type of objects being generated
 * 
 * @author Arina Buzdalova
 */
public interface RandomObjGenerator<T> {

	/**
	 * Randomly generates an object of the specified type using the specified source of randomness.
	 * @param rng the source of randomness
	 * @return randomly generated object
	 */
	T generate(Random rng);
	
	/**
	 * Shows whether this object cannot produce any object.
	 * @return <code>true</code> if this object cannot produce any object,
	 * otherwise, returns <code>false</code>
	 */
	boolean isNull();
}

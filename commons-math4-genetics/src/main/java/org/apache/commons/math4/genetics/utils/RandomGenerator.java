package org.apache.commons.math4.genetics.utils;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

public class RandomGenerator {

	private static UniformRandomProvider randomGenerator = RandomSource.create(RandomSource.WELL_19937_C);
	
	/**
	 * Returns the (static) random generator.
	 *
	 * @return the static random generator shared by GA implementation classes
	 */
	public static synchronized UniformRandomProvider getRandomGenerator() {
		return randomGenerator;
	}

}

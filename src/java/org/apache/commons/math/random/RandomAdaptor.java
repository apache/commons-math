/*
 * Copyright 2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.random;

import java.util.Random;

/**
 * Extension of <code>java.util.Random</code> wrapping a
 * {@link RandomGenerator}.   
 *
 * @since 1.1
 * @version $Revision:$ $Date$
 */
public class RandomAdaptor extends Random implements RandomGenerator {
    
    /** Wrapped randomGenerator instance */
    private RandomGenerator randomGenerator = null;
    
    /** 
     * Prevent instantiation without a generator argument
     */ 
    private RandomAdaptor() { }
    
    /**
     * Construct a RandomAdaptor wrapping the supplied RandomGenerator.
     * 
     * @param randomGenerator  the wrapped generator
     */
    public RandomAdaptor(RandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    } 
    
    /**
     * Factory method to create a <code>Random</code> using the supplied
     * <code>RandomGenerator</code>.
     * 
     * @param randomGenerator
     * @return a Random instance wrapping the RandomGenerator
     */
    public static Random createAdaptor(RandomGenerator randomGenerator) {
        return new RandomAdaptor(randomGenerator);
    }
    
    /* (non-Javadoc)
     * @see java.util.Random#nextBoolean()
     */
    public boolean nextBoolean() {
        return randomGenerator.nextBoolean();
    }

    /* (non-Javadoc)
     * @see java.util.Random#nextBytes(byte[])
     */
    public void nextBytes(byte[] bytes) {
        randomGenerator.nextBytes(bytes);
    }

    /* (non-Javadoc)
     * @see java.util.Random#nextDouble()
     */
    public double nextDouble() {
        return randomGenerator.nextDouble();
    }

    /* (non-Javadoc)
     * @see java.util.Random#nextFloat()
     */
    public float nextFloat() {
        return randomGenerator.nextFloat();
    }

    /* (non-Javadoc)
     * @see java.util.Random#nextGaussian()
     */
    public double nextGaussian() {
        return randomGenerator.nextGaussian();
    }

    /* (non-Javadoc)
     * @see java.util.Random#nextInt()
     */
    public int nextInt() {
        return randomGenerator.nextInt();
    }

    /* (non-Javadoc)
     * @see java.util.Random#nextInt(int)
     */
    public int nextInt(int n) {
        return randomGenerator.nextInt(n);
    }

    /* (non-Javadoc)
     * @see java.util.Random#nextLong()
     */
    public long nextLong() {
        return randomGenerator.nextLong();
    }

    /* (non-Javadoc)
     * @see java.util.Random#setSeed(long)
     */
    public void setSeed(long seed) {
        if (randomGenerator != null) {  // required to avoid NPE in constructor
            randomGenerator.setSeed(seed);
        }
    }
}

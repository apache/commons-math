/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.commons.math.random;
import java.util.Collection;

/**
 * Random data generation utilities
 * @version $Revision: 1.3 $ $Date: 2003/10/13 08:10:01 $
 */
public interface RandomData {      
    /**
     * Generates a random string of hex characters of length
     * <code>len</code>.
     * <p>
     * The generated string will be random, but not cryptographically 
     * secure. To generate cryptographically secure strings, use 
     * <code>nextSecureHexString</code>
     * <p>
     * <strong>Preconditions</strong>:<ul>
     * <li><code>len > 0</code> (otherwise an IllegalArgumentException 
     *     is thrown.)</li>
     * </ul>
     * 
     * @param len the length of the string to be generated
     * @return random string of hex characters of length <code>len</code>  
     */
    String nextHexString(int len);  
    
    /**
     * Generates a uniformly distributed random integer between 
     * <code>lower</code> and <code>upper</code> (endpoints included).
     * <p>
     * The generated integer will be random, but not cryptographically secure.
     * To generate cryptographically secure integer sequences, use 
     * <code>nextSecureInt</code>.
     * <p>
     * <strong>Preconditions</strong>:<ul>
     * <li><code>lower < upper</code> (otherwise an IllegalArgumentException 
     *     is thrown.)</li>
     * </ul>
     *
     * @param lower lower bound for generated integer
     * @param upper upper bound for generated integer
     * @return a random integer greater than or equal to <code>lower</code> 
     * and less than or equal to <code>upper</code>.
     */
    int nextInt(int lower, int upper);  
    
    /**
     * Generates a uniformly distributed random long integer between
     * <code>lower</code> and <code>upper</code> (endpoints included).
     * <p>
     * The generated long integer values will be random, but not 
     * cryptographically secure.
     * To generate cryptographically secure sequences of longs, use 
     * <code>nextSecureLong</code>
     * <p>
     * <strong>Preconditions</strong>:<ul>
     * <li><code>lower < upper</code> (otherwise an IllegalArgumentException 
     *     is thrown.)</li>
     * </ul>
     *
     * @param lower lower bound for generated integer
     * @param upper upper bound for generated integer
     * @return a random integer greater than or equal to <code>lower</code>
     * and less than or equal to <code>upper</code>.
     */
    long nextLong(long lower, long upper);  
    
    /**
     * Generates a random string of hex characters from a secure random 
     * sequence.
     * <p>
     * If cryptographic security is not required, 
     * use <code>nextHexString()</code>.
     * <p>
     * <strong>Preconditions</strong>:<ul>
     * <li><code>len > 0</code> (otherwise an IllegalArgumentException 
     *     is thrown.)</li>
     * </ul>
     * @param len length of return string
     * @return the random hex string
     */
    String nextSecureHexString(int len);  
    
    /**
     * Generates a uniformly distributed random integer between 
     * <code>lower</code> and <code>upper</code> (endpoints included) 
     * from a secure random sequence.
     * <p>
     * Sequences of integers generated using this method will be 
     * cryptographically secure. If cryptographic security is not required, 
     * <code>nextInt</code> should be used instead of this method. 
     * <p>
     * <strong>Definition</strong>:
     * <a href="http://www.wikipedia.org/wiki/
     * Cryptographically_secure_pseudo-random_number_generator">
     * Secure Random Sequence</a>
     * <p>
     * <strong>Preconditions</strong>:<ul>
     * <li><code>lower < upper</code> (otherwise an IllegalArgumentException 
     *     is thrown.)</li>
     * </ul>
     *
     * @param lower lower bound for generated integer
     * @param upper upper bound for generated integer
     * @return a random integer greater than or equal to <code>lower</code>
     * and less than or equal to <code>upper</code>.
     */
    int nextSecureInt(int lower, int upper);  
    
    /**
     * Generates a random long integer between <code>lower</code>
     * and <code>upper</code> (endpoints included).<p>
     * Sequences of long values generated using this method will be 
     * cryptographically secure. If cryptographic security is not required,
     * <code>nextLong</code> should be used instead of this method.
     * <p>
     * <strong>Definition</strong>:
     * <a href="http://www.wikipedia.org/wiki/
     * Cryptographically_secure_pseudo-random_number_generator">
     * Secure Random Sequence</a>
     * <p>
     * <strong>Preconditions</strong>:<ul>
     * <li><code>lower < upper</code> (otherwise an IllegalArgumentException 
     *     is thrown.)</li>
     * </ul>
     *
     * @param lower lower bound for generated integer
     * @param upper upper bound for generated integer
     * @return a long integer greater than or equal to <code>lower</code>
     * and less than or equal to <code>upper</code>.
     */
    long nextSecureLong(long lower, long upper);  
    
    /** 
     * Generates a random value from the Poisson distribution with 
     * the given mean.
     * <p>
     * <strong>Definition</strong>: 
     * <a href="http://www.itl.nist.gov/div898/handbook/
     * eda/section3/eda366j.htm">
     * Poisson Distribution</a>
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>The specified mean <i>must</i> be positive (otherwise an 
     *     IllegalArgumentException is thrown.)</li>
     * </ul>
     * @param mean Mean of the distribution
     * @return poisson deviate with the specified mean
     */
    long nextPoisson(double mean);  
    
    /** 
     * Generates a random value from the
     * Normal (or Gaussian) distribution with the given mean
     * and standard deviation.
     * <p>
     * <strong>Definition</strong>: 
     * <a href="http://www.itl.nist.gov/div898/handbook/
     * eda/section3/eda3661.htm">
     * Normal Distribution</a>
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li><code>sigma > 0</code> (otherwise an IllegalArgumentException 
     *     is thrown.)</li>
     * </ul>
     * @param mu Mean of the distribution
     * @param sigma Standard deviation of the distribution
     * @return random value from Gaussian distribution with mean = mu,
     * standard deviation = sigma
     */
    double nextGaussian(double mu, double sigma);  
    
    /**
     * Generates a random value from the exponential distribution
     * with expected value = <code>mean</code>.
     * <p>
     * <strong>Definition</strong>: 
     * <a href="http://www.itl.nist.gov/div898/handbook/
     * eda/section3/eda3667.htm">
     * Exponential Distribution</a>
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li><code>mu >= 0</code> (otherwise an IllegalArgumentException 
     *     is thrown.)</li>
     * </ul>
     * @param mean Mean of the distribution
     * @return random value from exponential distribution
     */
    double nextExponential(double mean);   
    
    /**
     * Generates a uniformly distributed random value from the open interval
     * (<code>lower</code>,<code>upper</code>) (i.e., endpoints excluded).
     * <p>
     * <strong>Definition</strong>: 
     * <a href="http://www.itl.nist.gov/div898/handbook/
     * eda/section3/eda3662.htm">
     * Uniform Distribution</a> <code>lower</code> and 
     * <code>upper - lower</code> are the 
     * <a href = "http://www.itl.nist.gov/div898/handbook/eda/
     * section3/eda364.htm">
     * location and scale parameters</a>, respectively.
     * <p>
     * <strong>Preconditions</strong>:<ul>
     * <li><code>lower < upper</code> (otherwise an IllegalArgumentException 
     *     is thrown.)</li>
     * </ul>
     *
     * @param lower lower endpoint of the interval of support
     * @param upper upper endpoint of the interval of support
     * @return uniformly distributed random value between lower
     * and upper (exclusive)
     */
    double nextUniform(double lower, double upper);
    
    /**
     * Generates an integer array of length <code>k</code> whose entries
     * are selected randomly, without repetition, from the integers <code>
     * 0 through n-1</code> (inclusive). 
     * <p>
     * Generated arrays represent permutations
     * of <code>n</code> taken <code>k</code> at a time. 
     * <p>
     * <strong>Preconditions:</strong><ul>
     * <li> <code>k <= n</code></li>
     * <li> <code>n > 0</code> </li>
     * </ul>
     * If the preconditions are not met, an IllegalArgumentException is
     * thrown.
     * 
     * @param n domain of the permutation
     * @param k size of the permutation
     * @return random k-permutation of n 
     */
    int[] nextPermutation(int n, int k);
    
    /**
     * Returns an array of <code>k</code> objects selected randomly
     * from the Collection <code>c</code>. 
     * <p>
     * Sampling from <code>c</code>
     * is without replacement; but if <code>c</code> contains identical
     * objects, the sample may include repeats.  If all elements of <code>
     * c</code> are distinct, the resulting object array represents a 
     * <a href="http://rkb.home.cern.ch/rkb/AN16pp/
     * node250.html#SECTION0002500000000000000000">
     * Simple Random Sample</a> of size
     * <code>k</code> from the elements of <code>c</code>.
     * <p>   
     * <strong>Preconditions:</strong><ul>
     * <li> k must be less than or equal to the size of c </li>
     * <li> c must not be empty </li>
     * </ul>
     * If the preconditions are not met, an IllegalArgumentException is
     * thrown.
     * 
     * @param c collection to be sampled
     * @param k size of the sample
     * @return random sample of k elements from c 
     */
    Object[] nextSample(Collection c, int k);
}

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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
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

package org.apache.commons.math;
import java.util.Collection;

/**
 * Random data generation utilities
 * @author Phil Steitz
 * @version $Revision: 1.2 $ $Date: 2003/05/29 19:45:35 $
 */
public interface RandomData {      
    /**
     * Generates a random string of hex characters of length
     * <code>len</code>.<br>
     * The generated string will be random, but not cryptographically 
     * secure. To generate cryptographically secure strings, use 
     * <code>nextSecureHexString</code>
     * @param len the length of the string to be generated
     * @throws IllegalArgumentException if <code>len</code> is not positive.
     */
    public String nextHexString(int len);  
    
    /**
     * Generates a uniformly distributed random integer between 
     * <code>lower</code> and <code>upper</code> (endpoints included).<br>
     * The generated integer will be random, but not cryptographically secure.
     * To generate cryptographically secure integer sequences, use 
     * <code>nextSecureInt</code>.
     * @param lower lower bound for generated integer
     * @param upper upper bound for generated integer
     * @exception IllegalArgumentException thrown if
     * <code>lower</code> is not strictly less than <code>upper</code>.
     * @return a random integer greater than or equal to <code>lower</code> 
     * and less than or equal to <code>upper</code>.
     */
    public int nextInt(int lower, int upper);  
    
    /**
     * Generates a uniformly distributed random long integer between <
     * code>lower</code> and <code>upper</code> (endpoints included).
     * The generated long integer values will be random, but not 
     * cryptographically secure.<br> 
     * To generate cryptographically secure sequences of longs, use 
     * <code>nextSecureLong</code>
     * @param lower lower bound for generated integer
     * @param upper upper bound for generated integer
     * @exception IllegalArgumentException Thrown if lower > upper
     * @return a random integer greater than or equal to <code>lower</code>
     * and less than or equal to <code>upper</code>.
     */
    public long nextLong(long lower, long upper);  
    
    /**
     * Generates a random string of hex characters from a secure random sequence.
     * If cryptographic security is not required, 
     * use <code>nextHexString()</code>.
     * @param len length of return string
     * @exception IllegalArgumentException thrown if len <= 0 
     * @return the random hex string
     */
    public String nextSecureHexString(int len);  
    
    /**
     * Generates a uniformly distributed random integer between 
     * <code>lower</code> and <code>upper</code> (endpoints included) 
     * from a secure random sequence.<br>
     * The generated sequence will be cryptographically secure.<br>
     * If cryptographic security is not required, <code>nextInt</code>
     * should be used.<br>
     * <strong>Definition</strong>(secure random sequence):
     * http://www.wikipedia.org/wiki/Cryptographically_secure_pseudo-random_number_generator<br>
     * @param lower lower bound for generated integer
     * @param upper upper bound for generated integer
     * @exception IllegalArgumentException thrown if
     * <code>lower</code> is not strictly less than <code>upper</code>.
     * @return a random integer greater than or equal to <code>lower</code>
     * and less than or equal to <code>upper</code>.
     */
    public int nextSecureInt(int lower, int upper);  
    
    /**
     * Generates a random long integer between <code>lower</code>
     * and <code>upper</code> (endpoints included).<br>
     * The generated long sequence will be cryptographically secure.<br>
     * If cryptographic security is not required,
     * use <code>nextLong</code><br>
     * <strong>Definition</strong>:
     * <a href=http://www.wikipedia.org/wiki/Cryptographically_secure_pseudo-random_number_generator>
     * Secure Random Sequence</a>
     * @param lower lower bound for generated integer
     * @param upper upper bound for generated integer
     * @exception IllegalArgumentException thrown if
     * <code>lower</code> is not strictly less than <code>upper</code>.
     * @return a long integer greater than or equal to <code>lower</code>
     * and less than or equal to <code>upper</code>.
     */
    public long nextSecureLong(long lower, long upper);  
    
    /** 
     * Generates a random value from the Poisson distribution with 
     * the given mean.<br>
     * <strong>Definition</strong>: 
     * <a href=http://www.itl.nist.gov/div898/handbook/eda/section3/eda366j.htm>
     * Poisson Distribution</a><br>
     * <strong>Preconditions</strong>: <ul>
     * <li>The specified mean <i>must</i> be positive </li>
     * </ul>
     * @param mean Mean of the distribution
     * @returns long
     * @throws IllegalArgumentException if mean <= 0
     */
    public long nextPoisson(double mean);  
    
    /** 
     * Generates a random value from the
     * Normal (a.k.a. Gaussian) distribution with the given mean
     * and standard deviation.<br>
     * <strong>Definition</strong>: 
     * <a href=http://www.itl.nist.gov/div898/handbook/eda/section3/eda3661.htm>
     * Normal Distribution</a><br>
     * <strong>Preconditions</strong>: <ul>
     * <li>The specified standard deviation <i>must</i> be positive </li>
     * </ul>
     * @param mu Mean of the distribution
     * @param sigma Standard deviation of the distribution
     * @return random value from Gaussian distribution with mean = mu,
     * standard deviation = sigma
     * @throws IllegalArgumentExcption if sigma <= 0
     */
    public double nextGaussian(double mu,double sigma);  
    
    /**
     * Generates a random value from the exponential distribution
     * with expected value = <code>mean</code><br>
     * <strong>Definition</strong>: 
     * <a href=http://www.itl.nist.gov/div898/handbook/eda/section3/eda3667.htm>
     * Exponential Distribution</a><br>
     * <strong>Preconditions</strong>: <ul>
     * <li>The specified mean <i>must</i> be non-negative</li>
     * </ul>
     * @param mu Mean of the distribution
     * @return random value from exponential distribution
     */
    public double nextExponential(double mean);   
    
    /**
     * Generates a uniformly distributed random value from the open interval
     * (<code>lower</code>,<code>upper</code>) (i.e., endpoints excluded)
     * <strong>Definition</strong>: 
     * <a href=http://www.itl.nist.gov/div898/handbook/eda/section3/eda3662.htm>
     * Uniform Distribution</a> <code>lower</code> and <code>upper - lower</code>
     * are the 
     * <a href = http://www.itl.nist.gov/div898/handbook/eda/section3/eda364.htm>
     * location and scale parameters</a>, respectively<br>
     * @param lower lower endpoint of the interval of support
     * @param upper upper endpoint of the interval of support
     * @return uniformly distributed random value between lower
     * and upper (exclusive)
     * @exception IllegalArgumentException thrown if
     * <code>lower</code> is not strictly less than <code>upper</code>.
     */
    public double nextUniform(double lower, double upper);
    
    /**
     * Generates an integer array of length <code>k</code> whose entries
     * are selected randomly, without repetition, from the integers
     * {0, ... , n-1} -- i.e., generated arrays represent permutations
     * of <code>n</code> taken <code>k</code> at a time. <p>
     *
     * <strong>Preconditions:</strong><ul>
     * <li> k must be less than or equal to n </li>
     * <li> n must be positive (i.e. greater than 0) </li>
     * </ul>
     * 
     * @param n domain of the permutation
     * @param k size of the permutation
     * @return random k-permutation of n 
     */
    public int[] nextPermutation(int n, int k);
    
    /**
     * Returns an array of <code>k</code> objects selected randomly
     * from the Collection <code>c</code>. Sampling from <code>c</code>
     * is without replacement; but if <code>c</code> contains identical
     * objects, the sample may include repeats.  If all elements of <code>
     * c</code> are distinct, the resulting object array represents a 
     * <a href=http://rkb.home.cern.ch/rkb/AN16pp/node250.html#SECTION0002500000000000000000>
     * Simple Random Sample</a> of size
     * <code>k</code> from the elements of <code>c</code>.<p>   
     *
     * <strong>Preconditions:</strong><ul>
     * <li> k must be less than or equal to the size of c </li>
     * <li> c must not be empty </li>
     * </ul>
     * 
     * @param c collection to be sampled
     * @param k size of the sample
     * @return random sample of k elements from c 
     */
    public Object[] nextSample(Collection c, int k);
}

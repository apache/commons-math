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

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Random;
import java.util.Collection;

/**
 * Implements the <code>RandomData</code> interface using 
 * <code>java.util.Random</code> and 
 * <code>java.util.Random.SecureRandom</code> instances to generate data. 
 * <p>
 * Supports reseeding the underlying 
 * <a href="http://www.wikipedia.org/wiki/Pseudo-random_number_generator">
 * PRNG</a>. The <code>SecurityProvider</code> and <code>Algorithm</code>
 * used by the <code>SecureRandom</code> instance can also be reset.
 * <p>
 * For details on the PRNGs, see the JDK documentation for 
 * <code>java.util.Random</code> and 
 * <code>java.util.Random.SecureRandom</code>
 * <p>
 * <strong>Usage Notes</strong>: <ul>
 * <li>
 * Instance variables are used to maintain <code>Random</code> and 
 * <code>SecureRandom</code> instances used in data generation. Therefore,
 * to generate a random sequence of values or strings, you should use just
 * <strong>one</strong> <code>RandomDataImpl</code> instance repeatedly.</li>
 * <li>
 * The "secure" methods are *much* slower.  These should be used only when
 * a <a href="http://www.wikipedia.org/wiki/
 * Cryptographically_secure_pseudo-random_number_generator"> Secure Random 
 * Sequence</a> is required.</li>
 * <li>
 * When a new <code>RandomDataImpl</code> is created, the underlying random
 * number generators are <strong>not</strong> intialized.  The first call to a
 * data generation method, or to a <code>reSeed()</code> method instantiates
 * the appropriate generator.  If you do not explicitly seed the generator, it
 * is by default seeded with the current time in milliseconds</li>
 * <li>
 * The <code>reSeed</code> and <code>reSeedSecure</code> methods delegate 
 * to the corresponding methods on the underlying <code>Random</code> and 
 * <code>SecureRandom</code> instances.  Therefore, the contracts of these 
 * methods are as defined in the JDK documentation.  In particular, 
 * <code>reSeed(long)</code> fully resets the initial state of the non-secure 
 * random number generator (so that reseeding with a specific value always 
 * results in the same subsequent random sequence); whereas reSeedSecure(long)
 * does <strong>not</strong> reinitialize the secure random number generator 
 * (so secure sequences started with calls to reseedSecure(long) won't be 
 * identical).</li></ul>
 * 
 * @version $Revision: 1.5 $ $Date: 2003/10/13 08:10:01 $
 */
public class RandomDataImpl implements RandomData {
    
    /** underlying random number generator */
    private Random rand = null;
    
    /** underlying secure random number generator */
    private SecureRandom secRand = null;
    
    /**
     * Construct a RandomDataImpl.
     */
    public RandomDataImpl() {
    }
          
    /**
     * <strong>Algorithm Description:</strong> hex strings are generated 
     * using a 2-step process. <ol>
     * <li>
     * len/2+1 binary bytes are generated using the underlying Random</li>
     * <li>
     * Each binary byte is translated into 2 hex digits</li></ol>
     * @param len the desired string length.
     * @return the random string.
     */
    public String nextHexString(int len) {
        if (len <= 0) {
            throw new IllegalArgumentException("length must be positive");
        }
            
        //Get a random number generator
        Random ran = getRan();
        
        //Initialize output buffer
        StringBuffer outBuffer = new StringBuffer();
            
        //Get int(len/2)+1 random bytes
        byte[] randomBytes = new byte[(len / 2) + 1];
        ran.nextBytes(randomBytes);
 
        //Convert each byte to 2 hex digits
        for (int i = 0; i < randomBytes.length; i++) {
            Integer c = new Integer(randomBytes[i]);
                
            /* Add 128 to byte value to make interval 0-255 before
             * doing hex conversion.
             * This guarantees <= 2 hex digits from toHexString()
             * toHexString would otherwise add 2^32 to negative arguments.
             */
             String hex = Integer.toHexString(c.intValue() + 128);
                
             // Make sure we add 2 hex digits for each byte
             if (hex.length() == 1)  {
                 hex = "0" + hex;
             }
             outBuffer.append(hex);
        }
        return outBuffer.toString().substring(0, len);
    }

    /**
     * Generate a random int value uniformly distributed between
     * <code>lower</code> and <code>upper</code>, inclusive.
     * @param lower the lower bound.
     * @param upper the upper bound.
     * @return the random integer.
     */       
    public int nextInt(int lower, int upper) {
        if (lower >= upper) {
            throw new IllegalArgumentException
                ("upper bound must be > lower bound");
        }
        Random rand = getRan();
        return lower + (int) (rand.nextDouble() * (upper - lower + 1));
    }
    
    /**
     * Generate a random long value uniformly distributed between
     * <code>lower</code> and <code>upper</code>, inclusive.
     * @param lower the lower bound.
     * @param upper the upper bound.
     * @return the random integer.
     */       
    public long nextLong(long lower, long upper) {
        if (lower >= upper) {
            throw new IllegalArgumentException
                ("upper bound must be > lower bound");
        }
        Random rand = getRan();
        return lower + (long) (rand.nextDouble() * (upper - lower + 1));
    }
    
     /**
     * <strong>Algorithm Description:</strong> hex strings are generated in 
     * 40-byte segments using a 3-step process. <ol>
     * <li>
     * 20 random bytes are generated using the underlying 
     * <code>SecureRandom</code>.</li>
     * <li>
     * SHA-1 hash is applied to yield a 20-byte binary digest.</li>
     * <li>
     * Each byte of the binary digest is converted to 2 hex digits</li></ol>
     * <p>
     * TODO: find external reference or provide justification for the claim 
     * that this yields a cryptographically secure sequence of hex strings.
     * @param len the desired string length.
     * @return the random string.
     */
    public String nextSecureHexString(int len) {
        if (len <= 0) {
            throw new IllegalArgumentException("length must be positive");
        }
       
       // Get SecureRandom and setup Digest provider
       SecureRandom secRan = getSecRan();
       MessageDigest alg = null;
       try {
            alg = MessageDigest.getInstance("SHA-1");
       } catch (NoSuchAlgorithmException ex) {
           return null; // gulp FIXME? -- this *should* never fail.
       }
       alg.reset(); 
       
       //Compute number of iterations required (40 bytes each)
       int numIter = (len / 40) + 1;
       
       StringBuffer outBuffer = new StringBuffer();
       for (int iter = 1; iter < numIter + 1; iter++) {
            byte[] randomBytes = new byte[40];
            secRan.nextBytes(randomBytes);
            alg.update(randomBytes);
    
            //Compute hash -- will create 20-byte binary hash
            byte hash[] = alg.digest();
            
            //Loop over the hash, converting each byte to 2 hex digits
            for (int i = 0; i < hash.length; i++) {
                Integer c = new Integer(hash[i]);
        
                /* Add 128 to byte value to make interval 0-255
                 * This guarantees <= 2 hex digits from toHexString()
                 * toHexString would otherwise add 2^32 to negative 
                 * arguments
                 */
                String hex = Integer.toHexString(c.intValue() + 128);
                    
               //Keep strings uniform length -- guarantees 40 bytes
                if (hex.length() == 1) {
                    hex = "0" + hex;
                }
               outBuffer.append(hex);
            }
        }
        return outBuffer.toString().substring(0, len);
    }
     
    /**
     * Generate a random int value uniformly distributed between
     * <code>lower</code> and <code>upper</code>, inclusive.  This algorithm
     * using a secure random number generator for its engine.
     * @param lower the lower bound.
     * @param upper the upper bound.
     * @return the random integer.
     */       
    public int nextSecureInt(int lower, int upper) {
          if (lower >= upper) {
              throw new IllegalArgumentException
                ("lower bound must be < upper bound");
          }
          SecureRandom sec = getSecRan();
          return lower + (int) (sec.nextDouble() * (upper - lower + 1));
    }
     
    /**
     * Generate a random long value uniformly distributed between
     * <code>lower</code> and <code>upper</code>, inclusive.  This algorithm
     * using a secure random number generator for its engine.
     * @param lower the lower bound.
     * @param upper the upper bound.
     * @return the random integer.
     */       
    public long nextSecureLong(long lower, long upper) {
        if (lower >= upper) {
            throw new IllegalArgumentException
            ("lower bound must be < upper bound");
        }
        SecureRandom sec = getSecRan();
        return lower + (long) (sec.nextDouble() * (upper - lower + 1));
    }
    
    /** 
     * <strong>Algorithm Description</strong>:
     * Uses simulation of a Poisson process using Uniform deviates, as 
     * described 
     * <a href ="http://dmawww.epfl.ch/benarous/Pmmi/interactive/rng7.htm">
     * here</a>
     * @param mean mean of the Poisson distribution.
     * @return the random Poisson value.
     */
    public long nextPoisson(double mean) {
        if (mean <= 0) {
            throw new IllegalArgumentException("Poisson mean must be > 0");
        }
        double p = Math.exp(-mean);
        long n = 0;
        double r = 1.0d;
        Random rand = getRan();
        while (true) {
            double rnd = rand.nextDouble();
            r = r * rnd;
            if (r >= p) {
                n++;
            } else {
                return n;
            }
        }
    }
    
    /**
     * Generate a random value from a Normal distribution.  This algorithm 
     * generates random values for the general Normal distribution with the
     * given mean, <code>mu</code> and the given standard deviation,
     * <code>sigma</code>.
     * @param mu the mean of the distribution.
     * @param sigma the standard deviation of the distribution.
     * @return the random Normal value.
     */
    public double nextGaussian(double mu, double sigma) {
        if (sigma <= 0) {
            throw new IllegalArgumentException("Gaussian std dev must be > 0");
        }
        Random rand = getRan();
        return sigma * rand.nextGaussian() + mu;
    }
    
    /**
     * <strong>Algorithm Description</strong>:  Uses the 
     * <a href="http://www.jesus.ox.ac.uk/~clifford/a5/chap1/node5.html"> 
     * Inversion Method</a> to generate exponential from uniform deviates.
     * @param mean the mean of the distribution.
     * @return the random Exponential value.
     */
    public double nextExponential(double mean)  {
        if (mean < 0.0)  {
            throw new IllegalArgumentException
                ("Exponential mean must be >= 0");
        }
        Random rand = getRan();
        double unif = rand.nextDouble();
        while (unif == 0.0d) {
            unif = rand.nextDouble();
        }
        return -mean * Math.log(unif);
    }
    
    /**
     * <strong>Algorithm Description</strong>: scales the output of 
     * Random.nextDouble(), but rejects 0 values (i.e., will generate another
     * random double if Random.nextDouble() returns 0). 
     * This is necessary to provide a symmetric output interval 
     * (both endpoints excluded).
     * @param lower the lower bound.
     * @param upper the upper bound.
     * @return the random value.
     */
    public double nextUniform(double lower, double upper) {
        if (lower >= upper) {
            throw new IllegalArgumentException
            ("lower bound must be <= upper bound");
        }
        Random rand = getRan();
        
        // insure nextDouble() isn't 0.0
        double u = rand.nextDouble();
        while(u <= 0.0){
            u = rand.nextDouble();
        }
        
        return lower + u * (upper - lower);
    }
    
    /** 
     * Returns the static Random used to generate random data.
     * <p>
     * Creates and initializes if null.
     * 
     * @return the static Random used to generate random data
     */
    private Random getRan() {
        if (rand == null) {
            rand = new Random();
            rand.setSeed(System.currentTimeMillis());
        }
        return rand;
    }
    
    /** 
     * Returns the static SecureRandom used to generate secure random data.
     * <p>
     * Creates and initializes if null.
     *
     * @return the static SecureRandom used to generate secure random data
     */
    private SecureRandom getSecRan() {
        if (secRand == null) {
            secRand = new SecureRandom();
            secRand.setSeed(System.currentTimeMillis());
        }
        return secRand;
    }
    
    /**
     * Reseeds the random number generator with the supplied seed.
     * <p>
     * Will create and initialize if null.
     *
     * @param seed the seed value to use
     */
    public void reSeed(long seed) {
        if (rand == null) {
            rand = new Random();
        }
        rand.setSeed(seed);
    }
    
    /**
     * Reseeds the secure random number generator with the current time
     * in milliseconds. 
     * <p> 
     * Will create and initialize if null.
     */
    public void reSeedSecure() {
        if (secRand == null) {
            secRand = new SecureRandom();
        }
        secRand.setSeed(System.currentTimeMillis());
    }
    
    /**
     * Reseeds the secure random number generator with the supplied seed.
     * <p>
     * Will create and initialize if null.
     *
     * @param seed the seed value to use
     */
    public void reSeedSecure(long seed) {
        if (secRand == null) {
            secRand = new SecureRandom();
        }
        secRand.setSeed(seed);
    }
    
    /**
     * Reseeds the random number generator with the current time
     * in milliseconds.
     */
    public void reSeed() {
        if (rand == null) {
            rand = new Random();
        }
        rand.setSeed(System.currentTimeMillis());
    }
    
    /**
     * Sets the PRNG algorithm for the underlying SecureRandom instance
     * using the Security Provider API.  The Security Provider API is defined in 
     * <a href="http://java.sun.com/j2se/1.3/docs/guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification & Reference.</a>
     * <p>
     * <strong>USAGE NOTE:</strong> This method carries <i>significant</i> 
     * overhead and may take several seconds to execute.
     * </p>
     *
     * @param algorithm the name of the PRNG algorithm
     * @param provider the name of the provider 
     * @throws NoSuchAlgorithmException if the specified algorithm 
     * is not available
     * @throws NoSuchProviderException if the specified provider 
     * is not installed
     */
    public void setSecureAlgorithm(String algorithm, String provider) 
        throws NoSuchAlgorithmException, NoSuchProviderException {
        secRand = SecureRandom.getInstance(algorithm, provider);
    }
    
    /**
     * Uses a 2-cycle permutation shuffle to generate a random permutation.
     * The shuffling process is described
     * <a href=http://www.maths.abdn.ac.uk/~igc/tch/mx4002/notes/node83.html>
     * here</a>.
     * @param n the population size.
     * @param k the number to choose.
     * @return the random permutation.
     */
    public int[] nextPermutation(int n, int k) {
        if (k > n) {
            throw new IllegalArgumentException
                ("permutation k exceeds n");
        }       
        if (k == 0) {
            throw new IllegalArgumentException
                ("permutation k must be > 0");
        }
        
        int[] index = getNatural(n);
        shuffle(index, n - k);
        int[] result = new int[k];
        for (int i = 0; i < k; i++) {
            result[i] = index[n - i - 1];
        }
  
        return result;
    }
    
    /**
     * Uses a 2-cycle permutation shuffle to generate a random permutation.
     * <strong>Algorithm Description</strong>: Uses a 2-cycle permutation 
     * shuffle to generate a random permutation of <code>c.size()</code> and 
     * then returns the elements whose indexes correspond to the elements of 
     * the generated permutation.  
     * This technique is described, and proven to generate random samples, 
     * <a href="http://www.maths.abdn.ac.uk/~igc/tch/mx4002/notes/node83.html">
     * here</a>
     * @param c Collection to sample from.
     * @param k sample size.
     * @return the random sample.
     */ 
    public Object[] nextSample(Collection c, int k) {
        int len = c.size();
        if (k > len) {
            throw new IllegalArgumentException
                ("sample size exceeds collection size");
        }
        if (k == 0) {
            throw new IllegalArgumentException
                ("sample size must be > 0");
        }
            
       Object[] objects = c.toArray();
       int[] index = nextPermutation(len, k);
       Object[] result = new Object[k];
       for (int i = 0; i < k; i++) {
           result[i] = objects[index[i]];
       }  
       return result;
    }
    
    //------------------------Private methods----------------------------------
    
    /** 
     * Uses a 2-cycle permutation shuffle to randomly re-order the last elements
     * of list.
     * 
     * @param list list to be shuffled
     * @param end element past which shuffling begins
     */
    private void shuffle(int[] list, int end) {
        int target = 0;
        for (int i = list.length - 1 ; i >= end; i--) {
            if (i == 0) {
                target = 0; 
            } else {
                target = nextInt(0, i);
            }
            int temp = list[target];
            list[target] = list[i];
            list[i] = temp;
        }      
    }
    
    /**
     * Returns an array representing n.
     *
     * @param n the natural number to represent
     * @return array with entries = elements of n
     */
    private int[] getNatural(int n) {
        int[] natural = new int[n];
        for (int i = 0; i < n; i++) {
            natural[i] = i;
        }
        return natural;
    }
        
}
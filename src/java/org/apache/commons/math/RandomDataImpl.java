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

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Random;

/**
 * Implements the <code>RandomData</code> interface using 
 * <code>java.util.Random</code> and 
 * <code>java.util.Random.SecureRandom</code> instances to generate data.
 * Supports reseeding the underlying 
 * <a href=http://www.wikipedia.org/wiki/Pseudo-random_number_generator>PRNG</a>. 
 * The <code>SecurityProvider</code> and <code>Algorithm</code>
 * used by the <code>SecureRandom</code> instance can also be reset.<p>
 * For details on the PRNGs, see the JDK documentation for 
 * <code>java.util.Random</code> and 
 * <code>java.util.Random.SecureRandom</code></p><p>
 * <strong>Usage Notes</strong>: <ul>
 * <li>Instance variables are used to maintain <code>Random</code> and 
 * <code>SecureRandom</code> instances used in data generation. Therefore,
 * to generate a random sequence of values or strings, you should use just
 * <strong>one</strong> <code>RandomDataImpl</code> instance repeatedly.</li>
 * <li>The "secure" methods are *much* slower.  These should be used only when
 * a <a href=http://www.wikipedia.org/wiki/Cryptographically_secure_pseudo-random_number_generator>
 * Secure Random Sequence</a> is required.</li>
 *<li>When a new <code>RandomDataImpl</code> is created, the underlying random
 * number generators are <strong>not</strong> intialized.  The first call to a
 * data generation method, or to a <code>reSeed()</code> method instantiates
 * the appropriate generator.  If you do not explicitly seed the generator, it
 * is by default seeded with the current time in milliseconds</li>
 * <li>The <code>reSeed</code> and <code>reSeedSecure</code> methods delegate to
 * the corresponding methods on the underlying <code>Random</code> and <code>
 * SecureRandom</code> instances.  Therefore, the contracts of these methods
 * are as defined in the JDK documentation.  In particular, <code>reSeed(long)
 * </code> fully resets the initial state of the non-secure random number 
 * generator (so that reseeding with a specific value always results in the
 * same subsequent random sequence); whereas reSeedSecure(long) does <strong> not 
 * </strong> reinitialize the secure random number generator (so secure sequences
 * started with calls to reseedSecure(long) won't be identical).</li></ul>
 *</p>
 * 
 * @author Phil Steitz
 * @version $Revision: 1.1 $ $Date: 2003/05/18 00:58:51 $
 */
public class RandomDataImpl implements RandomData{
    
    /** underlying random number generator */
    private Random rand = null;
    
    /** underlying secure random number generator */
    private SecureRandom secRand = null;
    
    public RandomDataImpl(){
    }
          
    /**
     * Generates a random string of hex characters
     * If cryptographic security is required, use 
     * <code>nextSecureHexString()</code>.<br>
     * <strong>Algorithm Description:</strong> hex strings are generated 
     * using a 2-step process. <ol>
     * <li>len/2+1 binary bytes are generated using the underlying Random</li>
     * <li>Each binary byte is translated into 2 hex digits</li></ol>
     * @param len length of return string
     * @exception IllegalArgumentException thrown if len <= 0 
     * @return the random hex string
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
             String hex = Integer.toHexString(c.intValue()+128);
                
             // Make sure we add 2 hex digits for each byte
             if (hex.length() == 1) hex = "0" + hex;
             outBuffer.append(hex);
        }
        return outBuffer.toString().substring(0, len);
    }
    
     
    public int nextInt(int lower, int upper) {
        if (lower >= upper) {
            throw new IllegalArgumentException
                ("incorrect bounds for rendomInt");
        }
        Random rand = getRan();
        return lower + (int)(Math.random() * (upper-lower+1));
    }
    
    public long nextLong(long lower, long upper) {
        if (lower >= upper) {
            throw new IllegalArgumentException
                ("upper bound must be >= lower bound");
        }
        Random rand = getRan();
        return lower + (long)(rand.nextDouble() * (upper-lower+1));
    }
    
     /**
     * Generates a random string of hex characters from a secure random sequence.
     * If cryptographic security is not required, 
     * use <code>nextHexString()</code>.<br>
     * <strong>Algorithm Description:</strong> hex strings are generated in 40-byte
     * segments using a 3-step process. <ol>
     * <li>20 random bytes are generated using the underlying SecureRandom</li>
     * <li>SHA-1 hash is applied to yield a 20-byte binary digest</li>
     * <li>Each byte of the binary digest is converted to 2 hex digits</li></ol><p>
     * TODO: find external reference or provide justification for the claim that this
     * yields a cryptographically secure sequence of hex strings.</p>
     * @param len length of return string
     * @exception IllegalArgumentException thrown if len <= 0 
     * @return the random hex string
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
           return null; // gulp FIXME? -- this *should* never fail. OK to swallow????
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
                String hex = Integer.toHexString(c.intValue()+128);
                    
               //Keep strings uniform length -- guarantees 40 bytes
               if (hex.length() == 1) hex = "0" + hex;
               outBuffer.append(hex);
            }
        }
        return outBuffer.toString().substring(0, len);
    }
    
    public int nextSecureInt(int lower, int upper) {
          if (lower >= upper) {
              throw new IllegalArgumentException
                ("lower bound must be <= upper bound");
          }
          SecureRandom sec = getSecRan();
          return lower + (int)(sec.nextDouble() * (upper-lower+1));
    }
    
    
    public long nextSecureLong(long lower, long upper) {
        if (lower >= upper) {
            throw new IllegalArgumentException
            ("lower bound must be <= upper bound");
        }
        SecureRandom sec = getSecRan();
        return lower + (long)(sec.nextDouble() * (upper-lower+1));
    }
    
    /** 
     * Generates a random value from the Poisson distribution with 
     * the given mean.<br>
     * <strong>Definition</strong>: 
     * <a href=http://www.itl.nist.gov/div898/handbook/eda/section3/eda366j.htm>
     * Poisson Distribution</a><br>
     * <strong>Algorithm Description</strong>:
     * Uses simulation of a Poisson process using Uniform deviates, as described 
     * <a href = http://dmawww.epfl.ch/benarous/Pmmi/interactive/rng7.htm>
     * here</a>
     * @param mean Mean of the distribution
     * @returns long
     * @throws IllegalArgumentException if mean <= 0
     */
    public long nextPoisson(double mean) {
        double p = Math.exp(-mean);
        long n = 0;
        double r = 1.0d;
        Random rand = getRan();
        if (mean <= 0) {
            throw new IllegalArgumentException("Poisson mean must be > 0");
        }
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
    
    public double nextGaussian(double mu,double sigma) {
        if (sigma <= 0) {
            throw new IllegalArgumentException("Gaussian std dev must be > 0");
        }
        Random rand = getRan();
        return sigma*rand.nextGaussian() + mu;
    }
    
    /**
     * Generates a random value from the exponential distribution
     * with expected value = <code>mean</code><br>
     * <strong>Definition</strong>: 
     * <a href=http://www.itl.nist.gov/div898/handbook/eda/section3/eda3667.htm>
     * Exponential Distribution</a><br>
     * <strong>Preconditions</strong>: <ul>
     * <li>The specified mean <i>must</i> be non-negative</li>
     * </ul>
     * <strong>Algorithm Description</strong>:  Uses the 
     * <a href=http://www.jesus.ox.ac.uk/~clifford/a5/chap1/node5.html> 
     * Inversion Method</a> to generate exponential from uniform deviates.
     * @param mu Mean of the distribution
     * @return random value from exponential distribution
     */
    public double nextExponential(double mean)  {
        if (mean < 0.0) throw new IllegalArgumentException
            ("Exponential mean must be >= 0");
        Random rand = getRan();
        double unif = rand.nextDouble();
        while (unif == 0.0d) {
            unif = rand.nextDouble();
        }
        return -mean*Math.log(unif);
    }
    
    /**
     * Generates a uniformly distributed random value from the open interval
     * (<code>lower</code>,<code>upper</code>) (i.e., endpoints excluded)
     * <strong>Definition</strong>: 
     * <a href=http://www.itl.nist.gov/div898/handbook/eda/section3/eda3662.htm>
     * Uniform Distribution</a> <code>lower</code> and <code>upper - lower</code>
     * are the 
     * <a href = http://www.itl.nist.gov/div898/handbook/eda/section3/eda364.htm>
     * location and scale parameters</a>, respectively<br>
     * <strong>Algorithm Description</strong>: scales the output of 
     * Random.nextDouble(), but rejects 0 values (i.e., will generate another
     * random double if Random.nextDouble() returns 0).  This is necessary to
     * provide a symmetric output interval (both endpoints excluded).
     * @param lower lower endpoint of the interval of support
     * @param upper upper endpoint of the interval of support
     * @return uniformly distributed random value between lower
     * and upper (exclusive)
     * @exception IllegalArgumentException thrown if
     * <code>lower</code> is not strictly less than <code>upper</code>.
     */
    public double nextUniform(double lower, double upper) {
        if (lower >= upper) {
            throw new IllegalArgumentException
            ("lower bound must be <= upper bound");
        }
        Random rand = getRan();
        double result = lower + rand.nextDouble()*(upper-lower);
        while (result == lower) {
              result = lower + rand.nextDouble()*(upper-lower);
        }
        return result;   
    }
    
    /** 
     * Returns the static Random used to generate random data.<br>
     * Creates and initializes if null
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
     * Returns the static SecureRandom used to generate secure random data.<br>
     * Creates and initializes if null.
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
     * Reseeds the random number generator with the supplied seed.  Will
     * create and initialize if null.
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
     * in milliseconds.  Will create and initialize if null.
     */
    public void reSeedSecure() {
        if (rand == null) {
            rand = new Random();
        }
        rand.setSeed(System.currentTimeMillis());
    }
    
    /**
     * Reseeds the secure random number generator with the supplied seed.
     * Will create and initialize if null.
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
     * in milliseconds
     */
    public void reSeed() {
        if (rand == null) {
            rand = new Random();
        }
        rand.setSeed(System.currentTimeMillis());
    }
    
    /**
     * Sets the PRNG algorithm for the underlying SecureRandom instance
     * using the Security Provider API, as defined in 
     * <a href=http://java.sun.com/j2se/1.3/docs/guide/security/CryptoSpec.html#AppA>
     * Java Cryptography Architecture API Specification & Reference</a><p>
     * <strong>USAGE NOTE:</strong> This method carries <i>significant</i> overhead
     * and may take several seconds to execute.</p>
     * @param algorithm the name of the PRNG algorithm
     * @param provider the name of the provider 
     * @throws NoSuchAlgorithmException if the specified algorithm is not available
     * @throws NoSuchProviderException if the specified provider is not installed
     */
    public void setSecureAlgorithm(String algorithm, String provider) 
        throws NoSuchAlgorithmException,NoSuchProviderException {
        secRand = SecureRandom.getInstance(algorithm,provider);
    }
        
}
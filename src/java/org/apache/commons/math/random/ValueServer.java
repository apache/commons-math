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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.net.URL;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Generates values for use in simulation applications.
 * <p>
 * How values are generated is determined by the <code>mode</code>
 * property. 
 * <p> 
 * Supported <code>mode</code> values are: <ul>
 * <li> DIGEST_MODE -- uses an empirical distribution </li>
 * <li> REPLAY_MODE -- replays data from <code>valuesFile</code></li> 
 * <li> UNIFORM_MODE -- generates uniformly distributed random values with
 *                      mean = <code>mu</code> </li>
 * <li> EXPONENTIAL_MODE -- generates exponentially distributed random values
 *                         with mean = <code>mu</code></li>
 * <li> GAUSSIAN_MODE -- generates Gaussian distributed random values with
 *                       mean = <code>mu</code> and 
 *                       standard deviation = <code>sigma</code></li>
 * <li> CONSTANT_MODE -- returns <code>mu</code> every time.</li></ul> 
 *
 * @version $Revision: 1.4 $ $Date: 2003/10/13 08:10:01 $
 *
 */
public class ValueServer {
    /** mode determines how values are generated */
    private int mode = 5;
    
    /** URI to raw data values  */
    private URL valuesFileURL = null;
    
    /** Mean for use with non-data-driven modes */
    private double mu = 0.0;
    
    /** Standard deviation for use with GAUSSIAN_MODE */
    private double sigma = 0.0;
    
    /** Empirical probability distribution for use with DIGEST_MODE */
    private EmpiricalDistribution empiricalDistribution = null;
    
    /** file pointer for REPLAY_MODE */
    private BufferedReader filePointer = null;
    
    /** RandomDataImpl to use for random data generation */
    private RandomDataImpl randomData = new RandomDataImpl();
    
    // Data generation modes ======================================
   
    /** Use empirical distribution  */
    public static final int DIGEST_MODE = 0;        
    
    /** Replay data from valuesFilePath */
    public static final int REPLAY_MODE = 1;      
    
    /** Uniform random deviates with mean = mu */
    public static final int UNIFORM_MODE = 2;    
    
    /** Exponential random deviates with mean = mu */
    public static final int EXPONENTIAL_MODE = 3;  
    
    /** Gaussian random deviates with mean = mu, std dev = sigma */
    public static final int GAUSSIAN_MODE = 4;  
    
    /** Always return mu */
    public static final int CONSTANT_MODE = 5;   
    
    /** Creates new ValueServer */
    public ValueServer() {
    }

    /** 
     * Returns the next generated value, generated according
     * to the mode value (see MODE constants). 
     *
     * @return generated value 
     * @throws IOException in REPLAY_MODE if a file I/O error occurs
     */
    public double getNext() throws IOException {
        switch (mode) {
            case DIGEST_MODE: return getNextDigest();
            case REPLAY_MODE: return getNextReplay();
            case UNIFORM_MODE: return getNextUniform();
            case EXPONENTIAL_MODE: return getNextExponential();
            case GAUSSIAN_MODE: return getNextGaussian();
            case CONSTANT_MODE: return mu;
            default: throw new IllegalStateException
                       ("Bad mode: " + mode);
        }
    }
    
    /**
     * Fills the input array with values generated using getNext() repeatedly.
     *
     * @param values array to be filled
     * @throws IOException in REPLAY_MODE if a file I/O error occurs
     */
    public void fill(double[] values) throws IOException {
        for (int i = 0; i < values.length; i++) {
            values[i] = getNext();
        }
    }
    
    /**
     * Returns an array of length <code>length</code> with values generated 
     * using getNext() repeatedly.
     *
     * @param length length of output array
     * @return array of generated values
     * @throws IOException in REPLAY_MODE if a file I/O error occurs
     */
    public double[] fill(int length) throws IOException {
        double[] out = new double[length];
        for (int i = 0; i < length; i++) {
            out[i] = getNext();
        }
        return out;
    }       
    
    /** 
     * Computes the empirical distribution using values from the file
     * in <code>valuesFilePath</code>, using the default number of bins.
     * <p>
     * <code>valuesFileURL</code> must exist and be
     * readable by *this at runtime.
     * <p>
     * This method must be called before using <code>getNext()</code>
     * with <code>mode = DISGEST_MODE</code>
     *
     * @throws IOException if an I/O error occurs reading the input file
     */
    public void computeDistribution() throws IOException {
        empiricalDistribution = new EmpiricalDistributionImpl();
        empiricalDistribution.load(valuesFileURL.getFile());
    }
    
    /** 
     * Computes the empirical distribution using values from the file
     * in <code>valuesFilePath</code> and <code>binCount</code> bins.
     * <p>
     * <code>valuesFileURL</code> must exist and be
     * readable by *this at runtime.
     * <p>
     * This method must be called before using <code>getNext()</code>
     * with <code>mode = DISGEST_MODE</code>
     *
     * @param binCount the number of bins used in computing the empirical
     * distribution
     * @throws IOException if an error occurs reading the input file
     */
    public void computeDistribution(int binCount) 
            throws IOException {
        empiricalDistribution = new EmpiricalDistributionImpl(binCount);
        empiricalDistribution.load(valuesFileURL.getFile());
        mu = empiricalDistribution.getSampleStats().getMean();
        sigma = empiricalDistribution.getSampleStats().getStandardDeviation();
    }
    
    /** Getter for property mode.
     * @return Value of property mode.
     */
    public int getMode() {
        return mode;
    }
    
    /** Setter for property mode.
     * @param mode New value of property mode.
     */
    public void setMode(int mode) {
        this.mode = mode;
    }
    
    /** Getter for property valuesFilePath.
     * @return Value of property valuesFilePath.
     */
    public String getValuesFileURL() {
        return valuesFileURL.toString();
    }
    
    /** Setter for property valuesFilePath.
     * @param url New value of property valuesFilePath.
     * @throws MalformedURLException if url is not well formed
     */
    public void setValuesFileURL(String url) throws MalformedURLException {
        this.valuesFileURL = new URL(url);
    }
    
    /** Getter for property empiricalDistribution.
     * @return Value of property empiricalDistribution.
     */
    public EmpiricalDistribution getEmpiricalDistribution() {
        return empiricalDistribution;
    }    
    
    /**  
     * Opens <code>valuesFilePath</code> to use in REPLAY_MODE.
     *
     * @throws IOException if an error occurs opening the file
     */
    public void openReplayFile() throws IOException {
        filePointer = new BufferedReader(new FileReader
                            (new File(valuesFileURL.getFile())));
    }
    
    /** 
     * Closes <code>valuesFilePath</code> after use in REPLAY_MODE.
     *
     * @throws IOException if an error occurs closing the file
     */
    public void closeReplayFile() throws IOException {
        if (filePointer != null) {
            filePointer.close();
            filePointer = null;
        }     
    }
    
    /** Getter for property mu.
     * @return Value of property mu.
     */
    public double getMu() {
        return mu;
    }
    
    /** Setter for property mu.
     * @param mu New value of property mu.
     */
    public void setMu(double mu) {
        this.mu = mu;
    }
    
    /** Getter for property sigma.
     * @return Value of property sigma.
     */
    public double getSigma() {
        return sigma;
    }
    
    /** Setter for property sigma.
     * @param sigma New value of property sigma.
     */
    public void setSigma(double sigma) {
        this.sigma = sigma;
    }
    
    //------------- private methods ---------------------------------
    
    /** 
     * Gets a random value in DIGEST_MODE.
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>Before this method is called, <code>computeDistribution()</code>
     * must have completed successfully; otherwise an 
     * <code>IllegalStateException</code> will be thrown</li></ul>
     *
     * @return next random value from the empirical distribution digest 
     */
    private double getNextDigest() {
        if ((empiricalDistribution == null) ||
            (empiricalDistribution.getBinStats().size() == 0)) {
            throw new IllegalStateException("Digest not initialized");
        }
        return empiricalDistribution.getNextValue();     
    }
    
    /**
     * Gets next sequential value from the <code>valuesFilePath</code> 
     * opened by <code>openReplayFile()</code>.
     * <p>
     * Throws an IOException if <code>filePointer</code> is null or read fails.
     * Will wrap around to BOF is EOF is encountered.
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li> openReplayfile() must have completed successfully before 
     * invoking this method; otherwise an <code>IlleglaStateException</code>
     * will be thrown</li></ul>
     *
     * @return next value from the replay file
     * @throws IOException if there is a problem reading from the file
     */
    private double getNextReplay() throws IOException {
        String str = null;
        if (filePointer == null) {
            throw new IllegalStateException("replay file not open");
        }
        if ((str = filePointer.readLine()) == null) {
            closeReplayFile();
            openReplayFile();
            str = filePointer.readLine();
        }         
        return new Double(str).doubleValue();
    }
    
    /** 
     * Gets a uniformly distributed random value with mean = mu. 
     *
     * @return random uniform value
     */
    private double getNextUniform() {
        return randomData.nextUniform(0, 2 * mu);
    }
    
    /** 
     * Gets an exponentially distributed random value with mean = mu. 
     *
     * @return random exponential value
     */
    private double getNextExponential() {
        return randomData.nextExponential(mu);    
    }
    
    /** 
     * Gets a Gaussian distributed random value with mean = mu
     * and standard deviation = sigma.
     *
     * @return random Gaussian value
     */
    private double getNextGaussian() {
        return randomData.nextGaussian(mu, sigma);
    }
    
}

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

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

import org.apache.commons.math.stat.Univariate;

/**
 * Represents an <a href="http://random.mat.sbg.ac.at/~ste/dipl/node11.html">
 * empirical probability distribution</a> -- a probability distribution derived
 * from observed data without making any assumptions about the functional form
 * of the population distribution that the data come from.<p>
 * Implementations of this interface maintain data structures, called 
 * <i>distribution digests</i>, that describe empirical distributions and 
 * support the following operations: <ul>
 * <li>loading the distribution from a file of observed data values</li>
 * <li>saving and re-loading distribution digests to/from "digest files" </li>
 * <li>dividing the input data into "bin ranges" and reporting bin frequency
 *     counts (data for histogram)</li>
 * <li>reporting univariate statistics describing the full set of data values
 *     as well as the observations within each bin</li>
 * <li>generating random values from the distribution</li>
 * </ul>
 * Applications can use <code>EmpiricalDistribution</code> implementations to 
 * build grouped frequnecy histograms representing the input data or to
 * generate random values "like" those in the input file -- i.e., the values
 * generated will follow the distribution of the values in the file.
 * @version $Revision: 1.6 $ $Date: 2003/10/13 08:10:01 $
 */
public interface EmpiricalDistribution {
    
    /**
     * Computes the empirical distribution from the input file
     * @param filePath fully qualified name of a file in the local file system
     * @throws IOException if an IO error occurs
     */
    void load(String filePath) throws IOException; 
    
    /**
     * Computes the empirical distribution from the input file
     * @param file url of the input file
     * @throws IOException if an IO error occurs
     */
    void load(File file) throws IOException;
    
    /** 
     * Generates a random value from this distribution.
     * <strong>Preconditions:</strong><ul>
     * <li>the distribution must be loaded before invoking this method</li></ul>
     * @return the random value.
     * @throws IllegalStateException if the distribution has not been loaded
     */
    double getNextValue() throws IllegalStateException;  
    
     
    /** 
     * Returns a Univariate describing this distribution.
     * <strong>Preconditions:</strong><ul>
     * <li>the distribution must be loaded before invoking this method</li></ul>
     * @return the sample statistics
     * @throws IllegalStateException if the distribution has not been loaded
     */
    Univariate getSampleStats() throws IllegalStateException;
    
    /** 
     * Loads a saved distribution from a file.
     * @param file File reference for a file containing a digested distribution
     * @throws IOException if an error occurs reading the file
     */
    void loadDistribution(File file) throws IOException;  
    
    /** 
     * Loads a saved distribution from a file.
     * @param filePath fully qualified file path for a file 
     * containing a digested distribution 
     * @throws IOException if an error occurs reading the file
     */
    void loadDistribution(String filePath) throws IOException; 
    
    /** 
     * Saves distribution to a file. Overwrites the file if it exists.
     * <strong>Preconditions:</strong><ul>
     * <li>the distribution must be loaded before invoking this method</li></ul>
     * @param filePath fully qualified file path for the file to be written
     * @throws IOException if an error occurs reading the file
     * @throws IllegalStateException if the distribution has not been loaded
     */
    void saveDistribution(String filePath) throws 
        IOException,IllegalStateException;
    
    /** 
     * Saves distribution to a file. Overwrites the file if it exists.
     * <strong>Preconditions:</strong><ul>
     * <li>the distribution must be loaded before invoking this method</li></ul>
     * @param file File reference for the file to be written
     * @throws IOException if an error occurs reading the file
     * @throws IllegalStateException if the distribution has not been loaded
     */
    void saveDistribution(File file) throws IOException,IllegalStateException;
    
    /**
     * property indicating whether or not the distribution has been loaded
     * @return true if the distribution has been loaded
     */
    boolean isLoaded();  
    
     /** 
     * Returns the number of bins
     * @return the number of bins.
     */
    int getBinCount();
    
    /** 
     * Returns a list of Univariates containing statistics describing the
     * values in each of the bins.  The ArrayList is indexed on the bin number.
     * @return ArrayList of bin statistics.
     */
    ArrayList getBinStats();
    
    /** 
     * Returns the array of upper bounds for the bins.  Bins are: <br/>
     * [min,upperBounds[0]],(upperBounds[0],upperBounds[1]],...,
     *  (upperBounds[binCount-1],max]
     * @return array of bin upper bounds
     */
    double[] getUpperBounds();
    
}

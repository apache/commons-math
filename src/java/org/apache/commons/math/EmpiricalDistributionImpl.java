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

import java.util.ArrayList;
import java.io.Serializable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

/**
 * Implements <code>EmpiricalDistribution</code> interface using 
 * what amounts to the 
 * <a href=http://nedwww.ipac.caltech.edu/level5/March02/Silverman/Silver2_6.html>
 * Variable Kernel Method</a> with Gaussian smoothing:<p>
 * <strong>Digesting the input file</strong>
 * <ol><li>Pass the file once to compute min and max.</li>  
 * <li>Divide the range from min-max into <code>binCount</code> "bins."</li>
 * <li>Pass the data file again, computing bin counts and univariate
 *     statistics (mean, std dev.) for each of the bins </li>
 * <li>Divide the interval (0,1) into subintervals associated with the bins,
 *     with the length of a bin's subinterval proportional to its count.</li></ol>
 * <strong>Generating random values from the distribution</strong><ol>
 * <li>Generate a uniformly distributed value in (0,1) </li>
 * <li>Select the subinterval to which the value belongs.
 * <li>Generate a random Gaussian value with mean = mean of the associated
 *     bin and std dev = std dev of associated bin.</li></ol></p><p>
 *<strong>USAGE NOTES:</strong><ul>
 *<li>The <code>binCount</code> is set by default to 1000.  A good rule of thumb
 *    is to set the bin count to approximately the length of the input file divided
 *    by 10.  See TODO: add reference </li>
 *<li>The input file <i>must</i> be a plain text file containing one valid numeric
 *    entry per line.</li>
 * </ol></p>
 *
 * @author  Phil Steitz
 * @version $Revision: 1.1 $
 */
public class EmpiricalDistributionImpl implements Serializable,EmpiricalDistribution {

    
    /** List of Univariate objects characterizing the bins */
    private ArrayList binStats = null;
    
    /** Sample statistics */
    Univariate sampleStats = null;
    
    /** number of bins */
    private int binCount = 1000;
    
    /** is the distribution loaded? */
    private boolean loaded = false;
    
    /** upper bounds of subintervals in (0,1) "belonging" to the bins */
    private double[] upperBounds = null;
    
    /** 
     * Creates a new EmpiricalDistribution  with the default bin count
     */
    public EmpiricalDistributionImpl() {
        binStats = new ArrayList();
    }
    
    /** 
     * Creates a new EmpiricalDistribution  with the specified bin count
     * @param binCount number of bins
     */
    public EmpiricalDistributionImpl(int binCount) {
        this.binCount = binCount;
        binStats = new ArrayList();
    }
    
    
    public void load(String filePath) throws IOException {
        File file = new File(filePath);
        load(file);
    }
    
     
    public void load(File file) throws IOException {
        // Pass the file once to get sample stats
         BufferedReader in = null;
         try {  
            in = new BufferedReader(new FileReader(file));
            String str = null;
            double val = 0.0;
            sampleStats = new UnivariateImpl();
            while ((str = in.readLine()) != null) {
              val = new Double(str).doubleValue();
              sampleStats.addValue(val);   
            }
            in.close();
            in = null;
         } finally {
             if (in != null) try {in.close();} catch (Exception ex) {};
         }               
        
         // Load array of bin upper bounds -- evenly spaced from min - max
         double min = sampleStats.getMin();
         double max = sampleStats.getMax();
         double delta = (max - min)/(new Double(binCount)).doubleValue();
         double[] binUpperBounds = new double[binCount];
         binUpperBounds[0] = min + delta;
         for (int i = 1; i< binCount - 1; i++) {
             binUpperBounds[i] = binUpperBounds[i-1] + delta;
         }
         binUpperBounds[binCount -1] = max;
         
        // Initialize binStats ArrayList
        if (!binStats.isEmpty()) {
            binStats.clear();
        }
        for (int i = 0; i < binCount; i++) {
            Univariate stats = new UnivariateImpl();
            binStats.add(i,stats);
        }
         
        // Pass the data again, filling data in binStats Array 
         try {
            in = new BufferedReader(new FileReader(file));
            String str = null;
            double val = 0.0d;
            while ((str = in.readLine()) != null) {
              val = new Double(str).doubleValue();
              
              // Find bin and add value to binStats for the bin
              boolean found = false;
              int i = 0; 
              while (!found) {
                  if (i >= binCount) {
                      throw new RuntimeException("bin alignment error");
                  }
                  if (val <= binUpperBounds[i]) {
                      found = true;
                      Univariate stats = (Univariate)binStats.get(i);
                      stats.addValue(val);
                  }
                  i++;
              }       
            }
            in.close();
            in = null;
         } finally {
             if (in != null) try {in.close();} catch (Exception ex) {};
         }               
        
         // Assign upperBounds based on bin counts
         upperBounds = new double[binCount];
         upperBounds[0] = 
            (((Univariate)binStats.get(0)).getN())/sampleStats.getN();
         for (int i = 1; i < binCount-1; i++) {
             upperBounds[i] = upperBounds[i-1] +
               (((Univariate)binStats.get(i)).getN())/sampleStats.getN();
         }
         upperBounds[binCount-1] = 1.0d;   
         
         loaded = true;
    }
    
    /** Generates a random value from this distribution */
    public double getNextValue() throws IllegalStateException {    
        
        if (!loaded) {
            throw new IllegalStateException("distribution not loaded");
        }
        
        // Start with a uniformly distributed random number in (0,1)
        double x = Math.random();
       
        // Use this to select the bin and generate a Gaussian within the bin
        RandomData rd = new RandomDataImpl();
        for (int i = 0; i < binCount; i++) {
           if (x <= upperBounds[i]) {
               Univariate stats = (Univariate)binStats.get(i);
               if (stats.getN() > 0.5) { // really mean > 0, but avoid fp error
                   if (stats.getStandardDeviation() > 0) {  // more than one obs 
                        return rd.nextGaussian
                            (stats.getMean(),stats.getStandardDeviation());
                   } else {
                       return stats.getMean(); // only one obs in bin
                   }
               }
           }
        }
        throw new RuntimeException("No bin selected");
    }
       
    public void loadDistribution(String filePath) throws IOException {
        throw new UnsupportedOperationException("Not Implemented yet :-(");
    }
    
    public void loadDistribution(File file) throws IOException {
        throw new UnsupportedOperationException("Not Implemented yet :-(");
    }
       
    public void saveDistribution(String filePath) throws 
        IOException,IllegalStateException {
       throw new UnsupportedOperationException("Not Implemented yet :-(");
    }
    
    public void saveDistribution(File file) throws 
        IOException,IllegalStateException {
       throw new UnsupportedOperationException("Not Implemented yet :-(");
    }
        
    public Univariate getSampleStats() {
        return sampleStats;
    }
    
    public int getBinCount() {
        return binCount;
    }
      
    public ArrayList getBinStats() {
        return binStats;
    }
       
    public double[] getUpperBounds() {
        return upperBounds;
    }
    
    public boolean isLoaded() {
        return loaded;
    }
    
}

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2004 The Apache Software Foundation.  All rights
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
 *    nor may "Apache" appear in their name without prior written
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

import java.util.ArrayList;
import java.io.Serializable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.math.stat.SummaryStatistics;

/**
 * Implements <code>EmpiricalDistribution</code> interface.  This implementation
 * uses what amounts to the 
 * <a href="http://nedwww.ipac.caltech.edu/level5/March02/Silverman/Silver2_6.html">
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
 * @version $Revision: 1.16 $ $Date: 2004/02/12 04:35:08 $
 */
public class EmpiricalDistributionImpl implements Serializable, EmpiricalDistribution {

    
    /** List of DescriptiveStatistics objects characterizing the bins */
    private ArrayList binStats = null;
    
    /** Sample statistics */
    SummaryStatistics sampleStats = null;
    
    /** number of bins */
    private int binCount = 1000;
    
    /** is the distribution loaded? */
    private boolean loaded = false;
    
    /** upper bounds of subintervals in (0,1) "belonging" to the bins */
    private double[] upperBounds = null;
    
    /** RandomData instance to use in repeated calls to getNext() */
    private RandomData randomData = new RandomDataImpl();
    
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

    /**
     * @see org.apache.commons.math.random.EmpiricalDistribution#load(double[])
     */
    public void load(double[] in) {
        DataAdapter da = new ArrayDataAdapter(in);
        try {
            da.computeStats();
            fillBinStats(in);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        loaded = true;
        
    }
    
    public void load(String filePath) throws IOException {
        BufferedReader in = 
            new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));  
        try {
            DataAdapter da = new StreamDataAdapter(in);
            try {
                da.computeStats();
            } catch (Exception e) {
                throw new IOException(e.getMessage());
            }
            in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));  
            fillBinStats(in);
            loaded = true;
        } finally {
           if (in != null) try {in.close();} catch (Exception ex) {};
        }
    }
    
    public void load(URL url) throws IOException {
        BufferedReader in = 
            new BufferedReader(new InputStreamReader(url.openStream()));
        try {
            DataAdapter da = new StreamDataAdapter(in);
            try {
                da.computeStats();
            } catch (Exception e) {
                throw new IOException(e.getMessage());
            }
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            fillBinStats(in);
            loaded = true;
        } finally {
           if (in != null) try {in.close();} catch (Exception ex) {};
        }
    }
     
    public void load(File file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));
        try {
            DataAdapter da = new StreamDataAdapter(in);
            try {
                da.computeStats();
            } catch (Exception e) {
                throw new IOException(e.getMessage());
            }
            in = new BufferedReader(new FileReader(file));
            fillBinStats(in);
            loaded = true;
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (Exception ex) {
                };
        }
    }
    
    /**
     * Provides methods for computing <code>sampleStats</code> and 
     * <code>beanStats</code> abstracting the source of data. 
     */
    private abstract class DataAdapter{
        public abstract void computeBinStats(double min, double delta) 
                throws Exception;
        public abstract void computeStats() throws Exception;
    }
    /**
     * Factory of <code>DataAdapter</code> objects. For every supported source
     * of data (array of doubles, file, etc.) an instance of the proper object
     * is returned. 
     */
    private class DataAdapterFactory{
        public DataAdapter getAdapter(Object in) {
            if (in instanceof BufferedReader) {
                BufferedReader inputStream = (BufferedReader) in;
                return new StreamDataAdapter(inputStream);
            } else if (in instanceof double[]) {
                double[] inputArray = (double[]) in;
                return new ArrayDataAdapter(inputArray);
            } else {
                throw new IllegalArgumentException(
                    "Input data comes from the" + " unsupported source");
            }
        }
    }
    /**
     * <code>DataAdapter</code> for data provided through some input stream
     */
    private class StreamDataAdapter extends DataAdapter{
        BufferedReader inputStream;
        public StreamDataAdapter(BufferedReader in){
            super();
            inputStream = in;
        }
        /**
         * Computes binStats
         */
        public void computeBinStats(double min, double delta) 
                throws IOException {
            String str = null;
            double val = 0.0d;
            while ((str = inputStream.readLine()) != null) {
                val = Double.parseDouble(str);
                SummaryStatistics stats =
                    (SummaryStatistics) binStats.get(
                        Math.max((int) Math.ceil((val - min) / delta) - 1, 0));
                stats.addValue(val);
            }

            inputStream.close();
            inputStream = null;
        }
        /**
         * Computes sampleStats
         */
        public void computeStats() throws IOException {
            String str = null;
            double val = 0.0;
            sampleStats = SummaryStatistics.newInstance();
            while ((str = inputStream.readLine()) != null) {
                val = new Double(str).doubleValue();
                sampleStats.addValue(val);
            }
            inputStream.close();
            inputStream = null;
        }
    }

    /**
     * <code>DataAdapter</code> for data provided as array of doubles.
     */
    private class ArrayDataAdapter extends DataAdapter{
        private double[] inputArray;
        public ArrayDataAdapter(double[] in){
            super();
            inputArray = in;
        }
        /**
         * Computes sampleStats
         */
        public void computeStats() throws IOException {
            sampleStats = SummaryStatistics.newInstance();
            for (int i = 0; i < inputArray.length; i++) {
                sampleStats.addValue(inputArray[i]);
            }
        }
        /**
         * Computes binStats
         */
        public void computeBinStats(double min, double delta)
            throws IOException {
            for (int i = 0; i < inputArray.length; i++) {
                SummaryStatistics stats =
                    (SummaryStatistics) binStats.get(
                        Math.max((int) Math.ceil((inputArray[i] - min) / delta) 
                            - 1, 0));
                stats.addValue(inputArray[i]);
            }
        }    
    }

    /**
     * Fills binStats array (second pass through data file).
     */
    private void fillBinStats(Object in) throws IOException {    
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
            SummaryStatistics stats = SummaryStatistics.newInstance();
            binStats.add(i,stats);
        }
        
        // Filling data in binStats Array
        DataAdapterFactory aFactory = new DataAdapterFactory();
        DataAdapter da = aFactory.getAdapter(in);
        try {
            da.computeBinStats(min, delta);
        } catch (Exception e) {
            if(e instanceof RuntimeException){
                throw new RuntimeException(e.getMessage());
            }else{
                throw new IOException(e.getMessage());
            }
        }
        
        // Assign upperBounds based on bin counts
        upperBounds = new double[binCount];
        upperBounds[0] =
        ((double)((SummaryStatistics)binStats.get(0)).getN())/
        (double)sampleStats.getN();
        for (int i = 1; i < binCount-1; i++) {
            upperBounds[i] = upperBounds[i-1] +
            ((double)((SummaryStatistics)binStats.get(i)).getN())/
            (double)sampleStats.getN();
        }
        upperBounds[binCount-1] = 1.0d;
    }
    
    /**
     * Generates a random value from this distribution
     * @return the random value.
     * @throws IllegalStateException if the distribution has not been loaded
     */
    public double getNextValue() throws IllegalStateException {    
        
        if (!loaded) {
            throw new IllegalStateException("distribution not loaded");
        }
        
        // Start with a uniformly distributed random number in (0,1)
        double x = Math.random();
       
        // Use this to select the bin and generate a Gaussian within the bin
        for (int i = 0; i < binCount; i++) {
           if (x <= upperBounds[i]) {
               SummaryStatistics stats = (SummaryStatistics)binStats.get(i);
               if (stats.getN() > 0) { 
                   if (stats.getStandardDeviation() > 0) {  // more than one obs 
                        return randomData.nextGaussian
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
        
    public SummaryStatistics getSampleStats() {
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

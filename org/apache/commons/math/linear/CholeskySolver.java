/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math.linear;


/**
 * Solves a linear equitation with symmetrical, positiv definit 
 * coefficient matrix by Cholesky decomposition.
 * <p>
 * For every symmetric, positiv definit matrix <code>M</code> there is a
 * lower triangular matrix <code>L</code> so that <code>L*L^T=M</code>. 
 * <code>L</code> is called the <i>Cholesky decomposition</i> of <code>M</code>.  
 * For any constant vector <code>c</code> it can be used to solve 
 * the linear equitation <code>M*x=L*(L^T*x)=c</code>.<br>
 * Compared to the LU-decompoistion the Cholesky methods requires only half 
 * the number of operations.
 * <p>
 * @author Stefan Koeberle, 11/2003
 */
public class CholeskySolver {
    
    private double numericalZero = 10E-12;
    
    /** The lower triangular matrix */
    private Array2DRowRealMatrix decompMatrix;
    
   
    /** 
     * Creates a new instance of CholeskySolver
     */
    public CholeskySolver() {
    }//constructor CholeskySolver
    
    
    /** 
     * Every double <code>d</code> satisfying 
     * <code>java.lang.Math.abs(d) <= numericalZero</code> 
     * is considered equal to <code>0.0d.</code>
     */
    public void setNumericalZero(double numericalZero) {
        this.numericalZero = numericalZero;
    }//setNumericalZero
    
    /**
     * See <code>setNumericalZero</code>
     */
    public double getNumericalZero() {
        return numericalZero;
    }//getNumericalZero
    
    
    /**
     * Calculates the Cholesky-decomposition of the symmetrical, positiv definit 
     * matrix <code>M</code>.
     * <p>
     * The decomposition matrix is internally stored.
     * <p>
     * @throws IllegalArgumentException   if <code>M</code> ist not square or 
     *                                    not positiv definit
     */
    public void decompose(RealMatrix m) 
    throws IllegalArgumentException {
       
       decompMatrix = null;
       double[][] mval = m.getData();
       int numRows = m.getRowDimension();
       int numCols = m.getColumnDimension();
       if (numRows != numCols) 
           throw new IllegalArgumentException("matrix is not square"); 
       double[][] decomp = new double[numRows][numCols];       
       double sum;
       
       //for all columns
       for (int col=0; col<numCols; col++) {
          
           //diagonal element
           sum = mval[col][col];
           for (int k=0; k<col; k++) 
               sum = sum - decomp[col][k]*decomp[col][k];
           if (sum <= numericalZero) {
               throw new IllegalArgumentException(
                             "Matrix is not positiv definit");
           }
           decomp[col][col] += Math.sqrt(sum);
           
           //column below diagonal
           for (int row=col+1; row<numRows; row++) {
               sum = mval[row][col];
               for (int k=0; k<col; k++) 
                   sum = sum - decomp[col][k]*decomp[row][k];
               decomp[row][col] = sum/decomp[col][col]; 
           }//for
           
       }//for all columns
       
       decompMatrix = new Array2DRowRealMatrix(decomp);
       
    }//decompose
    
    
    /**
     * Returns the last calculated decomposition matrix.
     * <p>
     * Caution: Every call of this Method will return the same object.
     * Decomposing another matrix will generate a new one.
     */
    public Array2DRowRealMatrix getDecomposition() {
        return decompMatrix;
    }//getDecomposition
    
    
    /**
     * Returns the solution for a linear system with constant vector <code>c</code>. 
     * <p>
     * This method solves a linear system <code>M*x=c</code> for a symmetrical,
     * positiv definit coefficient matrix <code>M</code>. Before using this 
     * method the matrix <code>M</code> must have been decomposed.
     * <p>
     * @throws IllegalStateException    if this methode is called before 
     *                                  a matrix was decomposed
     * @throws IllegalArgumentException if the dimension of <code>c</code> doesn't
     *                                  match the row dimension of <code>M</code>
     */    
    public double[] solve(double[] c) 
    throws IllegalStateException, IllegalArgumentException {
      
        if (decompMatrix == null) {
            throw new IllegalStateException("no decomposed matrix available");
        }//if
        if (decompMatrix.getColumnDimension() != c.length) 
           throw new IllegalArgumentException("matrix dimension mismatch"); 
       
        double[][] decomp = decompMatrix.getData();
        double[] x = new double[decomp.length];
        double sum;
        
        //forward elimination
        for (int i=0; i<x.length; i++) {
            sum = c[i];
            for (int k=0; k<i; k++) 
                sum = sum - decomp[i][k]*x[k];
            x[i] = sum / decomp[i][i];
        }//forward elimination
        
        //backward elimination
        for (int i=x.length-1; i>=0; i--) {
            sum = x[i];
            for (int k=i+1; k<x.length; k++) 
                sum = sum - decomp[k][i]*x[k];        
            x[i] = sum / decomp[i][i];
        }//backward elimination
        
        return x;
    }//solve
    
    
    /**
     * Returns the solution for a linear system with a symmetrical, 
     * positiv definit coefficient matrix <code>M</code> and 
     * constant vector <code>c</code>. 
     * <p>
     * As a side effect, the Cholesky-decomposition <code>L*L^T=M</code> is 
     * calculated and internally stored.
     * <p>
     * This is a convenience method for <code><pre>
     *   solver.decompose(m);
     *   solver.solve(c);
     * </pre></code>
     * @throws IllegalArgumentException if M ist not square, not positive definit
     *                                  or the dimensions of <code>M</code> and 
     *                                  <code>c</code> don't match.                     
     */
    public double[] solve(RealMatrix m, double[] c) 
    throws IllegalArgumentException {
        decompose(m);
        return solve(c);
    }//solve 
    
    
    /**
     * Returns the determinant of the a matrix <code>M</code>.
     * <p>
     * Before using this  method the matrix <code>M</code> must 
     * have been decomposed. 
     * <p>
     * @throws IllegalStateException  if this method is called before 
     *                                a matrix was decomposed
     */
    public double getDeterminant() {
        
        if (decompMatrix == null) {
            throw new IllegalStateException("no decomposed matrix available");
        }//if
        
        double[][] data = decompMatrix.getData();
        double res = 1.0d; 
        for (int i=0; i<data.length; i++) {
            res *= data[i][i];
        }//for
        res = res*res;
        
        return res;
    }//getDeterminant
    
}//class CholeskySolver

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
    private RealMatrixImpl decompMatrix;
    
   
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
       
       decompMatrix = new RealMatrixImpl(decomp);
       
    }//decompose
    
    
    /**
     * Returns the last calculated decomposition matrix.
     * <p>
     * Caution: Every call of this Method will return the same object.
     * Decomposing another matrix will generate a new one.
     */
    public RealMatrixImpl getDecomposition() {
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

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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Test cases for the {@link CholeskySolver} class.
 * <p>
 * @author Stefan Koeberle, 11/2003
 */
public class CholeskySolverTest 
extends TestCase {
    
        private double[][] m1 = {{1}};
        private double m1Det = 1.0d;
        
        private double[][] m2 = {{1, 0} , 
                                 {0, 2}};
        private double m2Det = 2.0d;                                 
        
        private double[][] m3 = {{1, 0, 0}, 
                                 {0, 2, 0}, 
                                 {0, 0, 3}};
        private double m3Det = 6.0d;
                                 
        private double[][] m4 = {{1, 0, 0}, 
                                 {2, 3, 0}, 
                                 {4, 5, 6}};
        private double m4Det = 18.0d;
        
        private double[][] m5 = {{ 1,  0,  0,  0,  0}, 
                                 {-2,  3,  0,  0,  0}, 
                                 { 4, -5,  6,  0,  0},
                                 { 7,  8, -9, 10,  0},
                                 {11, 12, 13, 14, 15}};
        private double m5Det = 2700.0d;

                                 
        private double[][] m6 = {{1, 0,  0}, 
                                 {2, 0,  0}, 
                                 {4, 5,  6}};
        
        private double[][] m7 = {{1, 2, 3}, 
                                 {4, 5, 6}};  
                              
    /** 
     * Creates a new instance of CholeskySolverTest 
     */
    public CholeskySolverTest(String nameOfTest) {
        super(nameOfTest);
    }//constructor CholeskySolverTest
    
    public void setUp() 
    throws java.lang.Exception { 
       super.setUp();
    }//setUp
    
   
    public void tearDown() 
    throws java.lang.Exception {
        super.tearDown();
    }//tearDown
    
    public static Test suite() {
        TestSuite suite = new TestSuite(CholeskySolverTest.class);
        suite.setName("CholeskySolver Tests");
        return suite;
    }//suite

    
    /** 
     * tests CholeskySolver.setNumericalZero() 
     */   
    public void testNumericalZero() {
        CholeskySolver solver = new CholeskySolver();
        double numericalZero = 77.77d;
        solver.setNumericalZero(numericalZero);
        assertEquals(solver.getNumericalZero(), numericalZero, 0.0d);
        
        try {
            solver.decompose(
                new Array2DRowRealMatrix(new double[][]{{numericalZero/2, 0},
                                                  {0, numericalZero/2}}));
            fail("testing numericalZero");
        } catch (IllegalArgumentException e) {}
        
    }//testNumericalZero
    
    
    /** 
     * tests CholeskySolver.decompose(...) 
     */
    public void testDecompose() {
        
        //The following decompositions should succeed.
        testDecompose(m1, "Decomposing matrix m1");
        testDecompose(m2, "Decomposing matrix m2");
        testDecompose(m3, "Decomposing matrix m3");
        testDecompose(m4, "Decomposing matrix m4");
        testDecompose(m5, "Decomposing matrix m5");
        
        //The following decompositions will fail. An IllegalArgumentException
        //should be thrown.
        try {
            testDecompose(m6, "Decomposing matrix m6");
            fail("Decomposing matrix m6"); 
        } catch (IllegalArgumentException e) {}
        
         try {
             CholeskySolver solver = new CholeskySolver();
             solver.decompose(new Array2DRowRealMatrix(m7));
             fail("Decomposing matrix m7"); 
        } catch (IllegalArgumentException e) {}
        
    }//testDecomposition
    
    
    /** 
     * tests CholeskySolver.solve(...) 
     */
    public void testSolve() {

        //If there's no matrix, there's no linear euqitation to solve ...
        try {
             CholeskySolver solver = new CholeskySolver();
             solver.solve(new double[] {1,2,3});
             fail("solving a liniar equitation with a missing matrix should fail"); 
        } catch (IllegalStateException e) {}

        //The following operations should succeed.
        testSolve(m1, "Solving matrix m1");  
        testSolve(m2, "Solving matrix m2");
        testSolve(m3, "Solving matrix m3");
        testSolve(m4, "Solving matrix m4");
        testSolve(m5, "Solving matrix m5");
     
        //The following operations will fail. An IllegalArgumentException
        //should be thrown.
        try {
          testSolve(m6, "Solving matrix m6");
          fail("Solving matrix m6"); 
        } catch (IllegalArgumentException e) {}

         try {
             CholeskySolver solver = new CholeskySolver();
             solver.solve(new Array2DRowRealMatrix(m3), new double[] {1, 2, 3, 4});
             fail("Solving matrix m3[3x3], v[4]"); 
        } catch (IllegalArgumentException e) {}
        
    }//testDecomposition
    
    
    /** 
     * tests CholeskySolver.getDeterminant(...) 
     */
    public void testGetDeterminant() {
        
        //Since no matrix was decomposed, there's no determinant.
        try {
             CholeskySolver solver = new CholeskySolver();
             solver.getDeterminant();
             fail("Calculating determinant of missing matrix should fail"); 
        } catch (IllegalStateException e) {}
       
        //These test will suceed.
        testGetDeterminant(m1, m1Det, "Calculating determinant of m1");
        testGetDeterminant(m2, m2Det, "Calculating determinant of m2");
        testGetDeterminant(m3, m3Det, "Calculating determinant of m3");
        testGetDeterminant(m4, m4Det, "Calculating determinant of m4");
        testGetDeterminant(m5, m5Det, "Calculating determinant of m5");
    }//test
    
    
    /**
     * Generates the matrix 
     * <code>m = lowerTriangularMatrix * lowerTriangularMatrix^T</code>.
     * If alle diagonalelements of <code>lowerTriangularMatrix</code> are
     * positiv, <code>m</code> will be positiv definit. 
     * Decomposing <code>m</code> should result in
     * <code>lowerTriangularMatrix</code> again. So there's a simple test ...
     */
    private void testDecompose(double[][] lowerTriangularMatrix, String message) 
    throws IllegalArgumentException {
    
        RealMatrix triangularMatrix = new Array2DRowRealMatrix(lowerTriangularMatrix);
        RealMatrix pdMatrix = 
            triangularMatrix.multiply(triangularMatrix.transpose());
        
        CholeskySolver solver = new CholeskySolver();
        solver.decompose(pdMatrix);
        
        assertTrue(message, 
            areEqual(triangularMatrix, solver.getDecomposition(), 1.0E-10));
    
    }//testDecompose
  
    
    /**
     * Similar to <code> private testDecompose(...)</code>.
     */
    private void testSolve(double[][] lowerTriangularMatrix, String message)  {
      
        RealMatrix triangularMatrix = 
            new Array2DRowRealMatrix(lowerTriangularMatrix);
        Array2DRowRealMatrix pdMatrix = 
            (Array2DRowRealMatrix) triangularMatrix.multiply(triangularMatrix.transpose());
        CholeskySolver solver = 
            new CholeskySolver();
        
        double[] c = new double[lowerTriangularMatrix.length];
        for (int i=0; i<c.length; i++) 
            for (int j=0; j<lowerTriangularMatrix[0].length; j++) 
                c[i] += lowerTriangularMatrix[i][j];
        
        solver.decompose(pdMatrix);
        RealMatrix x = new Array2DRowRealMatrix(solver.solve(c));

        assertTrue(message, 
            areEqual(pdMatrix.multiply(x),  new Array2DRowRealMatrix(c), 1.0E-10));
    }//testSolve

    
    /**
     * Similar to <code> private testDecompose(...)</code>.
     */
    private void testGetDeterminant(double[][] lowerTriangularMatrix, 
                                    double determinant,
                                    String message) 
    throws IllegalArgumentException {
    
        RealMatrix triangularMatrix = new Array2DRowRealMatrix(lowerTriangularMatrix);
        RealMatrix pdMatrix = 
            triangularMatrix.multiply(triangularMatrix.transpose());
        double pdDeterminant = determinant * determinant;
        
        CholeskySolver solver = new CholeskySolver();
        solver.decompose(pdMatrix);
        assertEquals(message, solver.getDeterminant(), pdDeterminant, 1.0E-10);
    }//testGetDeterminant
    
    
    /**
     * Are <code>m1</code> and <code>m2</code> equal?
     */
    private static boolean areEqual(RealMatrix m1, RealMatrix m2, double delta) {
        
        double[][] mv1 = m1.getData();
        double[][] mv2 = m2.getData();
        
        if (mv1.length != mv1.length  ||
            mv1[0].length != mv2[0].length) 
            return false;
        
        for (int i=0; i<mv1.length; i++) 
            for (int j=0; j<mv1[0].length; j++) 
                if (Math.abs(mv1[i][j] -mv2[i][j]) > delta) 
                    return false;
        
        return true;
    }//isEqual
 
     
    /**
     * Executes all tests of this class
     */
    public static void main(String[] args) {
        System.out.println("Start");
        TestRunner runner = new TestRunner();
        runner.doRun(CholeskySolverTest.suite());
        System.out.println("End");
    }//main
    
}//class CholeskySolverTest

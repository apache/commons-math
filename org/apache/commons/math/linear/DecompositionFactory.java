/*
 * Created on Nov 19, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.commons.math.linear;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract  class DecompositionFactory {

    /*
     * get a matrix specific decomposer factory
     * class RealMatrix {
     *     DecompositionFactory getDecompositionFactory();
     *  }
     */
     
    // get overall default factory
    public static DecompositionFactory newInstance(){
        return null;
    }

    // construct a new default decomposer
    public abstract Decomposer newDecomposer();

    // example for a specific decomposer (Householder or QR)
    public abstract Decomposer newQRDecopmposer();
}

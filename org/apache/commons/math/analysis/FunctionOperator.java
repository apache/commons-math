/*
 * Created on Nov 19, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.commons.math.analysis;

import org.apache.commons.math.MathException;

public interface FunctionOperator {


    /**
     * Evaluate the Function Operator for a given real single variable function.
     *
     * @param f the function which should be evaluated
     * @return the resultant function
     * @throws MathException if the function couldn't be evaluated
     */
    public UnivariateRealFunction evaluate(UnivariateRealFunction f) throws MathException;

}
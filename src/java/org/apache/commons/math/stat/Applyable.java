/*
 * Created on Jul 15, 2003
 *
 */
package org.apache.commons.math.stat;

import org.apache.commons.math.stat.univariate.UnivariateStatistic;

/**
 * Applyable.java
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 * 
 */
public interface Applyable {
    
    /**
     * Applies a UnivariateStatistic object against this object 
     * and returns the result.
     * @param stat The stat to apply.
     * @return The result value of the application.
     */
    double apply(UnivariateStatistic stat);
    
}

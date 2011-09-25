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
package org.apache.commons.math.ode;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.exception.util.LocalizedFormats;


/**
 * This class represents a combined set of first order differential equations,
 * with at least a main set of equations expandable by some sets of additional
 * equations.
 * <p>
 * This class extends the {@link FirstOrderDifferentialEquations}. It allows to
 * identify which part of a complete set of differential equations correspond to
 * the main set and which part correspond to the expansion sets.
 * </p>
 * <p>
 * One typical use case is the computation of the jacobian matrix for some ODE.
 * The main set of equations corresponds to the raw ODE, and we add to this set
 * another bunch of equations which represent the jacobian matrix of the main
 * set. In that case, we want the integrator to use <em>only</em> the main set
 * to estimate the errors and hence the step sizes. It should <em>not</em> use
 * the additional equations in this computation.
 * The {@link ExpandableFirstOrderIntegrator integrator} will be able to know
 * where the main set ends and so where the expansion sets begin.
 * </p>
 * <p>
 * We consider that the main set always corresponds to the first equations and
 * the expansion sets to the last equations.
 * </p>
 *
 * @see FirstOrderDifferentialEquations
 * @see JacobianMatrices
 *
 * @version $Id$
 * @since 3.0
 */

public class ExpandableFirstOrderDifferentialEquations implements FirstOrderDifferentialEquations {

    /** Main set of differential equations. */
    private final FirstOrderDifferentialEquations mainSet;

    /** Additional sets of equations and associated states. */
    private final List<AdditionalStateAndEquations> addedSets;

    /** Create a new instance of ExpandableFirstOrderDifferentialEquations.
     * @param fode the main set of differential equations to be integrated.
     */
    public ExpandableFirstOrderDifferentialEquations(final FirstOrderDifferentialEquations fode) {
        this.mainSet   = fode;
        this.addedSets = new ArrayList<AdditionalStateAndEquations>();
    }

    /** Return the dimension of the whole state vector.
     * <p>
     * The whole state vector results in the assembly of the main set of
     * equations and, if there are some, the added sets of equations.
     * </p>
     * @return dimension of the whole state vector
     */
    public int getDimension()
        throws MathIllegalArgumentException {
        int dimension = this.getMainSetDimension();
        try {
            for (AdditionalStateAndEquations stateAndEqu : addedSets) {
                dimension += stateAndEqu.getAdditionalEquations().getDimension();
            }
            return dimension;
        } catch (Exception e) {
            // TODO we should not catch Exception, and we should identify the offending additional equation
            throw new MathIllegalArgumentException(LocalizedFormats.UNKNOWN_ADDITIONAL_EQUATION);
        }
    }

    /** Return the dimension of the main set of equations.
     * <p>
     * The main set of equations represents the first part of an ODE state.
     * The error estimations and adaptive step size computation should be
     * done on this first part only, not on the final part of the state
     * which represents expansion sets of equations considered as secondary.
     * </p>
     * @return dimension of the main set of equations, must be lesser than or
     * equal to the {@link #getDimension() total dimension}
     */
    public int getMainSetDimension() {
        return mainSet.getDimension();
    }

    /** Return the cumulated dimension of all added sets of equations.
     * @return dimension of all added sets of equations
     * @throws IllegalArgumentException if some additional equation is unknown
     */
    public int getAddedSetsDimension()
        throws IllegalArgumentException {
        int addDim = 0;
        try {
            for (AdditionalStateAndEquations stateAndEqu : addedSets) {
                addDim += stateAndEqu.getAdditionalEquations().getDimension();
            }
            return addDim;
        } catch (Exception e) {
            // TODO we should not catch Exception, and we should identify the offending additional equation
            throw new MathIllegalArgumentException(LocalizedFormats.UNKNOWN_ADDITIONAL_EQUATION);
        }
    }

    /** Return the dimension of one added set of equations.
     * @param  addEqu Additional equations used as a reference for selection
     * @return dimension of the added set of equations
     * @throws IllegalArgumentException if additional equation is unknown
     */
    public int getAddedSetDimension(final AdditionalEquations addEqu) {
        return selectStateAndEquations(addEqu).getAdditionalEquations().getDimension();
    }

    /** Get the current time derivative of the total state vector.
     * @param t current value of the independent <I>time</I> variable
     * @param y array containing the current value of the state vector
     * @param yDot placeholder array where to put the time derivative of the state vector
     */
    public void computeDerivatives(final double t, final double[] y, final double[] yDot) {

        // Add contribution for the main set of equations
        int index = getMainSetDimension();
        double[] m = new double[index];
        double[] mDot = new double[index];
        // update current main state
        System.arraycopy(y, 0, m, 0, index);
      // compute derivatives
        mainSet.computeDerivatives(t, m, mDot);
        // update main state contribution in global array
        System.arraycopy(mDot, 0, yDot, 0, index);

        // Add contribution for additional equations
        for (final AdditionalStateAndEquations stateAndEqu : addedSets) {
            final double[] p    = stateAndEqu.getAdditionalState();
            final double[] pDot = stateAndEqu.getAdditionalStateDot();

            // update current additional state
            System.arraycopy(y, index, p, 0, p.length);

            // compute additional derivatives
            stateAndEqu.getAdditionalEquations().computeDerivatives(t, m, mDot, p, pDot);

            // update each additional state contribution in global array
            System.arraycopy(pDot, 0, yDot, index, p.length);

            // incrementing index
            index += p.length;
        }

    }

    /** Add a set of user-specified equations to be integrated along with
     *  the main set of equations.
     *
     * @param addEqu additional equations
     * @see #setInitialAdditionalState(double[], AdditionalEquations)
     * @see #getCurrentAdditionalState(AdditionalEquations)
     */
    public void addAdditionalEquations(final AdditionalEquations addEqu) {
        addedSets.add(new AdditionalStateAndEquations(addEqu));
    }

    /** Get the instance of the main set of equations.
     * @return current value of the main set of equations.
     */
    public FirstOrderDifferentialEquations getMainSet() {
        return mainSet;
    }

    /** Set initial additional state.
     * @param addState additional state
     * @param addEqu additional equations used as a reference for selection
     * @throws IllegalArgumentException if additional equation is unknown
     */
    public void setInitialAdditionalState(final double[] addState, final AdditionalEquations addEqu) {
        selectStateAndEquations(addEqu).setAdditionalState(addState);
    }

    /** Set current additional state.
     * <p>
     * The total current state computed by the integrator
     * is dispatched here to the various additional states.
     * </p>
     * @param currentState total current state
     * @throws IllegalArgumentException if additional equation is unknown
     */
    public void setCurrentAdditionalState(final double[] currentState)
    throws IllegalArgumentException {
        int index = getMainSetDimension();
        try {
            for (AdditionalStateAndEquations stateAndEqu : addedSets) {
                final int addDim = stateAndEqu.getAdditionalEquations().getDimension();
                final double[] addState = new double[addDim];
                System.arraycopy(currentState, index, addState, 0, addDim);
                stateAndEqu.setAdditionalState(addState);
                index += addDim;
            }
        } catch (Exception e) {
            // TODO we should not catch Exception, and we should identify the offending additional equation
            throw new MathIllegalArgumentException(LocalizedFormats.UNKNOWN_ADDITIONAL_EQUATION);
        }
    }

    /** Get current additional state.
     * @param addEqu additional equations used as a reference for selection
     * @return current additional state
     * @throws IllegalArgumentException if additional equation is unknown
     */
    public double[] getCurrentAdditionalState(final AdditionalEquations addEqu) {
        return selectStateAndEquations(addEqu).getAdditionalState();
    }

    /** Get all current additional states accumulated.
     * @return current additional states
     * @throws IllegalArgumentException if additional equation is unknown
     */
    public double[] getCurrentAdditionalStates()
        throws IllegalArgumentException {
        int index = 0;
        final double[] cumulState = new double[getAddedSetsDimension()];
        try {
            for (AdditionalStateAndEquations stateAndEqu : addedSets) {
                final int addDim = stateAndEqu.getAdditionalEquations().getDimension();
                final double[] addState = stateAndEqu.getAdditionalState();
                System.arraycopy(addState, 0, cumulState, index, addDim);
                index += addDim;
            }
            return cumulState;
        } catch (Exception e) {
            // TODO we should not catch Exception, and we should identify the offending additional equation
            throw new MathIllegalArgumentException(LocalizedFormats.UNKNOWN_ADDITIONAL_EQUATION);
        }
    }

    /** Select additional state and equations pair in the list.
     * @param  addEqu Additional equations used as a reference for selection
     * @return additional state and equations pair
     * @throws IllegalArgumentException if additional equation is unknown
     */
    private AdditionalStateAndEquations selectStateAndEquations(final AdditionalEquations addEqu)
        throws IllegalArgumentException {
        for (AdditionalStateAndEquations stateAndEqu : addedSets) {
            if (stateAndEqu.getAdditionalEquations() == addEqu) {
                return stateAndEqu;
            }
        }
        // TODO we should not catch Exception, and we should identify the offending additional equation
        throw new MathIllegalArgumentException(LocalizedFormats.UNKNOWN_ADDITIONAL_EQUATION);
    }

}

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * This class defines a set of {@link AdditionalEquations additional equations} to
 * compute the jacobian matrices with respect to the initial state vector and, if
 * any, to some parameters of the main ODE set.
 * <p>
 * It is intended to be packed into an {@link ExpandableFirstOrderDifferentialEquations}
 * in conjunction with a main set of ODE, which may be:
 * <ul>
 * <li>a {@link FirstOrderDifferentialEquations}</li>
 * <li>a {@link MainStateJacobianProvider}</li>
 * </ul>
 * In order to compute jacobian matrices with respect to some parameters of the
 * main ODE set, the following parameter jacobian providers may be set:
 * <ul>
 * <li>a {@link ParameterJacobianProvider}</li>
 * <li>a {@link ParameterizedODE}</li>
 * </ul>
 * </p>
 *
 * @see ExpandableFirstOrderDifferentialEquations
 * @see FirstOrderDifferentialEquations
 * @see MainStateJacobianProvider
 * @see ParameterJacobianProvider
 * @see ParameterizedODE
 *
 * @version $Id$
 * @since 3.0
 */
public class JacobianMatrices implements AdditionalEquations {

    /** Expandable first order differential equation. */
    private ExpandableFirstOrderDifferentialEquations efode;

    /** FODE without exact main jacobian computation skill. */
    private FirstOrderDifferentialEquations fode = null;

    /** FODE with exact main jacobian computation skill. */
    private MainStateJacobianProvider jode = null;

    /** FODE without exact parameter jacobian computation skill. */
    private ParameterizedODE pode = null;

    /** FODE with exact parameter jacobian computation skill. */
    private List<ParameterJacobianProvider> pjp = new ArrayList<ParameterJacobianProvider>();;

    /** List of parameters selected for parameter jacobian computation. */
    private List<ParameterConfiguration> selectedParameters = null;

    /** Main state vector dimension. */
    private int stateDim;

    /** Parameters dimension. */
    private int paramDim = 0;

    /** Current main state jacobian matrix in a row. */
    private double[] mainJacobianInARow;

    /** Current parameters jacobian matrices in a row. */
    private double[] parameterJacobiansInARow = null;

    /** Step used for finite difference computation of jacobian matrix
     *  w.r.t. main state vector. */
    private double[] hY = null;

    /** Boolean for fode consistency. */
    private boolean dirtyMainState = false;

    /** Boolean for selected parameters consistency. */
    private boolean dirtyParameter = false;

    /** Simple constructor for an additional equations set computing jacobian matrices.
     * <p>This additional equations set is added internally to the expandable
     * first order differential equations set thanks to the
     * {@link ExpandableFirstOrderDifferentialEquations#addAdditionalEquations(AdditionalEquations)}
     * method.
     * @param extended the expandable first order differential equations set
     * @param jode the main first order differential equations set to extend
     * @exception IllegalArgumentException if jode does not match the main set to be extended given by
     *            {@link ExpandableFirstOrderDifferentialEquations#getMainSet() extended.getMainSet()}
     */
    public JacobianMatrices(final ExpandableFirstOrderDifferentialEquations extended,
                            final MainStateJacobianProvider jode)
        throws IllegalArgumentException {

        checkCompatibility(extended, jode);

        efode = extended;
        stateDim = efode.getMainSetDimension();
        mainJacobianInARow = new double[stateDim * stateDim];
        this.jode = jode;
        efode.addAdditionalEquations(this);
        setInitialMainStateJacobian();
    }

    /** Simple constructor for an additional equations set computing jacobian matrices.
     * <p>This additional equations set is added internally to the expandable
     * first order differential equations set thanks to the
     * {@link ExpandableFirstOrderDifferentialEquations#addAdditionalEquations(AdditionalEquations)}
     * method.
     * @param extended the expandable first order differential equations set
     * @param fode the main first order differential equations set to extend
     * @exception IllegalArgumentException if fode does not match the main set to be extended given by
     *            {@link ExpandableFirstOrderDifferentialEquations#getMainSet() extended.getMainSet()}
     */
    public JacobianMatrices(final ExpandableFirstOrderDifferentialEquations extended,
                            final FirstOrderDifferentialEquations fode)
        throws IllegalArgumentException {

        checkCompatibility(extended, fode);

        efode = extended;
        stateDim = efode.getMainSetDimension();
        mainJacobianInARow = new double[stateDim * stateDim];
        this.fode = fode;
        dirtyMainState = true;
        efode.addAdditionalEquations(this);
        setInitialMainStateJacobian();
    }

    /** Add a parameter jacobian provider.
     * @param pjp the parameter jacobian provider to compute exactly the parameter jacobian matrix
     */
    public void setParameterJacobianProvider(final ParameterJacobianProvider pjp) {
        this.pjp.add(pjp);
    }

    /** Add a parameter jacobian provider.
     * @param pjp the parameterized ODE to compute by finite difference the parameter jacobian matrix
     */
    public void setParameterizedODE(final ParameterizedODE pode) {
        this.pode = pode;
        dirtyParameter = true;
    }

    /** Select the parameters to consider for jacobian matrices processing.
     * <p>
     * Parameters must belong to the supported ones given by {@link
     * Parameterizable#getParametersNames()}, so the main set of differential
     * equations must be {@link Parameterizable}.
     * </p>
     * <p>Note that each selection clears the previous selected parameters.</p>
     *
     * @param parameters parameters to consider for jacobian matrices processing
     * @exception IllegalArgumentException if one parameter is not supported
     */
    public void selectParameters(final String... parameters) throws IllegalArgumentException {
        
        selectedParameters = new ArrayList<ParameterConfiguration>();
        for (String param : parameters) {
            selectedParameters.add(new ParameterConfiguration(param, Double.NaN));
        }
        paramDim = parameters.length;
        parameterJacobiansInARow = new double[paramDim * stateDim];
        setInitialParameterJacobians();
    }

    /** Set the step associated to a parameter in order to compute by finite
     *  difference the jacobian matrix.
     * <p>
     * Needed if and only if the main ODE set is a {@link ParameterizedODE}
     * and the parameter has been {@link #selectParameters(String ...) selected}
     * </p>
     * <p>
     * For pval, a non zero value of the parameter, pval * Math.sqrt(MathUtils.EPSILON)
     * is a reasonable value for such a step.
     * </p>
     * <p>
     * A zero value for such a step doesn't enable to compute the parameter jacobian matrix.
     * </p>
     * @param parameter parameter to consider for jacobian processing
     * @param hP step for jacobian finite difference computation w.r.t. the specified parameter
     * @see ParameterizedODE
     * @exception IllegalArgumentException if the parameter is not supported
     */
    public void setParameterStep(final String parameter, final double hP) {

        boolean found = false;
        for (ParameterConfiguration param: selectedParameters) {
            if (parameter.equals(param.getParameterName())) {
                param.setHP(hP);
                found = true;
                dirtyParameter = true;
                break;
            }
        }
        if (!found) {
            throw new MathIllegalArgumentException(LocalizedFormats.UNKNOWN_PARAMETER,
                                                   parameter);
        }
    }

    /** Set the steps in order to compute by finite difference the jacobian
     *  matrix with respect to main state.
     * <p>
     * Needed if and only if the main set is a {@link FirstOrderDifferentialEquations}.
     * </p>
     * <p>
     * Zero values for those steps don't enable to compute the main state jacobian matrix.
     * </p>
     * @param hY step used for finite difference computation with respect to state vector
     * @exception IllegalArgumentException if the hY has not the dimension of the main state
     * given by {@link ExpandableFirstOrderDifferentialEquations#getMainSetDimension()}
     */
    public void setMainStateSteps(final double[] hY) {

        if (fode != null) {
            // Check dimension
            checkDimension(stateDim, hY);
            this.hY = hY.clone();
            dirtyMainState = true;           
        }
    }

    /** Set the initial value of the jacobian matrix with respect to state.
     * @param dYdY0 initial jacobian matrix w.r.t. state
     * @exception IllegalArgumentException if matrix dimensions are incorrect
     */
    public void setInitialMainStateJacobian(final double[][] dYdY0) {

        // Check dimensions
        checkDimension(stateDim, dYdY0);
        checkDimension(stateDim, dYdY0[0]);

        // store the matrix in row major order as a single dimension array
        int index = 0;
        for (final double[] row : dYdY0) {
            System.arraycopy(row, 0, mainJacobianInARow, index, stateDim);
            index += stateDim;
        }
        // set initial additional state value in expandable fode
        efode.setInitialAdditionalState(mainJacobianInARow, this);
    }

    /** Set the initial value of the jacobian matrix with respect to one parameter.
     * <p>The parameter must be {@link #selectParameters(String...) selected}.</p>
     * @param pName parameter name
     * @param dYdP initial jacobian matrix w.r.t. the parameter
     * @exception IllegalArgumentException if matrix dimensions are incorrect
     */
    public void setInitialParameterJacobian(final String pName, final double[] dYdP) {

        // Check dimensions
        checkDimension(stateDim, dYdP);

        // store the matrix in a global single dimension array
        boolean found = false;
        int index = 0;
        for (ParameterConfiguration param: selectedParameters) {
            if (pName.equals(param.getParameterName())) {
                System.arraycopy(dYdP, 0, parameterJacobiansInARow, index, stateDim);
                double[] p = new double[this.getDimension()];
                index = stateDim * stateDim;
                System.arraycopy(mainJacobianInARow, 0, p, 0, index);
                System.arraycopy(parameterJacobiansInARow, 0, p, index, stateDim * paramDim);
                // set initial additional state value in expandable fode
                efode.setInitialAdditionalState(p, this);
                found = true;
                break;
            }
            index += stateDim;
        }
        if (! found) {
            throw new MathIllegalArgumentException(LocalizedFormats.UNKNOWN_PARAMETER,
                                                   pName);
        }
    }

    /** Set the default initial value of the jacobian matrix with respect to state.
     * <p>dYdY0 is set to the identity matrix.</p>
     */
    public void setInitialMainStateJacobian() {
        final double[][] dYdY0 = new double[stateDim][stateDim];
        for (int i = 0; i < stateDim; ++i) {
            dYdY0[i][i] = 1.0;
        }
        setInitialMainStateJacobian(dYdY0);
    }

    /** Set the default initial value of the jacobian matrix with respect to one parameter.
     * <p>The parameter must be {@link #selectParameters(String...) selected}.</p>
     * <p>dYdP is set to the null matrix.</p>
     * @param pName parameter name
     */
    public void setInitialParameterJacobian(final String pName) {
        setInitialParameterJacobian(pName, new double[stateDim]);
    }

    /** Set the default initial values of jacobian matrices with respect to all parameters.
     */
    public void setInitialParameterJacobians() {
        for (ParameterConfiguration param: selectedParameters) {
            setInitialParameterJacobian(param.getParameterName());
        }
    }

    /** Set default initial values for jacobian matrices.
     * <p>dYdY0 is set to the identity matrix and all dYdP are set to zero.</p>
     */
    public void setInitialJacobians() {
        setInitialMainStateJacobian();
        setInitialParameterJacobians();
    }

    /** Get the current value of the jacobian matrix with respect to state.
     * @param dYdY0 current jacobian matrix with respect to state.
     */
    public void getCurrentMainSetJacobian(final double[][] dYdY0) {

        // get current state for this set of equations from the expandable fode
        double[] p = efode.getCurrentAdditionalState(this);

        int index = 0;
        for (int i = 0; i < stateDim; i++) {
            System.arraycopy(p, index, dYdY0[i], 0, stateDim);
            index += stateDim;
        }

    }

    /** Get the current value of the jacobian matrix with respect to one parameter.
     * @param pName name of the parameter for the computed jacobian matrix 
     * @param dYdP current jacobian matrix with respect to the named parameter
     */
    public void getCurrentParameterJacobian(String pName, final double[] dYdP) {

        // get current state for this set of equations from the expandable fode
        double[] p = efode.getCurrentAdditionalState(this);

        int index = stateDim * stateDim;
        for (ParameterConfiguration param: selectedParameters) {
            if (param.getParameterName().equals(pName)) {
                System.arraycopy(p, index, dYdP, 0, stateDim);
                break;
            }
            index += stateDim;
        }

    }

    /** {@inheritDoc} */
    public int getDimension() {
        return stateDim * (stateDim + paramDim);
    }

    /** {@inheritDoc} */
    public void computeDerivatives(final double t, final double[] y, final double[] yDot,
                                   final double[] z, final double[] zDot) {

        // Lazy initialization
        if (dirtyMainState) {
            jode = new MainStateJacobianWrapper(fode, hY);
            dirtyMainState = false;
        }
        if (dirtyParameter && (paramDim != 0)) {
            pjp.add(new ParameterJacobianWrapper(jode, pode, selectedParameters));
            dirtyParameter = false;
        }

        // variational equations:
        // from d[dy/dt]/dy0 and d[dy/dt]/dp to d[dy/dy0]/dt and d[dy/dp]/dt

        // compute jacobian matrix with respect to main state
        double[][] dFdY = new double[stateDim][stateDim];
        jode.computeMainStateJacobian(t, y, yDot, dFdY);

        // Dispatch jacobian matrix in the compound additional state vector
        for (int i = 0; i < stateDim; ++i) {
            final double[] dFdYi = dFdY[i];
            for (int j = 0; j < stateDim; ++j) {
                double s = 0;
                final int startIndex = j;
                int zIndex = startIndex;
                for (int l = 0; l < stateDim; ++l) {
                    s += dFdYi[l] * z[zIndex];
                    zIndex += stateDim;
                }
                zDot[startIndex + i * stateDim] = s;
            }
        }

        if (paramDim != 0) {
            // compute jacobian matrices with respect to parameters
            double[] dFdP = new double[stateDim];
            int startIndex = stateDim * stateDim;
            for (ParameterConfiguration param: selectedParameters) {
                boolean found = false;
                for (ParameterJacobianProvider provider: pjp) {
                    if (provider.isSupported(param.getParameterName())) {
                        try {
                            provider.computeParameterJacobian(t, y, yDot, param.getParameterName(), dFdP);
                            for (int i = 0; i < stateDim; ++i) {
                                final double[] dFdYi = dFdY[i];
                                int zIndex = startIndex;
                                double s = dFdP[i];
                                for (int l = 0; l < stateDim; ++l) {
                                    s += dFdYi[l] * z[zIndex];
                                    zIndex++;
                                }
                                zDot[startIndex + i] = s;
                            }
                            startIndex += stateDim;
                            found = true;
                            break;
                        } catch (IllegalArgumentException iae) {
                        }
                    }
                }
                if (! found) {
                    throw new MathIllegalArgumentException(LocalizedFormats.UNKNOWN_PARAMETER,
                                                           param);
                }
            }
        }

    }

    /** Check compatibility between the main set in the expandable ode and an ordinary ode.
     * @param expended expandable ode containing a main set
     * @param ode single ode to check 
     * @throws MathIllegalArgumentException if single ode doesn't match the main ode set in the extended ode
     */
    private void checkCompatibility(final ExpandableFirstOrderDifferentialEquations extended,
                                    final FirstOrderDifferentialEquations ode)
        throws MathIllegalArgumentException {

        if (!(ode == extended.getMainSet())) {
            throw new MathIllegalArgumentException(LocalizedFormats.UNMATCHED_ODE_IN_EXTENDED_SET);
        }
    }

    /** Check array dimensions.
     * @param expected expected dimension
     * @param array (may be null if expected is 0)
     * @throws DimensionMismatchException if the array dimension does not match the expected one
     */
    private void checkDimension(final int expected, final Object array)
        throws DimensionMismatchException {
        int arrayDimension = (array == null) ? 0 : Array.getLength(array);
        if (arrayDimension != expected) {
            throw new DimensionMismatchException(arrayDimension, expected);
        }
    }

}


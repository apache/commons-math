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

package org.apache.commons.math4.ode.nonstiff;


import java.lang.reflect.InvocationTargetException;

import org.apache.commons.math4.Field;
import org.apache.commons.math4.RealFieldElement;
import org.apache.commons.math4.ode.FieldEquationsMapper;
import org.apache.commons.math4.ode.FieldExpandableODE;
import org.apache.commons.math4.ode.FieldFirstOrderDifferentialEquations;
import org.apache.commons.math4.ode.FieldODEStateAndDerivative;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.MathArrays;
import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractRungeKuttaFieldStepInterpolatorTest {

    protected abstract <T extends RealFieldElement<T>> RungeKuttaFieldStepInterpolator<T>
        createInterpolator(Field<T> field, boolean forward, FieldEquationsMapper<T> mapper);

    @Test
    public abstract void interpolationAtBounds();

    protected <T extends RealFieldElement<T>> void doInterpolationAtBounds(final Field<T> field, double epsilon) {

        RungeKuttaFieldStepInterpolator<T> interpolator = setUpInterpolator(field,
                                                                            new SinCos<>(field),
                                                                            0.0, new double[] { 0.0, 1.0 }, 0.125);

        Assert.assertEquals(0.0, interpolator.getPreviousState().getTime().getReal(), 1.0e-15);
        for (int i = 0; i < 2; ++i) {
            Assert.assertEquals(interpolator.getPreviousState().getState()[i].getReal(),
                                interpolator.getInterpolatedState(interpolator.getPreviousState().getTime()).getState()[i].getReal(),
                                epsilon);
        }
        Assert.assertEquals(0.125, interpolator.getCurrentState().getTime().getReal(), 1.0e-15);
        for (int i = 0; i < 2; ++i) {
            Assert.assertEquals(interpolator.getCurrentState().getState()[i].getReal(),
                                interpolator.getInterpolatedState(interpolator.getCurrentState().getTime()).getState()[i].getReal(),
                                epsilon);
        }

    }

    @Test
    public abstract void interpolationInside();

    protected <T extends RealFieldElement<T>> void doInterpolationInside(final Field<T> field,
                                                                         double epsilonSin, double epsilonCos) {

        RungeKuttaFieldStepInterpolator<T> interpolator = setUpInterpolator(field,
                                                                            new SinCos<>(field),
                                                                            0.0, new double[] { 0.0, 1.0 }, 0.125);

        int n = 100;
        double maxErrorSin = 0;
        double maxErrorCos = 0;
        for (int i = 0; i <= n; ++i) {
            T t =     interpolator.getPreviousState().getTime().multiply(n - i).
                  add(interpolator.getCurrentState().getTime().multiply(i)).
                  divide(n);
            FieldODEStateAndDerivative<T> state = interpolator.getInterpolatedState(t);
            maxErrorSin = FastMath.max(maxErrorSin, state.getState()[0].subtract(t.sin()).abs().getReal());
            maxErrorCos = FastMath.max(maxErrorCos, state.getState()[1].subtract(t.cos()).abs().getReal());
        }
        Assert.assertEquals(0.0, maxErrorSin, epsilonSin);
        Assert.assertEquals(0.0, maxErrorCos, epsilonCos);

    }

    private <T extends RealFieldElement<T>>
    RungeKuttaFieldStepInterpolator<T> setUpInterpolator(final Field<T> field,
                                                         final FieldFirstOrderDifferentialEquations<T> eqn,
                                                         final double t0, final double[] y0,
                                                         final double t1) {

        RungeKuttaFieldStepInterpolator<T> interpolator = createInterpolator(field, t1 > t0,
                                                                             new FieldExpandableODE<T>(eqn).getMapper());
        // get the Butcher arrays from the field integrator
        String interpolatorName = interpolator.getClass().getName();
        String integratorName = interpolatorName.replaceAll("StepInterpolator", "Integrator");

        RungeKuttaFieldIntegrator<T> fieldIntegrator = null;
        try {
            @SuppressWarnings("unchecked")
            Class<RungeKuttaFieldIntegrator<T>> clz = (Class<RungeKuttaFieldIntegrator<T>>) Class.forName(integratorName);
            try {
                fieldIntegrator = clz.getConstructor(Field.class, RealFieldElement.class).newInstance(field,
                                                                                                      field.getOne());
            } catch (NoSuchMethodException nsme) {
                try {
                    fieldIntegrator = clz.getConstructor(Field.class, RealFieldElement.class,
                                                         RealFieldElement.class, RealFieldElement.class).newInstance(field,
                                                                                                                     field.getZero().add(0.001),
                                                                                                                     field.getOne(),
                                                                                                                     field.getOne(),
                                                                                                                     field.getOne());
                } catch (NoSuchMethodException e) {
                    Assert.fail(e.getLocalizedMessage());
                }
            }
        } catch (InvocationTargetException ite) {
            Assert.fail(ite.getLocalizedMessage());
        } catch (IllegalAccessException iae) {
            Assert.fail(iae.getLocalizedMessage());
        } catch (InstantiationException ie) {
            Assert.fail(ie.getLocalizedMessage());
        } catch (ClassNotFoundException cnfe) {
            Assert.fail(cnfe.getLocalizedMessage());
        }
        T[][] a = fieldIntegrator.getA();
        T[]   b = fieldIntegrator.getB();
        T[]   c = fieldIntegrator.getC();

        // store initial state
        T     t          = field.getZero().add(t0);
        T[]   fieldY     = MathArrays.buildArray(field, eqn.getDimension());
        T[][] fieldYDotK = MathArrays.buildArray(field, b.length, -1);
        for (int i = 0; i < y0.length; ++i) {
            fieldY[i] = field.getZero().add(y0[i]);
        }
        fieldYDotK[0] = eqn.computeDerivatives(t, fieldY);
        interpolator.storeState(new FieldODEStateAndDerivative<T>(t, fieldY, fieldYDotK[0]));
        interpolator.shift();

        // perform one integration step, in order to get consistent derivatives
        T h = field.getZero().add(t1 - t0);
        for (int k = 0; k < a.length; ++k) {
            for (int i = 0; i < y0.length; ++i) {
                fieldY[i] = field.getZero().add(y0[i]);
                for (int s = 0; s < k; ++s) {
                    fieldY[i] = fieldY[i].add(h.multiply(a[s][i].multiply(fieldYDotK[s][i])));
                }
            }
            fieldYDotK[k + 1] = eqn.computeDerivatives(h.multiply(c[k]).add(t0), fieldY);
        }
        interpolator.setSlopes(fieldYDotK);

        // store state at step end
        for (int i = 0; i < y0.length; ++i) {
            fieldY[i] = field.getZero().add(y0[i]);
            for (int s = 0; s < b.length; ++s) {
                fieldY[i] = fieldY[i].add(h.multiply(b[s].multiply(fieldYDotK[s][i])));
            }
        }
        interpolator.storeState(new FieldODEStateAndDerivative<T>(field.getZero().add(t1),
                                                                  fieldY,
                                                                  eqn.computeDerivatives(field.getZero().add(t1), fieldY)));

        return interpolator;

    }

    private static class SinCos<T extends RealFieldElement<T>> implements FieldFirstOrderDifferentialEquations<T> {
        private final Field<T> field;
        protected SinCos(final Field<T> field) {
            this.field = field;
        }
        public int getDimension() {
            return 2;
        }
        public void init(final T t0, final T[] y0, final T finalTime) {
        }
        public T[] computeDerivatives(final T t, final T[] y) {
            T[] yDot = MathArrays.buildArray(field, 2);
            yDot[0] = y[1];
            yDot[1] = y[0].negate();
            return yDot;
        }
    }

}

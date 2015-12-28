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

package org.apache.commons.math3.ode;

import java.util.Random;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.nonstiff.DormandPrince54FieldIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853FieldIntegrator;
import org.apache.commons.math3.ode.sampling.DummyFieldStepInterpolator;
import org.apache.commons.math3.ode.sampling.FieldStepInterpolator;
import org.apache.commons.math3.util.Decimal64Field;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;
import org.junit.Assert;
import org.junit.Test;

public class ContinuousOutputFieldModelTest {

    @Test
    public void testBoundaries() {
        doTestBoundaries(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>> void doTestBoundaries(final Field<T> field) {
        TestFieldProblem3<T> pb = new TestFieldProblem3<T>(field, field.getZero().add(0.9));
        double minStep = 0;
        double maxStep = pb.getFinalTime().subtract(pb.getInitialState().getTime()).getReal();
        FirstOrderFieldIntegrator<T> integ = new DormandPrince54FieldIntegrator<T>(field, minStep, maxStep, 1.0e-8, 1.0e-8);
        integ.addStepHandler(new ContinuousOutputFieldModel<T>());
        integ.integrate(new FieldExpandableODE<T>(pb), pb.getInitialState(), pb.getFinalTime());
        ContinuousOutputFieldModel<T> cm = (ContinuousOutputFieldModel<T>) integ.getStepHandlers().iterator().next();
        cm.getInterpolatedState(pb.getInitialState().getTime().multiply(2).subtract(pb.getFinalTime()));
        cm.getInterpolatedState(pb.getFinalTime().multiply(2).subtract(pb.getInitialState().getTime()));
        cm.getInterpolatedState(pb.getInitialState().getTime().add(pb.getFinalTime()).multiply(0.5));
    }

    @Test
    public void testRandomAccess() {
        doTestRandomAccess(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>> void doTestRandomAccess(final Field<T> field)  {

        TestFieldProblem3<T> pb = new TestFieldProblem3<T>(field, field.getZero().add(0.9));
        double minStep = 0;
        double maxStep = pb.getFinalTime().subtract(pb.getInitialState().getTime()).getReal();
        FirstOrderFieldIntegrator<T> integ = new DormandPrince54FieldIntegrator<T>(field, minStep, maxStep, 1.0e-8, 1.0e-8);
        ContinuousOutputFieldModel<T> cm = new ContinuousOutputFieldModel<T>();
        integ.addStepHandler(cm);
        integ.integrate(new FieldExpandableODE<T>(pb), pb.getInitialState(), pb.getFinalTime());

        Random random = new Random(347588535632l);
        T maxError    = field.getZero();
        T maxErrorDot = field.getZero();
        for (int i = 0; i < 1000; ++i) {
            double r = random.nextDouble();
            T time = pb.getInitialState().getTime().multiply(r).add(pb.getFinalTime().multiply(1.0 - r));
            FieldODEStateAndDerivative<T> interpolated = cm.getInterpolatedState(time);
            T[] theoreticalY = pb.computeTheoreticalState(time);
            T[] theoreticalYDot  = pb.doComputeDerivatives(time, theoreticalY);
            T dx = interpolated.getState()[0].subtract(theoreticalY[0]);
            T dy = interpolated.getState()[1].subtract(theoreticalY[1]);
            T error = dx.multiply(dx).add(dy.multiply(dy));
            maxError = MathUtils.max(maxError, error);
            T dxDot = interpolated.getDerivative()[0].subtract(theoreticalYDot[0]);
            T dyDot = interpolated.getDerivative()[1].subtract(theoreticalYDot[1]);
            T errorDot = dxDot.multiply(dxDot).add(dyDot.multiply(dyDot));
            maxErrorDot = MathUtils.max(maxErrorDot, errorDot);
        }

        Assert.assertEquals(0.0, maxError.getReal(),    1.0e-9);
        Assert.assertEquals(0.0, maxErrorDot.getReal(), 4.0e-7);

    }

    @Test
    public void testModelsMerging() {
        doTestModelsMerging(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>> void doTestModelsMerging(final Field<T> field) {

        // theoretical solution: y[0] = cos(t), y[1] = sin(t)
        FirstOrderFieldDifferentialEquations<T> problem =
                        new FirstOrderFieldDifferentialEquations<T>() {
            public T[] computeDerivatives(T t, T[] y) {
                T[] yDot = MathArrays.buildArray(field, 2);
                yDot[0] = y[1].negate();
                yDot[1] = y[0];
                return yDot;
            }
            public int getDimension() {
                return 2;
            }
            public void init(T t0, T[] y0, T finalTime) {
            }
        };

        // integrate backward from &pi; to 0;
        ContinuousOutputFieldModel<T> cm1 = new ContinuousOutputFieldModel<T>();
        FirstOrderFieldIntegrator<T> integ1 =
                        new DormandPrince853FieldIntegrator<T>(field, 0, 1.0, 1.0e-8, 1.0e-8);
        integ1.addStepHandler(cm1);
        T t0 = field.getZero().add(FastMath.PI);
        T[] y0 = MathArrays.buildArray(field, 2);
        y0[0] = field.getOne().negate();
        y0[1] = field.getZero();
        integ1.integrate(new FieldExpandableODE<T>(problem),
                         new FieldODEState<T>(t0, y0),
                         field.getZero());

        // integrate backward from 2&pi; to &pi;
        ContinuousOutputFieldModel<T> cm2 = new ContinuousOutputFieldModel<T>();
        FirstOrderFieldIntegrator<T> integ2 =
                        new DormandPrince853FieldIntegrator<T>(field, 0, 0.1, 1.0e-12, 1.0e-12);
        integ2.addStepHandler(cm2);
        t0 = field.getZero().add(2.0 * FastMath.PI);
        y0[0] = field.getOne();
        y0[1] = field.getZero();
        integ2.integrate(new FieldExpandableODE<T>(problem),
                         new FieldODEState<T>(t0, y0),
                         field.getZero().add(FastMath.PI));

        // merge the two half circles
        ContinuousOutputFieldModel<T> cm = new ContinuousOutputFieldModel<T>();
        cm.append(cm2);
        cm.append(new ContinuousOutputFieldModel<T>());
        cm.append(cm1);

        // check circle
        Assert.assertEquals(2.0 * FastMath.PI, cm.getInitialTime().getReal(), 1.0e-12);
        Assert.assertEquals(0, cm.getFinalTime().getReal(), 1.0e-12);
        for (double t = 0; t < 2.0 * FastMath.PI; t += 0.1) {
            FieldODEStateAndDerivative<T> interpolated = cm.getInterpolatedState(field.getZero().add(t));
            Assert.assertEquals(FastMath.cos(t), interpolated.getState()[0].getReal(), 1.0e-7);
            Assert.assertEquals(FastMath.sin(t), interpolated.getState()[1].getReal(), 1.0e-7);
        }

    }

    @Test
    public void testErrorConditions() {
        doTestErrorConditions(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>> void doTestErrorConditions(final Field<T> field) {
        ContinuousOutputFieldModel<T> cm = new ContinuousOutputFieldModel<T>();
        cm.handleStep(buildInterpolator(field, 0, 1, new double[] { 0.0, 1.0, -2.0 }), true);

        // dimension mismatch
        Assert.assertTrue(checkAppendError(field, cm, 1.0, 2.0, new double[] { 0.0, 1.0 }));

        // hole between time ranges
        Assert.assertTrue(checkAppendError(field, cm, 10.0, 20.0, new double[] { 0.0, 1.0, -2.0 }));

        // propagation direction mismatch
        Assert.assertTrue(checkAppendError(field, cm, 1.0, 0.0, new double[] { 0.0, 1.0, -2.0 }));

        // no errors
        Assert.assertFalse(checkAppendError(field, cm, 1.0, 2.0, new double[] { 0.0, 1.0, -2.0 }));

    }

    private <T extends RealFieldElement<T>> boolean checkAppendError(Field<T> field, ContinuousOutputFieldModel<T> cm,
                                                                     double t0, double t1, double[] y) {
        try {
            ContinuousOutputFieldModel<T> otherCm = new ContinuousOutputFieldModel<T>();
            otherCm.handleStep(buildInterpolator(field, t0, t1, y), true);
            cm.append(otherCm);
        } catch(IllegalArgumentException iae) {
            return true; // there was an allowable error
        }
        return false; // no allowable error
    }

    private <T extends RealFieldElement<T>> FieldStepInterpolator<T> buildInterpolator(Field<T> field,
                                                                                       double t0, double t1, double[] y) {
        T[] fieldY = MathArrays.buildArray(field, y.length);
        for (int i = 0; i < y.length; ++i) {
            fieldY[i] = field.getZero().add(y[i]);
        }
        final FieldODEStateAndDerivative<T> s0 = new FieldODEStateAndDerivative<T>(field.getZero().add(t0), fieldY, fieldY);
        final FieldODEStateAndDerivative<T> s1 = new FieldODEStateAndDerivative<T>(field.getZero().add(t1), fieldY, fieldY);
        final FieldEquationsMapper<T> mapper   = new FieldExpandableODE<T>(new FirstOrderFieldDifferentialEquations<T>() {
            public int getDimension() {
                return s0.getStateDimension();
            }
            public void init(T t0, T[] y0, T finalTime) {
            }
            public T[] computeDerivatives(T t, T[] y) {
                return y;
            }
        }).getMapper();
        return new DummyFieldStepInterpolator<T>(t1 >= t0, s0, s1, s0, s1, mapper);
    }

    public void checkValue(double value, double reference) {
        Assert.assertTrue(FastMath.abs(value - reference) < 1.0e-10);
    }

}

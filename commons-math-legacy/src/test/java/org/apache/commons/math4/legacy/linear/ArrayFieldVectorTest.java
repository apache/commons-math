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
package org.apache.commons.math4.legacy.linear;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;

import org.apache.commons.math4.legacy.core.Field;
import org.apache.commons.math4.legacy.core.FieldElement;
import org.apache.commons.math4.legacy.TestUtils;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.apache.commons.math4.legacy.core.dfp.Dfp;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link ArrayFieldVector} class.
 *
 */
public class ArrayFieldVectorTest {

    //
    protected Dfp[][] ma1 = {
            {Dfp25.of(1), Dfp25.of(2), Dfp25.of(3)},
            {Dfp25.of(4), Dfp25.of(5), Dfp25.of(6)},
            {Dfp25.of(7), Dfp25.of(8), Dfp25.of(9)}
    };
    protected Dfp[] vec1 = {Dfp25.of(1), Dfp25.of(2), Dfp25.of(3)};
    protected Dfp[] vec2 = {Dfp25.of(4), Dfp25.of(5), Dfp25.of(6)};
    protected Dfp[] vec3 = {Dfp25.of(7), Dfp25.of(8), Dfp25.of(9)};
    protected Dfp[] vec4 = { Dfp25.of(1), Dfp25.of(2), Dfp25.of(3),
                                  Dfp25.of(4), Dfp25.of(5), Dfp25.of(6),
                                  Dfp25.of(7), Dfp25.of(8), Dfp25.of(9)};
    protected Dfp[] vec_null = {Dfp25.ZERO, Dfp25.ZERO, Dfp25.ZERO};
    protected Dfp[] dvec1 = {Dfp25.of(1), Dfp25.of(2), Dfp25.of(3),
                                  Dfp25.of(4), Dfp25.of(5), Dfp25.of(6),
                                  Dfp25.of(7), Dfp25.of(8), Dfp25.of(9)};
    protected Dfp[][] mat1 = {
            {Dfp25.of(1), Dfp25.of(2), Dfp25.of(3)},
            {Dfp25.of(4), Dfp25.of(5), Dfp25.of(6)},
            {Dfp25.of(7), Dfp25.of(8), Dfp25.of(9)}
    };

    // Testclass to test the FieldVector<Dfp> interface
    // only with enough content to support the test
    public static class FieldVectorTestImpl<T extends FieldElement<T>>
        implements FieldVector<T>, Serializable {

        private static final long serialVersionUID = 3970959016014158539L;

        private final Field<T> field;

        /** Entries of the vector. */
        protected T[] data;

        /** Build an array of elements.
         * @param length size of the array to build
         * @return a new array
         */
        @SuppressWarnings("unchecked") // field is of type T
        private T[] buildArray(final int length) {
            return (T[]) Array.newInstance(field.getRuntimeClass(), length);
        }

        public FieldVectorTestImpl(T[] d) {
            field = d[0].getField();
            data = d.clone();
        }

        @Override
        public Field<T> getField() {
            return field;
        }

        private UnsupportedOperationException unsupported() {
            return new UnsupportedOperationException("Not supported, unneeded for test purposes");
        }

        @Override
        public FieldVector<T> copy() {
            throw unsupported();
        }

        @Override
        public FieldVector<T> add(FieldVector<T> v) {
            throw unsupported();
        }

        public FieldVector<T> add(T[] v) {
            throw unsupported();
        }

        @Override
        public FieldVector<T> subtract(FieldVector<T> v) {
            throw unsupported();
        }

        public FieldVector<T> subtract(T[] v) {
            throw unsupported();
        }

        @Override
        public FieldVector<T> mapAdd(T d) {
            throw unsupported();
        }

        @Override
        public FieldVector<T> mapAddToSelf(T d) {
            throw unsupported();
        }

        @Override
        public FieldVector<T> mapSubtract(T d) {
            throw unsupported();
        }

        @Override
        public FieldVector<T> mapSubtractToSelf(T d) {
            throw unsupported();
        }

        @Override
        public FieldVector<T> mapMultiply(T d) {
            T[] out = buildArray(data.length);
            for (int i = 0; i < data.length; i++) {
                out[i] = data[i].multiply(d);
            }
            return new FieldVectorTestImpl<>(out);
        }

        @Override
        public FieldVector<T> mapMultiplyToSelf(T d) {
            throw unsupported();
        }

        @Override
        public FieldVector<T> mapDivide(T d) {
            throw unsupported();
        }

        @Override
        public FieldVector<T> mapDivideToSelf(T d) {
            throw unsupported();
        }

        @Override
        public FieldVector<T> mapInv() {
            throw unsupported();
        }

        @Override
        public FieldVector<T> mapInvToSelf() {
            throw unsupported();
        }

        @Override
        public FieldVector<T> ebeMultiply(FieldVector<T> v) {
            throw unsupported();
        }

        public FieldVector<T> ebeMultiply(T[] v) {
            throw unsupported();
        }

        @Override
        public FieldVector<T> ebeDivide(FieldVector<T> v) {
            throw unsupported();
        }

        public FieldVector<T> ebeDivide(T[] v) {
            throw unsupported();
        }

        public T[] getData() {
            return data.clone();
        }

        @Override
        public T dotProduct(FieldVector<T> v) {
            T dot = field.getZero();
            for (int i = 0; i < data.length; i++) {
                dot = dot.add(data[i].multiply(v.getEntry(i)));
            }
            return dot;
        }

        public T dotProduct(T[] v) {
            T dot = field.getZero();
            for (int i = 0; i < data.length; i++) {
                dot = dot.add(data[i].multiply(v[i]));
            }
            return dot;
        }

        @Override
        public FieldVector<T> projection(FieldVector<T> v) {
            throw unsupported();
        }

        public FieldVector<T> projection(T[] v) {
            throw unsupported();
        }

        @Override
        public FieldMatrix<T> outerProduct(FieldVector<T> v) {
            throw unsupported();
        }

        public FieldMatrix<T> outerProduct(T[] v) {
            throw unsupported();
        }

        @Override
        public T getEntry(int index) {
            return data[index];
        }

        @Override
        public int getDimension() {
            return data.length;
        }

        @Override
        public FieldVector<T> append(FieldVector<T> v) {
            throw unsupported();
        }

        @Override
        public FieldVector<T> append(T d) {
            throw unsupported();
        }

        public FieldVector<T> append(T[] a) {
            throw unsupported();
        }

        @Override
        public FieldVector<T> getSubVector(int index, int n) {
            throw unsupported();
        }

        @Override
        public void setEntry(int index, T value) {
            throw unsupported();
        }

        @Override
        public void setSubVector(int index, FieldVector<T> v) {
            throw unsupported();
        }

        public void setSubVector(int index, T[] v) {
            throw unsupported();
        }

        @Override
        public void set(T value) {
            throw unsupported();
        }

        @Override
        public T[] toArray() {
            return data.clone();
        }
    }

    @Test
    public void testConstructors() {

        ArrayFieldVector<Dfp> v0 = new ArrayFieldVector<>(Dfp25.getField());
        Assert.assertEquals(0, v0.getDimension());

        ArrayFieldVector<Dfp> v1 = new ArrayFieldVector<>(Dfp25.getField(), 7);
        Assert.assertEquals(7, v1.getDimension());
        Assert.assertEquals(Dfp25.ZERO, v1.getEntry(6));

        ArrayFieldVector<Dfp> v2 = new ArrayFieldVector<>(5, Dfp25.of(123, 100));
        Assert.assertEquals(5, v2.getDimension());
        Assert.assertEquals(Dfp25.of(123, 100), v2.getEntry(4));

        ArrayFieldVector<Dfp> v3 = new ArrayFieldVector<>(Dfp25.getField(), vec1);
        Assert.assertEquals(3, v3.getDimension());
        Assert.assertEquals(Dfp25.of(2), v3.getEntry(1));

        ArrayFieldVector<Dfp> v4 = new ArrayFieldVector<>(Dfp25.getField(), vec4, 3, 2);
        Assert.assertEquals(2, v4.getDimension());
        Assert.assertEquals(Dfp25.of(4), v4.getEntry(0));
        try {
            new ArrayFieldVector<>(vec4, 8, 3);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected behavior
        }

        FieldVector<Dfp> v5_i = new ArrayFieldVector<>(dvec1);
        Assert.assertEquals(9, v5_i.getDimension());
        Assert.assertEquals(Dfp25.of(9), v5_i.getEntry(8));

        ArrayFieldVector<Dfp> v5 = new ArrayFieldVector<>(dvec1);
        Assert.assertEquals(9, v5.getDimension());
        Assert.assertEquals(Dfp25.of(9), v5.getEntry(8));

        ArrayFieldVector<Dfp> v6 = new ArrayFieldVector<>(dvec1, 3, 2);
        Assert.assertEquals(2, v6.getDimension());
        Assert.assertEquals(Dfp25.of(4), v6.getEntry(0));
        try {
            new ArrayFieldVector<>(dvec1, 8, 3);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected behavior
        }

        ArrayFieldVector<Dfp> v7 = new ArrayFieldVector<>(v1);
        Assert.assertEquals(7, v7.getDimension());
        Assert.assertEquals(Dfp25.ZERO, v7.getEntry(6));

        FieldVectorTestImpl<Dfp> v7_i = new FieldVectorTestImpl<>(vec1);

        ArrayFieldVector<Dfp> v7_2 = new ArrayFieldVector<>(v7_i);
        Assert.assertEquals(3, v7_2.getDimension());
        Assert.assertEquals(Dfp25.of(2), v7_2.getEntry(1));

        ArrayFieldVector<Dfp> v8 = new ArrayFieldVector<>(v1, true);
        Assert.assertEquals(7, v8.getDimension());
        Assert.assertEquals(Dfp25.ZERO, v8.getEntry(6));
        Assert.assertNotSame("testData not same object ", v1.getDataRef(), v8.getDataRef());

        ArrayFieldVector<Dfp> v8_2 = new ArrayFieldVector<>(v1, false);
        Assert.assertEquals(7, v8_2.getDimension());
        Assert.assertEquals(Dfp25.ZERO, v8_2.getEntry(6));
        Assert.assertArrayEquals(v1.getDataRef(), v8_2.getDataRef());

        ArrayFieldVector<Dfp> v9 = new ArrayFieldVector<>((FieldVector<Dfp>) v1, (FieldVector<Dfp>) v3);
        Assert.assertEquals(10, v9.getDimension());
        Assert.assertEquals(Dfp25.of(1), v9.getEntry(7));
    }

    @Test
    public void testDataInOut() {

        ArrayFieldVector<Dfp> v1 = new ArrayFieldVector<>(vec1);
        ArrayFieldVector<Dfp> v2 = new ArrayFieldVector<>(vec2);
        ArrayFieldVector<Dfp> v4 = new ArrayFieldVector<>(vec4);
        FieldVectorTestImpl<Dfp> v2_t = new FieldVectorTestImpl<>(vec2);

        FieldVector<Dfp> v_append_1 = v1.append(v2);
        Assert.assertEquals(6, v_append_1.getDimension());
        Assert.assertEquals(Dfp25.of(4), v_append_1.getEntry(3));

        FieldVector<Dfp> v_append_2 = v1.append(Dfp25.of(2));
        Assert.assertEquals(4, v_append_2.getDimension());
        Assert.assertEquals(Dfp25.of(2), v_append_2.getEntry(3));

        FieldVector<Dfp> v_append_4 = v1.append(v2_t);
        Assert.assertEquals(6, v_append_4.getDimension());
        Assert.assertEquals(Dfp25.of(4), v_append_4.getEntry(3));

        FieldVector<Dfp> v_copy = v1.copy();
        Assert.assertEquals(3, v_copy.getDimension());
        Assert.assertNotSame("testData not same object ", v1.getDataRef(), v_copy.toArray());

        Dfp[] a_frac = v1.toArray();
        Assert.assertEquals(3, a_frac.length);
        Assert.assertNotSame("testData not same object ", v1.getDataRef(), a_frac);


//      ArrayFieldVector<Dfp> vout4 = (ArrayFieldVector<Dfp>) v1.clone();
//      Assert.assertEquals(3, vout4.getDimension());
//      Assert.assertEquals(v1.getDataRef(), vout4.getDataRef());


        FieldVector<Dfp> vout5 = v4.getSubVector(3, 3);
        Assert.assertEquals(3, vout5.getDimension());
        Assert.assertEquals(Dfp25.of(5), vout5.getEntry(1));
        try {
            v4.getSubVector(3, 7);
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException ex) {
            // expected behavior
        }

        ArrayFieldVector<Dfp> v_set1 = (ArrayFieldVector<Dfp>) v1.copy();
        v_set1.setEntry(1, Dfp25.of(11));
        Assert.assertEquals(Dfp25.of(11), v_set1.getEntry(1));
        try {
            v_set1.setEntry(3, Dfp25.of(11));
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException ex) {
            // expected behavior
        }

        ArrayFieldVector<Dfp> v_set2 = (ArrayFieldVector<Dfp>) v4.copy();
        v_set2.set(3, v1);
        Assert.assertEquals(Dfp25.of(1), v_set2.getEntry(3));
        Assert.assertEquals(Dfp25.of(7), v_set2.getEntry(6));
        try {
            v_set2.set(7, v1);
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException ex) {
            // expected behavior
        }

        ArrayFieldVector<Dfp> v_set3 = (ArrayFieldVector<Dfp>) v1.copy();
        v_set3.set(Dfp25.of(13));
        Assert.assertEquals(Dfp25.of(13), v_set3.getEntry(2));

        try {
            v_set3.getEntry(23);
            Assert.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected behavior
        }

        ArrayFieldVector<Dfp> v_set4 = (ArrayFieldVector<Dfp>) v4.copy();
        v_set4.setSubVector(3, v2_t);
        Assert.assertEquals(Dfp25.of(4), v_set4.getEntry(3));
        Assert.assertEquals(Dfp25.of(7), v_set4.getEntry(6));
        try {
            v_set4.setSubVector(7, v2_t);
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException ex) {
            // expected behavior
        }


        ArrayFieldVector<Dfp> vout10 = (ArrayFieldVector<Dfp>) v1.copy();
        ArrayFieldVector<Dfp> vout10_2 = (ArrayFieldVector<Dfp>) v1.copy();
        Assert.assertEquals(vout10, vout10_2);
        vout10_2.setEntry(0, Dfp25.of(11, 10));
        Assert.assertNotSame(vout10, vout10_2);
    }

    @Test
    public void testMapFunctions() {
        ArrayFieldVector<Dfp> v1 = new ArrayFieldVector<>(vec1);

        //octave =  v1 .+ 2.0
        FieldVector<Dfp> v_mapAdd = v1.mapAdd(Dfp25.of(2));
        Dfp[] result_mapAdd = {Dfp25.of(3), Dfp25.of(4), Dfp25.of(5)};
        checkArray("compare vectors" ,result_mapAdd,v_mapAdd.toArray());

        //octave =  v1 .+ 2.0
        FieldVector<Dfp> v_mapAddToSelf = v1.copy();
        v_mapAddToSelf.mapAddToSelf(Dfp25.of(2));
        Dfp[] result_mapAddToSelf = {Dfp25.of(3), Dfp25.of(4), Dfp25.of(5)};
        checkArray("compare vectors" ,result_mapAddToSelf,v_mapAddToSelf.toArray());

        //octave =  v1 .- 2.0
        FieldVector<Dfp> v_mapSubtract = v1.mapSubtract(Dfp25.of(2));
        Dfp[] result_mapSubtract = {Dfp25.of(-1), Dfp25.ZERO, Dfp25.of(1)};
        checkArray("compare vectors" ,result_mapSubtract,v_mapSubtract.toArray());

        //octave =  v1 .- 2.0
        FieldVector<Dfp> v_mapSubtractToSelf = v1.copy();
        v_mapSubtractToSelf.mapSubtractToSelf(Dfp25.of(2));
        Dfp[] result_mapSubtractToSelf = {Dfp25.of(-1), Dfp25.ZERO, Dfp25.of(1)};
        checkArray("compare vectors" ,result_mapSubtractToSelf,v_mapSubtractToSelf.toArray());

        //octave =  v1 .* 2.0
        FieldVector<Dfp> v_mapMultiply = v1.mapMultiply(Dfp25.of(2));
        Dfp[] result_mapMultiply = {Dfp25.of(2), Dfp25.of(4), Dfp25.of(6)};
        checkArray("compare vectors" ,result_mapMultiply,v_mapMultiply.toArray());

        //octave =  v1 .* 2.0
        FieldVector<Dfp> v_mapMultiplyToSelf = v1.copy();
        v_mapMultiplyToSelf.mapMultiplyToSelf(Dfp25.of(2));
        Dfp[] result_mapMultiplyToSelf = {Dfp25.of(2), Dfp25.of(4), Dfp25.of(6)};
        checkArray("compare vectors" ,result_mapMultiplyToSelf,v_mapMultiplyToSelf.toArray());

        //octave =  v1 ./ 2.0
        FieldVector<Dfp> v_mapDivide = v1.mapDivide(Dfp25.of(2));
        Dfp[] result_mapDivide = {Dfp25.of(1, 2), Dfp25.of(1), Dfp25.of(3, 2)};
        checkArray("compare vectors" ,result_mapDivide,v_mapDivide.toArray());

        //octave =  v1 ./ 2.0
        FieldVector<Dfp> v_mapDivideToSelf = v1.copy();
        v_mapDivideToSelf.mapDivideToSelf(Dfp25.of(2));
        Dfp[] result_mapDivideToSelf = {Dfp25.of(1, 2), Dfp25.of(1), Dfp25.of(3, 2)};
        checkArray("compare vectors" ,result_mapDivideToSelf,v_mapDivideToSelf.toArray());

        //octave =  v1 .^-1
        FieldVector<Dfp> v_mapInv = v1.mapInv();
        Dfp[] result_mapInv = {Dfp25.of(1),Dfp25.of(1, 2),Dfp25.of(1, 3)};
        checkArray("compare vectors" ,result_mapInv,v_mapInv.toArray());

        //octave =  v1 .^-1
        FieldVector<Dfp> v_mapInvToSelf = v1.copy();
        v_mapInvToSelf.mapInvToSelf();
        Dfp[] result_mapInvToSelf = {Dfp25.of(1),Dfp25.of(1, 2),Dfp25.of(1, 3)};
        checkArray("compare vectors" ,result_mapInvToSelf,v_mapInvToSelf.toArray());
    }

    @Test
    public void testBasicFunctions() {
        ArrayFieldVector<Dfp> v1 = new ArrayFieldVector<>(vec1);
        ArrayFieldVector<Dfp> v2 = new ArrayFieldVector<>(vec2);
        new ArrayFieldVector<>(vec_null);

        FieldVectorTestImpl<Dfp> v2_t = new FieldVectorTestImpl<>(vec2);

        //octave =  v1 + v2
        ArrayFieldVector<Dfp> v_add = v1.add(v2);
        Dfp[] result_add = {Dfp25.of(5), Dfp25.of(7), Dfp25.of(9)};
        checkArray("compare vect" ,v_add.toArray(),result_add);

        FieldVectorTestImpl<Dfp> vt2 = new FieldVectorTestImpl<>(vec2);
        FieldVector<Dfp> v_add_i = v1.add(vt2);
        Dfp[] result_add_i = {Dfp25.of(5), Dfp25.of(7), Dfp25.of(9)};
        checkArray("compare vect" ,v_add_i.toArray(),result_add_i);

        //octave =  v1 - v2
        ArrayFieldVector<Dfp> v_subtract = v1.subtract(v2);
        Dfp[] result_subtract = {Dfp25.of(-3), Dfp25.of(-3), Dfp25.of(-3)};
        checkArray("compare vect" ,v_subtract.toArray(),result_subtract);

        FieldVector<Dfp> v_subtract_i = v1.subtract(vt2);
        Dfp[] result_subtract_i = {Dfp25.of(-3), Dfp25.of(-3), Dfp25.of(-3)};
        checkArray("compare vect" ,v_subtract_i.toArray(),result_subtract_i);

        // octave v1 .* v2
        ArrayFieldVector<Dfp>  v_ebeMultiply = v1.ebeMultiply(v2);
        Dfp[] result_ebeMultiply = {Dfp25.of(4), Dfp25.of(10), Dfp25.of(18)};
        checkArray("compare vect" ,v_ebeMultiply.toArray(),result_ebeMultiply);

        FieldVector<Dfp>  v_ebeMultiply_2 = v1.ebeMultiply(v2_t);
        Dfp[] result_ebeMultiply_2 = {Dfp25.of(4), Dfp25.of(10), Dfp25.of(18)};
        checkArray("compare vect" ,v_ebeMultiply_2.toArray(),result_ebeMultiply_2);

        // octave v1 ./ v2
        ArrayFieldVector<Dfp>  v_ebeDivide = v1.ebeDivide(v2);
        Dfp[] result_ebeDivide = {Dfp25.of(1, 4), Dfp25.of(2, 5), Dfp25.of(1, 2)};
        checkArray("compare vect" ,v_ebeDivide.toArray(),result_ebeDivide);

        FieldVector<Dfp>  v_ebeDivide_2 = v1.ebeDivide(v2_t);
        Dfp[] result_ebeDivide_2 = {Dfp25.of(1, 4), Dfp25.of(2, 5), Dfp25.of(1, 2)};
        checkArray("compare vect" ,v_ebeDivide_2.toArray(),result_ebeDivide_2);

        // octave  dot(v1,v2)
        Dfp dot =  v1.dotProduct(v2);
        Assert.assertEquals("compare val ",Dfp25.of(32), dot);

        // octave  dot(v1,v2_t)
        Dfp dot_2 =  v1.dotProduct(v2_t);
        Assert.assertEquals("compare val ",Dfp25.of(32), dot_2);

        FieldMatrix<Dfp> m_outerProduct = v1.outerProduct(v2);
        Assert.assertEquals("compare val ",Dfp25.of(4), m_outerProduct.getEntry(0,0));

        FieldMatrix<Dfp> m_outerProduct_2 = v1.outerProduct(v2_t);
        Assert.assertEquals("compare val ",Dfp25.of(4), m_outerProduct_2.getEntry(0,0));

        ArrayFieldVector<Dfp> v_projection = v1.projection(v2);
        Dfp[] result_projection = {Dfp25.of(128, 77), Dfp25.of(160, 77), Dfp25.of(192, 77)};
        checkArray("compare vect", v_projection.toArray(), result_projection);

        FieldVector<Dfp> v_projection_2 = v1.projection(v2_t);
        Dfp[] result_projection_2 = {Dfp25.of(128, 77), Dfp25.of(160, 77), Dfp25.of(192, 77)};
        checkArray("compare vect", v_projection_2.toArray(), result_projection_2);
    }

    @Test
    public void testMisc() {
        ArrayFieldVector<Dfp> v1 = new ArrayFieldVector<>(vec1);
        ArrayFieldVector<Dfp> v4 = new ArrayFieldVector<>(vec4);
        FieldVector<Dfp> v4_2 = new ArrayFieldVector<>(vec4);

        String out1 = v1.toString();
        Assert.assertTrue("some output ",  out1.length()!=0);
        /*
         Dfp[] dout1 = v1.copyOut();
        Assert.assertEquals(3, dout1.length);
        assertNotSame("testData not same object ", v1.getDataRef(), dout1);
         */
        try {
            v1.checkVectorDimensions(2);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected behavior
        }

       try {
            v1.checkVectorDimensions(v4);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected behavior
        }

        try {
            v1.checkVectorDimensions(v4_2);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected behavior
        }
    }

    @Test
    public void testSerial()  {
        final int n = 2;
        ArrayFieldVector<BigReal> v = new ArrayFieldVector<>(BigRealField.getInstance());
        for (int i = 0; i < n; i++) {
            v.append(new BigReal(Math.random()));
        }
        Assert.assertEquals(v, TestUtils.serializeAndRecover(v));
    }

    @Test
    public void testZeroVectors() {

        // when the field is not specified, array cannot be empty
        try {
            new ArrayFieldVector<>(new Dfp[0]);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected behavior
        }
        try {
            new ArrayFieldVector<>(new Dfp[0], true);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected behavior
        }
        try {
            new ArrayFieldVector<>(new Dfp[0], false);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected behavior
        }

        // when the field is specified, array can be empty
        Assert.assertEquals(0, new ArrayFieldVector<>(Dfp25.getField(), new Dfp[0]).getDimension());
        Assert.assertEquals(0, new ArrayFieldVector<>(Dfp25.getField(), new Dfp[0], true).getDimension());
        Assert.assertEquals(0, new ArrayFieldVector<>(Dfp25.getField(), new Dfp[0], false).getDimension());
    }

    @Test
    public void testOuterProduct() {
        final ArrayFieldVector<Dfp> u
            = new ArrayFieldVector<>(Dfp25.getField(),
                                             new Dfp[] {Dfp25.of(1),
                                                             Dfp25.of(2),
                                                             Dfp25.of(-3)});
        final ArrayFieldVector<Dfp> v
            = new ArrayFieldVector<>(Dfp25.getField(),
                                             new Dfp[] {Dfp25.of(4),
                                                             Dfp25.of(-2)});

        final FieldMatrix<Dfp> uv = u.outerProduct(v);

        final double tol = Math.ulp(1d);
        Assert.assertEquals(Dfp25.of(4).toDouble(), uv.getEntry(0, 0).toDouble(), tol);
        Assert.assertEquals(Dfp25.of(-2).toDouble(), uv.getEntry(0, 1).toDouble(), tol);
        Assert.assertEquals(Dfp25.of(8).toDouble(), uv.getEntry(1, 0).toDouble(), tol);
        Assert.assertEquals(Dfp25.of(-4).toDouble(), uv.getEntry(1, 1).toDouble(), tol);
        Assert.assertEquals(Dfp25.of(-12).toDouble(), uv.getEntry(2, 0).toDouble(), tol);
        Assert.assertEquals(Dfp25.of(6).toDouble(), uv.getEntry(2, 1).toDouble(), tol);
    }

    /** verifies that two vectors are equals */
    protected void checkArray(String msg, Dfp[] m, Dfp[] n) {
        if (m.length != n.length) {
            Assert.fail("vectors have different lengths");
        }
        for (int i = 0; i < m.length; i++) {
            Assert.assertEquals(msg + " " +  i + " elements differ", m[i],n[i]);
        }
    }

    /*
     * TESTS OF THE VISITOR PATTERN
     */

    /** The whole vector is visited. */
    @Test
    public void testWalkInDefaultOrderPreservingVisitor1() {
        final Dfp[] data = new Dfp[] {
            Dfp25.ZERO, Dfp25.ONE, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.TWO, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.ZERO, Dfp25.of(3)
        };
        final ArrayFieldVector<Dfp> v = new ArrayFieldVector<>(data);
        final FieldVectorPreservingVisitor<Dfp> visitor;
        visitor = new FieldVectorPreservingVisitor<Dfp>() {

            private int expectedIndex;

            @Override
            public void visit(final int actualIndex, final Dfp actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
                ++expectedIndex;
            }

            @Override
            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                expectedIndex = 0;
            }

            @Override
            public Dfp end() {
                return Dfp25.ZERO;
            }
        };
        v.walkInDefaultOrder(visitor);
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInDefaultOrderPreservingVisitor2() {
        final ArrayFieldVector<Dfp> v = create(5);
        final FieldVectorPreservingVisitor<Dfp> visitor;
        visitor = new FieldVectorPreservingVisitor<Dfp>() {

            @Override
            public void visit(int index, Dfp value) {
                // Do nothing
            }

            @Override
            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            @Override
            public Dfp end() {
                return Dfp25.ZERO;
            }
        };
        try {
            v.walkInDefaultOrder(visitor, -1, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 5, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 0, -1);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 0, 5);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 4, 0);
            Assert.fail();
        } catch (NumberIsTooSmallException e) {
            // Expected behavior
        }
    }

    /** Visiting a valid subvector. */
    @Test
    public void testWalkInDefaultOrderPreservingVisitor3() {
        final Dfp[] data = new Dfp[] {
            Dfp25.ZERO, Dfp25.ONE, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.TWO, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.ZERO, Dfp25.of(3)
        };
        final ArrayFieldVector<Dfp> v = new ArrayFieldVector<>(data);
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final FieldVectorPreservingVisitor<Dfp> visitor;
        visitor = new FieldVectorPreservingVisitor<Dfp>() {

            private int expectedIndex;

            @Override
            public void visit(final int actualIndex, final Dfp actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
                ++expectedIndex;
            }

            @Override
            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                expectedIndex = expectedStart;
            }

            @Override
            public Dfp end() {
                return Dfp25.ZERO;
            }
        };
        v.walkInDefaultOrder(visitor, expectedStart, expectedEnd);
    }

    /** The whole vector is visited. */
    @Test
    public void testWalkInOptimizedOrderPreservingVisitor1() {
        final Dfp[] data = new Dfp[] {
            Dfp25.ZERO, Dfp25.ONE, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.TWO, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.ZERO, Dfp25.of(3)
        };
        final ArrayFieldVector<Dfp> v = new ArrayFieldVector<>(data);
        final FieldVectorPreservingVisitor<Dfp> visitor;
        visitor = new FieldVectorPreservingVisitor<Dfp>() {
            private final boolean[] visited = new boolean[data.length];

            @Override
            public void visit(final int actualIndex, final Dfp actualValue) {
                visited[actualIndex] = true;
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
            }

            @Override
            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                Arrays.fill(visited, false);
            }

            @Override
            public Dfp end() {
                for (int i = 0; i < data.length; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return Dfp25.ZERO;
            }
        };
        v.walkInOptimizedOrder(visitor);
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInOptimizedOrderPreservingVisitor2() {
        final ArrayFieldVector<Dfp> v = create(5);
        final FieldVectorPreservingVisitor<Dfp> visitor;
        visitor = new FieldVectorPreservingVisitor<Dfp>() {

            @Override
            public void visit(int index, Dfp value) {
                // Do nothing
            }

            @Override
            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            @Override
            public Dfp end() {
                return Dfp25.ZERO;
            }
        };
        try {
            v.walkInOptimizedOrder(visitor, -1, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 5, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 0, -1);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 0, 5);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 4, 0);
            Assert.fail();
        } catch (NumberIsTooSmallException e) {
            // Expected behavior
        }
    }

    /** Visiting a valid subvector. */
    @Test
    public void testWalkInOptimizedOrderPreservingVisitor3() {
        final Dfp[] data = new Dfp[] {
            Dfp25.ZERO, Dfp25.ONE, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.TWO, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.ZERO, Dfp25.of(3)
        };
        final ArrayFieldVector<Dfp> v = new ArrayFieldVector<>(data);
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final FieldVectorPreservingVisitor<Dfp> visitor;
        visitor = new FieldVectorPreservingVisitor<Dfp>() {
            private final boolean[] visited = new boolean[data.length];

            @Override
            public void visit(final int actualIndex, final Dfp actualValue) {
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
                visited[actualIndex] = true;
            }

            @Override
            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                Arrays.fill(visited, true);
            }

            @Override
            public Dfp end() {
                for (int i = expectedStart; i <= expectedEnd; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return Dfp25.ZERO;
            }
        };
        v.walkInOptimizedOrder(visitor, expectedStart, expectedEnd);
    }

    /** The whole vector is visited. */
    @Test
    public void testWalkInDefaultOrderChangingVisitor1() {
        final Dfp[] data = new Dfp[] {
            Dfp25.ZERO, Dfp25.ONE, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.TWO, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.ZERO, Dfp25.of(3)
        };
        final ArrayFieldVector<Dfp> v = new ArrayFieldVector<>(data);
        final FieldVectorChangingVisitor<Dfp> visitor;
        visitor = new FieldVectorChangingVisitor<Dfp>() {

            private int expectedIndex;

            @Override
            public Dfp visit(final int actualIndex, final Dfp actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
                ++expectedIndex;
                return actualValue.add(actualIndex);
            }

            @Override
            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                expectedIndex = 0;
            }

            @Override
            public Dfp end() {
                return Dfp25.ZERO;
            }
        };
        v.walkInDefaultOrder(visitor);
        for (int i = 0; i < data.length; i++) {
            Assert.assertEquals("entry " + i, data[i].add(i), v.getEntry(i));
        }
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInDefaultOrderChangingVisitor2() {
        final ArrayFieldVector<Dfp> v = create(5);
        final FieldVectorChangingVisitor<Dfp> visitor;
        visitor = new FieldVectorChangingVisitor<Dfp>() {

            @Override
            public Dfp visit(int index, Dfp value) {
                return Dfp25.ZERO;
            }

            @Override
            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            @Override
            public Dfp end() {
                return Dfp25.ZERO;
            }
        };
        try {
            v.walkInDefaultOrder(visitor, -1, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 5, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 0, -1);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 0, 5);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 4, 0);
            Assert.fail();
        } catch (NumberIsTooSmallException e) {
            // Expected behavior
        }
    }

    /** Visiting a valid subvector. */
    @Test
    public void testWalkInDefaultOrderChangingVisitor3() {
        final Dfp[] data = new Dfp[] {
            Dfp25.ZERO, Dfp25.ONE, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.TWO, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.ZERO, Dfp25.of(3)
        };
        final ArrayFieldVector<Dfp> v = new ArrayFieldVector<>(data);
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final FieldVectorChangingVisitor<Dfp> visitor;
        visitor = new FieldVectorChangingVisitor<Dfp>() {

            private int expectedIndex;

            @Override
            public Dfp visit(final int actualIndex, final Dfp actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
                ++expectedIndex;
                return actualValue.add(actualIndex);
            }

            @Override
            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                expectedIndex = expectedStart;
            }

            @Override
            public Dfp end() {
                return Dfp25.ZERO;
            }
        };
        v.walkInDefaultOrder(visitor, expectedStart, expectedEnd);
        for (int i = expectedStart; i <= expectedEnd; i++) {
            Assert.assertEquals("entry " + i, data[i].add(i), v.getEntry(i));
        }
    }

    /** The whole vector is visited. */
    @Test
    public void testWalkInOptimizedOrderChangingVisitor1() {
        final Dfp[] data = new Dfp[] {
            Dfp25.ZERO, Dfp25.ONE, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.TWO, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.ZERO, Dfp25.of(3)
        };
        final ArrayFieldVector<Dfp> v = new ArrayFieldVector<>(data);
        final FieldVectorChangingVisitor<Dfp> visitor;
        visitor = new FieldVectorChangingVisitor<Dfp>() {
            private final boolean[] visited = new boolean[data.length];

            @Override
            public Dfp visit(final int actualIndex, final Dfp actualValue) {
                visited[actualIndex] = true;
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
                return actualValue.add(actualIndex);
            }

            @Override
            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                Arrays.fill(visited, false);
            }

            @Override
            public Dfp end() {
                for (int i = 0; i < data.length; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return Dfp25.ZERO;
            }
        };
        v.walkInOptimizedOrder(visitor);
        for (int i = 0; i < data.length; i++) {
            Assert.assertEquals("entry " + i, data[i].add(i), v.getEntry(i));
        }
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInOptimizedOrderChangingVisitor2() {
        final ArrayFieldVector<Dfp> v = create(5);
        final FieldVectorChangingVisitor<Dfp> visitor;
        visitor = new FieldVectorChangingVisitor<Dfp>() {

            @Override
            public Dfp visit(int index, Dfp value) {
                return Dfp25.ZERO;
            }

            @Override
            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            @Override
            public Dfp end() {
                return Dfp25.ZERO;
            }
        };
        try {
            v.walkInOptimizedOrder(visitor, -1, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 5, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 0, -1);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 0, 5);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 4, 0);
            Assert.fail();
        } catch (NumberIsTooSmallException e) {
            // Expected behavior
        }
    }

    /** Visiting a valid subvector. */
    @Test
    public void testWalkInOptimizedOrderChangingVisitor3() {
        final Dfp[] data = new Dfp[] {
            Dfp25.ZERO, Dfp25.ONE, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.TWO, Dfp25.ZERO,
            Dfp25.ZERO, Dfp25.ZERO, Dfp25.of(3)
        };
        final ArrayFieldVector<Dfp> v = new ArrayFieldVector<>(data);
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final FieldVectorChangingVisitor<Dfp> visitor;
        visitor = new FieldVectorChangingVisitor<Dfp>() {
            private final boolean[] visited = new boolean[data.length];

            @Override
            public Dfp visit(final int actualIndex, final Dfp actualValue) {
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue);
                visited[actualIndex] = true;
                return actualValue.add(actualIndex);
            }

            @Override
            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                Arrays.fill(visited, true);
            }

            @Override
            public Dfp end() {
                for (int i = expectedStart; i <= expectedEnd; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return Dfp25.ZERO;
            }
        };
        v.walkInOptimizedOrder(visitor, expectedStart, expectedEnd);
        for (int i = expectedStart; i <= expectedEnd; i++) {
            Assert.assertEquals("entry " + i, data[i].add(i), v.getEntry(i));
        }
    }

    private ArrayFieldVector<Dfp> create(int n) {
        Dfp[] t = new Dfp[n];
        for (int i = 0; i < n; ++i) {
            t[i] = Dfp25.ZERO;
        }
        return new ArrayFieldVector<>(t);
    }
}

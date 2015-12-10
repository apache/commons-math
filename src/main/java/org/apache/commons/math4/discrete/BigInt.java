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
package org.apache.commons.math4.discrete;

import java.io.Serializable;
import java.math.BigInteger;

import org.apache.commons.math4.RingElement;

/**
 * Big integer.
 * <p>
 * This class is a simple wrapper around the standard <code>BigInteger</code> in
 * order to implement the {@link FieldElement} interface.
 * </p>
 * 
 * @since 2.0
 */
public class BigInt implements RingElement<BigInt>, Comparable<BigInt>,
        Serializable {

    /** A big real representing 0. */
    public static final BigInt ZERO = new BigInt(BigInteger.ZERO);

    /** A big real representing 1. */
    public static final BigInt ONE = new BigInt(BigInteger.ONE);

    /** A big real representing 2. */
    public static final BigInt TWO = new BigInt(2);

    /** A big real representing 3. */
    public static final BigInt THREE = new BigInt(3);

    /** A big real representing 4. */
    public static final BigInt FOUR = new BigInt(4);

    /** A big real representing 10. */
    public static final BigInt TEN = new BigInt(10);

    /** Serializable version identifier. */
    private static final long serialVersionUID = 498453482391310382L;

    /** Underlying BigInteger. */
    private final BigInteger d;

    /**
     * Build an instance from a BigInteger.
     * 
     * @param val
     *            value of the instance
     */
    public BigInt(BigInteger val) {
        d = val;
    }

    /**
     * Build an instance from an int.
     * 
     * @param val
     *            value of the instance
     */
    public BigInt(long val) {
        d = new BigInteger(Long.toString(val));
    }

    /**
     * Build an instance from a String representation.
     * 
     * @param val
     *            character representation of the value
     */
    public BigInt(String val) {
        d = new BigInteger(val);
    }

    /** Please notice Java's String size is limited. */
    public String toString() {
        return d.toString();
    }

    /** {@inheritDoc} */
    @Override
    public BigInt add(BigInt a) {
        return new BigInt(d.add(a.d));
    }

    /** {@inheritDoc} */
    @Override
    public BigInt subtract(BigInt a) {
        return new BigInt(d.subtract(a.d));
    }

    /** {@inheritDoc} */
    @Override
    public BigInt negate() {
        return new BigInt(d.negate());
    }

    /** {@inheritDoc} */
    @Override
    public BigInt multiply(BigInt a) {
        return new BigInt(d.multiply(a.d));
    }

    /** {@inheritDoc} */
    @Override
    public BigInt multiply(final int n) {
        return new BigInt(d.multiply(new BigInteger(Integer.toString(n))));
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(BigInt a) {
        return d.compareTo(a.d);
    }

    /**
     * Get the double value corresponding to the instance.
     * 
     * @return double value corresponding to the instance
     */
    public double doubleValue() {
        return d.doubleValue();
    }

    /**
     * Get the BigInteger value corresponding to the instance.
     * 
     * @return BigInteger value corresponding to the instance
     */
    public BigInteger BigIntegerValue() {
        return d;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof BigInt) {
            return d.equals(((BigInt) other).d);
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return d.hashCode();
    }

    /*
     * SHOULDN'T THESE BE IN SOME INTERFACE?
     */
    public BigInt mod(BigInt b) {
        return new BigInt(this.d.mod(b.d));
    }

    public BigInt divInt(BigInt b) {
        return new BigInt(this.d.divide(b.d));
    }

    public BigInteger getBigInteger() {
        return this.d;
    }
}

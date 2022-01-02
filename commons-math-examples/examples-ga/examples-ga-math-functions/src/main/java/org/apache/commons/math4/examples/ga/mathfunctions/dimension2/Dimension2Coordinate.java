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
package org.apache.commons.math4.examples.ga.mathfunctions.dimension2;

/**
 * This class represents the coordinate of the problem domain i.e. the phenotype of chromosome.
 */
public class Dimension2Coordinate {

    /** coordinate of first dimension. **/
    private final double x;

    /** coordinate of second dimension. **/
    private final double y;

    /**
     * constructor.
     * @param x coordinate of first dimension
     * @param y coordinate of second dimension
     */
    public Dimension2Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * returns the coordinate of first dimension.
     * @return coordinate of first dimension
     */
    public double getX() {
        return x;
    }

    /**
     * returns the coordinate of second dimension.
     * @return coordinate of second dimension
     */
    public double getY() {
        return y;
    }

    /**
     * Returns a string representation of coordinate.
     */
    @Override
    public String toString() {
        return "Coordinate [x=" + x + ", y=" + y + "]";
    }

}

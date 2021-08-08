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
package org.apache.commons.math4.examples.ga.tsp.commons;

/**
 * This class represents a city with location coordinate.
 */
public final class City {

    /** index of city. **/
    private final int index;

    /** x coordinate. **/
    private final double x;

    /** y coordinate. **/
    private final double y;

    /**
     * constructor.
     * @param index index of city
     * @param x     x coordinate
     * @param y     y coordinate
     */
    public City(int index, double x, double y) {
        this.index = index;
        this.x = x;
        this.y = y;
    }

    /**
     * Returns city index.
     * @return city index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns x coordinate.
     * @return x coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Returns y coordinate.
     * @return y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Node [index=" + index + ", x=" + x + ", y=" + y + "]";
    }

}

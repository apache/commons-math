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
package org.apache.commons.math3.userguide.genetics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;

/**
 * Represents a fixed size polgon with its fill color.
 */
public class Polygon {

    // the polygon in packed representation:
    // index | data
    //    0  | red component
    //    1  | green component
    //    2  | blue component
    //    3  | alpha channel
    //    4  | first x coordinate
    //    5  | first y coordinate
    //    6  | second x coordinate
    //  ...
    //    N  | last y coordinate
    // ---------------------------
    /// size = 4 + 2*polygonlength
    private float[] data;

    /**
     * Creates a new random Polygon of the given length.
     */
    public static Polygon randomPolygon(int length) {
        final int polygonSize = 4 + 2 * length;

        final RandomGenerator random = GeneticAlgorithm.getRandomGenerator();
        
        Polygon p = new Polygon();
        p.data = new float[polygonSize];

        p.data[0] = random.nextFloat(); // r
        p.data[1] = random.nextFloat(); // g
        p.data[2] = random.nextFloat(); // b
        p.data[3] = FastMath.max(0.2f, random.nextFloat() * random.nextFloat()); // a
        
        float px = random.nextFloat();
        float py = random.nextFloat();
        
        for (int k = 0; k < length; k++) {
            p.data[4 + 2*k] = px + (random.nextFloat() - 0.5f);
            p.data[5 + 2*k] = py + (random.nextFloat() - 0.5f);
        }
        return p;
    }

    /**
     * Return a new Polygon, mutated with the given rate and amount.
     * <p>
     * Each component of the Polygon may be mutated according to the specified mutation rate.
     * In case a component is going to be mutated, its value will be randomly modified in the
     * uniform range of [-mutationAmount, +mutationAmount].
     * 
     * @param mutationRate the mutation rate
     * @param mutationAmount the mutation amount
     * @return a new Polygon
     */
    public Polygon mutate(float mutationRate, float mutationAmount) {
        Polygon mutated = new Polygon();
        int size = data.length;
        mutated.data = new float[size];
        for (int i = 0; i < size; i++) {
            float val = this.data[i];
            if (GeneticAlgorithm.getRandomGenerator().nextFloat() < mutationRate) {
                val += GeneticAlgorithm.getRandomGenerator().nextFloat() * mutationAmount * 2 - mutationAmount;
                
                if (val < 0f) {
                    val = 0f;
                } else if (val > 1f) {
                    val = 1f;
                }
            }
            mutated.data[i] = val;
        }
        return mutated;
    }    

    /**
     * Draw the Polygon to the buffer of the given size.
     */
    public void draw(Graphics2D g, int width, int height) {
        g.setColor(new Color(data[0], data[1], data[2], data[3]));

        GeneralPath path = new GeneralPath();
        path.moveTo(data[4] * width, data[5] * height);
        
        int polygonLength = (data.length - 4) / 2;
        for (int j = 1; j < polygonLength; j++) {
            path.lineTo(data[4 + j * 2] * width, data[5 + j * 2] * height);
        }
        path.closePath();

        g.fill(path);
    }
}

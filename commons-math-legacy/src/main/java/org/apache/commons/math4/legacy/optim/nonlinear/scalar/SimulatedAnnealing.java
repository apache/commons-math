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
package org.apache.commons.math4.legacy.optim.nonlinear.scalar;

import java.util.function.BiFunction;
import java.util.function.DoublePredicate;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.math4.legacy.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math4.legacy.optim.OptimizationData;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.noderiv.Simplex;

/**
 * Simulated annealing setup.
 *
 * @since 4.0
 */
public class SimulatedAnnealing implements OptimizationData {
    /** Number of iterations at fixed temperature. */
    private final int epochDuration;
    /** Initial acceptance probability. */
    private final double startProbability;
    /** Final acceptance probability. */
    private final double endProbability;
    /** Cooling function. */
    private final CoolingSchedule coolingSchedule;
    /** RNG. */
    private final UniformRandomProvider rng;

    /**
     * @param epoch Number of iterations performed at fixed temperature.
     * @param startProb Initial acceptance probablility.
     * @param endProb Final acceptance probablility.
     * @param cooling Computes the temperature as a function of the initial
     * temperature and the epoch.
     * It is called for computing a new temperature after each cycle of
     * {@code epoch} iterations.
     * Simulated annealing <em>assumes</em> that the function decreases
     * monotically wrt the epoch (cf. {@link CoolingSchedule#decreasingExponential(double)
     * provided implementation}).
     * @param random Random number generator.
     * @throws IllegalArgumentException if {@code epoch < 1} or
     * {@code startProb} or {@code endProb} is outside the {@code [0, 1]}
     * interval.
     */
    public SimulatedAnnealing(int epoch,
                              double startProb,
                              double endProb,
                              CoolingSchedule cooling,
                              UniformRandomProvider random) {
        if (epoch < 1) {
            throw new IllegalArgumentException("Epoch out of range: " +
                                               epoch);
        }
        if (startProb < 0 ||
            startProb > 1) {
            throw new IllegalArgumentException("Initial acceptance probability out of range: " +
                                               startProb);
        }
        if (endProb < 0 ||
            endProb > 1) {
            throw new IllegalArgumentException("Final acceptance probability out of range: " +
                                               endProb);
        }
        if (endProb >= startProb) {
            throw new IllegalArgumentException("Final probability larger than initial probability");
        }

        epochDuration = epoch;
        startProbability = startProb;
        endProbability = endProb;
        coolingSchedule = cooling;
        rng = random;
    }

    /**
     * @return the epoch duration.
     */
    public int getEpochDuration() {
        return epochDuration;
    }

    /**
     * @return the acceptance probability at the beginning of the SA process.
     */
    public double getStartProbability() {
        return startProbability;
    }

    /**
     * @return the acceptance probability at the end of the SA process.
     */
    public double getEndProbability() {
        return endProbability;
    }

    /**
     * @return the cooling schedule.
     */
    public CoolingSchedule getCoolingSchedule() {
        return coolingSchedule;
    }

    /**
     * Specifies the cooling schedule.
     * It computes the current temperature as a function of two arguments:
     * <ol>
     *  <li>the previous temperature,</li>
     *  <li>the current simplex.</li>
     * </ol>
     */
    public interface CoolingSchedule extends BiFunction<Double, Simplex, Double> {
        /**
         * Power-law cooling scheme:
         * \[
         *   T_i = T_0 * f^i
         * \], where \( i \) is the current iteration.
         * <p>
         * Note: Simplex argument (of the returned function) is not used.
         *
         * @param f Factor by which the temperature is decreased.
         * @return the cooling schedule.
         */
        static CoolingSchedule decreasingExponential(final double f) {
            if (f <= 0 ||
                f >= 1) {
                throw new IllegalArgumentException("Factor out of range: " + f);
            }

            return (previousTemperature, simplex) -> f * previousTemperature;
        }

        /**
         * Aarst and van Laarhoven (1985) scheme:
         * \[
         *   T_{i + 1} = \frac{T_{i}}{1 + \frac{T_i \ln(1 + \delta)}{3 \sigma}}
         * \]
         * <p>
         * The simplex argument is used to compute the standard deviation
         * (\(\sigma\)) of all the vertices' objective function value.
         *
         * @param delta Trajectory parameter. Values smaller than 1 entail slow
         * convergence; values larger than 1 entail convergence to local optimum.
         * @return the cooling schedule.
         */
        static CoolingSchedule aarstAndVanLaarhoven(final double delta) {
            if (delta <= 0) {
                throw new IllegalArgumentException("Trajectory parameter out of range: " +
                                                   delta);
            }

            return (previousTemperature, simplex) -> {
                // Standard deviation of the values of the objective function.
                final StandardDeviation stddev = new StandardDeviation();
                for (int i = 0; i < simplex.getSize(); i++) {
                    stddev.increment(simplex.get(i).getValue());
                }
                final double sigma = stddev.getResult();

                final double a = previousTemperature * Math.log(1 + delta);
                final double b = 3 * sigma;
                return previousTemperature / (1 + a / b);
            };
        }
    }

    /**
     * Factory for the Metropolis check for accepting a worse state.
     * \( e^{-|\Delta E| / T} \geq \mathrm{rand}(0, 1) \).
     *
     * <p>
     * It is assumed that this check is performed <em>after</em> ensuring
     * that the alternate state is <em>worse</em> than the current best state.
     *
     * @param temperature Current temperature.
     * @return the acceptance test.
     */
    public DoublePredicate metropolis(final double temperature) {
        // Absolute value takes care of both minimization and maximization cases.
        return deltaE -> Math.exp(Math.abs(deltaE) / temperature) >= rng.nextDouble();
    }
}

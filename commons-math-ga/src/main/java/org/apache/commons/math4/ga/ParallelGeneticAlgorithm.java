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

package org.apache.commons.math4.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.math4.ga.convergence.StoppingCondition;
import org.apache.commons.math4.ga.internal.exception.GeneticIllegalArgumentException;
import org.apache.commons.math4.ga.population.Population;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of parallel genetic algorithm following island model. Few
 * independently generated populations are converged by separate algorithms in
 * parallel. All genetic operators and convergent conditions can be chosen
 * independently for each algorithm. After convergence the algorithm returns the
 * list of converged populations.
 * @param <P> chromosome phenotype
 */
public class ParallelGeneticAlgorithm<P> {

    /** instance of logger. **/
    private static final Logger LOGGER = LoggerFactory.getLogger(ParallelGeneticAlgorithm.class);
    /** List of algorithm execution configurations to be executed in parallel. **/
    private List<AlgorithmExecutionConfig> algorithmConfigParams = new ArrayList<>();

    /**
     * Adds an instance of Genetic algorithm to be executed in parallel.
     * @param algorithm         the algorithm for optimization
     * @param initialPopulation initial population to converge
     * @param stoppingCondition stopping condition for deciding the convergence
     */
    public void addAlgorithmExecutionConfig(AbstractGeneticAlgorithm<P> algorithm,
            Population<P> initialPopulation,
            StoppingCondition<P> stoppingCondition) {
        LOGGER.info("Adding a GA optimizer instance.");
        this.algorithmConfigParams.add(new AlgorithmExecutionConfig(algorithm, initialPopulation, stoppingCondition));
    }

    /**
     * Evolve the given population. Evolution stops when the stopping condition is
     * satisfied.
     * @param threadCount number of threads to be used in executor service.
     * @return the population that satisfies the stopping condition.
     */
    public List<Population<P>> evolve(int threadCount) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        try {
            return evolve(executorService);
        } finally {
            executorService.shutdown();
        }
    }

    /**
     * Evolve the given population. Evolution stops when the stopping condition is
     * satisfied. Updates the {@link #getGenerationsEvolved() generationsEvolved}
     * property with the number of generations evolved before the StoppingCondition
     * is satisfied.
     * @param executorService the executor service to run threads.
     * @return the population that satisfies the stopping condition.
     */
    private List<Population<P>> evolve(ExecutorService executorService) {
        List<Future<Population<P>>> futureConvergedPopulations = new ArrayList<>();
        for (AlgorithmExecutionConfig algorithmConfigParam : algorithmConfigParams) {
            futureConvergedPopulations.add(executorService.submit(() -> {
                return algorithmConfigParam.algorithm.evolve(algorithmConfigParam.initialPopulation,
                        algorithmConfigParam.stoppingCondition, executorService);
            }));
        }

        List<Population<P>> convergedPopulations = new ArrayList<>();
        try {
            for (Future<Population<P>> future : futureConvergedPopulations) {
                convergedPopulations.add(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new GeneticIllegalArgumentException(e);
        }
        return convergedPopulations;
    }

    private final class AlgorithmExecutionConfig {

        /** instance of genetic algorithm. **/
        private AbstractGeneticAlgorithm<P> algorithm;
        /** initial population to converge. **/
        private Population<P> initialPopulation;
        /** stopping condition to decide convergence. **/
        private StoppingCondition<P> stoppingCondition;

        private AlgorithmExecutionConfig(AbstractGeneticAlgorithm<P> algorithm,
                Population<P> initialPopulation,
                StoppingCondition<P> stoppingCondition) {
            this.algorithm = algorithm;
            this.initialPopulation = initialPopulation;
            this.stoppingCondition = stoppingCondition;
        }
    }
}

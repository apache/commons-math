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

package org.apache.commons.math4.genetics.utils;

/**
 * Contains all constants required for the library.
 */
public final class Constants {

    /** crossover rate. **/
    public static final String CROSSOVER_RATE = "CROSSOVER_RATE";

    /** mutation rate. **/
    public static final String MUTATION_RATE = "MUTATION_RATE";

    /** elitism rate. **/
    public static final String ELITISM_RATE = "ELITISM_RATE";

    /** allele value. **/
    public static final String ALLELE_VALUE = "ALLELE_VALUE";

    /** new line constant. **/
    public static final String NEW_LINE = System.getProperty("line.separator");

    private Constants() {
    }

}

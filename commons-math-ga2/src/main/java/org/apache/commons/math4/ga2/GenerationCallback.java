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
package org.apache.commons.math4.ga2;

import java.util.List;
import java.util.Map;

/**
 * Function for passing a copy of the current population contents to
 * external code.
 *
 * @param <G> Genotype.
 */
public interface GenerationCallback<G> {
    /**
     * @param contents Population (sorted in decreasing order of fitness).
     * @param generation Current generation.
     */
    void update(List<Map.Entry<G, Double>> contents,
                int generation);
}

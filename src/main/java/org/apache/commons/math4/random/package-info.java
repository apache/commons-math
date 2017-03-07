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
/**
 * <p>Random Data Generation</p>
 *
 * <p>
 *  Some of the utilities in this package use the pseudo-random number
 *  generators defined in the <a href="http://commons.apache.org/rng">
 *  Commons RNG</a> library in order to provide
 *  {@link org.apache.commons.math4.random.RandomUtils.DataGenerator
 *  higher level functionality} (such as random strings) based on an underlying
 *  source of randomness that provides sequences of uniformly distributed integers.
 * </p>
 * <p>
 *  Others are sources of pseudo-randomness that directly produce "compound" types
 *  such as {@link org.apache.commons.math4.random.RandomVectorGenerator random vectors}.
 * </p>
 */
package org.apache.commons.math4.random;

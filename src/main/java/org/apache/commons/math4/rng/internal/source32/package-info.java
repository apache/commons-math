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
 * <h3>
 * Concrete algorithms for {@code int}-based sources of randomness
 * </h3>
 *
 * <p>
 * <b>For internal use only:</b> Direct access to classes in this package
 * is discouraged, as they could be modified without notice.
 * </p>
 *
 * <p><b>Notes for developers</b></p>
 *
 * <ul>
 *  <li>
 *   A source of randomness must inherit from
 *   {@link org.apache.commons.math4.rng.internal.source32.IntProvider}
 *  </li>
 *  <li>
 *   The "provider" must specify <em>one</em> way for setting the seed.
 *   For a given seed, the generated sequence must always be the same.
 *  </li>
 *  <li>
 *   The "provider" must implement methods {@code getStateInternal} and
 *   {@code setStateInternal} in order to save and restore the state of an
 *   instance (cf. {@link org.apache.commons.math4.rng.internal.BaseProvider}).
 *  </li>
 *  <li>
 *   When a new class is implemented here, user-access to it must be provided
 *   through associated {@link org.apache.commons.math4.rng.RandomSource
 *   factory methods}.
 *  </li>
 * </ul>
 */

package org.apache.commons.math4.rng.internal.source32;

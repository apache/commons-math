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
 * <h3>Base classes for the {@link org.apache.commons.math4.rng.UniformRandomProvider
 * generation of uniformly distributed random numbers}.
 * </h3>
 *
 * <p>
 * <b>For internal use only:</b> Direct access to classes in this package
 * and below, is discouraged, as they could be modified without notice.
 * </p>
 *
 * <p><b>Notes for developers</b></p>
 *
 * <p>
 * This package contains the common functionality.
 * <br>
 * Implementations that produce
 * {@link org.apache.commons.math4.rng.internal.source32.RandomIntSource int}
 * values are defined in the
 * {@link org.apache.commons.math4.rng.internal.source32 source32} package.
 * <br>
 * Implementations that produce
 * {@link org.apache.commons.math4.rng.internal.source64.RandomLongSource long}
 * values are defined in the
 * {@link org.apache.commons.math4.rng.internal.source64 source64} package.
 * </p>
 *
 * <p>
 * Each implementation must have an identifier in
 * {@link org.apache.commons.math4.rng.internal.ProviderBuilder.RandomSourceInternal}
 * which must be referred to from the {@link org.apache.commons.math4.rng.RandomSource public API}.
 * </p>
 */

package org.apache.commons.math4.rng.internal;

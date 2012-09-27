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

package org.apache.commons.math3.linear;

/**
 * Marker interface for {@link RealMatrix} implementations that require sparse backing storage
 *
 * @version $Id$
 * @since 2.0
 * @deprecated As of version 3.1, this class is deprecated, for reasons exposed
 * in this JIRA
 * <a href="https://issues.apache.org/jira/browse/MATH-870">ticket</a>. This
 * class will be removed in version 4.0.
 *
 */
@Deprecated
public interface SparseRealMatrix extends RealMatrix {

}

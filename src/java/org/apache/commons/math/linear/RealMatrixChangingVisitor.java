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

package org.apache.commons.math.linear;

import java.io.Serializable;

/**
 * Interface defining a visitor for matrix entries.
 * 
 * @version $Revision$ $Date$
 * @since 2.0
 */
public interface RealMatrixChangingVisitor extends Serializable {

    /**
     * Visit one matrix entry.
     * @param row row index of the entry
     * @param column column index of the entry
     * @param value current value of the entry
     * @return the new value to be set for the entry
     * @throws MatrixVisitorException if something wrong occurs
     */
    double visit(int row, int column, double value)
        throws MatrixVisitorException;

}

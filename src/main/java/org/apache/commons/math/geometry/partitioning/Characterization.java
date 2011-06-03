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
package org.apache.commons.math.geometry.partitioning;

import org.apache.commons.math.geometry.Space;

/** Characterization of a sub-hyperplane.
 * @param <S> Type of the space.
 * @version $Id$
 * @since 3.0
 */
class Characterization<S extends Space> {

    /** Parts of the sub-hyperplane that have inside cells on the tested side. */
    private SubHyperplane<S> in;

    /** Parts of the sub-hyperplane that have outside cells on the tested side. */
    private SubHyperplane<S> out;

    /** Create an empty characterization of a sub-hyperplane.
     */
    public Characterization() {
        in  = null;
        out = null;
    }

    /** Check if the sub-hyperplane that have inside cells on the tested side.
     * @return true if the sub-hyperplane that have inside cells on the tested side
     */
    public boolean hasIn() {
        return (in != null) && (!in.isEmpty());
    }

    /** Get the parts of the sub-hyperplane that have inside cells on the tested side.
     * @return parts of the sub-hyperplane that have inside cells on the tested side
     */
    public SubHyperplane<S> getIn() {
        return in;
    }

    /** Check if the sub-hyperplane that have outside cells on the tested side.
     * @return true if the sub-hyperplane that have outside cells on the tested side
     */
    public boolean hasOut() {
        return (out != null) && (!out.isEmpty());
    }

    /** Get the parts of the sub-hyperplane that have outside cells on the tested side.
     * @return parts of the sub-hyperplane that have outside cells on the tested side
     */
    public SubHyperplane<S> getOut() {
        return out;
    }

    /** Add a part of the sub-hyperplane known to have inside or outside cell on the tested side.
     * @param sub part of the sub-hyperplane to add
     * @param inside if true, the part added as an inside cell on the tested side, otherwise
     * it has an outside cell on the tested side
     */
    public void add(final SubHyperplane<S> sub, final boolean inside) {
        if (inside) {
            if (in == null) {
                in = sub;
            } else {
                in = in.reunite(sub);
            }
        } else {
            if (out == null) {
                out = sub;
            } else {
                out = out.reunite(sub);
            }
        }
    }

}

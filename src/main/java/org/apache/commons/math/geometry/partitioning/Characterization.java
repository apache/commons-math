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

/** Characterization of a sub-hyperplane.
 * @version $Revision$ $Date$
 */
class Characterization {

    /** Parts of the sub-hyperplane that have inside cells on the tested side. */
    private SubHyperplane in;

    /** Parts of the sub-hyperplane that have outside cells on the tested side. */
    private SubHyperplane out;

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
        return (in != null) && (!in.getRemainingRegion().isEmpty());
    }

    /** Get the parts of the sub-hyperplane that have inside cells on the tested side.
     * @return parts of the sub-hyperplane that have inside cells on the tested side
     */
    public SubHyperplane getIn() {
        return in;
    }

    /** Check if the sub-hyperplane that have outside cells on the tested side.
     * @return true if the sub-hyperplane that have outside cells on the tested side
     */
    public boolean hasOut() {
        return (out != null) && (!out.getRemainingRegion().isEmpty());
    }

    /** Get the parts of the sub-hyperplane that have outside cells on the tested side.
     * @return parts of the sub-hyperplane that have outside cells on the tested side
     */
    public SubHyperplane getOut() {
        return out;
    }

    /** Add a part of the sub-hyperplane known to have inside or outside cell on the tested side.
     * @param sub part of the sub-hyperplane to add
     * @param inside if true, the part added as an inside cell on the tested side, otherwise
     * it has an outside cell on the tested side
     */
    public void add(final SubHyperplane sub, final boolean inside) {
        if (inside) {
            if (in == null) {
                in = sub;
            } else {
                in = new SubHyperplane(in.getHyperplane(),
                                       Region.union(in.getRemainingRegion(),
                                                    sub.getRemainingRegion()));
            }
        } else {
            if (out == null) {
                out = sub;
            } else {
                out = new SubHyperplane(out.getHyperplane(),
                                        Region.union(out.getRemainingRegion(),
                                                     sub.getRemainingRegion()));
            }
        }
    }

}

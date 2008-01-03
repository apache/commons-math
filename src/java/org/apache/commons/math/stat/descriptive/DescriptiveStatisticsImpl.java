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
package org.apache.commons.math.stat.descriptive;

import java.io.Serializable;

/**
 * Default implementation of
 * {@link org.apache.commons.math.stat.descriptive.DescriptiveStatistics}.
 * 
 * @deprecated to be removed in commons-math 2.0.  
 * Use {@link DescriptiveStatistics}
 *
 * @version $Revision$ $Date$
 */
public class DescriptiveStatisticsImpl extends DescriptiveStatistics implements Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = -6467796944112488424L;

    /**
     * Construct a DescriptiveStatisticsImpl with infinite window
     */
    public DescriptiveStatisticsImpl() {
        super();
    }
    
    /**
     * Construct a DescriptiveStatisticsImpl with finite window
     * @param window the finite window size.
     */
    public DescriptiveStatisticsImpl(int window) {
        super(window);
    }
    
    /** 
     * Resets all statistics and storage
     */
    public void clear() {
        super.clear();
    }
}

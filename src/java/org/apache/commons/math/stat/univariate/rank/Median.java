/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.stat.univariate.rank;

import java.io.Serializable;


/**
 * Returns the <a href="http://www.xycoon.com/median_2.htm">median</a> of the
 * available values.
 * 
 * @version $Revision: 1.12 $ $Date: 2004/04/26 19:15:48 $
 */
public class Median extends Percentile implements Serializable {

    static final long serialVersionUID = -3961477041290915687L;    

    /**
     * Default constructor.
     */
    public Median() {
        super(50.0);
    }

}
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
package org.apache.commons.math.util;


/**
 * This class contains test cases for the ExpandableDoubleArray.
 * 
 * @version $Revision: 1.9 $ $Date: 2004/05/04 13:15:47 $
 */
public class ContractableDoubleArrayTest extends ExpandableDoubleArrayTest {

	public ContractableDoubleArrayTest(String name) {
		super( name );
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		da = new ContractableDoubleArray();
		ra = new ContractableDoubleArray();
	}

    protected ExpandableDoubleArray newInstance(int initialCapacity) {
        return new ContractableDoubleArray(initialCapacity);
    }

    protected ExpandableDoubleArray newInstance() {
        return new ContractableDoubleArray();
    }
    
    protected ExpandableDoubleArray newInstance(int initialCapacity, float expansionFactor) {
        if (expansionFactor < 2.5f) {
            return new ContractableDoubleArray(initialCapacity, expansionFactor);
        } else {
            return newInstance(initialCapacity, expansionFactor, expansionFactor + 1.0f);
        }
    }

    protected ExpandableDoubleArray newInstance(int initialCapacity, float expansionFactor, float contractionFactor) {
        return new ContractableDoubleArray(initialCapacity, expansionFactor, contractionFactor);
    }
}

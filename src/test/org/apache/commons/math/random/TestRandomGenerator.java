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
package org.apache.commons.math.random;
import java.util.Random;

/**
 * Dummy AbstractRandomGenerator concrete subclass that just wraps a 
 * java.util.Random instance.  Used by AbstractRandomGeneratorTest to test
 * default implementations in AbstractRandomGenerator.
 *
 * @version $Revision$ $Date$
 */
public class TestRandomGenerator extends AbstractRandomGenerator {

    private static final long serialVersionUID = -9161426374178114548L;
    private Random random = new Random();
    
    public void setSeed(long seed) {
       clear();
       random.setSeed(seed);
    }
    
    public double nextDouble() {
        return random.nextDouble();
    }

}

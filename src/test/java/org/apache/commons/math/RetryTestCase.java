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

package org.apache.commons.math;


/**
 * A Test case that retries tests when assertions fail.
 * @version $Revision$ $Date$
 */
public abstract class RetryTestCase {

    // TODO implement retry policy using Junit 4  API

    //    /**
//     *  Override runTest() to catch AssertionFailedError and retry
//     */
//    @Override
//    protected void runTest() throws Throwable {
//        try {
//            super.runTest();
//        } catch (AssertionFailedError err) {
//            // System.out.println("Retrying " + this.getName());
//            super.runTest();
//        }
//    }

}

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

package org.apache.commons.math4;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;


/**
 * A test runner that retries tests when assertions fail.
 */
public class RetryRunner extends BlockJUnit4ClassRunner {
    /**
     * Simple constructor.
     *
     * @param testClass Class to test.
     * @throws InitializationError if default runner cannot be built.
     */
    public RetryRunner(final Class<?> testClass)
        throws InitializationError {
        super(testClass);
    }

    @Override
    public Statement methodInvoker(final FrameworkMethod method,
                                   Object test) {
        final Statement singleTryStatement = super.methodInvoker(method, test);
        return new Statement() {
            /**
             * Evaluate the statement.
             * We attempt several runs for the test, at most MAX_ATTEMPTS.
             * if one attempt succeeds, we succeed, if all attempts fail, we
             * fail with the reason corresponding to the last attempt
             */
            @Override
            public void evaluate() throws Throwable {
                Throwable failureReason = null;

                final Retry retry = method.getAnnotation(Retry.class);
                if (retry == null) {
                    // Do a single test run attempt.
                    singleTryStatement.evaluate();
                } else {
                    final int numRetries = retry.value();

                    for (int i = 0; i < numRetries; ++i) {
                        try {
                            // Do a single test run attempt.
                            singleTryStatement.evaluate();
                            // Attempt succeeded, stop evaluation here.
                            return;
                        } catch (Throwable t) {
                            // Attempt failed, store the reason.
                            failureReason = t;
                        }
                    }

                    // All attempts failed.
                    throw failureReason;
                }
            }
        };
    }
}

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
package org.apache.commons.math4.core.jdkmath;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import org.junit.Test;
import org.junit.Assert;

/**
 * Tests for {@link JdkMath}.
 */
public class JdkMathTest {
    /** Separator. */
    private static final String LINE_SEP = System.lineSeparator();

    @Test
    public void checkMissingMethods() {
        final List<String> notFound = compareClassMethods(StrictMath.class,
                                                          JdkMath.class);
        if (!notFound.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("JdkMath is missing the following StrictMath methods:");
            for (String m : notFound) {
                sb.append(LINE_SEP).append(m);
            }
            Assert.fail(sb.toString());
        }
    }

    /**
     * @param class1 Reference implementation.
     * @param class2 Alternate implementation.
     * @return the methods defined in {@code class1} that are not in {@code class2}.
     */
    private List<String> compareClassMethods(Class<?> class1,
                                             Class<?> class2) {
        final List<String> notFound = new ArrayList<>();
        for (Method method1 : class1.getDeclaredMethods()) {
            if (Modifier.isPublic(method1.getModifiers())) {
                final Type[] params = method1.getGenericParameterTypes();
                try {
                    class2.getDeclaredMethod(method1.getName(), (Class[]) params);
                } catch (NoSuchMethodException e) {
                    notFound.add(method1.toString());
                }
            }
        }

        return notFound;
    }
}

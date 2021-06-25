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
package org.apache.commons.math4.legacy.exception.util;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Utility class for transforming the list of arguments passed to
 * constructors of exceptions.
 */
public final class ArgUtils {
    /**
     * Class contains only static methods.
     */
    private ArgUtils() {}

    /**
     * Transform a multidimensional array into a one-dimensional list.
     *
     * @param array Array (possibly multidimensional).
     * @return a list of all the {@code Object} instances contained in
     * {@code array}.
     */
    public static Object[] flatten(Object[] array) {
        final List<Object> list = new ArrayList<>();
        if (array != null) {
            for (Object o : array) {
                if (o instanceof Object[]) {
                    list.addAll(Arrays.asList(flatten((Object[]) o)));
                } else {
                    list.add(o);
                }
            }
        }
        return list.toArray();
    }
}

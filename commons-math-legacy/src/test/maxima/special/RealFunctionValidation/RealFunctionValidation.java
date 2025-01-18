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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.math4.legacy.stat.descriptive.SummaryStatistics;
import org.apache.commons.math4.core.jdkmath.JdkMath;

/*
 * plot 'logGamma.dat' binary format="%double%double" endian=big u 1:2 w l
 */
public class RealFunctionValidation {

    public static class MissingRequiredPropertyException
        extends IllegalArgumentException {

        private static final long serialVersionUID = 20121017L;

        public MissingRequiredPropertyException(final String key) {

            super("missing required property " + key);
        }
    }

    public static class ApplicationProperties {

        private static final int DOT = '.';

        private static final String METHOD_KEY = "method";

        private static final String SIGNATURE_KEY = "signature";

        private static final String INPUT_FILE_MASK = "inputFileMask";

        private static final String OUTPUT_FILE_MASK = "outputFileMask";

        private static final String FROM_KEY = "from";

        private static final String TO_KEY = "to";

        private static final String BY_KEY = "by";

        final Method method;

        final String inputFileMask;

        final String outputFileMask;

        final int from;

        final int to;

        final int by;

        /**
         * Returns a {@link Method} with specified signature.
         *
         * @param className The fully qualified name of the class to which the
         * method belongs.
         * @param methodName The name of the method.
         * @param signature The signature of the method, as a list of parameter
         * types.
         * @return the method
         * @throws SecurityException
         * @throws ClassNotFoundException
         */
        public static Method findStaticMethod(final String className,
                                              final String methodName,
                                              final List<Class<?>> signature)
            throws SecurityException, ClassNotFoundException {

            final int n = signature.size();
            final Method[] methods = Class.forName(className).getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    final Class<?>[] parameters = method.getParameterTypes();
                    boolean sameSignature = true;
                    if (parameters.length == n) {
                        for (int i = 0; i < n; i++) {
                            sameSignature &= signature.get(i)
                                .equals(parameters[i]);
                        }
                        if (sameSignature) {
                            final int modifiers = method.getModifiers();
                            if ((modifiers & Modifier.STATIC) != 0) {
                                return method;
                            } else {
                                final String msg = "method must be static";
                                throw new IllegalArgumentException(msg);
                            }
                        }
                    }
                }
            }
            throw new IllegalArgumentException("method not found");
        }

        public static Class<?> parsePrimitiveType(final String type) {

            if (type.equals("boolean")) {
                return Boolean.TYPE;
            } else if (type.equals("byte")) {
                return Byte.TYPE;
            } else if (type.equals("char")) {
                return Character.TYPE;
            } else if (type.equals("double")) {
                return Double.TYPE;
            } else if (type.equals("float")) {
                return Float.TYPE;
            } else if (type.equals("int")) {
                return Integer.TYPE;
            } else if (type.equals("long")) {
                return Long.TYPE;
            } else if (type.equals("short")) {
                return Short.TYPE;
            } else {
                final StringBuilder builder = new StringBuilder();
                builder.append(type).append(" is not a primitive type");
                throw new IllegalArgumentException(builder.toString());
            }
        }

        private static String getPropertyAsString(final Properties properties,
                                                  final String key) {

            final String value = properties.getProperty(key);
            if (value == null) {
                throw new MissingRequiredPropertyException(key);
            } else {
                return value;
            }
        }

        private static int getPropertyAsInteger(final Properties properties,
                                                final String key) {

            final String value = properties.getProperty(key);
            if (value == null) {
                throw new MissingRequiredPropertyException(key);
            } else {
                return Integer.parseInt(value);
            }
        }

        private ApplicationProperties(final String fullyQualifiedName,
                                      final String signature,
                                      final String inputFileMask,
                                      final String outputFileMask,
                                      final int from, final int to, final int by) {

            this.inputFileMask = inputFileMask;
            this.outputFileMask = outputFileMask;
            this.from = from;
            this.to = to;
            this.by = by;

            final String[] types = signature.split(",");
            final List<Class<?>> parameterTypes = new ArrayList<Class<?>>();
            for (String type : types) {
                parameterTypes.add(parsePrimitiveType(type.trim()));
            }
            final int index = fullyQualifiedName.lastIndexOf(DOT);
            try {
                final String className, methodName;
                className = fullyQualifiedName.substring(0, index);
                methodName = fullyQualifiedName.substring(index + 1);
                this.method = findStaticMethod(className, methodName,
                                               parameterTypes);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public static final ApplicationProperties create(final Properties properties) {

            final String methodFullyQualifiedName;
            methodFullyQualifiedName = getPropertyAsString(properties,
                                                           METHOD_KEY);

            final String signature;
            signature = getPropertyAsString(properties, SIGNATURE_KEY);

            final String inputFileMask;
            inputFileMask = getPropertyAsString(properties, INPUT_FILE_MASK);

            final String outputFileMask;
            outputFileMask = getPropertyAsString(properties, OUTPUT_FILE_MASK);

            final int from = getPropertyAsInteger(properties, FROM_KEY);
            final int to = getPropertyAsInteger(properties, TO_KEY);
            final int by = getPropertyAsInteger(properties, BY_KEY);

            return new ApplicationProperties(methodFullyQualifiedName,
                                             signature, inputFileMask,
                                             outputFileMask, from, to, by);
        }
    };

    public static Object readAndWritePrimitiveValue(final DataInputStream in,
                                                    final DataOutputStream out,
                                                    final Class<?> type)
        throws IOException {

        if (!type.isPrimitive()) {
            throw new IllegalArgumentException("type must be primitive");
        }
        if (type.equals(Boolean.TYPE)) {
            final boolean x = in.readBoolean();
            out.writeBoolean(x);
            return Boolean.valueOf(x);
        } else if (type.equals(Byte.TYPE)) {
            final byte x = in.readByte();
            out.writeByte(x);
            return Byte.valueOf(x);
        } else if (type.equals(Character.TYPE)) {
            final char x = in.readChar();
            out.writeChar(x);
            return Character.valueOf(x);
        } else if (type.equals(Double.TYPE)) {
            final double x = in.readDouble();
            out.writeDouble(x);
            return Double.valueOf(x);
        } else if (type.equals(Float.TYPE)) {
            final float x = in.readFloat();
            out.writeFloat(x);
            return Float.valueOf(x);
        } else if (type.equals(Integer.TYPE)) {
            final int x = in.readInt();
            out.writeInt(x);
            return Integer.valueOf(x);
        } else if (type.equals(Long.TYPE)) {
            final long x = in.readLong();
            out.writeLong(x);
            return Long.valueOf(x);
        } else if (type.equals(Short.TYPE)) {
            final short x = in.readShort();
            out.writeShort(x);
            return Short.valueOf(x);
        } else {
            // This should never occur.
            throw new IllegalStateException();
        }
    }

    public static SummaryStatistics assessAccuracy(final Method method,
                                                   final DataInputStream in,
                                                   final DataOutputStream out)
        throws IOException, IllegalAccessException, IllegalArgumentException,
        InvocationTargetException {

        if (method.getReturnType() != Double.TYPE) {
            throw new IllegalArgumentException("method must return a double");
        }

        final Class<?>[] types = method.getParameterTypes();
        for (int i = 0; i < types.length; i++) {
            if (!types[i].isPrimitive()) {
                final StringBuilder builder = new StringBuilder();
                builder.append("argument #").append(i + 1)
                    .append(" of method ").append(method.getName())
                    .append("must be of primitive of type");
                throw new IllegalArgumentException(builder.toString());
            }
        }

        final SummaryStatistics stat = new SummaryStatistics();
        final Object[] parameters = new Object[types.length];
        while (true) {
            try {
                for (int i = 0; i < parameters.length; i++) {
                    parameters[i] = readAndWritePrimitiveValue(in, out,
                                                               types[i]);
                }
                final double expected = in.readDouble();
                if (JdkMath.abs(expected) > 1E-16) {
                    final Object value = method.invoke(null, parameters);
                    final double actual = ((Double) value).doubleValue();
                    final double err = JdkMath.abs(actual - expected);
                    final double ulps = err / JdkMath.ulp(expected);
                    out.writeDouble(expected);
                    out.writeDouble(actual);
                    out.writeDouble(ulps);
                    stat.addValue(ulps);
                }
            } catch (EOFException e) {
                break;
            }
        }
        return stat;
    }

    public static void run(final ApplicationProperties properties)
        throws IllegalAccessException, IllegalArgumentException,
        InvocationTargetException, IOException {

        for (int i = properties.from; i < properties.to; i += properties.by) {
            final String inputFileName;
            inputFileName = String.format(properties.inputFileMask, i);
            final String outputFileName;
            outputFileName = String.format(properties.outputFileMask, i);

            final DataInputStream in;
            in = new DataInputStream(new FileInputStream(inputFileName));
            final DataOutputStream out;
            out = new DataOutputStream(new FileOutputStream(outputFileName));

            final SummaryStatistics stats;
            stats = assessAccuracy(properties.method, in, out);

            System.out.println("input file name = " + inputFileName);
            System.out.println("output file name = " + outputFileName);
            System.out.println(stats);
        }
    }

    public static void main(final String[] args)
        throws IOException, IllegalAccessException, IllegalArgumentException,
        InvocationTargetException {

        if (args.length == 0) {
            final String msg = "missing required properties file";
            throw new IllegalArgumentException(msg);
        }

        final FileInputStream in = new FileInputStream(args[0]);
        final Properties properties = new Properties();
        properties.load(in);
        in.close();

        final ApplicationProperties p;
        p = ApplicationProperties.create(properties);

        run(p);
    }
}

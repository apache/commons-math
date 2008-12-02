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
package org.apache.commons.math.function;

/**

 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DefaultContext implements EvaluationContext {

    /**
     * 
     */
    public DefaultContext() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see org.apache.archimedes.EvaluationContext#doubleValue(org.apache.archimedes.Evaluation)
     */
    public double doubleValue(Evaluation argument) throws EvaluationException {
        // TODO Auto-generated method stub
        return ((DefaultValue)argument.evaluate(this)).doubleValue();
    }

    /* (non-Javadoc)
     * @see org.apache.archimedes.EvaluationContext#floatValue(org.apache.archimedes.Evaluation)
     */
    public float floatValue(Evaluation argument) throws EvaluationException {
        // TODO Auto-generated method stub
        return ((DefaultValue)argument.evaluate(this)).floatValue();
    }

    /* (non-Javadoc)
     * @see org.apache.archimedes.EvaluationContext#intValue(org.apache.archimedes.Evaluation)
     */
    public int intValue(Evaluation argument) throws EvaluationException {
        // TODO Auto-generated method stub
        return ((DefaultValue)argument.evaluate(this)).intValue();
    }

    /* (non-Javadoc)
     * @see org.apache.archimedes.EvaluationContext#longValue(org.apache.archimedes.Evaluation)
     */
    public long longValue(Evaluation argument) throws EvaluationException {
        // TODO Auto-generated method stub
        return ((DefaultValue)argument.evaluate(this)).longValue();
    }

    /* (non-Javadoc)
     * @see org.apache.archimedes.EvaluationContext#shortValue(org.apache.archimedes.Evaluation)
     */
    public short shortValue(Evaluation argument) throws EvaluationException {
        // TODO Auto-generated method stub
        return ((DefaultValue)argument.evaluate(this)).shortValue();
    }

    /* (non-Javadoc)
     * @see org.apache.archimedes.EvaluationContext#byteValue(org.apache.archimedes.Evaluation)
     */
    public byte byteValue(Evaluation argument) throws EvaluationException {
        // TODO Auto-generated method stub
        return ((DefaultValue)argument.evaluate(this)).byteValue();
    }

    /* (non-Javadoc)
     * @see org.apache.archimedes.EvaluationContext#evaluate(double)
     */
    public Evaluation evaluate(double d) {
        // TODO Auto-generated method stub
        return new DefaultValue(d);
    }

    /* (non-Javadoc)
     * @see org.apache.archimedes.EvaluationContext#evaluate(float)
     */
    public Evaluation evaluate(float f) {
        // TODO Auto-generated method stub
        return new DefaultValue(f);
    }

    /* (non-Javadoc)
     * @see org.apache.archimedes.EvaluationContext#evaluate(int)
     */
    public Evaluation evaluate(int i) {
        // TODO Auto-generated method stub
        return new DefaultValue(i);
    }

    /* (non-Javadoc)
     * @see org.apache.archimedes.EvaluationContext#evaluate(long)
     */
    public Evaluation evaluate(long l) {
        // TODO Auto-generated method stub
        return new DefaultValue(l);
    }

    /* (non-Javadoc)
     * @see org.apache.archimedes.EvaluationContext#evaluate(short)
     */
    public Evaluation evaluate(short s) {
        // TODO Auto-generated method stub
        return new DefaultValue(s);
    }

    /* (non-Javadoc)
     * @see org.apache.archimedes.EvaluationContext#evaluate(byte)
     */
    public Evaluation evaluate(byte b) {
        // TODO Auto-generated method stub
        return new DefaultValue(b);
    }

    public class DefaultValue extends Number implements Evaluation {

        Number value;

        /**
         * 
         */
        DefaultValue() {
            super();
            // TODO Auto-generated constructor stub
        }

        DefaultValue(Number n) {
            value = n;
        }
                
        DefaultValue(double d) {
            value = Double.valueOf(d);
        }

        DefaultValue(float f) {
            value = Float.valueOf(f);
        }

        DefaultValue(int i) {
            value = Integer.valueOf(i);
        }

        DefaultValue(long l) {
            value = Long.valueOf(l);
        }
        
        /* (non-Javadoc)
         * @see org.apache.archimedes.Evaluation#evaluate(org.apache.archimedes.EvaluationContext)
         */
        public Evaluation evaluate(EvaluationContext context)
            throws EvaluationException {
            return this;
        }

        /* (non-Javadoc)
         * @see java.lang.Number#intValue()
         */
        public int intValue() {
            // TODO Auto-generated method stub
            return value.intValue();
        }

        /* (non-Javadoc)
         * @see java.lang.Number#longValue()
         */
        public long longValue() {
            // TODO Auto-generated method stub
            return value.longValue();
        }

        /* (non-Javadoc)
         * @see java.lang.Number#floatValue()
         */
        public float floatValue() {
            // TODO Auto-generated method stub
            return value.floatValue();
        }

        /* (non-Javadoc)
         * @see java.lang.Number#doubleValue()
         */
        public double doubleValue() {
            // TODO Auto-generated method stub
            return value.doubleValue();
        }

        /* (non-Javadoc)
         * @see java.lang.Number#byteValue()
         */
        public byte byteValue() {
            // TODO Auto-generated method stub
            return value.byteValue();
        }

        /* (non-Javadoc)
         * @see java.lang.Number#shortValue()
         */
        public short shortValue() {
            // TODO Auto-generated method stub
            return value.shortValue();
        }

    }
}

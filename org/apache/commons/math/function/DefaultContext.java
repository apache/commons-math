/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their name without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.math.function;

/**
 * @author Administrator
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
            value = new Double(d);
        }

        DefaultValue(float f) {
            value = new Float(f);
        }

        DefaultValue(int i) {
            value = new Integer(i);
        }

        DefaultValue(long l) {
            value = new Long(l);
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

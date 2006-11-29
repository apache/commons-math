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
package org.apache.commons.math.function.simple;

import java.io.Serializable;

import org.apache.commons.math.function.Evaluation;
import org.apache.commons.math.function.EvaluationContext;
import org.apache.commons.math.function.EvaluationException;


/**


 */
public class Power implements Evaluation, Serializable {

	private Evaluation argument;

	private Evaluation power;
	
	public void setOperand(Evaluation argument) {
		this.argument = argument;
	}

	public void setPower(Evaluation power) {
		this.power = power;
	}
		
	public Evaluation evaluate(EvaluationContext context) throws EvaluationException {
        return context.evaluate(
            Math.pow(
                context.doubleValue(argument),
                context.doubleValue(power)
            )
        );
	}
    
    public String toString() {
        return "Power";
    }
}

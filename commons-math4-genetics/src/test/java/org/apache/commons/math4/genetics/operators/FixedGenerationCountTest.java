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
package org.apache.commons.math4.genetics.operators;

import java.util.Iterator;

import org.apache.commons.math4.genetics.model.Chromosome;
import org.apache.commons.math4.genetics.model.Population;
import org.apache.commons.math4.genetics.operators.FixedGenerationCount;
import org.junit.Assert;
import org.junit.Test;

public class FixedGenerationCountTest {

	@Test
	public void testIsSatisfied() {
		FixedGenerationCount fgc = new FixedGenerationCount(20);

		int cnt = 0;
		Population pop = new Population() {

			@Override
			public Iterator<Chromosome> iterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Population nextGeneration(double elitismRate) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getPopulationSize() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getPopulationLimit() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Chromosome getFittestChromosome() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void addChromosome(Chromosome chromosome) {
				// TODO Auto-generated method stub

			}
		};

		while (!fgc.isSatisfied(pop)) {
			cnt++;
		}
		Assert.assertEquals(20, cnt);
	}

}

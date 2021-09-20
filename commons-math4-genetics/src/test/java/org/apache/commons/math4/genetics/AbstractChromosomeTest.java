package org.apache.commons.math4.genetics;

import org.junit.Assert;
import org.junit.Test;

public class AbstractChromosomeTest {

    @Test
    public void testGetFitness() {
        Chromosome<String> c1 = new AbstractChromosome<String>(chromosome -> {
            return 1;
        }, chromosome -> {
            return "1";
        }) {
        };
        Assert.assertEquals(1, c1.evaluate(), .001);
    }

    @Test
    public void testDecode() {
        Chromosome<String> c1 = new AbstractChromosome<String>(chromosome -> {
            return 1;
        }, chromosome -> {
            return "1";
        }) {
        };
        Assert.assertEquals("1", c1.decode());
    }

    @Test
    public void testCompareTo() {
        Chromosome<String> c1 = new AbstractChromosome<String>(chromosome -> {
            return 0;
        }, chromosome -> {
            return "0";
        }) {
        };
        Chromosome<String> c2 = new AbstractChromosome<String>(chromosome -> {
            return 10;
        }, chromosome -> {
            return "10";
        }) {
        };
        Chromosome<String> c3 = new AbstractChromosome<String>(chromosome -> {
            return 10;
        }, chromosome -> {
            return "10";
        }) {
        };

        Assert.assertTrue(c1.compareTo(c2) < 0);
        Assert.assertTrue(c2.compareTo(c1) > 0);
        Assert.assertEquals(0, c3.compareTo(c2));
        Assert.assertEquals(0, c2.compareTo(c3));
    }

    @Test
    public void testIsSame() {
        AbstractChromosome<String> c1 = new AbstractChromosome<String>(chromosome -> {
            return 1;
        }, chromosome -> {
            return "1";
        }) {
        };
        AbstractChromosome<String> c2 = new AbstractChromosome<String>(chromosome -> {
            return 2;
        }, chromosome -> {
            return "2";
        }) {
        };
        AbstractChromosome<String> c3 = new AbstractChromosome<String>(chromosome -> {
            return 3;
        }, chromosome -> {
            return "1";
        }) {
        };
        Assert.assertTrue(c1.isSame(c3));
        Assert.assertFalse(c1.isSame(c2));
    }

}

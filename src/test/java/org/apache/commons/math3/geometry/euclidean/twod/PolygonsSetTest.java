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
package org.apache.commons.math3.geometry.euclidean.twod;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.RegionFactory;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

public class PolygonsSetTest {

    @Test
    public void testSimplyConnected() {
        Vector2D[][] vertices = new Vector2D[][] {
            new Vector2D[] {
                new Vector2D(36.0, 22.0),
                new Vector2D(39.0, 32.0),
                new Vector2D(19.0, 32.0),
                new Vector2D( 6.0, 16.0),
                new Vector2D(31.0, 10.0),
                new Vector2D(42.0, 16.0),
                new Vector2D(34.0, 20.0),
                new Vector2D(29.0, 19.0),
                new Vector2D(23.0, 22.0),
                new Vector2D(33.0, 25.0)
            }
        };
        PolygonsSet set = buildSet(vertices);
        Assert.assertEquals(Region.Location.OUTSIDE, set.checkPoint(new Vector2D(50.0, 30.0)));
        checkPoints(Region.Location.INSIDE, set, new Vector2D[] {
            new Vector2D(30.0, 15.0),
            new Vector2D(15.0, 20.0),
            new Vector2D(24.0, 25.0),
            new Vector2D(35.0, 30.0),
            new Vector2D(19.0, 17.0)
        });
        checkPoints(Region.Location.OUTSIDE, set, new Vector2D[] {
            new Vector2D(50.0, 30.0),
            new Vector2D(30.0, 35.0),
            new Vector2D(10.0, 25.0),
            new Vector2D(10.0, 10.0),
            new Vector2D(40.0, 10.0),
            new Vector2D(50.0, 15.0),
            new Vector2D(30.0, 22.0)
        });
        checkPoints(Region.Location.BOUNDARY, set, new Vector2D[] {
            new Vector2D(30.0, 32.0),
            new Vector2D(34.0, 20.0)
        });
        checkVertices(set.getVertices(), vertices);
    }

    @Test
    public void testStair() {
        Vector2D[][] vertices = new Vector2D[][] {
            new Vector2D[] {
                new Vector2D( 0.0, 0.0),
                new Vector2D( 0.0, 2.0),
                new Vector2D(-0.1, 2.0),
                new Vector2D(-0.1, 1.0),
                new Vector2D(-0.3, 1.0),
                new Vector2D(-0.3, 1.5),
                new Vector2D(-1.3, 1.5),
                new Vector2D(-1.3, 2.0),
                new Vector2D(-1.8, 2.0),
                new Vector2D(-1.8 - 1.0 / FastMath.sqrt(2.0),
                            2.0 - 1.0 / FastMath.sqrt(2.0))
            }
        };

        PolygonsSet set = buildSet(vertices);
        checkVertices(set.getVertices(), vertices);

        Assert.assertEquals(1.1 + 0.95 * FastMath.sqrt(2.0), set.getSize(), 1.0e-10);

    }

    @Test
    public void testHole() {
        Vector2D[][] vertices = new Vector2D[][] {
            new Vector2D[] {
                new Vector2D(0.0, 0.0),
                new Vector2D(3.0, 0.0),
                new Vector2D(3.0, 3.0),
                new Vector2D(0.0, 3.0)
            }, new Vector2D[] {
                new Vector2D(1.0, 2.0),
                new Vector2D(2.0, 2.0),
                new Vector2D(2.0, 1.0),
                new Vector2D(1.0, 1.0)
            }
        };
        PolygonsSet set = buildSet(vertices);
        checkPoints(Region.Location.INSIDE, set, new Vector2D[] {
            new Vector2D(0.5, 0.5),
            new Vector2D(1.5, 0.5),
            new Vector2D(2.5, 0.5),
            new Vector2D(0.5, 1.5),
            new Vector2D(2.5, 1.5),
            new Vector2D(0.5, 2.5),
            new Vector2D(1.5, 2.5),
            new Vector2D(2.5, 2.5),
            new Vector2D(0.5, 1.0)
        });
        checkPoints(Region.Location.OUTSIDE, set, new Vector2D[] {
            new Vector2D(1.5, 1.5),
            new Vector2D(3.5, 1.0),
            new Vector2D(4.0, 1.5),
            new Vector2D(6.0, 6.0)
        });
        checkPoints(Region.Location.BOUNDARY, set, new Vector2D[] {
            new Vector2D(1.0, 1.0),
            new Vector2D(1.5, 0.0),
            new Vector2D(1.5, 1.0),
            new Vector2D(1.5, 2.0),
            new Vector2D(1.5, 3.0),
            new Vector2D(3.0, 3.0)
        });
        checkVertices(set.getVertices(), vertices);
    }

    @Test
    public void testDisjointPolygons() {
        Vector2D[][] vertices = new Vector2D[][] {
            new Vector2D[] {
                new Vector2D(0.0, 1.0),
                new Vector2D(2.0, 1.0),
                new Vector2D(1.0, 2.0)
            }, new Vector2D[] {
                new Vector2D(4.0, 0.0),
                new Vector2D(5.0, 1.0),
                new Vector2D(3.0, 1.0)
            }
        };
        PolygonsSet set = buildSet(vertices);
        Assert.assertEquals(Region.Location.INSIDE, set.checkPoint(new Vector2D(1.0, 1.5)));
        checkPoints(Region.Location.INSIDE, set, new Vector2D[] {
            new Vector2D(1.0, 1.5),
            new Vector2D(4.5, 0.8)
        });
        checkPoints(Region.Location.OUTSIDE, set, new Vector2D[] {
            new Vector2D(1.0, 0.0),
            new Vector2D(3.5, 1.2),
            new Vector2D(2.5, 1.0),
            new Vector2D(3.0, 4.0)
        });
        checkPoints(Region.Location.BOUNDARY, set, new Vector2D[] {
            new Vector2D(1.0, 1.0),
            new Vector2D(3.5, 0.5),
            new Vector2D(0.0, 1.0)
        });
        checkVertices(set.getVertices(), vertices);
    }

    @Test
    public void testOppositeHyperplanes() {
        Vector2D[][] vertices = new Vector2D[][] {
            new Vector2D[] {
                new Vector2D(1.0, 0.0),
                new Vector2D(2.0, 1.0),
                new Vector2D(3.0, 1.0),
                new Vector2D(2.0, 2.0),
                new Vector2D(1.0, 1.0),
                new Vector2D(0.0, 1.0)
            }
        };
        PolygonsSet set = buildSet(vertices);
        checkVertices(set.getVertices(), vertices);
    }

    @Test
    public void testSingularPoint() {
        Vector2D[][] vertices = new Vector2D[][] {
            new Vector2D[] {
                new Vector2D( 0.0,  0.0),
                new Vector2D( 1.0,  0.0),
                new Vector2D( 1.0,  1.0),
                new Vector2D( 0.0,  1.0),
                new Vector2D( 0.0,  0.0),
                new Vector2D(-1.0,  0.0),
                new Vector2D(-1.0, -1.0),
                new Vector2D( 0.0, -1.0)
            }
        };
        PolygonsSet set = buildSet(vertices);
        checkVertices(set.getVertices(), vertices);
    }

    @Test
    public void testLineIntersection() {
        Vector2D[][] vertices = new Vector2D[][] {
            new Vector2D[] {
                new Vector2D( 0.0,  0.0),
                new Vector2D( 2.0,  0.0),
                new Vector2D( 2.0,  1.0),
                new Vector2D( 3.0,  1.0),
                new Vector2D( 3.0,  3.0),
                new Vector2D( 1.0,  3.0),
                new Vector2D( 1.0,  2.0),
                new Vector2D( 0.0,  2.0)
            }
        };
        PolygonsSet set = buildSet(vertices);

        Line l1 = new Line(new Vector2D(-1.5, 0.0), FastMath.PI / 4);
        SubLine s1 = (SubLine) set.intersection(l1.wholeHyperplane());
        List<Interval> i1 = ((IntervalsSet) s1.getRemainingRegion()).asList();
        Assert.assertEquals(2, i1.size());
        Interval v10 = i1.get(0);
        Vector2D p10Lower = l1.toSpace(new Vector1D(v10.getLower()));
        Assert.assertEquals(0.0, p10Lower.getX(), 1.0e-10);
        Assert.assertEquals(1.5, p10Lower.getY(), 1.0e-10);
        Vector2D p10Upper = l1.toSpace(new Vector1D(v10.getUpper()));
        Assert.assertEquals(0.5, p10Upper.getX(), 1.0e-10);
        Assert.assertEquals(2.0, p10Upper.getY(), 1.0e-10);
        Interval v11 = i1.get(1);
        Vector2D p11Lower = l1.toSpace(new Vector1D(v11.getLower()));
        Assert.assertEquals(1.0, p11Lower.getX(), 1.0e-10);
        Assert.assertEquals(2.5, p11Lower.getY(), 1.0e-10);
        Vector2D p11Upper = l1.toSpace(new Vector1D(v11.getUpper()));
        Assert.assertEquals(1.5, p11Upper.getX(), 1.0e-10);
        Assert.assertEquals(3.0, p11Upper.getY(), 1.0e-10);

        Line l2 = new Line(new Vector2D(-1.0, 2.0), 0);
        SubLine s2 = (SubLine) set.intersection(l2.wholeHyperplane());
        List<Interval> i2 = ((IntervalsSet) s2.getRemainingRegion()).asList();
        Assert.assertEquals(1, i2.size());
        Interval v20 = i2.get(0);
        Vector2D p20Lower = l2.toSpace(new Vector1D(v20.getLower()));
        Assert.assertEquals(1.0, p20Lower.getX(), 1.0e-10);
        Assert.assertEquals(2.0, p20Lower.getY(), 1.0e-10);
        Vector2D p20Upper = l2.toSpace(new Vector1D(v20.getUpper()));
        Assert.assertEquals(3.0, p20Upper.getX(), 1.0e-10);
        Assert.assertEquals(2.0, p20Upper.getY(), 1.0e-10);

    }

    @Test
    public void testUnlimitedSubHyperplane() {
        Vector2D[][] vertices1 = new Vector2D[][] {
            new Vector2D[] {
                new Vector2D(0.0, 0.0),
                new Vector2D(4.0, 0.0),
                new Vector2D(1.4, 1.5),
                new Vector2D(0.0, 3.5)
            }
        };
        PolygonsSet set1 = buildSet(vertices1);
        Vector2D[][] vertices2 = new Vector2D[][] {
            new Vector2D[] {
                new Vector2D(1.4,  0.2),
                new Vector2D(2.8, -1.2),
                new Vector2D(2.5,  0.6)
            }
        };
        PolygonsSet set2 = buildSet(vertices2);

        PolygonsSet set =
            (PolygonsSet) new RegionFactory<Euclidean2D>().union(set1.copySelf(),
                                                                 set2.copySelf());
        checkVertices(set1.getVertices(), vertices1);
        checkVertices(set2.getVertices(), vertices2);
        checkVertices(set.getVertices(), new Vector2D[][] {
            new Vector2D[] {
                new Vector2D(0.0,  0.0),
                new Vector2D(1.6,  0.0),
                new Vector2D(2.8, -1.2),
                new Vector2D(2.6,  0.0),
                new Vector2D(4.0,  0.0),
                new Vector2D(1.4,  1.5),
                new Vector2D(0.0,  3.5)
            }
        });

    }

    @Test
    public void testUnion() {
        Vector2D[][] vertices1 = new Vector2D[][] {
            new Vector2D[] {
                new Vector2D( 0.0,  0.0),
                new Vector2D( 2.0,  0.0),
                new Vector2D( 2.0,  2.0),
                new Vector2D( 0.0,  2.0)
            }
        };
        PolygonsSet set1 = buildSet(vertices1);
        Vector2D[][] vertices2 = new Vector2D[][] {
            new Vector2D[] {
                new Vector2D( 1.0,  1.0),
                new Vector2D( 3.0,  1.0),
                new Vector2D( 3.0,  3.0),
                new Vector2D( 1.0,  3.0)
            }
        };
        PolygonsSet set2 = buildSet(vertices2);
        PolygonsSet set  = (PolygonsSet) new RegionFactory<Euclidean2D>().union(set1.copySelf(),
                                                                                set2.copySelf());
        checkVertices(set1.getVertices(), vertices1);
        checkVertices(set2.getVertices(), vertices2);
        checkVertices(set.getVertices(), new Vector2D[][] {
            new Vector2D[] {
                new Vector2D( 0.0,  0.0),
                new Vector2D( 2.0,  0.0),
                new Vector2D( 2.0,  1.0),
                new Vector2D( 3.0,  1.0),
                new Vector2D( 3.0,  3.0),
                new Vector2D( 1.0,  3.0),
                new Vector2D( 1.0,  2.0),
                new Vector2D( 0.0,  2.0)
            }
        });
        checkPoints(Region.Location.INSIDE, set, new Vector2D[] {
            new Vector2D(1.0, 1.0),
            new Vector2D(0.5, 0.5),
            new Vector2D(2.0, 2.0),
            new Vector2D(2.5, 2.5),
            new Vector2D(0.5, 1.5),
            new Vector2D(1.5, 1.5),
            new Vector2D(1.5, 0.5),
            new Vector2D(1.5, 2.5),
            new Vector2D(2.5, 1.5),
            new Vector2D(2.5, 2.5)
        });
        checkPoints(Region.Location.OUTSIDE, set, new Vector2D[] {
            new Vector2D(-0.5, 0.5),
            new Vector2D( 0.5, 2.5),
            new Vector2D( 2.5, 0.5),
            new Vector2D( 3.5, 2.5)
        });
        checkPoints(Region.Location.BOUNDARY, set, new Vector2D[] {
            new Vector2D(0.0, 0.0),
            new Vector2D(0.5, 2.0),
            new Vector2D(2.0, 0.5),
            new Vector2D(2.5, 1.0),
            new Vector2D(3.0, 2.5)
        });

    }

    @Test
    public void testIntersection() {
        Vector2D[][] vertices1 = new Vector2D[][] {
            new Vector2D[] {
                new Vector2D( 0.0,  0.0),
                new Vector2D( 2.0,  0.0),
                new Vector2D( 2.0,  2.0),
                new Vector2D( 0.0,  2.0)
            }
        };
        PolygonsSet set1 = buildSet(vertices1);
        Vector2D[][] vertices2 = new Vector2D[][] {
            new Vector2D[] {
                new Vector2D( 1.0,  1.0),
                new Vector2D( 3.0,  1.0),
                new Vector2D( 3.0,  3.0),
                new Vector2D( 1.0,  3.0)
            }
        };
        PolygonsSet set2 = buildSet(vertices2);
        PolygonsSet set  = (PolygonsSet) new RegionFactory<Euclidean2D>().intersection(set1.copySelf(),
                                                                                       set2.copySelf());
        checkVertices(set1.getVertices(), vertices1);
        checkVertices(set2.getVertices(), vertices2);
        checkVertices(set.getVertices(), new Vector2D[][] {
            new Vector2D[] {
                new Vector2D( 1.0,  1.0),
                new Vector2D( 2.0,  1.0),
                new Vector2D( 2.0,  2.0),
                new Vector2D( 1.0,  2.0)
            }
        });
        checkPoints(Region.Location.INSIDE, set, new Vector2D[] {
            new Vector2D(1.5, 1.5)
        });
        checkPoints(Region.Location.OUTSIDE, set, new Vector2D[] {
            new Vector2D(0.5, 1.5),
            new Vector2D(2.5, 1.5),
            new Vector2D(1.5, 0.5),
            new Vector2D(0.5, 0.5)
        });
        checkPoints(Region.Location.BOUNDARY, set, new Vector2D[] {
            new Vector2D(1.0, 1.0),
            new Vector2D(2.0, 2.0),
            new Vector2D(1.0, 1.5),
            new Vector2D(1.5, 2.0)
        });
    }

    @Test
    public void testXor() {
        Vector2D[][] vertices1 = new Vector2D[][] {
            new Vector2D[] {
                new Vector2D( 0.0,  0.0),
                new Vector2D( 2.0,  0.0),
                new Vector2D( 2.0,  2.0),
                new Vector2D( 0.0,  2.0)
            }
        };
        PolygonsSet set1 = buildSet(vertices1);
        Vector2D[][] vertices2 = new Vector2D[][] {
            new Vector2D[] {
                new Vector2D( 1.0,  1.0),
                new Vector2D( 3.0,  1.0),
                new Vector2D( 3.0,  3.0),
                new Vector2D( 1.0,  3.0)
            }
        };
        PolygonsSet set2 = buildSet(vertices2);
        PolygonsSet set  = (PolygonsSet) new RegionFactory<Euclidean2D>().xor(set1.copySelf(),
                                                                              set2.copySelf());
        checkVertices(set1.getVertices(), vertices1);
        checkVertices(set2.getVertices(), vertices2);
        checkVertices(set.getVertices(), new Vector2D[][] {
            new Vector2D[] {
                new Vector2D( 0.0,  0.0),
                new Vector2D( 2.0,  0.0),
                new Vector2D( 2.0,  1.0),
                new Vector2D( 3.0,  1.0),
                new Vector2D( 3.0,  3.0),
                new Vector2D( 1.0,  3.0),
                new Vector2D( 1.0,  2.0),
                new Vector2D( 0.0,  2.0)
            },
            new Vector2D[] {
                new Vector2D( 1.0,  1.0),
                new Vector2D( 1.0,  2.0),
                new Vector2D( 2.0,  2.0),
                new Vector2D( 2.0,  1.0)
            }
        });
        checkPoints(Region.Location.INSIDE, set, new Vector2D[] {
            new Vector2D(0.5, 0.5),
            new Vector2D(2.5, 2.5),
            new Vector2D(0.5, 1.5),
            new Vector2D(1.5, 0.5),
            new Vector2D(1.5, 2.5),
            new Vector2D(2.5, 1.5),
            new Vector2D(2.5, 2.5)
        });
        checkPoints(Region.Location.OUTSIDE, set, new Vector2D[] {
            new Vector2D(-0.5, 0.5),
            new Vector2D( 0.5, 2.5),
            new Vector2D( 2.5, 0.5),
            new Vector2D( 1.5, 1.5),
            new Vector2D( 3.5, 2.5)
        });
        checkPoints(Region.Location.BOUNDARY, set, new Vector2D[] {
            new Vector2D(1.0, 1.0),
            new Vector2D(2.0, 2.0),
            new Vector2D(1.5, 1.0),
            new Vector2D(2.0, 1.5),
            new Vector2D(0.0, 0.0),
            new Vector2D(0.5, 2.0),
            new Vector2D(2.0, 0.5),
            new Vector2D(2.5, 1.0),
            new Vector2D(3.0, 2.5)
        });
    }

    @Test
    public void testDifference() {
        Vector2D[][] vertices1 = new Vector2D[][] {
            new Vector2D[] {
                new Vector2D( 0.0,  0.0),
                new Vector2D( 2.0,  0.0),
                new Vector2D( 2.0,  2.0),
                new Vector2D( 0.0,  2.0)
            }
        };
        PolygonsSet set1 = buildSet(vertices1);
        Vector2D[][] vertices2 = new Vector2D[][] {
            new Vector2D[] {
                new Vector2D( 1.0,  1.0),
                new Vector2D( 3.0,  1.0),
                new Vector2D( 3.0,  3.0),
                new Vector2D( 1.0,  3.0)
            }
        };
        PolygonsSet set2 = buildSet(vertices2);
        PolygonsSet set  = (PolygonsSet) new RegionFactory<Euclidean2D>().difference(set1.copySelf(),
                                                                                     set2.copySelf());
        checkVertices(set1.getVertices(), vertices1);
        checkVertices(set2.getVertices(), vertices2);
        checkVertices(set.getVertices(), new Vector2D[][] {
            new Vector2D[] {
                new Vector2D( 0.0,  0.0),
                new Vector2D( 2.0,  0.0),
                new Vector2D( 2.0,  1.0),
                new Vector2D( 1.0,  1.0),
                new Vector2D( 1.0,  2.0),
                new Vector2D( 0.0,  2.0)
            }
        });
        checkPoints(Region.Location.INSIDE, set, new Vector2D[] {
            new Vector2D(0.5, 0.5),
            new Vector2D(0.5, 1.5),
            new Vector2D(1.5, 0.5)
        });
        checkPoints(Region.Location.OUTSIDE, set, new Vector2D[] {
            new Vector2D( 2.5, 2.5),
            new Vector2D(-0.5, 0.5),
            new Vector2D( 0.5, 2.5),
            new Vector2D( 2.5, 0.5),
            new Vector2D( 1.5, 1.5),
            new Vector2D( 3.5, 2.5),
            new Vector2D( 1.5, 2.5),
            new Vector2D( 2.5, 1.5),
            new Vector2D( 2.0, 1.5),
            new Vector2D( 2.0, 2.0),
            new Vector2D( 2.5, 1.0),
            new Vector2D( 2.5, 2.5),
            new Vector2D( 3.0, 2.5)
        });
        checkPoints(Region.Location.BOUNDARY, set, new Vector2D[] {
            new Vector2D(1.0, 1.0),
            new Vector2D(1.5, 1.0),
            new Vector2D(0.0, 0.0),
            new Vector2D(0.5, 2.0),
            new Vector2D(2.0, 0.5)
        });
    }

    @Test
    public void testEmptyDifference() {
        Vector2D[][] vertices1 = new Vector2D[][] {
            new Vector2D[] {
                new Vector2D( 0.5, 3.5),
                new Vector2D( 0.5, 4.5),
                new Vector2D(-0.5, 4.5),
                new Vector2D(-0.5, 3.5)
            }
        };
        PolygonsSet set1 = buildSet(vertices1);
        Vector2D[][] vertices2 = new Vector2D[][] {
            new Vector2D[] {
                new Vector2D( 1.0, 2.0),
                new Vector2D( 1.0, 8.0),
                new Vector2D(-1.0, 8.0),
                new Vector2D(-1.0, 2.0)
            }
        };
        PolygonsSet set2 = buildSet(vertices2);
        Assert.assertTrue(new RegionFactory<Euclidean2D>().difference(set1.copySelf(), set2.copySelf()).isEmpty());
    }

    @Test
    public void testChoppedHexagon() {
        double pi6   = FastMath.PI / 6.0;
        double sqrt3 = FastMath.sqrt(3.0);
        SubLine[] hyp = {
            new Line(new Vector2D(   0.0, 1.0),  5 * pi6).wholeHyperplane(),
            new Line(new Vector2D(-sqrt3, 1.0),  7 * pi6).wholeHyperplane(),
            new Line(new Vector2D(-sqrt3, 1.0),  9 * pi6).wholeHyperplane(),
            new Line(new Vector2D(-sqrt3, 0.0), 11 * pi6).wholeHyperplane(),
            new Line(new Vector2D(   0.0, 0.0), 13 * pi6).wholeHyperplane(),
            new Line(new Vector2D(   0.0, 1.0),  3 * pi6).wholeHyperplane(),
            new Line(new Vector2D(-5.0 * sqrt3 / 6.0, 0.0), 9 * pi6).wholeHyperplane()
        };
        hyp[1] = (SubLine) hyp[1].split(hyp[0].getHyperplane()).getMinus();
        hyp[2] = (SubLine) hyp[2].split(hyp[1].getHyperplane()).getMinus();
        hyp[3] = (SubLine) hyp[3].split(hyp[2].getHyperplane()).getMinus();
        hyp[4] = (SubLine) hyp[4].split(hyp[3].getHyperplane()).getMinus().split(hyp[0].getHyperplane()).getMinus();
        hyp[5] = (SubLine) hyp[5].split(hyp[4].getHyperplane()).getMinus().split(hyp[0].getHyperplane()).getMinus();
        hyp[6] = (SubLine) hyp[6].split(hyp[3].getHyperplane()).getMinus().split(hyp[1].getHyperplane()).getMinus();
        BSPTree<Euclidean2D> tree = new BSPTree<Euclidean2D>(Boolean.TRUE);
        for (int i = hyp.length - 1; i >= 0; --i) {
            tree = new BSPTree<Euclidean2D>(hyp[i], new BSPTree<Euclidean2D>(Boolean.FALSE), tree, null);
        }
        PolygonsSet set = new PolygonsSet(tree);
        SubLine splitter =
            new Line(new Vector2D(-2.0 * sqrt3 / 3.0, 0.0), 9 * pi6).wholeHyperplane();
        PolygonsSet slice =
            new PolygonsSet(new BSPTree<Euclidean2D>(splitter,
                                                     set.getTree(false).split(splitter).getPlus(),
                                                     new BSPTree<Euclidean2D>(Boolean.FALSE), null));
        Assert.assertEquals(Region.Location.OUTSIDE,
                            slice.checkPoint(new Vector2D(0.1, 0.5)));
        Assert.assertEquals(11.0 / 3.0, slice.getBoundarySize(), 1.0e-10);

    }

    @Test
    public void testConcentric() {
        double h = FastMath.sqrt(3.0) / 2.0;
        Vector2D[][] vertices1 = new Vector2D[][] {
            new Vector2D[] {
                new Vector2D( 0.00, 0.1 * h),
                new Vector2D( 0.05, 0.1 * h),
                new Vector2D( 0.10, 0.2 * h),
                new Vector2D( 0.05, 0.3 * h),
                new Vector2D(-0.05, 0.3 * h),
                new Vector2D(-0.10, 0.2 * h),
                new Vector2D(-0.05, 0.1 * h)
            }
        };
        PolygonsSet set1 = buildSet(vertices1);
        Vector2D[][] vertices2 = new Vector2D[][] {
            new Vector2D[] {
                new Vector2D( 0.00, 0.0 * h),
                new Vector2D( 0.10, 0.0 * h),
                new Vector2D( 0.20, 0.2 * h),
                new Vector2D( 0.10, 0.4 * h),
                new Vector2D(-0.10, 0.4 * h),
                new Vector2D(-0.20, 0.2 * h),
                new Vector2D(-0.10, 0.0 * h)
            }
        };
        PolygonsSet set2 = buildSet(vertices2);
        Assert.assertTrue(set2.contains(set1));
    }

    @Test
    public void testBug20040520() {
        BSPTree<Euclidean2D> a0 =
            new BSPTree<Euclidean2D>(buildSegment(new Vector2D(0.85, -0.05),
                                                  new Vector2D(0.90, -0.10)),
                                                  new BSPTree<Euclidean2D>(Boolean.FALSE),
                                                  new BSPTree<Euclidean2D>(Boolean.TRUE),
                                                  null);
        BSPTree<Euclidean2D> a1 =
            new BSPTree<Euclidean2D>(buildSegment(new Vector2D(0.85, -0.10),
                                                  new Vector2D(0.90, -0.10)),
                                                  new BSPTree<Euclidean2D>(Boolean.FALSE), a0, null);
        BSPTree<Euclidean2D> a2 =
            new BSPTree<Euclidean2D>(buildSegment(new Vector2D(0.90, -0.05),
                                                  new Vector2D(0.85, -0.05)),
                                                  new BSPTree<Euclidean2D>(Boolean.FALSE), a1, null);
        BSPTree<Euclidean2D> a3 =
            new BSPTree<Euclidean2D>(buildSegment(new Vector2D(0.82, -0.05),
                                                  new Vector2D(0.82, -0.08)),
                                                  new BSPTree<Euclidean2D>(Boolean.FALSE),
                                                  new BSPTree<Euclidean2D>(Boolean.TRUE),
                                                  null);
        BSPTree<Euclidean2D> a4 =
            new BSPTree<Euclidean2D>(buildHalfLine(new Vector2D(0.85, -0.05),
                                                   new Vector2D(0.80, -0.05),
                                                   false),
                                                   new BSPTree<Euclidean2D>(Boolean.FALSE), a3, null);
        BSPTree<Euclidean2D> a5 =
            new BSPTree<Euclidean2D>(buildSegment(new Vector2D(0.82, -0.08),
                                                  new Vector2D(0.82, -0.18)),
                                                  new BSPTree<Euclidean2D>(Boolean.FALSE),
                                                  new BSPTree<Euclidean2D>(Boolean.TRUE),
                                                  null);
        BSPTree<Euclidean2D> a6 =
            new BSPTree<Euclidean2D>(buildHalfLine(new Vector2D(0.82, -0.18),
                                                   new Vector2D(0.85, -0.15),
                                                   true),
                                                   new BSPTree<Euclidean2D>(Boolean.FALSE), a5, null);
        BSPTree<Euclidean2D> a7 =
            new BSPTree<Euclidean2D>(buildHalfLine(new Vector2D(0.85, -0.05),
                                                   new Vector2D(0.82, -0.08),
                                                   false),
                                                   a4, a6, null);
        BSPTree<Euclidean2D> a8 =
            new BSPTree<Euclidean2D>(buildLine(new Vector2D(0.85, -0.25),
                                               new Vector2D(0.85,  0.05)),
                                               a2, a7, null);
        BSPTree<Euclidean2D> a9 =
            new BSPTree<Euclidean2D>(buildLine(new Vector2D(0.90,  0.05),
                                               new Vector2D(0.90, -0.50)),
                                               a8, new BSPTree<Euclidean2D>(Boolean.FALSE), null);

        BSPTree<Euclidean2D> b0 =
            new BSPTree<Euclidean2D>(buildSegment(new Vector2D(0.92, -0.12),
                                                  new Vector2D(0.92, -0.08)),
                                                  new BSPTree<Euclidean2D>(Boolean.FALSE), new BSPTree<Euclidean2D>(Boolean.TRUE),
                                                  null);
        BSPTree<Euclidean2D> b1 =
            new BSPTree<Euclidean2D>(buildHalfLine(new Vector2D(0.92, -0.08),
                                                   new Vector2D(0.90, -0.10),
                                                   true),
                                                   new BSPTree<Euclidean2D>(Boolean.FALSE), b0, null);
        BSPTree<Euclidean2D> b2 =
            new BSPTree<Euclidean2D>(buildSegment(new Vector2D(0.92, -0.18),
                                                  new Vector2D(0.92, -0.12)),
                                                  new BSPTree<Euclidean2D>(Boolean.FALSE), new BSPTree<Euclidean2D>(Boolean.TRUE),
                                                  null);
        BSPTree<Euclidean2D> b3 =
            new BSPTree<Euclidean2D>(buildSegment(new Vector2D(0.85, -0.15),
                                                  new Vector2D(0.90, -0.20)),
                                                  new BSPTree<Euclidean2D>(Boolean.FALSE), b2, null);
        BSPTree<Euclidean2D> b4 =
            new BSPTree<Euclidean2D>(buildSegment(new Vector2D(0.95, -0.15),
                                                  new Vector2D(0.85, -0.05)),
                                                  b1, b3, null);
        BSPTree<Euclidean2D> b5 =
            new BSPTree<Euclidean2D>(buildHalfLine(new Vector2D(0.85, -0.05),
                                                   new Vector2D(0.85, -0.25),
                                                   true),
                                                   new BSPTree<Euclidean2D>(Boolean.FALSE), b4, null);
        BSPTree<Euclidean2D> b6 =
            new BSPTree<Euclidean2D>(buildLine(new Vector2D(0.0, -1.10),
                                               new Vector2D(1.0, -0.10)),
                                               new BSPTree<Euclidean2D>(Boolean.FALSE), b5, null);

        PolygonsSet c =
            (PolygonsSet) new RegionFactory<Euclidean2D>().union(new PolygonsSet(a9),
                                                                 new PolygonsSet(b6));

        checkPoints(Region.Location.INSIDE, c, new Vector2D[] {
            new Vector2D(0.83, -0.06),
            new Vector2D(0.83, -0.15),
            new Vector2D(0.88, -0.15),
            new Vector2D(0.88, -0.09),
            new Vector2D(0.88, -0.07),
            new Vector2D(0.91, -0.18),
            new Vector2D(0.91, -0.10)
        });

        checkPoints(Region.Location.OUTSIDE, c, new Vector2D[] {
            new Vector2D(0.80, -0.10),
            new Vector2D(0.83, -0.50),
            new Vector2D(0.83, -0.20),
            new Vector2D(0.83, -0.02),
            new Vector2D(0.87, -0.50),
            new Vector2D(0.87, -0.20),
            new Vector2D(0.87, -0.02),
            new Vector2D(0.91, -0.20),
            new Vector2D(0.91, -0.08),
            new Vector2D(0.93, -0.15)
        });

        checkVertices(c.getVertices(),
                      new Vector2D[][] {
            new Vector2D[] {
                new Vector2D(0.85, -0.15),
                new Vector2D(0.90, -0.20),
                new Vector2D(0.92, -0.18),
                new Vector2D(0.92, -0.08),
                new Vector2D(0.90, -0.10),
                new Vector2D(0.90, -0.05),
                new Vector2D(0.82, -0.05),
                new Vector2D(0.82, -0.18),
            }
        });

    }

    @Test
    public void testBug20041003() {

        Line[] l = {
            new Line(new Vector2D(0.0, 0.625000007541172),
                     new Vector2D(1.0, 0.625000007541172)),
                     new Line(new Vector2D(-0.19204433621902645, 0.0),
                              new Vector2D(-0.19204433621902645, 1.0)),
                              new Line(new Vector2D(-0.40303524786887,  0.4248364535319128),
                                       new Vector2D(-1.12851149797877, -0.2634107480798909)),
                                       new Line(new Vector2D(0.0, 2.0),
                                                new Vector2D(1.0, 2.0))
        };

        BSPTree<Euclidean2D> node1 =
            new BSPTree<Euclidean2D>(new SubLine(l[0],
                                          new IntervalsSet(intersectionAbscissa(l[0], l[1]),
                                                           intersectionAbscissa(l[0], l[2]))),
                                                           new BSPTree<Euclidean2D>(Boolean.TRUE), new BSPTree<Euclidean2D>(Boolean.FALSE),
                                                           null);
        BSPTree<Euclidean2D> node2 =
            new BSPTree<Euclidean2D>(new SubLine(l[1],
                                          new IntervalsSet(intersectionAbscissa(l[1], l[2]),
                                                           intersectionAbscissa(l[1], l[3]))),
                                                           node1, new BSPTree<Euclidean2D>(Boolean.FALSE), null);
        BSPTree<Euclidean2D> node3 =
            new BSPTree<Euclidean2D>(new SubLine(l[2],
                                          new IntervalsSet(intersectionAbscissa(l[2], l[3]),
                                                           Double.POSITIVE_INFINITY)),
                                                           node2, new BSPTree<Euclidean2D>(Boolean.FALSE), null);
        BSPTree<Euclidean2D> node4 =
            new BSPTree<Euclidean2D>(l[3].wholeHyperplane(), node3, new BSPTree<Euclidean2D>(Boolean.FALSE), null);

        PolygonsSet set = new PolygonsSet(node4);
        Assert.assertEquals(0, set.getVertices().length);

    }

    private PolygonsSet buildSet(Vector2D[][] vertices) {
        ArrayList<SubHyperplane<Euclidean2D>> edges = new ArrayList<SubHyperplane<Euclidean2D>>();
        for (int i = 0; i < vertices.length; ++i) {
            int l = vertices[i].length;
            for (int j = 0; j < l; ++j) {
                edges.add(buildSegment(vertices[i][j], vertices[i][(j + 1) % l]));
            }
        }
        return new PolygonsSet(edges);
    }

    private SubHyperplane<Euclidean2D> buildLine(Vector2D start, Vector2D end) {
        return new Line(start, end).wholeHyperplane();
    }

    private double intersectionAbscissa(Line l0, Line l1) {
        Vector2D p = l0.intersection(l1);
        return (l0.toSubSpace(p)).getX();
    }

    private SubHyperplane<Euclidean2D> buildHalfLine(Vector2D start, Vector2D end,
                                                     boolean startIsVirtual) {
        Line   line  = new Line(start, end);
        double lower = startIsVirtual
        ? Double.NEGATIVE_INFINITY
        : (line.toSubSpace(start)).getX();
        double upper = startIsVirtual
        ? (line.toSubSpace(end)).getX()
        : Double.POSITIVE_INFINITY;
        return new SubLine(line, new IntervalsSet(lower, upper));
    }

    private SubHyperplane<Euclidean2D> buildSegment(Vector2D start, Vector2D end) {
        Line   line  = new Line(start, end);
        double lower = (line.toSubSpace(start)).getX();
        double upper = (line.toSubSpace(end)).getX();
        return new SubLine(line, new IntervalsSet(lower, upper));
    }

    private void checkPoints(Region.Location expected, PolygonsSet set,
                             Vector2D[] points) {
        for (int i = 0; i < points.length; ++i) {
            Assert.assertEquals(expected, set.checkPoint(points[i]));
        }
    }

    private boolean checkInSegment(Vector2D p,
                                   Vector2D p1, Vector2D p2,
                                   double tolerance) {
        Line line = new Line(p1, p2);
        if (line.getOffset(p) < tolerance) {
            double x  = (line.toSubSpace(p)).getX();
            double x1 = (line.toSubSpace(p1)).getX();
            double x2 = (line.toSubSpace(p2)).getX();
            return (((x - x1) * (x - x2) <= 0.0)
                    || (p1.distance(p) < tolerance)
                    || (p2.distance(p) < tolerance));
        } else {
            return false;
        }
    }

    private void checkVertices(Vector2D[][] rebuiltVertices,
                               Vector2D[][] vertices) {

        // each rebuilt vertex should be in a segment joining two original vertices
        for (int i = 0; i < rebuiltVertices.length; ++i) {
            for (int j = 0; j < rebuiltVertices[i].length; ++j) {
                boolean inSegment = false;
                Vector2D p = rebuiltVertices[i][j];
                for (int k = 0; k < vertices.length; ++k) {
                    Vector2D[] loop = vertices[k];
                    int length = loop.length;
                    for (int l = 0; (! inSegment) && (l < length); ++l) {
                        inSegment = checkInSegment(p, loop[l], loop[(l + 1) % length], 1.0e-10);
                    }
                }
                Assert.assertTrue(inSegment);
            }
        }

        // each original vertex should have a corresponding rebuilt vertex
        for (int k = 0; k < vertices.length; ++k) {
            for (int l = 0; l < vertices[k].length; ++l) {
                double min = Double.POSITIVE_INFINITY;
                for (int i = 0; i < rebuiltVertices.length; ++i) {
                    for (int j = 0; j < rebuiltVertices[i].length; ++j) {
                        min = FastMath.min(vertices[k][l].distance(rebuiltVertices[i][j]),
                                       min);
                    }
                }
                Assert.assertEquals(0.0, min, 1.0e-10);
            }
        }

    }

}

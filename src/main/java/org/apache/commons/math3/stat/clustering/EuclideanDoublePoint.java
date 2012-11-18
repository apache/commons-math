package org.apache.commons.math3.stat.clustering;

import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.math3.util.MathArrays;

/**
 * A simple implementation of {@link Clusterable} for points with double coordinates.
 * @version $Id$
 * @since 3.1
 */
public class EuclideanDoublePoint implements Clusterable<EuclideanDoublePoint>, Serializable {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 8026472786091227632L;

    /** Point coordinates. */
    private final double[] point;

    /**
     * Build an instance wrapping an integer array.
     * <p>
     * The wrapped array is referenced, it is <em>not</em> copied.
     *
     * @param point the n-dimensional point in integer space
     */
    public EuclideanDoublePoint(final double[] point) {
        this.point = point;
    }

    /** {@inheritDoc} */
    public EuclideanDoublePoint centroidOf(final Collection<EuclideanDoublePoint> points) {
        final double[] centroid = new double[getPoint().length];
        for (final EuclideanDoublePoint p : points) {
            for (int i = 0; i < centroid.length; i++) {
                centroid[i] += p.getPoint()[i];
            }
        }
        for (int i = 0; i < centroid.length; i++) {
            centroid[i] /= points.size();
        }
        return new EuclideanDoublePoint(centroid);
    }

    /** {@inheritDoc} */
    public double distanceFrom(final EuclideanDoublePoint p) {
        return MathArrays.distance(point, p.getPoint());
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof EuclideanDoublePoint)) {
            return false;
        }
        final double[] otherPoint = ((EuclideanDoublePoint) other).getPoint();
        if (point.length != otherPoint.length) {
            return false;
        }
        for (int i = 0; i < point.length; i++) {
            if (point[i] != otherPoint[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the n-dimensional point in integer space.
     *
     * @return a reference (not a copy!) to the wrapped array
     */
    public double[] getPoint() {
        return point;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int hashCode = 0;
        for (final Double i : point) {
            hashCode += i.hashCode() * 13 + 7;
        }
        return hashCode;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        final StringBuilder buff = new StringBuilder("(");
        final double[] coordinates = getPoint();
        for (int i = 0; i < coordinates.length; i++) {
            buff.append(coordinates[i]);
            if (i < coordinates.length - 1) {
                buff.append(',');
            }
        }
        buff.append(')');
        return buff.toString();
    }

}

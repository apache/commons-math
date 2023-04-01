package org.apache.commons.math4.legacy.stat.descriptive;

public interface StatisticAccumulator<T extends StorelessUnivariateStatistic> {

    <U extends StatisticAccumulator<T>> void merge(U other);

    void merge(T other);

    T get();

}

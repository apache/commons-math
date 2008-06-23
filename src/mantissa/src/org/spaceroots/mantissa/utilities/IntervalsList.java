// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
// 
//   http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.spaceroots.mantissa.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/** This class represents an intervals list.
 * <p>An interval list represent a list of contiguous regions on the
 * real line. All intervals of the list are disjoints to each other,
 * they are stored in ascending order.</p>
 * <p>The class supports the main set operations like union and
 * intersection.</p>
 * @see Interval
 * @author Luc Maisonobe
 * @version $Id$
 */
public class IntervalsList {

  /** Build an empty intervals list.
   */
  public IntervalsList() {
    intervals = new ArrayList();
  }

  /** Build an intervals list containing only one interval.
   * @param a first bound of the interval
   * @param b second bound of the interval
   */
  public IntervalsList(double a, double b) {
    intervals = new ArrayList();
    intervals.add(new Interval(a, b));
  }

  /** Build an intervals list containing only one interval.
   * @param i interval
   */
  public IntervalsList(Interval i) {
    intervals = new ArrayList();
    intervals.add(i);
  }

  /** Build an intervals list containing two intervals.
   * @param i1 first interval
   * @param i2 second interval
   */
  public IntervalsList(Interval i1, Interval i2) {
    intervals = new ArrayList();
    if (i1.intersects(i2)) {
      intervals.add(new Interval(Math.min(i1.getInf(), i2.getInf()),
                                 Math.max(i1.getSup(), i2.getSup())));
    } else  if (i1.getSup () < i2.getInf()) {
      intervals.add(i1);
      intervals.add(i2);
    } else {
      intervals.add(i2);
      intervals.add(i1);
    }
  }

  /** Copy constructor.
   * <p>The copy operation is a deep copy: the underlying intervals
   * are independant of the instances of the copied list.</p>
   * @param list intervals list to copy
   */
  public IntervalsList(IntervalsList list) {
    intervals = new ArrayList(list.intervals.size());
    for (Iterator iterator = list.intervals.iterator(); iterator.hasNext();) {
      intervals.add(new Interval((Interval) iterator.next()));
    }
  }

  /** Check if the instance is empty.
   * @return true if the instance is empty
   */
  public boolean isEmpty() {
    return intervals.isEmpty();
  }

  /** Check if the instance is connected.
   * <p>An interval list is connected if it contains only one
   * interval.</p>
   * @return true is the instance is connected
   */
  public boolean isConnex() {
    return intervals.size() == 1;
  }

  /** Get the lower bound of the list.
   * @return lower bound of the list or Double.NaN if the list does
   * not contain any interval
   */
  public double getInf() {
    return intervals.isEmpty()
         ? Double.NaN : ((Interval) intervals.get(0)).getInf();
  }

  /** Get the upper bound of the list.
   * @return upper bound of the list or Double.NaN if the list does
   * not contain any interval
   */
  public double getSup() {
    return intervals.isEmpty()
         ? Double.NaN : ((Interval) intervals.get(intervals.size() - 1)).getSup();
  }

  /** Get the number of intervals of the list.
   * @return number of intervals in the list
   */
  public int getSize() {
    return intervals.size();
  }

  /** Get an interval from the list.
   * @param i index of the interval
   * @return interval at index i
   */
  public Interval getInterval(int i) {
    return (Interval) intervals.get(i);
  }

  /** Get the ordered list of disjoints intervals.
   * @return list of disjoints intervals in ascending order
   */
  public List getIntervals() {
    return intervals;
  }

  /** Check if the list contains a point.
   * @param x point to check
   * @return true if the list contains x
   */
  public boolean contains(double x) {
    for (Iterator iterator = intervals.iterator(); iterator.hasNext();) {
      if (((Interval) iterator.next()).contains(x)) {
        return true;
      }
    }
    return false;
  }

  /** Check if the list contains an interval.
   * @param i interval to check
   * @return true if i is completely included in the instance
   */
  public boolean contains(Interval i) {
    for (Iterator iterator = intervals.iterator(); iterator.hasNext();) {
      if (((Interval) iterator.next()).contains(i)) {
        return true;
      }
    }
    return false;
  }

  /** Check if an interval intersects the instance.
   * @param i interval to check
   * @return true if i intersects the instance
   */
  public boolean intersects(Interval i) {
    for (Iterator iterator = intervals.iterator(); iterator.hasNext();) {
      if (((Interval) iterator.next()).intersects(i)) {
        return true;
      }
    }
    return false;
  }

  /** Add an interval to the instance.
   * <p>This method expands the instance.</p>
   * <p>This operation is a union operation. The number of intervals
   * in the list can decrease if the interval fills some holes between
   * existing intervals in the list.</p>
   * @param i interval to add to the instance
   */
  public void addToSelf(Interval i) {

    List    newIntervals = new ArrayList();
    double  inf          = Double.NaN;
    double  sup          = Double.NaN;
    boolean pending      = false;
    boolean processed    = false;
    for (Iterator iterator = intervals.iterator(); iterator.hasNext();) {
      Interval local = (Interval) iterator.next();
      
      if (local.getSup() < i.getInf()) {
        newIntervals.add(local);
      } else if (local.getInf() < i.getSup()) {
        if (! pending) {
          inf     = Math.min(local.getInf(), i.getInf());
          pending   = true;
          processed = true;
        }
        sup = Math.max(local.getSup(), i.getSup());
      } else {
        if (pending) {
          newIntervals.add(new Interval(inf, sup));
          pending   = false;
        } else if (! processed) {
          newIntervals.add(i);
        }
        processed = true;
        newIntervals.add(local);
      }
    }

    if (pending) {
      newIntervals.add(new Interval(inf, sup));
    } else if (! processed) {
      newIntervals.add(i);
    }

    intervals = newIntervals;

  }

  /** Add an intervals list and an interval.
   * @param list intervals list
   * @param i interval
   * @return a new intervals list which is the union of list and i
   */
  public static IntervalsList add(IntervalsList list, Interval i) {
    IntervalsList copy = new IntervalsList(list);
    copy.addToSelf(i);
    return copy;
  }

  /** Remove an interval from the list.
   * <p>This method reduces the instance. This operation is defined in
   * terms of points set operation. As an example, if the [2, 3]
   * interval is subtracted from the list containing only the [0, 10]
   * interval, the result will be the [0, 2] U [3, 10] intervals
   * list.</p>
   * @param i interval to remove
   */
  public void subtractFromSelf(Interval i) {
    double a = Math.min(getInf(), i.getInf());
    double b = Math.max(getSup(), i.getSup());
    intersectSelf(new IntervalsList(new Interval(a - 1.0, i.getInf()),
                                   new Interval(i.getSup(), b + 1.0)));
  }

  /** Remove an interval from a list.
   * @param list intervals list
   * @param i interval to remove
   * @return a new intervals list
   */
  public static IntervalsList subtract(IntervalsList list, Interval i) {
    IntervalsList copy = new IntervalsList(list);
    copy.subtractFromSelf(i);
    return copy;
  }

  /** Intersects the instance and an interval.
   * @param i interval
   */
  public void intersectSelf(Interval i) {
    List newIntervals = new ArrayList();
    for (Iterator iterator = intervals.iterator(); iterator.hasNext();) {
      Interval local = (Interval) iterator.next();
      if (local.intersects(i)) {
        newIntervals.add(Interval.intersection(local, i));
      }
    }
    intervals = newIntervals;
  }

  /** Intersect a list and an interval.
   * @param list intervals list
   * @param i interval
   * @return the intersection of list and i
   */
  public static IntervalsList intersection(IntervalsList list, Interval i) {
    IntervalsList copy = new IntervalsList(list);
    copy.intersectSelf(i);
    return copy;
  }

  /** Add an intervals list to the instance.
   * <p>This method expands the instance.</p>
   * <p>This operation is a union operation. The number of intervals
   * in the list can decrease if the list fills some holes between
   * existing intervals in the instance.</p>
   * @param list intervals list to add to the instance
   */
  public void addToSelf(IntervalsList list) {
    for (Iterator iterator = list.intervals.iterator(); iterator.hasNext();) {
      addToSelf((Interval) iterator.next());
    }
  }

  /** Add two intervals lists.
   * @param list1 first intervals list
   * @param list2 second intervals list
   * @return a new intervals list which is the union of list1 and list2
   */
  public static IntervalsList add(IntervalsList list1, IntervalsList list2) {
    IntervalsList copy = new IntervalsList(list1);
    copy.addToSelf(list2);
    return copy;
  }

  /** Remove an intervals list from the instance.
   * @param list intervals list to remove
   */
  public void subtractFromSelf(IntervalsList list) {
    for (Iterator iterator = list.intervals.iterator(); iterator.hasNext();) {
      subtractFromSelf((Interval) iterator.next());
    }
  }

  /** Remove an intervals list from another one.
   * @param list1 intervals list
   * @param list2 intervals list to remove
   * @return a new intervals list
   */
  public static IntervalsList subtract(IntervalsList list1, IntervalsList list2) {
    IntervalsList copy = new IntervalsList(list1);
    copy.subtractFromSelf(list2);
    return copy;
  }

  /** Intersect the instance and another intervals list.
   * @param list list to intersect with the instance
   */
  public void intersectSelf(IntervalsList list) {
    intervals = intersection(this, list).intervals;
  }

  /** Intersect two intervals lists.
   * @param list1 first intervals list
   * @param list2 second intervals list
   * @return a new list which is the intersection of list1 and list2
   */
  public static IntervalsList intersection(IntervalsList list1, IntervalsList list2) {
    IntervalsList list = new IntervalsList();
    for (Iterator iterator = list2.intervals.iterator(); iterator.hasNext();) {
      list.addToSelf(intersection(list1, (Interval) iterator.next()));
    }
    return list;
  }

  /** The list of intervals. */
  private List intervals;

}


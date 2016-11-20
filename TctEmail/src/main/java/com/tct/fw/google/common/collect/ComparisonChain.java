/*
 * Copyright (C) 2009 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tct.fw.google.common.collect;

import com.tct.fw.google.common.annotations.GwtCompatible;
import com.tct.fw.google.common.primitives.Booleans;
import com.tct.fw.google.common.primitives.Ints;
import com.tct.fw.google.common.primitives.Longs;
import com.tct.fw.javax.annotation.Nullable;

import java.util.Comparator;


/**
 * A utility for performing a "lazy" chained comparison statement, which 
 * performs comparisons only until it finds a nonzero result. For example:
 * <pre>   {@code
 *
 *   public int compareTo(Foo that) {
 *     return ComparisonChain.start()
 *         .compare(this.aString, that.aString)
 *         .compare(this.anInt, that.anInt)
 *         .compare(this.anEnum, that.anEnum, Ordering.natural().nullsLast())
 *         .result();
 *   }}</pre>
 *
 * The value of this expression will have the same sign as the <i>first
 * nonzero</i> comparison result in the chain, or will be zero if every
 * comparison result was zero.
 *
 * <p>Once any comparison returns a nonzero value, remaining comparisons are
 * "short-circuited".
 *
 * @author Mark Davis
 * @author Kevin Bourrillion
 * @since 2.0
 */
@GwtCompatible
public abstract class ComparisonChain {
  private ComparisonChain() {}

  /**
   * Begins a new chained comparison statement. See example in the class
   * documentation.
   */
  public static ComparisonChain start() {
    return ACTIVE;
  }

  private static final ComparisonChain ACTIVE = new ComparisonChain() {
    @SuppressWarnings("unchecked")
    @Override public ComparisonChain compare(
        Comparable left, Comparable right) {
      return classify(left.compareTo(right));
    }
    @Override public <T> ComparisonChain compare(
        @Nullable T left, @Nullable T right, Comparator<T> comparator) {
      return classify(comparator.compare(left, right));
    }
    @Override public ComparisonChain compare(int left, int right) {
      return classify(Ints.compare(left, right));
    }
    @Override public ComparisonChain compare(long left, long right) {
      return classify(Longs.compare(left, right));
    }
    @Override public ComparisonChain compare(float left, float right) {
      return classify(Float.compare(left, right));
    }
    @Override public ComparisonChain compare(double left, double right) {
      return classify(Double.compare(left, right));
    }
    @Override public ComparisonChain compare(boolean left, boolean right) {
      return classify(Booleans.compare(left, right));
    }
    ComparisonChain classify(int result) {
      return (result < 0) ? LESS : (result > 0) ? GREATER : ACTIVE;
    }
    @Override public int result() {
      return 0;
    }
  };

  private static final ComparisonChain LESS = new InactiveComparisonChain(-1);

  private static final ComparisonChain GREATER = new InactiveComparisonChain(1);

  private static final class InactiveComparisonChain extends ComparisonChain {
    final int result;

    InactiveComparisonChain(int result) {
      this.result = result;
    }
    @Override public ComparisonChain compare(
        @Nullable Comparable left, @Nullable Comparable right) {
      return this;
    }
    @Override public <T> ComparisonChain compare(@Nullable T left,
        @Nullable T right, @Nullable Comparator<T> comparator) {
      return this;
    }
    @Override public ComparisonChain compare(int left, int right) {
      return this;
    }
    @Override public ComparisonChain compare(long left, long right) {
      return this;
    }
    @Override public ComparisonChain compare(float left, float right) {
      return this;
    }
    @Override public ComparisonChain compare(double left, double right) {
      return this;
    }
    @Override public ComparisonChain compare(boolean left, boolean right) {
      return this;
    }
    @Override public int result() {
      return result;
    }
  }

  /**
   * Compares two comparable objects as specified by {@link
   * Comparable#compareTo}, <i>if</i> the result of this comparison chain
   * has not already been determined.
   */
  public abstract ComparisonChain compare(
      Comparable<?> left, Comparable<?> right);

  /**
   * Compares two objects using a comparator, <i>if</i> the result of this
   * comparison chain has not already been determined.
   */
  public abstract <T> ComparisonChain compare(
      @Nullable T left, @Nullable T right, Comparator<T> comparator);

  /**
   * Compares two {@code int} values as specified by {@link Ints#compare},
   * <i>if</i> the result of this comparison chain has not already been
   * determined.
   */
  public abstract ComparisonChain compare(int left, int right);

  /**
   * Compares two {@code long} values as specified by {@link Longs#compare},
   * <i>if</i> the result of this comparison chain has not already been
   * determined.
   */
  public abstract ComparisonChain compare(long left, long right);

  /**
   * Compares two {@code float} values as specified by {@link
   * Float#compare}, <i>if</i> the result of this comparison chain has not
   * already been determined.
   */
  public abstract ComparisonChain compare(float left, float right);

  /**
   * Compares two {@code double} values as specified by {@link
   * Double#compare}, <i>if</i> the result of this comparison chain has not
   * already been determined.
   */
  public abstract ComparisonChain compare(double left, double right);

  /**
   * Compares two {@code boolean} values as specified by {@link
   * Booleans#compare}, <i>if</i> the result of this comparison chain has not
   * already been determined.
   */
  public abstract ComparisonChain compare(boolean left, boolean right);

  /**
   * Ends this comparison chain and returns its result: a value having the
   * same sign as the first nonzero comparison result in the chain, or zero if
   * every result was zero.
   */
  public abstract int result();
}

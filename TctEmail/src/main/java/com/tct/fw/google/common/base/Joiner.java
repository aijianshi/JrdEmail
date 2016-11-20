/*
 * Copyright (C) 2008 The Guava Authors
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

package com.tct.fw.google.common.base;

import static com.tct.fw.google.common.base.Preconditions.checkNotNull;

import com.tct.fw.google.common.annotations.Beta;
import com.tct.fw.google.common.annotations.GwtCompatible;
import com.tct.fw.javax.annotation.CheckReturnValue;
import com.tct.fw.javax.annotation.Nullable;

import java.io.IOException;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


/**
 * An object which joins pieces of text (specified as an array, {@link Iterable}, varargs or even a
 * {@link Map}) with a separator. It either appends the results to an {@link Appendable} or returns
 * them as a {@link String}. Example: <pre>   {@code
 *
 *   Joiner joiner = Joiner.on("; ").skipNulls();
 *    . . .
 *   return joiner.join("Harry", null, "Ron", "Hermione");}</pre>
 *
 * This returns the string {@code "Harry; Ron; Hermione"}. Note that all input elements are
 * converted to strings using {@link Object#toString()} before being appended.
 *
 * <p>If neither {@link #skipNulls()} nor {@link #useForNull(String)} is specified, the joining
 * methods will throw {@link NullPointerException} if any given element is null.
 *
 * <p><b>Warning: joiner instances are always immutable</b>; a configuration method such as {@code
 * useForNull} has no effect on the instance it is invoked on! You must store and use the new joiner
 * instance returned by the method. This makes joiners thread-safe, and safe to store as {@code
 * static final} constants. <pre>   {@code
 *
 *   // Bad! Do not do this!
 *   Joiner joiner = Joiner.on(',');
 *   joiner.skipNulls(); // does nothing!
 *   return joiner.join("wrong", null, "wrong");}</pre>
 *
 * @author Kevin Bourrillion
 * @since 2.0 (imported from Google Collections Library)
 */
@GwtCompatible
public class Joiner {
  /**
   * Returns a joiner which automatically places {@code separator} between consecutive elements.
   */
  public static Joiner on(String separator) {
    return new Joiner(separator);
  }

  /**
   * Returns a joiner which automatically places {@code separator} between consecutive elements.
   */
  public static Joiner on(char separator) {
    return new Joiner(String.valueOf(separator));
  }

  private final String separator;

  private Joiner(String separator) {
    this.separator = checkNotNull(separator);
  }

  private Joiner(Joiner prototype) {
    this.separator = prototype.separator;
  }

  /**
   * <b>Deprecated.</b>
   *
   * @since 11.0
   * @deprecated use {@link #appendTo(Appendable, Iterator)} by casting {@code parts} to
   *     {@code Iterator<?>}, or better yet, by implementing only {@code Iterator} and not
   *     {@code Iterable}. <b>This method is scheduled for deletion in June 2013.</b>
   */
  @Beta
  @Deprecated
  public final <A extends Appendable, I extends Object & Iterable<?> & Iterator<?>> A
      appendTo(A appendable, I parts) throws IOException {
    return appendTo(appendable, (Iterator<?>) parts);
  }

  /**
   * Appends the string representation of each of {@code parts}, using the previously configured
   * separator between each, to {@code appendable}.
   */
  public <A extends Appendable> A appendTo(A appendable, Iterable<?> parts) throws IOException {
    return appendTo(appendable, parts.iterator());
  }

  /**
   * Appends the string representation of each of {@code parts}, using the previously configured
   * separator between each, to {@code appendable}.
   *
   * @since 11.0
   */
  @Beta
  public <A extends Appendable> A appendTo(A appendable, Iterator<?> parts) throws IOException {
    checkNotNull(appendable);
    if (parts.hasNext()) {
      appendable.append(toString(parts.next()));
      while (parts.hasNext()) {
        appendable.append(separator);
        appendable.append(toString(parts.next()));
      }
    }
    return appendable;
  }

  /**
   * Appends the string representation of each of {@code parts}, using the previously configured
   * separator between each, to {@code appendable}.
   */
  public final <A extends Appendable> A appendTo(A appendable, Object[] parts) throws IOException {
    return appendTo(appendable, Arrays.asList(parts));
  }

  /**
   * Appends to {@code appendable} the string representation of each of the remaining arguments.
   */
  public final <A extends Appendable> A appendTo(
      A appendable, @Nullable Object first, @Nullable Object second, Object... rest)
          throws IOException {
    return appendTo(appendable, iterable(first, second, rest));
  }

  /**
   * <b>Deprecated.</b>
   *
   * @since 11.0
   * @deprecated use {@link #appendTo(StringBuilder, Iterator)} by casting {@code parts} to
   *     {@code Iterator<?>}, or better yet, by implementing only {@code Iterator} and not
   *     {@code Iterable}. <b>This method is scheduled for deletion in June 2013.</b>
   */
  @Beta
  @Deprecated
  public final <I extends Object & Iterable<?> & Iterator<?>> StringBuilder
      appendTo(StringBuilder builder, I parts) {
    return appendTo(builder, (Iterator<?>) parts);
  }

  /**
   * Appends the string representation of each of {@code parts}, using the previously configured
   * separator between each, to {@code builder}. Identical to {@link #appendTo(Appendable,
   * Iterable)}, except that it does not throw {@link IOException}.
   */
  public final StringBuilder appendTo(StringBuilder builder, Iterable<?> parts) {
    return appendTo(builder, parts.iterator());
  }

  /**
   * Appends the string representation of each of {@code parts}, using the previously configured
   * separator between each, to {@code builder}. Identical to {@link #appendTo(Appendable,
   * Iterable)}, except that it does not throw {@link IOException}.
   *
   * @since 11.0
   */
  @Beta
  public final StringBuilder appendTo(StringBuilder builder, Iterator<?> parts) {
    try {
      appendTo((Appendable) builder, parts);
    } catch (IOException impossible) {
      throw new AssertionError(impossible);
    }
    return builder;
  }

  /**
   * Appends the string representation of each of {@code parts}, using the previously configured
   * separator between each, to {@code builder}. Identical to {@link #appendTo(Appendable,
   * Iterable)}, except that it does not throw {@link IOException}.
   */
  public final StringBuilder appendTo(StringBuilder builder, Object[] parts) {
    return appendTo(builder, Arrays.asList(parts));
  }

  /**
   * Appends to {@code builder} the string representation of each of the remaining arguments.
   * Identical to {@link #appendTo(Appendable, Object, Object, Object...)}, except that it does not
   * throw {@link IOException}.
   */
  public final StringBuilder appendTo(
      StringBuilder builder, @Nullable Object first, @Nullable Object second, Object... rest) {
    return appendTo(builder, iterable(first, second, rest));
  }

  /**
   * <b>Deprecated.</b>
   *
   * @since 11.0
   * @deprecated use {@link #join(Iterator)} by casting {@code parts} to
   *     {@code Iterator<?>}, or better yet, by implementing only {@code Iterator} and not
   *     {@code Iterable}. <b>This method is scheduled for deletion in June 2013.</b>
   */
  @Beta
  @Deprecated
  public final <I extends Object & Iterable<?> & Iterator<?>> String join(I parts) {
    return join((Iterator<?>) parts);
  }

  /**
   * Returns a string containing the string representation of each of {@code parts}, using the
   * previously configured separator between each.
   */
  public final String join(Iterable<?> parts) {
    return join(parts.iterator());
  }

  /**
   * Returns a string containing the string representation of each of {@code parts}, using the
   * previously configured separator between each.
   *
   * @since 11.0
   */
  @Beta
  public final String join(Iterator<?> parts) {
    return appendTo(new StringBuilder(), parts).toString();
  }

  /**
   * Returns a string containing the string representation of each of {@code parts}, using the
   * previously configured separator between each.
   */
  public final String join(Object[] parts) {
    return join(Arrays.asList(parts));
  }

  /**
   * Returns a string containing the string representation of each argument, using the previously
   * configured separator between each.
   */
  public final String join(@Nullable Object first, @Nullable Object second, Object... rest) {
    return join(iterable(first, second, rest));
  }

  /**
   * Returns a joiner with the same behavior as this one, except automatically substituting {@code
   * nullText} for any provided null elements.
   */
  @CheckReturnValue
  public Joiner useForNull(final String nullText) {
    checkNotNull(nullText);
    return new Joiner(this) {
      @Override CharSequence toString(Object part) {
        return (part == null) ? nullText : Joiner.this.toString(part);
      }

      @Override public Joiner useForNull(String nullText) {
        checkNotNull(nullText); // weird: just to satisfy NullPointerTester.
        throw new UnsupportedOperationException("already specified useForNull");
      }

      @Override public Joiner skipNulls() {
        throw new UnsupportedOperationException("already specified useForNull");
      }
    };
  }

  /**
   * Returns a joiner with the same behavior as this joiner, except automatically skipping over any
   * provided null elements.
   */
  @CheckReturnValue
  public Joiner skipNulls() {
    return new Joiner(this) {
      @Override public <A extends Appendable> A appendTo(A appendable, Iterator<?> parts)
          throws IOException {
        checkNotNull(appendable, "appendable");
        checkNotNull(parts, "parts");
        while (parts.hasNext()) {
          Object part = parts.next();
          if (part != null) {
            appendable.append(Joiner.this.toString(part));
            break;
          }
        }
        while (parts.hasNext()) {
          Object part = parts.next();
          if (part != null) {
            appendable.append(separator);
            appendable.append(Joiner.this.toString(part));
          }
        }
        return appendable;
      }

      @Override public Joiner useForNull(String nullText) {
        checkNotNull(nullText); // weird: just to satisfy NullPointerTester.
        throw new UnsupportedOperationException("already specified skipNulls");
      }

      @Override public MapJoiner withKeyValueSeparator(String kvs) {
        checkNotNull(kvs); // weird: just to satisfy NullPointerTester.
        throw new UnsupportedOperationException("can't use .skipNulls() with maps");
      }
    };
  }

  /**
   * Returns a {@code MapJoiner} using the given key-value separator, and the same configuration as
   * this {@code Joiner} otherwise.
   */
  @CheckReturnValue
  public MapJoiner withKeyValueSeparator(String keyValueSeparator) {
    return new MapJoiner(this, keyValueSeparator);
  }

  /**
   * An object that joins map entries in the same manner as {@code Joiner} joins iterables and
   * arrays. Like {@code Joiner}, it is thread-safe and immutable.
   *
   * <p>In addition to operating on {@code Map} instances, {@code MapJoiner} can operate on {@code
   * Multimap} entries in two distinct modes:
   *
   * <ul>
   * <li>To output a separate entry for each key-value pair, pass {@code multimap.entries()} to a
   *     {@code MapJoiner} method that accepts entries as input, and receive output of the form
   *     {@code key1=A&key1=B&key2=C}.
   * <li>To output a single entry for each key, pass {@code multimap.asMap()} to a {@code MapJoiner}
   *     method that accepts a map as input, and receive output of the form {@code
   *     key1=[A, B]&key2=C}.
   * </ul>
   *
   * @since 2.0 (imported from Google Collections Library)
   */
  public final static class MapJoiner {
    private final Joiner joiner;
    private final String keyValueSeparator;

    private MapJoiner(Joiner joiner, String keyValueSeparator) {
      this.joiner = joiner; // only "this" is ever passed, so don't checkNotNull
      this.keyValueSeparator = checkNotNull(keyValueSeparator);
    }

    /**
     * Appends the string representation of each entry of {@code map}, using the previously
     * configured separator and key-value separator, to {@code appendable}.
     */
    public <A extends Appendable> A appendTo(A appendable, Map<?, ?> map) throws IOException {
      return appendTo(appendable, map.entrySet());
    }

    /**
     * Appends the string representation of each entry of {@code map}, using the previously
     * configured separator and key-value separator, to {@code builder}. Identical to {@link
     * #appendTo(Appendable, Map)}, except that it does not throw {@link IOException}.
     */
    public StringBuilder appendTo(StringBuilder builder, Map<?, ?> map) {
      return appendTo(builder, map.entrySet());
    }

    /**
     * Returns a string containing the string representation of each entry of {@code map}, using the
     * previously configured separator and key-value separator.
     */
    public String join(Map<?, ?> map) {
      return join(map.entrySet());
    }

    /**
     * <b>Deprecated.</b>
     *
     * @since 11.0
     * @deprecated use {@link #appendTo(Appendable, Iterator)} by casting {@code entries} to
     *     {@code Iterator<? extends Entry<?, ?>>}, or better yet, by implementing only
     *     {@code Iterator} and not {@code Iterable}. <b>This method is scheduled for deletion
     *     in June 2013.</b>
     */
    @Beta
    @Deprecated
    public <A extends Appendable,
        I extends Object & Iterable<? extends Entry<?, ?>> & Iterator<? extends Entry<?, ?>>>
        A appendTo(A appendable, I entries) throws IOException {
      Iterator<? extends Entry<?, ?>> iterator = entries;
      return appendTo(appendable, iterator);
    }

    /**
     * Appends the string representation of each entry in {@code entries}, using the previously
     * configured separator and key-value separator, to {@code appendable}.
     *
     * @since 10.0
     */
    @Beta
    public <A extends Appendable> A appendTo(A appendable, Iterable<? extends Entry<?, ?>> entries)
        throws IOException {
      return appendTo(appendable, entries.iterator());
    }

    /**
     * Appends the string representation of each entry in {@code entries}, using the previously
     * configured separator and key-value separator, to {@code appendable}.
     *
     * @since 11.0
     */
    @Beta
    public <A extends Appendable> A appendTo(A appendable, Iterator<? extends Entry<?, ?>> parts)
        throws IOException {
      checkNotNull(appendable);
      if (parts.hasNext()) {
        Entry<?, ?> entry = parts.next();
        appendable.append(joiner.toString(entry.getKey()));
        appendable.append(keyValueSeparator);
        appendable.append(joiner.toString(entry.getValue()));
        while (parts.hasNext()) {
          appendable.append(joiner.separator);
          Entry<?, ?> e = parts.next();
          appendable.append(joiner.toString(e.getKey()));
          appendable.append(keyValueSeparator);
          appendable.append(joiner.toString(e.getValue()));
        }
      }
      return appendable;
    }

    /**
     * <b>Deprecated.</b>
     *
     * @since 11.0
     * @deprecated use {@link #appendTo(StringBuilder, Iterator)} by casting {@code entries} to
     *     {@code Iterator<? extends Entry<?, ?>>}, or better yet, by implementing only
     *     {@code Iterator} and not {@code Iterable}. <b>This method is scheduled for deletion
     *     in June 2013.</b>
     */
    @Beta
    @Deprecated
    public <I extends Object & Iterable<? extends Entry<?, ?>> & Iterator<? extends Entry<?, ?>>>
        StringBuilder appendTo(StringBuilder builder, I entries) throws IOException {
      Iterator<? extends Entry<?, ?>> iterator = entries;
      return appendTo(builder, iterator);
    }

    /**
     * Appends the string representation of each entry in {@code entries}, using the previously
     * configured separator and key-value separator, to {@code builder}. Identical to {@link
     * #appendTo(Appendable, Iterable)}, except that it does not throw {@link IOException}.
     *
     * @since 10.0
     */
    @Beta
    public StringBuilder appendTo(StringBuilder builder, Iterable<? extends Entry<?, ?>> entries) {
      return appendTo(builder, entries.iterator());
    }

    /**
     * Appends the string representation of each entry in {@code entries}, using the previously
     * configured separator and key-value separator, to {@code builder}. Identical to {@link
     * #appendTo(Appendable, Iterable)}, except that it does not throw {@link IOException}.
     *
     * @since 11.0
     */
    @Beta
    public StringBuilder appendTo(StringBuilder builder, Iterator<? extends Entry<?, ?>> entries) {
      try {
        appendTo((Appendable) builder, entries);
      } catch (IOException impossible) {
        throw new AssertionError(impossible);
      }
      return builder;
    }

    /**
     * <b>Deprecated.</b>
     *
     * @since 11.0
     * @deprecated use {@link #join(Iterator)} by casting {@code entries} to
     *     {@code Iterator<? extends Entry<?, ?>>}, or better yet, by implementing only
     *     {@code Iterator} and not {@code Iterable}. <b>This method is scheduled for deletion
     *     in June 2013.</b>
     */
    @Beta
    @Deprecated
    public <I extends Object & Iterable<? extends Entry<?, ?>> & Iterator<? extends Entry<?, ?>>>
        String join(I entries) throws IOException {
      Iterator<? extends Entry<?, ?>> iterator = entries;
      return join(iterator);
    }

    /**
     * Returns a string containing the string representation of each entry in {@code entries}, using
     * the previously configured separator and key-value separator.
     *
     * @since 10.0
     */
    @Beta
    public String join(Iterable<? extends Entry<?, ?>> entries) {
      return join(entries.iterator());
    }

    /**
     * Returns a string containing the string representation of each entry in {@code entries}, using
     * the previously configured separator and key-value separator.
     *
     * @since 11.0
     */
    @Beta
    public String join(Iterator<? extends Entry<?, ?>> entries) {
      return appendTo(new StringBuilder(), entries).toString();
    }

    /**
     * Returns a map joiner with the same behavior as this one, except automatically substituting
     * {@code nullText} for any provided null keys or values.
     */
    @CheckReturnValue
    public MapJoiner useForNull(String nullText) {
      return new MapJoiner(joiner.useForNull(nullText), keyValueSeparator);
    }
  }

  CharSequence toString(Object part) {
    checkNotNull(part);  // checkNotNull for GWT (do not optimize).
    return (part instanceof CharSequence) ? (CharSequence) part : part.toString();
  }

  private static Iterable<Object> iterable(
      final Object first, final Object second, final Object[] rest) {
    checkNotNull(rest);
    return new AbstractList<Object>() {
      @Override public int size() {
        return rest.length + 2;
      }

      @Override public Object get(int index) {
        switch (index) {
          case 0:
            return first;
          case 1:
            return second;
          default:
            return rest[index - 2];
        }
      }
    };
  }
}

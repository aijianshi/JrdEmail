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

package com.tct.fw.google.common.collect;

import com.tct.fw.google.common.annotations.GwtCompatible;
import com.tct.fw.javax.annotation.Nullable;

import java.io.Serializable;


/**
 * @see com.tct.fw.google.common.collect.Maps#immutableEntry(Object, Object)
 */
@GwtCompatible(serializable = true)
class ImmutableEntry<K, V> extends AbstractMapEntry<K, V>
    implements Serializable {
  private final K key;
  private final V value;

  ImmutableEntry(@Nullable K key, @Nullable V value) {
    this.key = key;
    this.value = value;
  }

  @Nullable @Override public K getKey() {
    return key;
  }

  @Nullable @Override public V getValue() {
    return value;
  }

  @Override public final V setValue(V value){
    throw new UnsupportedOperationException();
  }

  private static final long serialVersionUID = 0;
}

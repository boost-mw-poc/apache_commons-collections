/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.keyvalue;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.Unmodifiable;
import org.junit.jupiter.api.Test;

/**
 * Test the UnmodifiableMapEntry class.
 */
public class UnmodifiableMapEntryTest<K, V> extends AbstractMapEntryTest<K, V> {

    /**
     * Make an instance of Map.Entry with the default (null) key and value.
     * Subclasses should override this method to return a Map.Entry
     * of the type being tested.
     */
    @Override
    public Map.Entry<K, V> makeMapEntry() {
        return new UnmodifiableMapEntry<>(null, null);
    }

    /**
     * Make an instance of Map.Entry with the specified key and value.
     * Subclasses should override this method to return a Map.Entry
     * of the type being tested.
     */
    @Override
    public Map.Entry<K, V> makeMapEntry(final K key, final V value) {
        return new UnmodifiableMapEntry<>(key, value);
    }

    @Test
    @Override
    @SuppressWarnings("unchecked")
    public void testAccessorsAndMutators() {
        Map.Entry<K, V> entry = makeMapEntry((K) key, (V) value);

        assertSame(key, entry.getKey());
        assertSame(value, entry.getValue());

        // check that null doesn't do anything funny
        entry = makeMapEntry(null, null);
        assertSame(null, entry.getKey());
        assertSame(null, entry.getValue());
    }

    /**
     * Subclasses should override this method.
     */
    @Test
    @Override
    @SuppressWarnings("unchecked")
    public void testConstructors() {
        // 1. test key-value constructor
        Map.Entry<K, V> entry = new UnmodifiableMapEntry<>((K) key, (V) value);
        assertSame(key, entry.getKey());
        assertSame(value, entry.getValue());

        // 2. test pair constructor
        final KeyValue<K, V> pair = new DefaultKeyValue<>((K) key, (V) value);
        entry = new UnmodifiableMapEntry<>(pair);
        assertSame(key, entry.getKey());
        assertSame(value, entry.getValue());

        // 3. test copy constructor
        final Map.Entry<K, V> entry2 = new UnmodifiableMapEntry<>(entry);
        assertSame(key, entry2.getKey());
        assertSame(value, entry2.getValue());

        assertInstanceOf(Unmodifiable.class, entry);
    }

    @Test
    @Override
    public void testSelfReferenceHandling() {
        // block
    }

    @Test
    void testUnmodifiable() {
        final Map.Entry<K, V> entry = makeMapEntry();

        assertThrows(UnsupportedOperationException.class, () -> entry.setValue(null));
    }

}

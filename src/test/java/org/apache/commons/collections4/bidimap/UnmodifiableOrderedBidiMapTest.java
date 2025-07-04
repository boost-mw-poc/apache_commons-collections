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
package org.apache.commons.collections4.bidimap;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.collections4.Unmodifiable;
import org.junit.jupiter.api.Test;

/**
 * JUnit tests.
 */
public class UnmodifiableOrderedBidiMapTest<K extends Comparable<K>, V extends Comparable<V>> extends AbstractOrderedBidiMapTest<K, V> {

    @Override
    public boolean isAllowNullKey() {
        return false;
    }

    @Override
    public boolean isAllowNullValueGet() {
        return false;
    }

    @Override
    public boolean isAllowNullValuePut() {
        return false;
    }

    @Override
    public boolean isPutAddSupported() {
        return false;
    }

    @Override
    public boolean isPutChangeSupported() {
        return false;
    }

    @Override
    public boolean isRemoveSupported() {
        return false;
    }

    @Override
    public Map<K, V> makeConfirmedMap() {
        return new TreeMap<>();
    }

    @Override
    public OrderedBidiMap<K, V> makeFullMap() {
        final OrderedBidiMap<K, V> bidi = new TreeBidiMap<>();
        addSampleMappings(bidi);
        return UnmodifiableOrderedBidiMap.unmodifiableOrderedBidiMap(bidi);
    }

    @Override
    public OrderedBidiMap<K, V> makeObject() {
        return UnmodifiableOrderedBidiMap.unmodifiableOrderedBidiMap(new TreeBidiMap<>());
    }

    @Test
    void testDecorateFactory() {
        final OrderedBidiMap<K, V> map = makeFullMap();
        assertSame(map, UnmodifiableOrderedBidiMap.unmodifiableOrderedBidiMap(map));

        assertThrows(NullPointerException.class, () -> UnmodifiableOrderedBidiMap.unmodifiableOrderedBidiMap(null));
    }

    @Test
    void testUnmodifiable() {
        assertTrue(makeObject() instanceof Unmodifiable);
        assertTrue(makeFullMap() instanceof Unmodifiable);
    }

}

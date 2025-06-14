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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.collections4.SortedBidiMap;
import org.apache.commons.collections4.comparators.ComparableComparator;
import org.apache.commons.collections4.comparators.ReverseComparator;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link DualTreeBidiMap}.
 */
@SuppressWarnings("boxing")
public class DualTreeBidiMap2Test<K extends Comparable<K>, V extends Comparable<V>> extends AbstractSortedBidiMapTest<K, V> {

    private static final class IntegerComparator implements Comparator<Integer>, Serializable {
        private static final long serialVersionUID = 1L;

        @Override
        public int compare(final Integer o1, final Integer o2) {
            return o1.compareTo(o2);
        }
    }

    @Override
    public String getCompatibilityVersion() {
        return "4.Test2";
    }

    @Override
    public boolean isAllowNullValueGet() {
        // TODO Is this a bug or a feature?
        return true;
    }

    @Override
    public TreeMap<K, V> makeConfirmedMap() {
        return new TreeMap<>(new ReverseComparator<>(ComparableComparator.<K>comparableComparator()));
    }

    @Override
    public DualTreeBidiMap<K, V> makeObject() {
        return new DualTreeBidiMap<>(
                new ReverseComparator<>(ComparableComparator.<K>comparableComparator()),
                new ReverseComparator<>(ComparableComparator.<V>comparableComparator()));
    }

    @Test
    void testCollections364() throws Exception {
        final DualTreeBidiMap<String, Integer> original = new DualTreeBidiMap<>(
                String.CASE_INSENSITIVE_ORDER, new IntegerComparator());
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(buffer);
        out.writeObject(original);
        out.close();

        final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        @SuppressWarnings("unchecked")
        final DualTreeBidiMap<String, Integer> deserialized = (DualTreeBidiMap<String, Integer>) in.readObject();
        in.close();

        assertNotNull(original.comparator());
        assertNotNull(deserialized.comparator());
        assertEquals(original.comparator().getClass(), deserialized.comparator().getClass());
        assertEquals(original.valueComparator().getClass(), deserialized.valueComparator().getClass());
    }

    @Test
    void testComparator() {
        resetEmpty();
        final SortedBidiMap<K, V> bidi = (SortedBidiMap<K, V>) map;
        assertNotNull(bidi.comparator());
        assertTrue(bidi.comparator() instanceof ReverseComparator);
    }

    @Test
    void testComparator2() {
        final DualTreeBidiMap<String, Integer> dtbm = new DualTreeBidiMap<>(
                String.CASE_INSENSITIVE_ORDER, null);
        dtbm.put("two", 0);
        dtbm.put("one", 1);
        assertEquals("one", dtbm.firstKey());
        assertEquals("two", dtbm.lastKey());

    }

    @Test
    void testSerializeDeserializeCheckComparator() throws Exception {
        final SortedBidiMap<?, ?> obj = makeObject();
        if (obj instanceof Serializable && isTestSerialization()) {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final ObjectOutputStream out = new ObjectOutputStream(buffer);
            out.writeObject(obj);
            out.close();

            final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            final Object dest = in.readObject();
            in.close();

            final SortedBidiMap<?, ?> bidi = (SortedBidiMap<?, ?>) dest;
            assertNotNull(obj.comparator());
            assertNotNull(bidi.comparator());
            assertTrue(bidi.comparator() instanceof ReverseComparator);
        }
    }

    @Test
    void testSortOrder() throws Exception {
        final SortedBidiMap<K, V> sm = makeFullMap();

        // Sort by the comparator used in the makeEmptyBidiMap() method
        List<K> newSortedKeys = getAsList(getSampleKeys());
        newSortedKeys.sort(new ReverseComparator<>(ComparableComparator.<K>comparableComparator()));
        newSortedKeys = Collections.unmodifiableList(newSortedKeys);

        final Iterator<K> mapIter = sm.keySet().iterator();
        for (final K expectedKey : newSortedKeys) {
            final K mapKey = mapIter.next();
            assertNotNull(expectedKey, "key in sorted list may not be null");
            assertNotNull(mapKey, "key in map may not be null");
            assertEquals(expectedKey, mapKey, "key from sorted list and map must be equal");
        }
    }

//    void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) map, "src/test/resources/data/test/DualTreeBidiMap.emptyCollection.version4.Test2.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) map, "src/test/resources/data/test/DualTreeBidiMap.fullCollection.version4.Test2.obj");
//    }
}

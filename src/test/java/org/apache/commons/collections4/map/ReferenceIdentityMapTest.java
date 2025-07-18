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
package org.apache.commons.collections4.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections4.IterableMap;
import org.apache.commons.collections4.map.AbstractReferenceMap.ReferenceStrength;
import org.junit.jupiter.api.Test;

/**
 * Tests for ReferenceIdentityMap.
 *
 * @param <K> the key type.
 * @param <V> the value type.
 */
public class ReferenceIdentityMapTest<K, V> extends AbstractIterableMapTest<K, V> {

    private static final Integer I1A = new Integer(1); // Cannot use valueOf here
    private static final Integer I1B = new Integer(1);
    private static final Integer I2A = new Integer(2);
    private static final Integer I2B = new Integer(2);

    @SuppressWarnings("unused")
    private static void gc() {
        try {
            // trigger GC
            final byte[][] tooLarge = new byte[1000000000][1000000000];
            fail("you have too much RAM");
        } catch (final OutOfMemoryError ex) {
            System.gc(); // ignore
        }
    }

    WeakReference<K> keyReference;

    WeakReference<V> valueReference;

    @SuppressWarnings("unchecked")
    private Map<K, V> buildRefMap() {
        final K key = (K) new Object();
        final V value = (V) new Object();

        keyReference = new WeakReference<>(key);
        valueReference = new WeakReference<>(value);

        final Map<K, V> testMap = new ReferenceIdentityMap<>(ReferenceStrength.WEAK, ReferenceStrength.HARD, true);
        testMap.put(key, value);

        assertEquals(value, testMap.get(key), "In map");
        assertNotNull(keyReference.get(), "Weak reference released early (1)");
        assertNotNull(valueReference.get(), "Weak reference released early (2)");
        return testMap;
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/ReferenceIdentityMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/ReferenceIdentityMap.fullCollection.version4.obj");
//    }

    @Override
    public boolean isAllowNullKey() {
        return false;
    }

    @Override
    public boolean isAllowNullValueGet() {
        return true;
    }

    @Override
    public boolean isAllowNullValuePut() {
        return false;
    }

    @Override
    public Map<K, V> makeConfirmedMap() {
        // Testing against another [collections] class generally isn't a good idea,
        // but the closest alternative is IdentityHashMap, which propagates reference-equality down to keySet and values.
        // arguably ReferenceIdentityMap should do the same but that's a later discussion.
        return new IdentityMap<>();
    }

/*
    // Tests often fail because gc is uncontrollable

    @Test
    void testPurge() {
        ReferenceIdentityMap map = new ReferenceIdentityMap(ReferenceIdentityMap.WEAK, ReferenceIdentityMap.WEAK);
        Object[] hard = new Object[10];
        for (int i = 0; i < hard.length; i++) {
            hard[i] = new Object();
            map.put(hard[i], new Object());
        }
        gc();
        assertTrue("map should be empty after purge of weak values", map.isEmpty());

        for (int i = 0; i < hard.length; i++) {
            map.put(new Object(), hard[i]);
        }
        gc();
        assertTrue("map should be empty after purge of weak keys", map.isEmpty());

        for (int i = 0; i < hard.length; i++) {
            map.put(new Object(), hard[i]);
            map.put(hard[i], new Object());
        }

        gc();
        assertTrue("map should be empty after purge of weak keys and values", map.isEmpty());
    }

    @Test
    void testGetAfterGC() {
        ReferenceIdentityMap map = new ReferenceIdentityMap(ReferenceIdentityMap.WEAK, ReferenceIdentityMap.WEAK);
        for (int i = 0; i < 10; i++) {
            map.put(Integer.valueOf(i), Integer.valueOf(i));
        }

        gc();
        for (int i = 0; i < 10; i++) {
            Integer I = Integer.valueOf(i);
            assertTrue("map.containsKey should return false for GC'd element", !map.containsKey(I));
            assertTrue("map.get should return null for GC'd element", map.get(I) == null);
        }
    }

    @Test
    void testEntrySetIteratorAfterGC() {
        ReferenceIdentityMap map = new ReferenceIdentityMap(ReferenceIdentityMap.WEAK, ReferenceIdentityMap.WEAK);
        Object[] hard = new Object[10];
        for (int i = 0; i < 10; i++) {
            hard[i] = Integer.valueOf(10 + i);
            map.put(Integer.valueOf(i), Integer.valueOf(i));
            map.put(hard[i], hard[i]);
        }

        gc();
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            Integer key = (Integer)entry.getKey();
            Integer value = (Integer)entry.getValue();
            assertTrue("iterator should skip GC'd keys", key.intValue() >= 10);
            assertTrue("iterator should skip GC'd values", value.intValue() >= 10);
        }

    }

    @Test
    void testMapIteratorAfterGC() {
        ReferenceIdentityMap map = new ReferenceIdentityMap(ReferenceIdentityMap.WEAK, ReferenceIdentityMap.WEAK);
        Object[] hard = new Object[10];
        for (int i = 0; i < 10; i++) {
            hard[i] = Integer.valueOf(10 + i);
            map.put(Integer.valueOf(i), Integer.valueOf(i));
            map.put(hard[i], hard[i]);
        }

        gc();
        MapIterator iterator = map.mapIterator();
        while (iterator.hasNext()) {
            Object key1 = iterator.next();
            Integer key = (Integer) iterator.getKey();
            Integer value = (Integer) iterator.getValue();
            assertTrue("iterator keys should match", key == key1);
            assertTrue("iterator should skip GC'd keys", key.intValue() >= 10);
            assertTrue("iterator should skip GC'd values", value.intValue() >= 10);
        }

    }

    @Test
    void testMapIteratorAfterGC2() {
        ReferenceIdentityMap map = new ReferenceIdentityMap(ReferenceIdentityMap.WEAK, ReferenceIdentityMap.WEAK);
        Object[] hard = new Object[10];
        for (int i = 0; i < 10; i++) {
            hard[i] = Integer.valueOf(10 + i);
            map.put(Integer.valueOf(i), Integer.valueOf(i));
            map.put(hard[i], hard[i]);
        }

        MapIterator iterator = map.mapIterator();
        while (iterator.hasNext()) {
            Object key1 = iterator.next();
            gc();
            Integer key = (Integer) iterator.getKey();
            Integer value = (Integer) iterator.getValue();
            assertTrue("iterator keys should match", key == key1);
            assertTrue("iterator should skip GC'd keys", key.intValue() >= 10);
            assertTrue("iterator should skip GC'd values", value.intValue() >= 10);
        }

    }
*/

    @Override
    public ReferenceIdentityMap<K, V> makeObject() {
        return new ReferenceIdentityMap<>(ReferenceStrength.WEAK, ReferenceStrength.WEAK);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testBasics() {
        final IterableMap<K, V> map = new ReferenceIdentityMap<>(ReferenceStrength.HARD, ReferenceStrength.HARD);
        assertEquals(0, map.size());

        map.put((K) I1A, (V) I2A);
        assertEquals(1, map.size());
        assertSame(I2A, map.get(I1A));
        assertSame(null, map.get(I1B));
        assertTrue(map.containsKey(I1A));
        assertFalse(map.containsKey(I1B));
        assertTrue(map.containsValue(I2A));
        assertFalse(map.containsValue(I2B));

        map.put((K) I1A, (V) I2B);
        assertEquals(1, map.size());
        assertSame(I2B, map.get(I1A));
        assertSame(null, map.get(I1B));
        assertTrue(map.containsKey(I1A));
        assertFalse(map.containsKey(I1B));
        assertFalse(map.containsValue(I2A));
        assertTrue(map.containsValue(I2B));

        map.put((K) I1B, (V) I2B);
        assertEquals(2, map.size());
        assertSame(I2B, map.get(I1A));
        assertSame(I2B, map.get(I1B));
        assertTrue(map.containsKey(I1A));
        assertTrue(map.containsKey(I1B));
        assertFalse(map.containsValue(I2A));
        assertTrue(map.containsValue(I2B));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testHashEntry() {
        final IterableMap<K, V> map = new ReferenceIdentityMap<>(ReferenceStrength.HARD, ReferenceStrength.HARD);

        map.put((K) I1A, (V) I2A);
        map.put((K) I1B, (V) I2A);

        final Map.Entry<K, V> entry1 = map.entrySet().iterator().next();
        final Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
        final Map.Entry<K, V> entry2 = it.next();
        final Map.Entry<K, V> entry3 = it.next();

        assertTrue(entry1.equals(entry2));
        assertTrue(entry2.equals(entry1));
        assertFalse(entry1.equals(entry3));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testNullHandling() {
        resetFull();
        assertNull(getMap().get(null));
        assertFalse(getMap().containsKey(null));
        assertFalse(getMap().containsValue(null));
        assertNull(getMap().remove(null));
        assertFalse(getMap().entrySet().contains(null));
        assertFalse(getMap().containsKey(null));
        assertFalse(getMap().containsValue(null));
        assertThrows(NullPointerException.class, () -> getMap().put(null, null));
        assertThrows(NullPointerException.class, () -> getMap().put((K) new Object(), null));
        assertThrows(NullPointerException.class, () -> getMap().put(null, (V) new Object()));
    }

    /** Tests whether purge values setting works */
    @Test
    void testPurgeValues() throws Exception {
        // many thanks to Juozas Baliuka for suggesting this method
        final Map<K, V> testMap = buildRefMap();

        int iterations = 0;
        int bytz = 2;
        while (true) {
            System.gc();
            if (iterations++ > 50) {
                fail("Max iterations reached before resource released.");
            }
            testMap.isEmpty();
            if (
                keyReference.get() == null &&
                valueReference.get() == null) {
                break;

            }
            // create garbage:
            @SuppressWarnings("unused")
            final byte[] b =  new byte[bytz];
            bytz *= 2;
        }
    }

}

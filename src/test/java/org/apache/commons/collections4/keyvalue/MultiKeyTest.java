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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

/**
 * Tests for {@link org.apache.commons.collections4.keyvalue.MultiKey}.
 */
class MultiKeyTest {

    static class DerivedMultiKey<T> extends MultiKey<T> {

        private static final long serialVersionUID = 1928896152249821416L;

        DerivedMultiKey(final T key1, final T key2) {
            super(key1, key2);
        }

        public T getFirst() {
            return getKey(0);
        }

        public T getSecond() {
            return getKey(1);
        }

    }

    static class SystemHashCodeSimulatingKey implements Serializable {

        private static final long serialVersionUID = -1736147315703444603L;
        private final String name;
        private int hashCode = 1;

        SystemHashCodeSimulatingKey(final String name) {
            this.name = name;
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof SystemHashCodeSimulatingKey
                && name.equals(((SystemHashCodeSimulatingKey) obj).name);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        private Object readResolve() {
            hashCode = 2; // simulate different hashCode after deserialization in another process
            return this;
        }
    }

    Integer ONE = Integer.valueOf(1);

    Integer TWO = Integer.valueOf(2);
    Integer THREE = Integer.valueOf(3);
    Integer FOUR = Integer.valueOf(4);
    Integer FIVE = Integer.valueOf(5);

    @Test
    void testConstructors() throws Exception {
        MultiKey<Integer> mk;
        mk = new MultiKey<>(ONE, TWO);
        assertArrayEquals(new Object[]{ONE, TWO}, mk.getKeys());

        mk = new MultiKey<>(ONE, TWO, THREE);
        assertArrayEquals(new Object[]{ONE, TWO, THREE}, mk.getKeys());

        mk = new MultiKey<>(ONE, TWO, THREE, FOUR);
        assertArrayEquals(new Object[]{ONE, TWO, THREE, FOUR}, mk.getKeys());

        mk = new MultiKey<>(ONE, TWO, THREE, FOUR, FIVE);
        assertArrayEquals(new Object[]{ONE, TWO, THREE, FOUR, FIVE}, mk.getKeys());

        mk = new MultiKey<>(new Integer[] { THREE, FOUR, ONE, TWO }, false);
        assertArrayEquals(new Object[]{THREE, FOUR, ONE, TWO}, mk.getKeys());
    }

    @Test
    void testConstructorsByArray() throws Exception {
        MultiKey<Integer> mk;
        Integer[] keys = { THREE, FOUR, ONE, TWO };
        mk = new MultiKey<>(keys);
        assertArrayEquals(new Object[]{THREE, FOUR, ONE, TWO}, mk.getKeys());
        keys[3] = FIVE;  // no effect
        assertArrayEquals(new Object[]{THREE, FOUR, ONE, TWO}, mk.getKeys());

        keys = new Integer[] {};
        mk = new MultiKey<>(keys);
        assertArrayEquals(new Object[]{}, mk.getKeys());

        keys = new Integer[] { THREE, FOUR, ONE, TWO };
        mk = new MultiKey<>(keys, true);
        assertArrayEquals(new Object[]{THREE, FOUR, ONE, TWO}, mk.getKeys());
        keys[3] = FIVE;  // no effect
        assertArrayEquals(new Object[]{THREE, FOUR, ONE, TWO}, mk.getKeys());

        keys = new Integer[] { THREE, FOUR, ONE, TWO };
        mk = new MultiKey<>(keys, false);
        assertArrayEquals(new Object[]{THREE, FOUR, ONE, TWO}, mk.getKeys());
        // change key - don't do this!
        // the hash code of the MultiKey is now broken
        keys[3] = FIVE;
        assertArrayEquals(new Object[]{THREE, FOUR, ONE, FIVE}, mk.getKeys());
    }

    @TestFactory
    public Collection<DynamicTest> testConstructorsByArrayNull() {
        final Integer[] keys = null;
        return Arrays.asList(
                dynamicTest("Integer[] null", () -> {
                    assertThrows(NullPointerException.class, () -> new MultiKey<>(keys));
                }),
                dynamicTest("Integer[] null + makeClone true", () -> {
                    assertThrows(NullPointerException.class, () -> new MultiKey<>(keys, true));
                }),
                dynamicTest("Integer[] null + makeClone false", () -> {
                    assertThrows(NullPointerException.class, () -> new MultiKey<>(keys, false));
                })
        );
    }

    @Test
    void testEquals() {
        final MultiKey<Integer> mk1 = new MultiKey<>(ONE, TWO);
        final MultiKey<Integer> mk2 = new MultiKey<>(ONE, TWO);
        final MultiKey<Object> mk3 = new MultiKey<>(ONE, "TWO");

        assertEquals(mk1, mk1);
        assertEquals(mk1, mk2);
        assertNotEquals(mk1, mk3);
        assertNotEquals(StringUtils.EMPTY, mk1);
        assertNotEquals(null, mk1);
    }

    @Test
    void testEqualsAfterSerialization() throws IOException, ClassNotFoundException {
        SystemHashCodeSimulatingKey sysKey = new SystemHashCodeSimulatingKey("test");
        final MultiKey<?> mk = new MultiKey<Object>(ONE, sysKey);
        final Map<MultiKey<?>, Integer> map = new HashMap<>();
        map.put(mk, TWO);

        // serialize
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(sysKey);
        out.writeObject(map);
        out.close();

        // deserialize
        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ObjectInputStream in = new ObjectInputStream(bais);
        sysKey = (SystemHashCodeSimulatingKey) in.readObject(); // simulate deserialization in another process
        final Map<?, ?> map2 = (Map<?, ?>) in.readObject();
        in.close();

        assertEquals(2, sysKey.hashCode()); // different hashCode now

        final MultiKey<?> mk2 = new MultiKey<Object>(ONE, sysKey);
        assertEquals(TWO, map2.get(mk2));
    }

    @Test
    void testEqualsAfterSerializationOfDerivedClass() throws IOException, ClassNotFoundException {
        final DerivedMultiKey<?> mk = new DerivedMultiKey<>("A", "B");

        // serialize
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(mk);
        out.close();

        // deserialize
        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ObjectInputStream in = new ObjectInputStream(bais);
        final DerivedMultiKey<?> mk2 = (DerivedMultiKey<?>) in.readObject();
        in.close();

        assertEquals(mk.hashCode(), mk2.hashCode());
    }

    @TestFactory
    public Collection<DynamicTest> testGetIndexed() {
        final MultiKey<Integer> mk = new MultiKey<>(ONE, TWO);
        return Arrays.asList(
                dynamicTest("0", () -> {
                    assertSame(ONE, mk.getKey(0));
                }),
                dynamicTest("1", () -> {
                    assertSame(TWO, mk.getKey(1));
                }),
                dynamicTest("-1", () -> {
                    assertThrows(IndexOutOfBoundsException.class, () -> mk.getKey(-1));
                }),
                dynamicTest("2", () -> {
                    assertThrows(IndexOutOfBoundsException.class, () -> mk.getKey(2));
                })
        );
    }

    @Test
    void testGetKeysArrayConstructorCloned() {
        final Integer[] keys = { ONE, TWO };
        final MultiKey<Integer> mk = new MultiKey<>(keys, true);
        final Object[] array = mk.getKeys();
        assertNotSame(array, keys);
        assertArrayEquals(array, keys);
        assertSame(ONE, array[0]);
        assertSame(TWO, array[1]);
        assertEquals(2, array.length);
    }

    @Test
    void testGetKeysArrayConstructorNonCloned() {
        final Integer[] keys = { ONE, TWO };
        final MultiKey<Integer> mk = new MultiKey<>(keys, false);
        final Object[] array = mk.getKeys();
        assertNotSame(array, keys);  // still not equal
        assertArrayEquals(array, keys);
        assertSame(ONE, array[0]);
        assertSame(TWO, array[1]);
        assertEquals(2, array.length);
    }

    @Test
    void testGetKeysSimpleConstructor() {
        final MultiKey<Integer> mk = new MultiKey<>(ONE, TWO);
        final Object[] array = mk.getKeys();
        assertSame(ONE, array[0]);
        assertSame(TWO, array[1]);
        assertEquals(2, array.length);
    }

    @Test
    void testHashCode() {
        final MultiKey<Integer> mk1 = new MultiKey<>(ONE, TWO);
        final MultiKey<Integer> mk2 = new MultiKey<>(ONE, TWO);
        final MultiKey<Object> mk3 = new MultiKey<>(ONE, "TWO");

        assertEquals(mk1.hashCode(), mk1.hashCode());
        assertEquals(mk1.hashCode(), mk2.hashCode());
        assertTrue(mk1.hashCode() != mk3.hashCode());

        final int total = 0 ^ ONE.hashCode() ^ TWO.hashCode();
        assertEquals(total, mk1.hashCode());
    }

    @Test
    void testSize() {
        assertEquals(2, new MultiKey<>(ONE, TWO).size());
        assertEquals(2, new MultiKey<>(null, null).size());
        assertEquals(3, new MultiKey<>(ONE, TWO, THREE).size());
        assertEquals(3, new MultiKey<>(null, null, null).size());
        assertEquals(4, new MultiKey<>(ONE, TWO, THREE, FOUR).size());
        assertEquals(4, new MultiKey<>(null, null, null, null).size());
        assertEquals(5, new MultiKey<>(ONE, TWO, THREE, FOUR, FIVE).size());
        assertEquals(5, new MultiKey<>(null, null, null, null, null).size());

        assertEquals(0, new MultiKey<>(new Object[] {}).size());
        assertEquals(1, new MultiKey<>(new Integer[] { ONE }).size());
        assertEquals(2, new MultiKey<>(new Integer[] { ONE, TWO }).size());
        assertEquals(7, new MultiKey<>(new Integer[] { ONE, TWO, ONE, TWO, ONE, TWO, ONE }).size());
    }

    @Test
    void testTwoArgCtor() {
        final MultiKeyTest key1 = new MultiKeyTest();
        final MultiKeyTest key2 = new MultiKeyTest();
        final MultiKeyTest[] keys = new MultiKey<>(key1, key2).getKeys();
        assertNotNull(keys);
    }

}

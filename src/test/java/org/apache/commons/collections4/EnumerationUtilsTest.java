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
package org.apache.commons.collections4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.junit.jupiter.api.Test;

/**
 * Tests EnumerationUtils.
 */
class EnumerationUtilsTest {

    public static final String TO_LIST_FIXTURE = "this is a test";

    @Test
    void testAsIterableFor() {
        final Vector<String> vector = new Vector<>();
        vector.addElement("zero");
        vector.addElement("one");
        final Enumeration<String> en = vector.elements();
        final Iterator<String> iterator = EnumerationUtils.asIterable(en).iterator();
        assertTrue(iterator.hasNext());
        assertEquals("zero", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("one", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void testAsIterableForNull() {
        assertThrows(NullPointerException.class, () -> EnumerationUtils.asIterable((Enumeration) null).iterator().next());
    }

    @Test
    void testGetFromEnumeration() throws Exception {
        // Enumeration, entry exists
        final Vector<String> vector = new Vector<>();
        vector.addElement("zero");
        vector.addElement("one");
        Enumeration<String> en = vector.elements();
        assertEquals("zero", EnumerationUtils.get(en, 0));
        en = vector.elements();
        assertEquals("one", EnumerationUtils.get(en, 1));

        // Enumerator, non-existent entry
        final Enumeration<String> finalEn = en;
        assertThrows(IndexOutOfBoundsException.class, () -> EnumerationUtils.get(finalEn, 3));

        assertFalse(en.hasMoreElements());
    }

    @Test
    void testToListWithHashtable() {
        final Hashtable<String, Integer> expected = new Hashtable<>();
        expected.put("one", Integer.valueOf(1));
        expected.put("two", Integer.valueOf(2));
        expected.put("three", Integer.valueOf(3));
        // validate elements.
        final List<Integer> actualEltList = EnumerationUtils.toList(expected.elements());
        assertEquals(expected.size(), actualEltList.size());
        assertTrue(actualEltList.contains(Integer.valueOf(1)));
        assertTrue(actualEltList.contains(Integer.valueOf(2)));
        assertTrue(actualEltList.contains(Integer.valueOf(3)));
        final List<Integer> expectedEltList = new ArrayList<>();
        expectedEltList.add(Integer.valueOf(1));
        expectedEltList.add(Integer.valueOf(2));
        expectedEltList.add(Integer.valueOf(3));
        assertTrue(actualEltList.containsAll(expectedEltList));

        // validate keys.
        final List<String> actualKeyList = EnumerationUtils.toList(expected.keys());
        assertEquals(expected.size(), actualEltList.size());
        assertTrue(actualKeyList.contains("one"));
        assertTrue(actualKeyList.contains("two"));
        assertTrue(actualKeyList.contains("three"));
        final List<String> expectedKeyList = new ArrayList<>();
        expectedKeyList.add("one");
        expectedKeyList.add("two");
        expectedKeyList.add("three");
        assertTrue(actualKeyList.containsAll(expectedKeyList));
    }

    @Test
    void testToListWithStringTokenizer() {
        final List<String> expectedList1 = new ArrayList<>();
        final StringTokenizer st = new StringTokenizer(TO_LIST_FIXTURE);
        while (st.hasMoreTokens()) {
            expectedList1.add(st.nextToken());
        }
        final List<String> expectedList2 = new ArrayList<>();
        expectedList2.add("this");
        expectedList2.add("is");
        expectedList2.add("a");
        expectedList2.add("test");
        final List<String> actualList = EnumerationUtils.toList(new StringTokenizer(TO_LIST_FIXTURE));
        assertEquals(expectedList1, expectedList2);
        assertEquals(expectedList1, actualList);
        assertEquals(expectedList2, actualList);
    }

    @Test
    void testToSetWithHashtable() {
        final Hashtable<String, Integer> expected = new Hashtable<>();
        expected.put("one", Integer.valueOf(1));
        expected.put("two", Integer.valueOf(2));
        expected.put("three", Integer.valueOf(3));
        // validate elements.
        final Set<Integer> actualEltSet = EnumerationUtils.toSet(expected.elements());
        assertEquals(expected.size(), actualEltSet.size());
        assertTrue(actualEltSet.contains(Integer.valueOf(1)));
        assertTrue(actualEltSet.contains(Integer.valueOf(2)));
        assertTrue(actualEltSet.contains(Integer.valueOf(3)));
        final Set<Integer> expectedEltList = new HashSet<>();
        expectedEltList.add(Integer.valueOf(1));
        expectedEltList.add(Integer.valueOf(2));
        expectedEltList.add(Integer.valueOf(3));
        assertTrue(actualEltSet.containsAll(expectedEltList));

        // validate keys.
        final Set<String> actualKeySet = EnumerationUtils.toSet(expected.keys());
        assertEquals(expected.size(), actualEltSet.size());
        assertTrue(actualKeySet.contains("one"));
        assertTrue(actualKeySet.contains("two"));
        assertTrue(actualKeySet.contains("three"));
        final Set<String> expectedKeySet = new HashSet<>();
        expectedKeySet.add("one");
        expectedKeySet.add("two");
        expectedKeySet.add("three");
        assertTrue(actualKeySet.containsAll(expectedKeySet));
    }

}

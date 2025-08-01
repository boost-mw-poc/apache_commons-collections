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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections4.functors.EqualPredicate;
import org.apache.commons.collections4.functors.TruePredicate;
import org.apache.commons.collections4.iterators.ArrayIterator;
import org.apache.commons.collections4.iterators.EmptyIterator;
import org.apache.commons.collections4.iterators.EmptyListIterator;
import org.apache.commons.collections4.iterators.EmptyMapIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedMapIterator;
import org.apache.commons.collections4.iterators.EnumerationIterator;
import org.apache.commons.collections4.iterators.IteratorChainTest;
import org.apache.commons.collections4.iterators.NodeListIterator;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.apache.commons.collections4.iterators.ZippingIterator;
import org.apache.commons.collections4.map.EntrySetToMapIteratorAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Tests for IteratorUtils.
 */
class IteratorUtilsTest {

    /**
     * Collection of {@link Integer}s
     */
    private List<Integer> collectionA;

    /**
     * Collection of even {@link Integer}s
     */
    private List<Integer> collectionEven;

    /**
     * Collection of odd {@link Integer}s
     */
    private List<Integer> collectionOdd;

    private final Collection<Integer> emptyCollection = new ArrayList<>(1);

    private Iterable<Integer> iterableA;

    /**
     * Creates a NodeList containing the specified nodes.
     */
    private NodeList createNodeList(final Node[] nodes) {
        return new NodeList() {
            @Override
            public int getLength() {
                return nodes.length;
            }

            @Override
            public Node item(final int index) {
                return nodes[index];
            }
        };
    }

    /**
     * creates an array of four Node instances, mocked by EasyMock.
     */
    private Node[] createNodes() {
        final Node node1 = createMock(Node.class);
        final Node node2 = createMock(Node.class);
        final Node node3 = createMock(Node.class);
        final Node node4 = createMock(Node.class);
        replay(node1);
        replay(node2);
        replay(node3);
        replay(node4);

        return new Node[] { node1, node2, node3, node4 };
    }

    /**
     * Gets an immutable Iterator operating on the elements ["a", "b", "c", "d"].
     */
    private Iterator<String> getImmutableIterator() {
        final List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        return IteratorUtils.unmodifiableIterator(list.iterator());
    }

    /**
     * Gets an immutable ListIterator operating on the elements ["a", "b", "c", "d"].
     */
    private ListIterator<String> getImmutableListIterator() {
        final List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        return IteratorUtils.unmodifiableListIterator(list.listIterator());
    }

    @BeforeEach
    public void setUp() {
        collectionA = new ArrayList<>();
        collectionA.add(1);
        collectionA.add(2);
        collectionA.add(2);
        collectionA.add(3);
        collectionA.add(3);
        collectionA.add(3);
        collectionA.add(4);
        collectionA.add(4);
        collectionA.add(4);
        collectionA.add(4);

        iterableA = collectionA;

        collectionEven = Arrays.asList(2, 4, 6, 8, 10, 12);
        collectionOdd = Arrays.asList(1, 3, 5, 7, 9, 11);
    }

    @Test
    void testArrayIterator() {
        final Object[] objArray = { "a", "b", "c" };
        ResettableIterator<Object> iterator = IteratorUtils.arrayIterator(objArray);
        assertEquals("a", iterator.next());
        assertEquals("b", iterator.next());
        iterator.reset();
        assertEquals("a", iterator.next());

        assertThrows(IllegalArgumentException.class, () -> IteratorUtils.arrayIterator(Integer.valueOf(0)));
        assertThrows(NullPointerException.class, () -> IteratorUtils.arrayIterator((Object[]) null));

        iterator = IteratorUtils.arrayIterator(objArray, 1);
        assertEquals("b", iterator.next());

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> IteratorUtils.arrayIterator(objArray, -1));

        iterator = IteratorUtils.arrayIterator(objArray, 3);
        assertFalse(iterator.hasNext());
        iterator.reset();

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> IteratorUtils.arrayIterator(objArray, 4));

        iterator = IteratorUtils.arrayIterator(objArray, 2, 3);
        assertEquals("c", iterator.next());

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> IteratorUtils.arrayIterator(objArray, 2, 4));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> IteratorUtils.arrayIterator(objArray, -1, 1));
        assertThrows(IllegalArgumentException.class, () -> IteratorUtils.arrayIterator(objArray, 2, 1));

        final int[] intArray = { 0, 1, 2 };
        iterator = IteratorUtils.arrayIterator(intArray);
        assertEquals(0, iterator.next());
        assertEquals(1, iterator.next());
        iterator.reset();
        assertEquals(0, iterator.next());

        iterator = IteratorUtils.arrayIterator(intArray, 1);
        assertEquals(1, iterator.next());

        assertThrows(IndexOutOfBoundsException.class, () -> IteratorUtils.arrayIterator(intArray, -1));

        iterator = IteratorUtils.arrayIterator(intArray, 3);
        assertFalse(iterator.hasNext());
        iterator.reset();

        assertThrows(IndexOutOfBoundsException.class, () -> IteratorUtils.arrayIterator(intArray, 4));

        iterator = IteratorUtils.arrayIterator(intArray, 2, 3);
        assertEquals(2, iterator.next());

        assertThrows(IndexOutOfBoundsException.class, () -> IteratorUtils.arrayIterator(intArray, 2, 4));
        assertThrows(IndexOutOfBoundsException.class, () -> IteratorUtils.arrayIterator(intArray, -1, 1));
        assertThrows(IllegalArgumentException.class, () -> IteratorUtils.arrayIterator(intArray, 2, 1));
    }

    @Test
    void testArrayListIterator() {
        final Object[] objArray = { "a", "b", "c", "d" };
        ResettableListIterator<Object> iterator = IteratorUtils.arrayListIterator(objArray);
        assertFalse(iterator.hasPrevious());
        assertEquals(-1, iterator.previousIndex());
        assertEquals(0, iterator.nextIndex());
        assertEquals("a", iterator.next());
        assertEquals("a", iterator.previous());
        assertEquals("a", iterator.next());
        assertEquals(0, iterator.previousIndex());
        assertEquals(1, iterator.nextIndex());
        assertEquals("b", iterator.next());
        assertEquals("c", iterator.next());
        assertEquals("d", iterator.next());
        assertEquals(4, iterator.nextIndex()); // size of list
        assertEquals(3, iterator.previousIndex());

        assertThrows(IllegalArgumentException.class, () -> IteratorUtils.arrayListIterator(Integer.valueOf(0)));
        assertThrows(NullPointerException.class, () -> IteratorUtils.arrayListIterator((Object[]) null));

        iterator = IteratorUtils.arrayListIterator(objArray, 1);
        assertEquals(-1, iterator.previousIndex());
        assertFalse(iterator.hasPrevious());
        assertEquals(0, iterator.nextIndex());
        assertEquals("b", iterator.next());
        assertEquals(0, iterator.previousIndex());
        assertThrows(IndexOutOfBoundsException.class, () -> IteratorUtils.arrayListIterator(objArray, -1));

        iterator = IteratorUtils.arrayListIterator(objArray, 3);
        assertTrue(iterator.hasNext());

        final ResettableListIterator<Object> finalIterator = iterator;

        assertThrows(NoSuchElementException.class, () -> finalIterator.previous());
        assertThrows(IndexOutOfBoundsException.class, () -> IteratorUtils.arrayListIterator(objArray, 5));

        iterator = IteratorUtils.arrayListIterator(objArray, 2, 3);
        assertEquals("c", iterator.next());

        assertThrows(IndexOutOfBoundsException.class, () -> IteratorUtils.arrayListIterator(objArray, 2, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> IteratorUtils.arrayListIterator(objArray, -1, 1));
        assertThrows(IllegalArgumentException.class, () -> IteratorUtils.arrayListIterator(objArray, 2, 1));

        final int[] intArray = { 0, 1, 2 };
        iterator = IteratorUtils.arrayListIterator(intArray);
        assertEquals(iterator.previousIndex(), -1);
        assertFalse(iterator.hasPrevious());
        assertEquals(0, iterator.nextIndex());
        assertEquals(0, iterator.next());
        assertEquals(0, iterator.previousIndex());
        assertEquals(1, iterator.nextIndex());
        assertEquals(1, iterator.next());
        assertEquals(1, iterator.previousIndex());
        assertEquals(2, iterator.nextIndex());
        assertEquals(1, iterator.previous());
        assertEquals(1, iterator.next());

        iterator = IteratorUtils.arrayListIterator(intArray, 1);
        assertEquals(-1, iterator.previousIndex());
        assertFalse(iterator.hasPrevious());
        assertEquals(0, iterator.nextIndex());
        assertEquals(1, iterator.next());
        assertEquals(1, iterator.previous());
        assertEquals(1, iterator.next());
        assertEquals(0, iterator.previousIndex());
        assertEquals(1, iterator.nextIndex());
        assertEquals(2, iterator.next());
        assertEquals(1, iterator.previousIndex());
        assertEquals(2, iterator.nextIndex());
        assertEquals(2, iterator.previous());
        assertEquals(0, iterator.previousIndex());
        assertEquals(1, iterator.nextIndex());

        assertThrows(IndexOutOfBoundsException.class, () -> IteratorUtils.arrayListIterator(intArray, -1),
                "Expecting IndexOutOfBoundsException");

        iterator = IteratorUtils.arrayListIterator(intArray, 3);
        assertFalse(iterator.hasNext());

        assertThrows(IndexOutOfBoundsException.class, () -> IteratorUtils.arrayListIterator(intArray, 4),
                "Expecting IndexOutOfBoundsException");

        iterator = IteratorUtils.arrayListIterator(intArray, 2, 3);
        assertFalse(iterator.hasPrevious());
        assertEquals(-1, iterator.previousIndex());
        assertEquals(2, iterator.next());
        assertTrue(iterator.hasPrevious());
        assertFalse(iterator.hasNext());

        assertThrows(IndexOutOfBoundsException.class, () -> IteratorUtils.arrayListIterator(intArray, 2, 4));
        assertThrows(IndexOutOfBoundsException.class, () -> IteratorUtils.arrayListIterator(intArray, -1, 1));
        assertThrows(IllegalArgumentException.class, () -> IteratorUtils.arrayListIterator(intArray, 2, 1));
    }

    @Test
    void testAsEnumerationNull() {
        assertThrows(NullPointerException.class, () -> IteratorUtils.asEnumeration(null));
    }

    @Test
    void testAsIterable() {
        final List<Integer> list = new ArrayList<>();
        list.add(Integer.valueOf(0));
        list.add(Integer.valueOf(1));
        list.add(Integer.valueOf(2));
        final Iterator<Integer> iterator = list.iterator();

        final Iterable<Integer> iterable = IteratorUtils.asIterable(iterator);
        int expected = 0;
        for (final Integer actual : iterable) {
            assertEquals(expected, actual.intValue());
            ++expected;
        }
        // insure iteration occurred
        assertTrue(expected > 0);

        // single use iterator
        assertFalse(IteratorUtils.asIterable(iterator).iterator().hasNext(), "should not be able to iterate twice");
    }

    @Test
    void testAsIterableNull() {
        assertThrows(NullPointerException.class, () -> IteratorUtils.asIterable(null));
    }

    @Test
    void testAsIterator() {
        final Vector<String> vector = new Vector<>();
        vector.addElement("zero");
        vector.addElement("one");
        final Enumeration<String> en = vector.elements();
        assertTrue(IteratorUtils.asIterator(en) instanceof Iterator, "create instance fail");
        assertThrows(NullPointerException.class, () -> IteratorUtils.asIterator(null));
    }

    @Test
    void testAsIteratorNull() {
        final Collection coll = new ArrayList();
        coll.add("test");
        final Vector<String> vector = new Vector<>();
        vector.addElement("test");
        vector.addElement("one");
        final Enumeration<String> en = vector.elements();
        assertTrue(IteratorUtils.asIterator(en, coll) instanceof Iterator, "create instance fail");

        assertThrows(NullPointerException.class, () -> IteratorUtils.asIterator(null, coll));
        assertThrows(NullPointerException.class, () -> IteratorUtils.asIterator(en, null));
    }

    @Test
    void testAsMultipleIterable() {
        final List<Integer> list = new ArrayList<>();
        list.add(Integer.valueOf(0));
        list.add(Integer.valueOf(1));
        list.add(Integer.valueOf(2));
        final Iterator<Integer> iterator = list.iterator();

        final Iterable<Integer> iterable = IteratorUtils.asMultipleUseIterable(iterator);
        int expected = 0;
        for (final Integer actual : iterable) {
            assertEquals(expected, actual.intValue());
            ++expected;
        }
        // insure iteration occurred
        assertTrue(expected > 0);

        // multiple use iterator
        expected = 0;
        for (final Integer actual : iterable) {
            assertEquals(expected, actual.intValue());
            ++expected;
        }
        // insure iteration occurred
        assertTrue(expected > 0);
    }

    @Test
    void testAsMultipleIterableNull() {
        assertThrows(NullPointerException.class, () -> IteratorUtils.asMultipleUseIterable(null));
    }

    @Test
    void testChainedIteratorArrayOfIterator() {
        // String
        final IteratorChainTest iteratorChainTest = new IteratorChainTest();
        iteratorChainTest.setUp();
        // @formatter:off
        final Iterator<String> iterator = IteratorUtils.chainedIterator(
                iteratorChainTest.getList1().iterator(),
                iteratorChainTest.getList2().iterator(),
                iteratorChainTest.getList3().iterator());
        // @formatter:on
        assertEquals("One", iterator.next());
        assertEquals("Two", iterator.next());
        assertEquals("Three", iterator.next());
        assertEquals("Four", iterator.next());
        assertEquals("Five", iterator.next());
        assertEquals("Six", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void testChainedIteratorList() {
        final IteratorChainTest iteratorChainTest = new IteratorChainTest();
        iteratorChainTest.setUp();
        final List<Iterator<String>> list = new ArrayList<>();
        list.add(iteratorChainTest.getList1().iterator());
        list.add(iteratorChainTest.getList2().iterator());
        list.add(iteratorChainTest.getList3().iterator());
        final List<String> expected = new ArrayList<>(iteratorChainTest.getList1());
        expected.addAll(iteratorChainTest.getList2());
        expected.addAll(iteratorChainTest.getList3());
        final Iterator<String> iter = IteratorUtils.chainedIterator(list);
        final List<String> actual = new ArrayList<>();
        iter.forEachRemaining(actual::add);
        assertEquals(actual, expected);
    }

    @Test
    void testChainedIteratorOfIterators() {
        final List<List<Number>> lst = new ArrayList<>();
        final List<Integer> iList = Arrays.asList(1, 3);
        lst.add(Arrays.asList(3.14f, Math.sqrt(2.0)));
        final Iterator<Iterator<Number>> toBeUnwound = new Iterator<Iterator<Number>>() {
            List<List<Number>> lst = Arrays.asList(Arrays.asList(1, 3), Arrays.asList(3.14F, Math.sqrt(2.0)));
            Iterator<List<Number>> lstIter = lst.iterator();

            @Override
            public boolean hasNext() {
                return lstIter.hasNext();
            }

            @Override
            public Iterator<Number> next() {
                return lstIter.next().iterator();
            }
        };

        final List<Number> expected = Arrays.asList(1, 3, 3.14f, Math.sqrt(2.0));
        final Iterator<Number> iter = IteratorUtils.chainedIterator(toBeUnwound);
        final List<Number> actual = new ArrayList<>();
        iter.forEachRemaining(actual::add);
        assertEquals(actual, expected);
    }

    @Test
    void testChainedIteratorRawGenerics() {
        final ArrayList arrayList = new ArrayList();
        final Iterator iterator = arrayList.iterator();
        assertTrue(IteratorUtils.chainedIterator(iterator) instanceof Iterator, "create instance fail");
        final Collection<Iterator<?>> coll = new ArrayList();
        assertTrue(IteratorUtils.chainedIterator(coll) instanceof Iterator, "create instance fail");
    }

    /**
     * Tests methods collatedIterator(...)
     */
    @Test
    void testCollatedIterator() {

        assertThrows(NullPointerException.class, () -> IteratorUtils.collatedIterator(null, collectionOdd.iterator(), null));
        assertThrows(NullPointerException.class, () -> IteratorUtils.collatedIterator(null, null, collectionEven.iterator()));

        // natural ordering
        Iterator<Integer> it = IteratorUtils.collatedIterator(null, collectionOdd.iterator(),
                collectionEven.iterator());

        List<Integer> result = IteratorUtils.toList(it);
        assertEquals(12, result.size());

        final List<Integer> combinedList = new ArrayList<>(collectionOdd);
        combinedList.addAll(collectionEven);
        Collections.sort(combinedList);

        assertEquals(combinedList, result);

        it = IteratorUtils.collatedIterator(null, collectionOdd.iterator(), emptyCollection.iterator());
        result = IteratorUtils.toList(it);
        assertEquals(collectionOdd, result);

        final Comparator<Integer> reverseComparator = ComparatorUtils
                .reversedComparator(ComparatorUtils.<Integer>naturalComparator());

        Collections.reverse(collectionOdd);
        Collections.reverse(collectionEven);
        Collections.reverse(combinedList);

        it = IteratorUtils.collatedIterator(reverseComparator, collectionOdd.iterator(), collectionEven.iterator());
        result = IteratorUtils.toList(it);
        assertEquals(combinedList, result);
    }

    @Test
    void testCollatedIteratorCollectionNull() {
        final Collection<Iterator<?>> coll = new ArrayList<>();
        coll.add(collectionOdd.iterator());
        // natural ordering
        final Iterator<?> it = IteratorUtils.collatedIterator(null, coll);
        final List<?> result = IteratorUtils.toList(it);
        assertEquals(6, result.size());
        assertThrows(NullPointerException.class, () -> IteratorUtils.collatedIterator(null, (Collection<Iterator<?>>) null));
    }

    @Test
    void testCollatedIteratorNull() {
        final ArrayList arrayList = new ArrayList();
        // natural ordering
        Iterator<Integer> it = IteratorUtils.collatedIterator(null, collectionOdd.iterator(), collectionOdd.iterator(),
                collectionOdd.iterator());

        List<Integer> result = IteratorUtils.toList(it);
        assertEquals(18, result.size());

        it = IteratorUtils.collatedIterator(null, collectionOdd.iterator());
        result = IteratorUtils.toList(it);
        assertEquals(collectionOdd, result);

        final Comparator<Integer> reverseComparator = ComparatorUtils
                .reversedComparator(ComparatorUtils.<Integer>naturalComparator());

        Collections.reverse(collectionOdd);

        it = IteratorUtils.collatedIterator(reverseComparator, collectionOdd.iterator());
        result = IteratorUtils.toList(it);
        assertEquals(collectionOdd, result);
        assertThrows(NullPointerException.class, () -> IteratorUtils.collatedIterator(null, arrayList.iterator(), arrayList.listIterator(), null));
    }

    /**
     * Test empty iterator
     */
    @Test
    void testEmptyIterator() {
        assertSame(EmptyIterator.INSTANCE, IteratorUtils.EMPTY_ITERATOR);
        assertSame(EmptyIterator.RESETTABLE_INSTANCE, IteratorUtils.EMPTY_ITERATOR);
        assertTrue(IteratorUtils.EMPTY_ITERATOR instanceof Iterator);
        assertTrue(IteratorUtils.EMPTY_ITERATOR instanceof ResettableIterator);
        assertFalse(IteratorUtils.EMPTY_ITERATOR instanceof OrderedIterator);
        assertFalse(IteratorUtils.EMPTY_ITERATOR instanceof ListIterator);
        assertFalse(IteratorUtils.EMPTY_ITERATOR instanceof MapIterator);
        assertFalse(IteratorUtils.EMPTY_ITERATOR.hasNext());
        IteratorUtils.EMPTY_ITERATOR.reset();
        assertSame(IteratorUtils.EMPTY_ITERATOR, IteratorUtils.EMPTY_ITERATOR);
        assertSame(IteratorUtils.EMPTY_ITERATOR, IteratorUtils.emptyIterator());

        assertThrows(NoSuchElementException.class, () -> IteratorUtils.EMPTY_ITERATOR.next());
        assertThrows(IllegalStateException.class, () -> IteratorUtils.EMPTY_ITERATOR.remove());
    }

    /**
     * Test empty list iterator
     */
    @Test
    void testEmptyListIterator() {
        assertSame(EmptyListIterator.INSTANCE, IteratorUtils.EMPTY_LIST_ITERATOR);
        assertSame(EmptyListIterator.RESETTABLE_INSTANCE, IteratorUtils.EMPTY_LIST_ITERATOR);
        assertTrue(IteratorUtils.EMPTY_LIST_ITERATOR instanceof Iterator);
        assertTrue(IteratorUtils.EMPTY_LIST_ITERATOR instanceof ListIterator);
        assertTrue(IteratorUtils.EMPTY_LIST_ITERATOR instanceof ResettableIterator);
        assertTrue(IteratorUtils.EMPTY_LIST_ITERATOR instanceof ResettableListIterator);
        assertFalse(IteratorUtils.EMPTY_LIST_ITERATOR instanceof MapIterator);
        assertFalse(IteratorUtils.EMPTY_LIST_ITERATOR.hasNext());
        assertEquals(0, IteratorUtils.EMPTY_LIST_ITERATOR.nextIndex());
        assertEquals(-1, IteratorUtils.EMPTY_LIST_ITERATOR.previousIndex());
        IteratorUtils.EMPTY_LIST_ITERATOR.reset();
        assertSame(IteratorUtils.EMPTY_LIST_ITERATOR, IteratorUtils.EMPTY_LIST_ITERATOR);
        assertSame(IteratorUtils.EMPTY_LIST_ITERATOR, IteratorUtils.emptyListIterator());

        assertThrows(NoSuchElementException.class, () -> IteratorUtils.EMPTY_LIST_ITERATOR.next());
        assertThrows(NoSuchElementException.class, () -> IteratorUtils.EMPTY_LIST_ITERATOR.previous());
        assertThrows(IllegalStateException.class, () -> IteratorUtils.EMPTY_LIST_ITERATOR.remove());
        assertThrows(IllegalStateException.class, () -> IteratorUtils.emptyListIterator().set(null));
        assertThrows(UnsupportedOperationException.class, () -> IteratorUtils.emptyListIterator().add(null));
    }

    /**
     * Test empty map iterator
     */
    @Test
    @SuppressWarnings("unchecked")
    void testEmptyMapIterator() {
        assertSame(EmptyMapIterator.INSTANCE, IteratorUtils.EMPTY_MAP_ITERATOR);
        assertTrue(IteratorUtils.EMPTY_MAP_ITERATOR instanceof Iterator);
        assertTrue(IteratorUtils.EMPTY_MAP_ITERATOR instanceof MapIterator);
        assertTrue(IteratorUtils.EMPTY_MAP_ITERATOR instanceof ResettableIterator);
        assertFalse(IteratorUtils.EMPTY_MAP_ITERATOR instanceof ListIterator);
        assertFalse(IteratorUtils.EMPTY_MAP_ITERATOR instanceof OrderedIterator);
        assertFalse(IteratorUtils.EMPTY_MAP_ITERATOR instanceof OrderedMapIterator);
        assertFalse(IteratorUtils.EMPTY_MAP_ITERATOR.hasNext());
        ((ResettableIterator<Object>) IteratorUtils.EMPTY_MAP_ITERATOR).reset();
        assertSame(IteratorUtils.EMPTY_MAP_ITERATOR, IteratorUtils.EMPTY_MAP_ITERATOR);
        assertSame(IteratorUtils.EMPTY_MAP_ITERATOR, IteratorUtils.emptyMapIterator());

        assertThrows(NoSuchElementException.class, () -> IteratorUtils.EMPTY_MAP_ITERATOR.next());
        assertThrows(IllegalStateException.class, () -> IteratorUtils.EMPTY_MAP_ITERATOR.remove());
        assertThrows(IllegalStateException.class, () -> IteratorUtils.EMPTY_MAP_ITERATOR.getKey());
        assertThrows(IllegalStateException.class, () -> IteratorUtils.EMPTY_MAP_ITERATOR.getValue());
        assertThrows(IllegalStateException.class, () -> IteratorUtils.EMPTY_MAP_ITERATOR.setValue(null));
    }

    /**
     * Test empty map iterator
     */
    @Test
    @SuppressWarnings("unchecked")
    void testEmptyOrderedIterator() {
        assertSame(EmptyOrderedIterator.INSTANCE, IteratorUtils.EMPTY_ORDERED_ITERATOR);
        assertTrue(IteratorUtils.EMPTY_ORDERED_ITERATOR instanceof Iterator);
        assertTrue(IteratorUtils.EMPTY_ORDERED_ITERATOR instanceof OrderedIterator);
        assertTrue(IteratorUtils.EMPTY_ORDERED_ITERATOR instanceof ResettableIterator);
        assertFalse(IteratorUtils.EMPTY_ORDERED_ITERATOR instanceof ListIterator);
        assertFalse(IteratorUtils.EMPTY_ORDERED_ITERATOR instanceof MapIterator);
        assertFalse(IteratorUtils.EMPTY_ORDERED_ITERATOR.hasNext());
        assertFalse(IteratorUtils.EMPTY_ORDERED_ITERATOR.hasPrevious());
        ((ResettableIterator<Object>) IteratorUtils.EMPTY_ORDERED_ITERATOR).reset();
        assertSame(IteratorUtils.EMPTY_ORDERED_ITERATOR, IteratorUtils.EMPTY_ORDERED_ITERATOR);
        assertSame(IteratorUtils.EMPTY_ORDERED_ITERATOR, IteratorUtils.emptyOrderedIterator());

        assertThrows(NoSuchElementException.class, () -> IteratorUtils.EMPTY_ORDERED_ITERATOR.next());
        assertThrows(NoSuchElementException.class, () -> IteratorUtils.EMPTY_ORDERED_ITERATOR.previous());
        assertThrows(IllegalStateException.class, () -> IteratorUtils.EMPTY_ORDERED_ITERATOR.remove());
    }

    /**
     * Test empty map iterator
     */
    @Test
    @SuppressWarnings("unchecked")
    void testEmptyOrderedMapIterator() {
        assertSame(EmptyOrderedMapIterator.INSTANCE, IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR);
        assertTrue(IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR instanceof Iterator);
        assertTrue(IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR instanceof MapIterator);
        assertTrue(IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR instanceof OrderedMapIterator);
        assertTrue(IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR instanceof ResettableIterator);
        assertFalse(IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR instanceof ListIterator);
        assertFalse(IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR.hasNext());
        assertFalse(IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR.hasPrevious());
        ((ResettableIterator<Object>) IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR).reset();
        assertSame(IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR, IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR);
        assertSame(IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR, IteratorUtils.emptyOrderedMapIterator());

        assertThrows(NoSuchElementException.class, () -> IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR.next());
        assertThrows(NoSuchElementException.class, () -> IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR.previous());
        assertThrows(IllegalStateException.class, () -> IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR.remove());
        assertThrows(IllegalStateException.class, () -> IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR.getKey());
        assertThrows(IllegalStateException.class, () -> IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR.getValue());
        assertThrows(IllegalStateException.class, () -> IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR.setValue(null));
    }

    @Test
    void testFilteredIterator() {
        final ArrayList arrayList = new ArrayList();
        final Iterator iterator = arrayList.iterator();

        assertThrows(NullPointerException.class, () -> IteratorUtils.filteredIterator(iterator, null));
        assertThrows(NullPointerException.class, () -> IteratorUtils.filteredIterator(null, null));
    }

    @Test
    void testFilteredListIterator() {
        final List arrayList = new ArrayList();
        arrayList.add("test");
        final Predicate predicate = TruePredicate.INSTANCE;
        assertTrue(IteratorUtils.filteredListIterator(arrayList.listIterator(), predicate) instanceof ListIterator,
                "create instance fail");

        assertThrows(NullPointerException.class, () -> IteratorUtils.filteredListIterator(null, predicate));
        assertThrows(NullPointerException.class, () -> IteratorUtils.filteredListIterator(arrayList.listIterator(), null));
    }

    @Test
    void testFind() {
        Predicate<Number> testPredicate = EqualPredicate.equalPredicate((Number) 4);
        Integer test = IteratorUtils.find(iterableA.iterator(), testPredicate);
        assertEquals(4, (int) test);
        testPredicate = EqualPredicate.equalPredicate((Number) 45);
        test = IteratorUtils.find(iterableA.iterator(), testPredicate);
        assertNull(test);
        assertNull(IteratorUtils.find(null, testPredicate));

        assertThrows(NullPointerException.class, () -> IteratorUtils.find(iterableA.iterator(), null));
    }

    @Test
    void testFirstFromIterator() throws Exception {
        // Iterator, entry exists
        final Iterator<Integer> iterator = iterableA.iterator();
        assertEquals(1, (int) IteratorUtils.first(iterator));
    }

    @Test
    void testForEach() {
        final List<Integer> listA = new ArrayList<>();
        listA.add(1);

        final List<Integer> listB = new ArrayList<>();
        listB.add(2);

        final Closure<List<Integer>> testClosure = ClosureUtils.invokerClosure("clear");
        final Collection<List<Integer>> col = new ArrayList<>();
        col.add(listA);
        col.add(listB);
        IteratorUtils.forEach(col.iterator(), testClosure);
        assertTrue(listA.isEmpty() && listB.isEmpty());

        assertThrows(NullPointerException.class, () -> IteratorUtils.forEach(col.iterator(), null));

        IteratorUtils.forEach(null, testClosure);

        // null should be OK
        col.add(null);
        IteratorUtils.forEach(col.iterator(), testClosure);
    }

    @Test
    void testForEachButLast() {
        final List<Integer> listA = new ArrayList<>();
        listA.add(1);

        final List<Integer> listB = new ArrayList<>();
        listB.add(2);

        final Closure<List<Integer>> testClosure = ClosureUtils.invokerClosure("clear");
        final Collection<List<Integer>> col = new ArrayList<>();
        col.add(listA);
        col.add(listB);
        List<Integer> last = IteratorUtils.forEachButLast(col.iterator(), testClosure);
        assertTrue(listA.isEmpty() && !listB.isEmpty());
        assertSame(listB, last);

        assertThrows(NullPointerException.class, () -> IteratorUtils.forEachButLast(col.iterator(), null));

        IteratorUtils.forEachButLast(null, testClosure);

        // null should be OK
        col.add(null);
        col.add(null);
        last = IteratorUtils.forEachButLast(col.iterator(), testClosure);
        assertNull(last);
    }

    @Test
    void testGetAtIndexFromIterator() throws Exception {
        // Iterator, entry exists
        Iterator<Integer> iterator = iterableA.iterator();
        assertEquals(1, (int) IteratorUtils.get(iterator, 0));
        iterator = iterableA.iterator();
        assertEquals(2, (int) IteratorUtils.get(iterator, 1));
        // Iterator, non-existent entry
        final Iterator<Integer> finalIterator = iterator;
        assertThrows(IndexOutOfBoundsException.class, () -> IteratorUtils.get(finalIterator, 10));
        assertFalse(iterator.hasNext());
    }

    @Test
    void testGetAtIndexFromIteratorDefault() throws Exception {
        // Iterator, entry exists
        Iterator<Integer> iterator = iterableA.iterator();
        assertEquals(1, (int) IteratorUtils.get(iterator, 0, i -> 0));
        iterator = iterableA.iterator();
        assertEquals(2, (int) IteratorUtils.get(iterator, 1, i -> 0));
        // Iterator, non-existent entry
        assertEquals(111, (int) IteratorUtils.get(iterator, 10, i -> 111));
        assertFalse(iterator.hasNext());
    }

    @Test
    void testGetIterator() {
        final Object[] objArray = { "a", "b", "c" };
        final Map<String, String> inMap = new HashMap<>();
        final Node[] nodes = createNodes();
        final NodeList nodeList = createNodeList(nodes);

        assertTrue(IteratorUtils.getIterator(null) instanceof EmptyIterator, "returns empty iterator when null passed");
        assertTrue(IteratorUtils.getIterator(iterableA.iterator()) instanceof Iterator, "returns Iterator when Iterator directly ");
        assertTrue(IteratorUtils.getIterator(iterableA) instanceof Iterator, "returns Iterator when iterable passed");
        assertTrue(IteratorUtils.getIterator(objArray) instanceof ObjectArrayIterator,
                "returns ObjectArrayIterator when Object array passed");
        assertTrue(IteratorUtils.getIterator(inMap) instanceof Iterator, "returns Iterator when Map passed");
        assertTrue(IteratorUtils.getIterator(nodeList) instanceof NodeListIterator, "returns NodeListIterator when nodeList passed");
        assertTrue(IteratorUtils.getIterator(new Vector().elements()) instanceof EnumerationIterator,
                "returns EnumerationIterator when Enumeration passed");
        final Node node1 = createMock(Node.class);
        assertTrue(IteratorUtils.getIterator(node1) instanceof NodeListIterator,
                "returns NodeListIterator when nodeList passed");
        final Dictionary dic = createMock(Dictionary.class);
        assertTrue(IteratorUtils.getIterator(dic) instanceof EnumerationIterator,
                "returns EnumerationIterator when Dictionary passed");
        final int[] arr = new int[8];
        assertTrue(IteratorUtils.getIterator(arr) instanceof ArrayIterator, "returns ArrayIterator when array passed");
    }

    @Test
    void testIndexOf() {
        Predicate<Number> testPredicate = EqualPredicate.equalPredicate((Number) 4);
        int index = IteratorUtils.indexOf(iterableA.iterator(), testPredicate);
        assertEquals(6, index);
        testPredicate = EqualPredicate.equalPredicate((Number) 45);
        index = IteratorUtils.indexOf(iterableA.iterator(), testPredicate);
        assertEquals(-1, index);
        assertEquals(-1, IteratorUtils.indexOf(null, testPredicate));

        assertThrows(NullPointerException.class, () -> IteratorUtils.indexOf(iterableA.iterator(), null));
    }

    @Test
    void testLoopingIterator() {
        final ArrayList arrayList = new ArrayList();
        arrayList.add("test");
        final Collection coll = new ArrayList();
        coll.add("test");
        final Iterator iterator = arrayList.iterator();
        assertTrue(IteratorUtils.loopingIterator(coll) instanceof ResettableIterator, "create instance fail");
        assertThrows(NullPointerException.class, () -> IteratorUtils.loopingIterator(null));
    }

    @Test
    void testLoopingListIterator() {
        final ArrayList arrayList = new ArrayList();
        arrayList.add("test");
        final Iterator iterator = arrayList.iterator();
        assertTrue(IteratorUtils.loopingListIterator(arrayList) instanceof ResettableIterator, "create instance fail");
        assertThrows(NullPointerException.class, () -> IteratorUtils.loopingListIterator(null));
    }

    /**
     * Tests method nodeListIterator(Node)
     */
    @Test
    void testNodeIterator() {
        final Node[] nodes = createNodes();
        final NodeList nodeList = createNodeList(nodes);
        final Node parentNode = createMock(Node.class);
        expect(parentNode.getChildNodes()).andStubReturn(nodeList);
        replay(parentNode);

        final Iterator<Node> iterator = IteratorUtils.nodeListIterator(parentNode);
        int expectedNodeIndex = 0;
        for (final Node actual : IteratorUtils.asIterable(iterator)) {
            assertEquals(nodes[expectedNodeIndex], actual);
            ++expectedNodeIndex;
        }

        // insure iteration occurred
        assertTrue(expectedNodeIndex > 0);

        // single use iterator
        assertFalse(IteratorUtils.asIterable(iterator).iterator().hasNext(), "should not be able to iterate twice");

        assertThrows(NullPointerException.class, () -> IteratorUtils.nodeListIterator((Node) null));
    }

    /**
     * Tests method nodeListIterator(NodeList)
     */
    @Test
    void testNodeListIterator() {
        final Node[] nodes = createNodes();
        final NodeList nodeList = createNodeList(nodes);

        final Iterator<Node> iterator = IteratorUtils.nodeListIterator(nodeList);
        int expectedNodeIndex = 0;
        for (final Node actual : IteratorUtils.asIterable(iterator)) {
            assertEquals(nodes[expectedNodeIndex], actual);
            ++expectedNodeIndex;
        }

        // insure iteration occurred
        assertTrue(expectedNodeIndex > 0);

        // single use iterator
        assertFalse(IteratorUtils.asIterable(iterator).iterator().hasNext(), "should not be able to iterate twice");

        assertThrows(NullPointerException.class, () -> IteratorUtils.nodeListIterator((NodeList) null));
    }

    @Test
    void testObjectGraphIterator() {
        assertTrue(IteratorUtils.objectGraphIterator(null, null) instanceof Iterator, "create instance fail");
    }

    @Test
    void testPeekingIterator() {
        final ArrayList arrayList = new ArrayList();
        final Iterator iterator = arrayList.iterator();
        assertTrue(IteratorUtils.peekingIterator(iterator) instanceof Iterator, "create instance fail");
        assertThrows(NullPointerException.class, () -> IteratorUtils.peekingIterator(null));
    }

    @Test
    void testPushBackIterator() {
        final ArrayList arrayList = new ArrayList();
        final Iterator iterator = arrayList.iterator();
        assertTrue(IteratorUtils.pushbackIterator(iterator) instanceof Iterator, "create instance fail");
        assertThrows(NullPointerException.class, () -> IteratorUtils.pushbackIterator(null));
    }

    @Test
    void testSingletonIterator() {
        assertTrue(IteratorUtils.singletonIterator(new Object()) instanceof ResettableIterator, "create instance fail");
    }

    @Test
    void testSingletonListIterator() {
        assertTrue(IteratorUtils.singletonListIterator(new Object()) instanceof Iterator, "create instance fail");
    }

    @Test
    void testToArray() {
        final List<Object> list = new ArrayList<>();
        list.add(Integer.valueOf(1));
        list.add("Two");
        list.add(null);
        final Object[] result = IteratorUtils.toArray(list.iterator());
        assertEquals(list, Arrays.asList(result));

        assertThrows(NullPointerException.class, () -> IteratorUtils.toArray(null));
    }

    @Test
    void testToArray2() {
        final List<String> list = new ArrayList<>();
        list.add("One");
        list.add("Two");
        list.add(null);
        final String[] result = IteratorUtils.toArray(list.iterator(), String.class);
        assertEquals(list, Arrays.asList(result));

        assertThrows(NullPointerException.class, () -> IteratorUtils.toArray(list.iterator(), null));
        assertThrows(NullPointerException.class, () -> IteratorUtils.toArray(null, String.class));
    }

    @Test
    void testToList() {
        final List<Object> list = new ArrayList<>();
        list.add(Integer.valueOf(1));
        list.add("Two");
        list.add(null);
        final List<Object> result = IteratorUtils.toList(list.iterator());
        assertEquals(list, result);
        // add

        assertThrows(NullPointerException.class, () -> IteratorUtils.toList(null, 10));
        assertThrows(IllegalArgumentException.class, () -> IteratorUtils.toList(list.iterator(), -1));
    }

    @Test
    void testToListIterator() {
        final List<Integer> list = new ArrayList<>();
        list.add(Integer.valueOf(0));
        list.add(Integer.valueOf(1));
        list.add(Integer.valueOf(2));
        final Iterator<Integer> iterator = list.iterator();

        final ListIterator<Integer> liItr = IteratorUtils.toListIterator(iterator);
        int expected = 0;
        while (liItr.hasNext()) {
            assertEquals(expected, liItr.next().intValue());
            ++expected;
        }
    }

    @Test
    void testToListIteratorNull() {
        assertThrows(NullPointerException.class, () -> IteratorUtils.toListIterator(null));
    }

    @Test
    void testToSet() {
        final Set<Object> set = new HashSet<>();
        set.add(Integer.valueOf(1));
        set.add("Two");
        set.add(null);
        final Set<Object> result = IteratorUtils.toSet(set.iterator());
        assertEquals(set, result);
        assertThrows(NullPointerException.class, () -> IteratorUtils.toSet(null, 10));
        assertThrows(IllegalArgumentException.class, () -> IteratorUtils.toSet(set.iterator(), -1));
    }

    @Test
    void testTransformedIterator() {
        final ArrayList arrayList = new ArrayList();
        final Iterator iterator = arrayList.iterator();

        assertThrows(NullPointerException.class, () -> IteratorUtils.transformedIterator(iterator, null));
        assertThrows(NullPointerException.class, () -> IteratorUtils.transformedIterator(null, null));
    }

    /**
     * Test remove() for an immutable Iterator.
     */
    @Test
    void testUnmodifiableIteratorImmutability() {
        final Iterator<String> iterator = getImmutableIterator();

        assertThrows(UnsupportedOperationException.class, () -> iterator.remove());

        iterator.next();

        assertThrows(UnsupportedOperationException.class, () -> iterator.remove());
    }

    /**
     * Test next() and hasNext() for an immutable Iterator.
     */
    @Test
    void testUnmodifiableIteratorIteration() {
        final Iterator<String> iterator = getImmutableIterator();

        assertTrue(iterator.hasNext());

        assertEquals("a", iterator.next());

        assertTrue(iterator.hasNext());

        assertEquals("b", iterator.next());

        assertTrue(iterator.hasNext());

        assertEquals("c", iterator.next());

        assertTrue(iterator.hasNext());

        assertEquals("d", iterator.next());

        assertFalse(iterator.hasNext());
    }

    /**
     * Test remove() for an immutable ListIterator.
     */
    @Test
    void testUnmodifiableListIteratorImmutability() {
        final ListIterator<String> listIterator = getImmutableListIterator();

        assertThrows(UnsupportedOperationException.class, () -> listIterator.remove());
        assertThrows(UnsupportedOperationException.class, () -> listIterator.set("a"));
        assertThrows(UnsupportedOperationException.class, () -> listIterator.add("a"));

        listIterator.next();

        assertThrows(UnsupportedOperationException.class, () -> listIterator.remove());
        assertThrows(UnsupportedOperationException.class, () -> listIterator.set("a"));
        assertThrows(UnsupportedOperationException.class, () -> listIterator.add("a"));
    }

    /**
     * Test next(), hasNext(), previous() and hasPrevious() for an immutable ListIterator.
     */
    @Test
    void testUnmodifiableListIteratorIteration() {
        final ListIterator<String> listIterator = getImmutableListIterator();

        assertFalse(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());

        assertEquals("a", listIterator.next());

        assertTrue(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());

        assertEquals("b", listIterator.next());

        assertTrue(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());

        assertEquals("c", listIterator.next());

        assertTrue(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());

        assertEquals("d", listIterator.next());

        assertTrue(listIterator.hasPrevious());
        assertFalse(listIterator.hasNext());

        assertEquals("d", listIterator.previous());

        assertTrue(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());

        assertEquals("c", listIterator.previous());

        assertTrue(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());

        assertEquals("b", listIterator.previous());

        assertTrue(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());

        assertEquals("a", listIterator.previous());

        assertFalse(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());
    }

    @Test
    void testUnmodifiableMapIterator() {
        final Set<?> set = new LinkedHashSet<>();
        final MapIterator iterator = new EntrySetToMapIteratorAdapter(set);
        assertTrue(IteratorUtils.unmodifiableMapIterator(iterator) instanceof MapIterator, "create instance fail");
        assertThrows(NullPointerException.class, () -> IteratorUtils.unmodifiableMapIterator(null));
    }

    @Test
    void testZippingIterator() {
        final ArrayList arrayList = new ArrayList();
        final Iterator iterator = arrayList.iterator();
        assertTrue(IteratorUtils.zippingIterator(iterator, iterator, iterator) instanceof ZippingIterator, "create instance fail");
        assertTrue(IteratorUtils.zippingIterator(iterator, iterator) instanceof ZippingIterator, "create instance fail");
    }

}

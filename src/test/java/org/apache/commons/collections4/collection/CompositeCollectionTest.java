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
package org.apache.commons.collections4.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractCollectionTest} for exercising the
 * {@link CompositeCollection} implementation.
 */
public class CompositeCollectionTest<E> extends AbstractCollectionTest<E> {

    protected CompositeCollection<E> c;

    protected Collection<E> one;

    protected Collection<E> two;

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @Override
    @SuppressWarnings("unchecked")
    public E[] getFullElements() {
        return (E[]) new Object[] { "1", "2", "3", "4" };
    }

    /**
     * Run stock collection tests without Mutator, so turn off add, remove
     */
    @Override
    public boolean isAddSupported() {
        return false;
    }

    @Override
    public boolean isRemoveSupported() {
        return false;
    }

    @Override
    public Collection<E> makeConfirmedCollection() {
        return new HashSet<>();
    }

    /**
     * Full collection should look like a collection with 4 elements
     */
    @Override
    public Collection<E> makeConfirmedFullCollection() {
        return new HashSet<>(Arrays.asList(getFullElements()));
    }
    /**
     * Full collection consists of 4 collections, each with one element
     */
    @Override
    public Collection<E> makeFullCollection() {
        final CompositeCollection<E> compositeCollection = new CompositeCollection<>();
        final E[] elements = getFullElements();
        for (final E element : elements) {
            final Collection<E> summand = new HashSet<>();
            summand.add(element);
            compositeCollection.addComposited(summand);
        }
        return compositeCollection;
    }
    /**
     * Empty collection is empty composite
     */
    @Override
    public Collection<E> makeObject() {
        return new CompositeCollection<>();
    }

    protected void setUpMutatorTest() {
        setUpTest();
        c.setMutator(new CompositeCollection.CollectionMutator<E>() {

            private static final long serialVersionUID = 1L;

            @Override
            public boolean add(final CompositeCollection<E> composite, final List<Collection<E>> collections, final E obj) {
                for (final Collection<E> coll : collections) {
                    coll.add(obj);
                }
                return true;
            }

            @Override
            public boolean addAll(final CompositeCollection<E> composite,
                    final List<Collection<E>> collections, final Collection<? extends E> coll) {
                for (final Collection<E> collection : collections) {
                    collection.addAll(coll);
                }
                return true;
            }

            @Override
            public boolean remove(final CompositeCollection<E> composite,
                    final List<Collection<E>> collections, final Object obj) {
                for (final Collection<E> collection : collections) {
                    collection.remove(obj);
                }
                return true;
            }
        });
    }

    protected void setUpTest() {
        c = new CompositeCollection<>();
        one = new HashSet<>();
        two = new HashSet<>();
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    void testAddAllMutator() {
        setUpTest();
        c.setMutator(new CompositeCollection.CollectionMutator<E>() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean add(final CompositeCollection<E> composite,
                    final List<Collection<E>> collections, final E obj) {
                for (final Collection<E> collection : collections) {
                    collection.add(obj);
                }
                return true;
            }

            @Override
            public boolean addAll(final CompositeCollection<E> composite,
                    final List<Collection<E>> collections, final Collection<? extends E> coll) {
                for (final Collection<E> collection : collections) {
                    collection.addAll(coll);
                }
                return true;
            }

            @Override
            public boolean remove(final CompositeCollection<E> composite,
                    final List<Collection<E>> collections, final Object obj) {
                return false;
            }
        });

        c.addComposited(one);
        two.add((E) "foo");
        c.addAll(two);
        assertTrue(c.contains("foo"));
        assertTrue(one.contains("foo"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testAddAllToCollection() {
        setUpTest();
        one.add((E) "1");
        two.add((E) "2");
        c.addComposited(one, two);
        final Collection<E> toCollection = new HashSet<>(c);
        assertTrue(toCollection.containsAll(c));
        assertEquals(c.size(), toCollection.size());
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    void testAddMutator() {
        setUpTest();
        c.setMutator(new CompositeCollection.CollectionMutator<E>() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean add(final CompositeCollection<E> composite,
                    final List<Collection<E>> collections, final E obj) {
                for (final Collection<E> collection : collections) {
                    collection.add(obj);
                }
                return true;
            }

            @Override
            public boolean addAll(final CompositeCollection<E> composite,
                    final List<Collection<E>> collections, final Collection<? extends E> coll) {
                for (final Collection<E> collection : collections) {
                    collection.addAll(coll);
                }
                return true;
            }

            @Override
            public boolean remove(final CompositeCollection<E> composite,
                    final List<Collection<E>> collections, final Object obj) {
                return false;
            }
        });

        c.addComposited(one);
        c.add((E) "foo");
        assertTrue(c.contains("foo"));
        assertTrue(one.contains("foo"));
    }

    @Test
    void testAddNullList() {
        final ArrayList<String> nullList = null;
        final CompositeCollection<String> cc = new CompositeCollection<>();
        cc.addComposited(nullList);
        assertEquals(0, cc.size());
    }

    @Test
    void testAddNullLists2Args() {
        final ArrayList<String> nullList = null;
        final CompositeCollection<String> cc = new CompositeCollection<>();
        cc.addComposited(nullList, nullList);
        assertEquals(0, cc.size());
    }

    @Test
    void testAddNullListsVarArgs() {
        final ArrayList<String> nullList = null;
        final CompositeCollection<String> cc = new CompositeCollection<>();
        cc.addComposited(nullList, nullList, nullList);
        assertEquals(0, cc.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testClear() {
        setUpTest();
        one.add((E) "1");
        two.add((E) "2");
        c.addComposited(one, two);
        c.clear();
        assertTrue(one.isEmpty());
        assertTrue(two.isEmpty());
        assertTrue(c.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testContainsAll() {
        setUpTest();
        one.add((E) "1");
        two.add((E) "1");
        c.addComposited(one);
        assertTrue(c.containsAll(two));
        assertFalse(c.containsAll(null));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testIsEmpty() {
        setUpTest();
        assertTrue(c.isEmpty());
        final HashSet<E> empty = new HashSet<>();
        c.addComposited(empty);
        assertTrue(c.isEmpty());
        empty.add((E) "a");
        assertFalse(c.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testIterator() {
        setUpTest();
        one.add((E) "1");
        two.add((E) "2");
        c.addComposited(one);
        c.addComposited(two);
        final Iterator<E> i = c.iterator();
        E next = i.next();
        assertTrue(c.contains(next));
        assertTrue(one.contains(next));
        next = i.next();
        i.remove();
        assertFalse(c.contains(next));
        assertFalse(two.contains(next));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testMultipleCollectionsSize() {
        setUpTest();
        final HashSet<E> set = new HashSet<>();
        set.add((E) "a");
        set.add((E) "b");
        c.addComposited(set);
        final HashSet<E> other = new HashSet<>();
        other.add((E) "c");
        c.addComposited(other);
        assertEquals(set.size() + other.size(), c.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRemove() {
        setUpMutatorTest();
        one.add((E) "1");
        two.add((E) "2");
        two.add((E) "1");
        c.addComposited(one, two);
        c.remove("1");
        assertFalse(c.contains("1"));
        assertFalse(one.contains("1"));
        assertFalse(two.contains("1"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRemoveAll() {
        setUpMutatorTest();
        one.add((E) "1");
        two.add((E) "2");
        two.add((E) "1");
        // need separate list to remove, as otherwise one clears itself
        final Collection<E> removing = new ArrayList<>(one);
        c.addComposited(one, two);
        c.removeAll(removing);
        assertFalse(c.contains("1"));
        assertFalse(one.contains("1"));
        assertFalse(two.contains("1"));
        c.removeAll(null);
        assertFalse(c.contains("1"));
        assertFalse(one.contains("1"));
        assertFalse(two.contains("1"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRemoveComposited() {
        setUpMutatorTest();
        one.add((E) "1");
        two.add((E) "2");
        two.add((E) "1");
        c.addComposited(one, two);
        c.removeComposited(one);
        assertTrue(c.contains("1"));
        assertEquals(2, c.size());
    }

    /**
     */
    @Test
    @SuppressWarnings("unchecked")
    void testRemoveIf() {
        setUpMutatorTest();
        one.add((E) "1");
        two.add((E) "2");
        two.add((E) "1");
        // need separate list to remove, as otherwise one clears itself
        final Predicate<E> predicate = "1"::equals;
        c.addComposited(one, two);
        c.removeIf(predicate);
        assertFalse(c.contains("1"));
        assertFalse(one.contains("1"));
        assertFalse(two.contains("1"));
        c.removeIf(null);
        assertFalse(c.contains("1"));
        assertFalse(one.contains("1"));
        assertFalse(two.contains("1"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRetainAll() {
        setUpTest();
        one.add((E) "1");
        one.add((E) "2");
        two.add((E) "1");
        c.addComposited(one);
        c.retainAll(two);
        assertFalse(c.contains("2"));
        assertFalse(one.contains("2"));
        assertTrue(c.contains("1"));
        assertTrue(one.contains("1"));
        c.retainAll(null);
        assertFalse(c.contains("2"));
        assertFalse(one.contains("2"));
        assertTrue(c.contains("1"));
        assertTrue(one.contains("1"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSize() {
        setUpTest();
        final HashSet<E> set = new HashSet<>();
        set.add((E) "a");
        set.add((E) "b");
        c.addComposited(set);
        assertEquals(set.size(), c.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testToCollection() {
        setUpTest();
        one.add((E) "1");
        two.add((E) "2");
        c.addComposited(one, two);
        final Collection<E> foo = c.toCollection();
        assertTrue(foo.containsAll(c));
        assertEquals(c.size(), foo.size());
        one.add((E) "3");
        assertFalse(foo.containsAll(c));
    }

    /**
     * Override testUnsupportedRemove, since the default impl expects removeAll,
     * retainAll and iterator().remove to throw
     */
    @Test
    @Override
    public void testUnsupportedRemove() {
        resetFull();

        assertThrows(UnsupportedOperationException.class, () -> getCollection().remove(null));

        verify();
    }

//    void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/CompositeCollection.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/CompositeCollection.fullCollection.version4.obj");
//    }

}

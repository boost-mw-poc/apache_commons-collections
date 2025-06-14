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
package org.apache.commons.collections4.set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.collection.TransformedCollectionTest;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractSetTest} for exercising the {@link TransformedSet}
 * implementation.
 */
public class TransformedSetTest<E> extends AbstractSetTest<E> {

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @Override
    protected int getIterationBehaviour() {
        return UNORDERED;
    }

    @Override
    public Set<E> makeConfirmedCollection() {
        return new HashSet<>();
    }

    @Override
    public Set<E> makeConfirmedFullCollection() {
        return new HashSet<>(Arrays.asList(getFullElements()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<E> makeFullCollection() {
        final Set<E> list = new HashSet<>(Arrays.asList(getFullElements()));
        return TransformedSet.transformingSet(list,
                (Transformer<E, E>) TransformedCollectionTest.NOOP_TRANSFORMER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<E> makeObject() {
        return TransformedSet.transformingSet(new HashSet<>(),
                (Transformer<E, E>) TransformedCollectionTest.NOOP_TRANSFORMER);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testTransformedSet() {
        final Set<E> set = TransformedSet.transformingSet(new HashSet<>(),
                (Transformer<E, E>) TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(0, set.size());
        final E[] els = (E[]) new Object[] { "1", "3", "5", "7", "2", "4", "6" };
        for (int i = 0; i < els.length; i++) {
            set.add(els[i]);
            assertEquals(i + 1, set.size());
            assertTrue(set.contains(Integer.valueOf((String) els[i])));
            assertFalse(set.contains(els[i]));
        }

        assertFalse(set.remove(els[0]));
        assertTrue(set.remove(Integer.valueOf((String) els[0])));

    }

    @Test
    void testTransformedSet_decorateTransform() {
        final Set<Object> originalSet = new HashSet<>();
        final Object[] els = {"1", "3", "5", "7", "2", "4", "6"};
        Collections.addAll(originalSet, els);
        final Set<?> set = TransformedSet.transformedSet(originalSet, TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(els.length, set.size());
        for (final Object el : els) {
            assertTrue(set.contains(Integer.valueOf((String) el)));
            assertFalse(set.contains(el));
        }

        assertFalse(set.remove(els[0]));
        assertTrue(set.remove(Integer.valueOf((String) els[0])));
    }

//    void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/TransformedSet.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/TransformedSet.fullCollection.version4.obj");
//    }

}

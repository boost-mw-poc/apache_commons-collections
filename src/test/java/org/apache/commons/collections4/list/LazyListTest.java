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
package org.apache.commons.collections4.list;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.AbstractObjectTest;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.Transformer;
import org.junit.jupiter.api.Test;

class LazyListTest extends AbstractObjectTest {

    @Override
    public Object makeObject() {
        final Factory<LocalDateTime> dateFactory = LocalDateTime::now;
        return new LazyList<>(new ArrayList<>(), dateFactory);
    }

    @Test
    @Override
    public void testCanonicalEmptyCollectionExists() {
        // Factory and Transformer are not serializable
    }

    @Test
    @Override
    public void testCanonicalFullCollectionExists() {
        // Factory and Transformer are not serializable
    }

    @Test
    void testCreateNullGapsWithFactory() {
        final Factory<LocalDateTime> dateFactory = LocalDateTime::now;
        final List<LocalDateTime> list = new LazyList<>(new ArrayList<>(), dateFactory);

        final LocalDateTime fourthElement = list.get(3);
        assertFalse(list.isEmpty());
        assertNotNull(fourthElement);
    }

    @Test
    void testCreateNullGapsWithTransformer() {
        final List<Integer> hours = Arrays.asList(7, 5, 8, 2);
        final Transformer<Integer, LocalDateTime> dateFactory = input -> LocalDateTime.now().withHour(hours.get(input));
        final List<LocalDateTime> list = new LazyList<>(new ArrayList<>(), dateFactory);

        final LocalDateTime fourthElement = list.get(3);
        assertFalse(list.isEmpty());
        assertNotNull(fourthElement);
    }

    @Test
    void testElementCreationWithFactory() {
        final Factory<LocalDateTime> dateFactory = LocalDateTime::now;
        final List<LocalDateTime> list = new LazyList<>(new ArrayList<>(), dateFactory);

        assertTrue(list.isEmpty());

        final LocalDateTime firstElement = list.get(0);
        assertNotNull(firstElement);
        assertFalse(list.isEmpty());
    }

    @Test
    void testElementCreationWithTransformer() {
        final Factory<LocalDateTime> dateFactory = LocalDateTime::now;
        final List<LocalDateTime> list = new LazyList<>(new ArrayList<>(), dateFactory);

        assertTrue(list.isEmpty());

        final LocalDateTime firstElement = list.get(0);
        assertNotNull(firstElement);
        assertFalse(list.isEmpty());
    }

    @Test
    void testGetWithNull() {
        final List<Integer> hours = Arrays.asList(7, 5, 8, 2);
        final Transformer<Integer, LocalDateTime> transformer = input -> LocalDateTime.now().withHour(hours.get(input));
        final List<LocalDateTime> list = new LazyList<>(new ArrayList<>(), transformer);
        LocalDateTime fourthElement = list.get(3);
        assertFalse(list.isEmpty());
        assertNotNull(fourthElement);
        list.remove(3);
        list.add(3, null);
        fourthElement = list.get(3);
        assertNotNull(fourthElement);
    }

    @Test
    @Override
    public void testSerializeDeserializeThenCompare() {
        // Factory and Transformer are not serializable
    }

    @Test
    @Override
    public void testSimpleSerialization() {
        // Factory and Transformer are not serializable
    }

    private void testSubList(final List<LocalDateTime> list) {
        List<LocalDateTime> subList = list.subList(1, 3);
        assertFalse(subList.isEmpty());
        assertNotNull(subList);
        assertEquals(2, subList.size());

        subList = list.subList(0, 1);
        assertFalse(subList.isEmpty());
        assertEquals(1, subList.size());

        subList = list.subList(1, 1);
        assertTrue(subList.isEmpty());

        subList = list.subList(0, list.size());
        assertFalse(subList.isEmpty());
        assertEquals(list.size(), subList.size());
    }

    @Test
    void testSubListWitheFactory() {
        final Factory<LocalDateTime> dateFactory = LocalDateTime::now;
        final List<LocalDateTime> list = new LazyList<>(new ArrayList<>(), dateFactory);
        final LocalDateTime fourthElement = list.get(3);
        assertFalse(list.isEmpty());
        assertNotNull(fourthElement);
        testSubList(list);
    }

    @Test
    void testSubListWithTransformer() {
        final List<Integer> hours = Arrays.asList(7, 5, 8, 2);
        final Transformer<Integer, LocalDateTime> transformer = input -> LocalDateTime.now().withHour(hours.get(input));
        final List<LocalDateTime> list = new LazyList<>(new ArrayList<>(), transformer);
        final LocalDateTime fourthElement = list.get(3);
        assertFalse(list.isEmpty());
        assertNotNull(fourthElement);
        testSubList(list);
    }

}

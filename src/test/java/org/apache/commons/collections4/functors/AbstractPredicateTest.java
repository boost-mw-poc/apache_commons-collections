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
package org.apache.commons.collections4.functors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.collections4.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractPredicateTest {

    protected Object cObject;
    protected String cString;
    protected Integer cInteger;

    protected <T> void assertPredicateFalse(final Predicate<T> predicate, final T testObject) {
        assertFalse(predicate.evaluate(testObject));
    }

    protected <T> void assertPredicateTrue(final Predicate<T> predicate, final T testObject) {
        assertTrue(predicate.evaluate(testObject));
    }

    /**
     * @return a predicate for general sanity tests.
     */
    protected abstract Predicate<?> generatePredicate();

    @BeforeEach
    public void initializeTestObjects() throws Exception {
        cObject = new Object();
        cString = "Hello";
        cInteger = Integer.valueOf(6);
    }

    @Test
    void testPredicateSanityTests() throws Exception {
        final Predicate<?> predicate = generatePredicate();
        assertNotNull(predicate);
    }

}

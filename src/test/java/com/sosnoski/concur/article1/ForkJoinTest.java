/*
 * Copyright (c) 2014, Dennis M. Sosnoski.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.sosnoski.concur.article1;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ForkJoinPool;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Basic tests for fork join implementation.
 */
public class ForkJoinTest
{
    private ForkJoinPool pool;
    
    @Before
    public void setUp() {
        pool = new ForkJoinPool();
    }
    
    @After
    public void tearDown() throws Exception {
        pool.shutdown();
    }
    
/*    @Test
    public void testList() {
        assertEquals(new DistancePair(0, "teachers"), runTest("teachers"));
        assertEquals(new DistancePair(1, "teachers"), runTest("terchers"));
        assertEquals(new DistancePair(1, "unsuccessful"), runTest("unsuccesful"));
        assertEquals(new DistancePair(4, "turntable"), runTest("adresable"));
        assertEquals(new DistancePair(2), runTest("blovd"));
        assertEquals(new DistancePair(2, "million"), runTest("minion"));
        assertEquals(new DistancePair(4, "million"), runTest("comision"));
        assertEquals(new DistancePair(1, "defended"), runTest("defnded"));
        assertEquals(new DistancePair(1, "valued"), runTest("value"));
        assertEquals(new DistancePair(1, "care"), runTest("cars"));
        assertEquals(new DistancePair(2, "article's"), runTest("articls"));
    }
    
    private DistancePair runTest(String word) {
        ThreadPoolDistance task = new ThreadPoolDistance(TestWords.TEST_WORDS, 100);
        return pool.invoke(task);
    }   */
}

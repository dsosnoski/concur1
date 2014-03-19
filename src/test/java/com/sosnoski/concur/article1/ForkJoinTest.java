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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Basic tests for fork join implementation.
 */
public class ForkJoinTest
{
    private ForkJoinDistance impl;
    
    @Before
    public void setUp() {
        impl = new ForkJoinDistance(TestWords.TEST_WORDS, 100);
    }
    
    @After
    public void tearDown() throws Exception {
        impl.shutdown();
    }
    
    @Test
    public void testList() {
        assertEquals(new DistancePair(0, "teachers"), impl.bestMatch("teachers"));
        assertEquals(new DistancePair(1, "teachers"), impl.bestMatch("terchers"));
        assertEquals(new DistancePair(1, "unsuccessful"), impl.bestMatch("unsuccesful"));
        assertEquals(new DistancePair(4, "turntable"), impl.bestMatch("adresable"));
        assertEquals(new DistancePair(2), impl.bestMatch("blovd"));
        assertEquals(new DistancePair(2, "million"), impl.bestMatch("minion"));
        assertEquals(new DistancePair(4, "million"), impl.bestMatch("comision"));
        assertEquals(new DistancePair(1, "defended"), impl.bestMatch("defnded"));
        assertEquals(new DistancePair(1, "valued"), impl.bestMatch("value"));
        assertEquals(new DistancePair(1, "care"), impl.bestMatch("cars"));
        assertEquals(new DistancePair(2, "article's"), impl.bestMatch("articls"));
    }
}

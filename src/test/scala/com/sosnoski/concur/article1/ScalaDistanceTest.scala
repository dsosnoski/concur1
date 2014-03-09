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

package com.sosnoski.concur.article1

import org.scalatest.FunSuite

class Distance extends FunSuite {

  val matcher = new Matcher(TestWords.TEST_WORDS)

  test("match selection of misspelled words") {
    assert(DistancePair(0, Some("teachers")) === matcher.bestMatch("teachers"));
    assert(DistancePair(1, Some("teachers")) === matcher.bestMatch("terchers"));
    assert(DistancePair(1, Some("unsuccessful")) === matcher.bestMatch("unsuccesful"));
    assert(DistancePair(4, Some("turntable")) === matcher.bestMatch("adresable"));
    assert(DistancePair(2, None) === matcher.bestMatch("blovd"));
    assert(DistancePair(2, Some("million")) === matcher.bestMatch("minion"));
    assert(DistancePair(4, Some("million")) === matcher.bestMatch("comision"));
    assert(DistancePair(1, Some("defended")) === matcher.bestMatch("defnded"));
    assert(DistancePair(1, Some("valued")) === matcher.bestMatch("value"));
    assert(DistancePair(1, Some("care")) === matcher.bestMatch("cars"));
    assert(DistancePair(2, Some("article's")) === matcher.bestMatch("articls"));
  }
}
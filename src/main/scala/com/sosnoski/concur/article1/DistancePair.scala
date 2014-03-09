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

case class DistancePair(val distance: Int, val word: Option[String]) {
  def this(distance: Int) = this(distance, None)
  def this(distance: Int, word: String) = this(distance, Some(word))
}

object DistancePair {
  def best(a: DistancePair, b: DistancePair) =
    if (a.distance > b.distance) b
    else if (b.distance > a.distance) a
    else DistancePair(a.distance, None)
  val worstMatch = DistancePair(Int.MaxValue, None)
}
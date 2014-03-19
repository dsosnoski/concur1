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

abstract class TimingTestBase {

  def bestMatch(target: String): DistancePair

  def shutdown: Unit

  def blockSize: Int

  def runTest(words: Array[String], pairs: List[(String, String)]) = {

    def timePass(): (Int, Long) = {
      val start = System.currentTimeMillis
      val found = pairs.foldLeft(0)((count, pair) => bestMatch(pair._1) match {
          case DistancePair(_, Some(pair._2)) => count + 1
          case _ => count
      })
      (found, System.currentTimeMillis() - start)
    }
    
    val count = pairs.size
    val high = (0 until 10).foldLeft(0)((high, i) => {
      if (i > 0) {
        println("Pausing for JVM to settle")
        Thread.sleep(3000)
      }
      val (found, time) = timePass()
      val rate = count * 1000 / time.intValue
      println(count + " words took " + time + " ms. (" + found + " results matched) for rate " + rate)
      if (high < rate) rate else high
    })
    
    println("Completed test for class " + this.getClass.getSimpleName + " with block size " +
      blockSize + ", maximum rate " + high + " words / second")
  }
}
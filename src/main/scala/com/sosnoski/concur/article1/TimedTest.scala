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

import scala.io.Source

/** Test application runner. The main() method takes command line parameters giving the details of the
  * test to be executed. The actual test classes must be subclasses of TimedTestBase.
  */
object TimedTest {

  def main(args: Array[String]) {
    if (args.length < 4) {
      println("Usage:\n  scala com.sosnoski.concur1.TimedTest word-file error-file class-name block-size")
      return
    }

    // read all known words
    val words = Source.fromFile(args(0), "UTF-8").getLines.map(x => x.trim).toArray

    // read all errors and corrections
    val pairs = Source.fromFile(args(1)).getLines.map(x => {
      val splits = x.trim.split("->")
      (splits(0), splits(1))
    }).toList

    // load test class using reflection
    val constr = Class.forName(args(2)).getConstructor(words.getClass, Integer.TYPE)
    constr.newInstance(words, Integer.parseInt(args(3)): Integer) match {
      case t: TimingTestBase => {
        t.runTest(words, pairs)
        t.shutdown
      }
      case _ => println("Class " + args(2) + " is not an instance of TimingTestBase")
    }
  }
}
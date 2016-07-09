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

import scala.concurrent._
import scala.concurrent.duration._
import java.util.concurrent.atomic.AtomicInteger

/** Demonstration of futures in Scala. */
object Futures extends App {
  
  import ExecutionContext.Implicits.global
  
  val lastInteger = new AtomicInteger
  def futureInt() = Future {
    Thread sleep 2000
    lastInteger incrementAndGet
  }
  
  // use callbacks for completion of futures
  val a1 = futureInt
  val a2 = futureInt
  a1.onSuccess {
      case i1 => {
        a2.onSuccess {
          case i2 => println("Sum of values is " + (i1 + i2))
        }
      }
  }
  Thread sleep 3000
  
  // use for construct to extract values when futures complete
  val b1 = futureInt
  val b2 = futureInt
  for (i1 <- b1; i2 <- b2) yield println("Sum of values is " + (i1 + i2))
  Thread sleep 3000
  
  // wait directly for completion of futures
  val c1 = futureInt
  val c2 = futureInt
  println("Sum of values is " + (Await.result(c1, Duration.Inf) +
    Await.result(c2, Duration.Inf)))
}

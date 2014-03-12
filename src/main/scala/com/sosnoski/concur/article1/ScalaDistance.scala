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

import scala.annotation.tailrec
import scala.concurrent._
import scala.concurrent.duration.Duration
import java.util.concurrent.ForkJoinPool

/** Matcher for array of words. */
class Matcher(words: Array[String]) {

  /** Find best match for word in array of known words.
    * @param targetText
    * @return distancePair for best match
    */
  def bestMatch(targetText: String) = {

    // basic value definitions
    val limit = targetText.length
    val v0 = new Array[Int](limit + 1)
    val v1 = new Array[Int](limit + 1)

    /** Minimum value of three. */
    def min(a: Int, b: Int, c: Int) = Math.min(a, Math.min(b, c))

    /** Calculate edit distance from targetText to known word, with full tail-recursion for best performance.
      *
      * @param word known word
      * @param v0 int array of length targetText.length + 1
      * @param v1 int array of length targetText.length + 1
      * @return distance
      */
    def editDistance(word: String, v0: Array[Int], v1: Array[Int]) = {

      val length = word.length

      @tailrec
      def distanceByRow(row: Int, r0: Array[Int], r1: Array[Int]): Int = {
        if (row >= length) r0(limit)
        else {

          // first element of v1 = delete (i+1) chars from target to match empty 'word'
          r1(0) = row + 1

          // use formula recursively to fill in the rest of the row
          @tailrec
          def distanceByColumn(col: Int): Unit = {
            if (col < limit) {
              val cost = if (word(row) == targetText(col)) 0 else 1
              r1(col + 1) = min(r1(col) + 1, r0(col + 1) + 1, r0(col) + cost)
              distanceByColumn(col + 1)
            }
          }
          distanceByColumn(0)

          // recurse with arrays swapped for next row
          distanceByRow(row + 1, r1, r0)
        }
      }

      // initialize v0 (prior row of distances) as edit distance for empty 'word'
      @tailrec
      def initArray(index: Int): Unit = {
        if (index <= limit) {
          v0(index) = index
          initArray(index + 1)
        }
      }
      initArray(0)

      // recursively process rows matching characters in word being compared to find best
      distanceByRow(0, v0, v1)
    }

    /** Calculate edit distance from targetText to known word, with idiomatic Scala.
      *
      * @param word known word
      * @param v0 int array of length targetText.length + 1
      * @param v1 int array of length targetText.length + 1
      * @return distance
      */
    def editDistance1(word: String, v0: Array[Int], v1: Array[Int]) = {

      val length = word.length

      @tailrec
      def distanceByRow(rnum: Int, r0: Array[Int], r1: Array[Int]): Int = {
        if (rnum >= length) r0(limit)
        else {

          // first element of r1 = delete (i+1) chars from target to match empty 'word'
          r1(0) = rnum + 1

          // use formula to fill in the rest of the row
          for (j <- 0 until limit) {
            val cost = if (word(rnum) == targetText(j)) 0 else 1
            r1(j + 1) = min(r1(j) + 1, r0(j + 1) + 1, r0(j) + cost);
          }

          // recurse with arrays swapped for next row
          distanceByRow(rnum + 1, r1, r0)
        }
      }

      // initialize v0 (prior row of distances) as edit distance for empty 'word'
      for (i <- 0 to limit) v0(i) = i

      // recursively process rows matching characters in word being compared to find best
      distanceByRow(0, v0, v1)
    }

    /** Scan all known words in range to find best match.
      *
      * @param index next word index
      * @param bestDist minimum distance found so far
      * @param bestMatch unique word at minimum distance, or None if not unique
      * @return best match
      */
    @tailrec
    def best(index: Int, bestDist: Int, bestMatch: Option[String]): DistancePair =
      if (index < words.length) {
        val newDist = editDistance(words(index), v0, v1)
        val next = index + 1
        if (newDist < bestDist) best(next, newDist, Some(words(index)))
        else if (newDist == bestDist) best(next, bestDist, None)
        else best(next, bestDist, bestMatch)
      } else DistancePair(bestDist, bestMatch)

    best(0, Int.MaxValue, None)
  }
}

/** Controls collection of matchers and merges results (alternative 1). */
class ParallelCollectionDistance(words: Array[String], size: Int) extends TimingTestBase {

  val matchers = words.grouped(size).map(l => new Matcher(l)).toList

  def shutdown = {}

  def blockSize = size

  /** Find best result across all matchers, using parallel collection. */
  def bestMatch(target: String) = {
    matchers.par.map(m => m.bestMatch(target)).
      foldLeft(DistancePair.worstMatch)((a, m) => DistancePair.best(a, m))
  }
}

/** Controls collection of matchers and merges results (alternative 2). */
class FutureFoldDistance(words: Array[String], size: Int) extends TimingTestBase {

  val matchers = words.grouped(size).map(l => new Matcher(l)).toList

  def shutdown = {}

  def blockSize = size

  /** Find best result across all matchers, using Future.fold helper. */
  def bestMatch(target: String) = {
    import ExecutionContext.Implicits.global
    val futures = matchers.map(m => future { m.bestMatch(target) })
    val combined = Future.fold(futures)(DistancePair.worstMatch)((a, v) =>
      DistancePair.best(a, v))
    Await.result(combined, Duration.Inf)
  }
}

/** Controls collection of matchers and merges results (alternative 3). */
class DirectBlockingDistance(words: Array[String], size: Int) extends TimingTestBase {

  val matchers = words.grouped(size).map(l => new Matcher(l)).toList

  def shutdown = {}

  def blockSize = size

  /** Find best result across all matchers, using direct blocking waits. */
  def bestMatch(target: String) = {
    import ExecutionContext.Implicits.global

    val futures = matchers.map(m => future { m.bestMatch(target) })
    futures.foldLeft(DistancePair.worstMatch)((a, v) =>
      DistancePair.best(a, Await.result(v, Duration.Inf)))
  }
}

/** Controls collection of matchers and merges results, using Java ForkJoin pool (alternative 4). */
class RecursiveSplitDistance(words: Array[String], size: Int) extends TimingTestBase {
  
  implicit val executionContext = ExecutionContext.fromExecutor(new ForkJoinPool)

  val matchers = words.grouped(size).map(l => new Matcher(l)).toArray

  def shutdown = {}

  def blockSize = size

  /** Find best result across all matchers, splitting range recursively to emulate Java recursive tasks. */
  def bestMatch(target: String) = {
    def evaluateRange(start: Int, length: Int): Future[DistancePair] = {
      if (length == 1) future { matchers(start).bestMatch(target) }
      else {
        val promise = Promise[DistancePair]
        val half = length / 2
        val low = future { evaluateRange(start, half) }
        val high = future { evaluateRange(start + half, length - half) }
        low.onSuccess {
          case a => high.onSuccess {
            case b => a.onSuccess {
              case x => b.onSuccess {
                case y => promise.success(DistancePair.best(x, y))
              }
            }
          }
        }
        promise.future
      }
    }
    Await.result(evaluateRange(0, matchers.length), Duration.Inf)
  }
}
package com.sosnoski.concur.article1

import scala.annotation.tailrec

class ClosureTest(size: Int) {
  val array = new Array[Int](size)

  def runForeach(count: Int) {
    (0 until count).foreach { i =>
      array(i) = i
    }
  }

  def runWhile(count: Int) {
    var i = 0
    while (i < count) {
      array(i) = i
      i += 1
    }
  }

  def runRecur(count: Int) {
    @tailrec
    def recur(i: Int): Unit =
      if (i < count) {
        array(i) = i
        recur(i + 1)
      }
    recur(0)
  }
}

object ClosureTest extends App {
  
  val start = 1000
  val limit = 128000
  val test = new ClosureTest(limit)

  def timed(name: String, count: Int, f: (Int) => Unit): Unit = {
    val start = System.nanoTime
    f(count)
    println(" time " + (System.nanoTime - start) + " for test case " + name)
  }

  @tailrec
  def testSet(count: Int): Unit =
    if (count <= limit) {
      println()
      println("Running test with count " + count)
      timed("runForeach", count, test.runForeach)
      timed("runWhile", count, test.runWhile)
      timed("runRecur", count, test.runRecur)
      testSet(count * 2)
    }

  (1 until 10).foreach { _ =>
    test.runForeach(limit)
    test.runWhile(limit)
    test.runRecur(limit)
    println("Pausing for JVM to settle")
    Thread.sleep(3000)
  }
  testSet(start)
}
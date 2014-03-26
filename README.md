concur1
============

This gives sample Java and Scala code for the
[first article in my JVM Concurrency series](http://www.ibm.com/developerworks/library/j-jvmc1/index.html) on IBM
developerWorks. The project uses a Maven build, so just do the usual `mvn clean install` to get
everything to a working state. The code is all in the `com.sosnoski.concur.article1` package, but
is split between *main/java* and *main/scala* trees. The core code, including the test launcher
(`TimedTest`), is in the Scala tree.

The *test/java* and *test/scala* trees include some basic unit tests
for the edit distance calculations.

The *data* directory contains the list of known words, and a list of misspelled words paired with
the corrected spellings. The limited list of known words included does not contain all the correct
spellings of the misspelled words, so many of the words will not be found - but that's fine, as long
as the same ones are always either found or not found. The list of known words was generated using
Spell Checking Oriented Word Lists (SCOWL) 7.1: http://wordlist.sourceforge.net/ The list of misspelled
words are a randomized selection from http://en.wikipedia.org/wiki/Wikipedia:Lists_of_common_misspellings/For_machines

The timing test code in the Scala `TimingTestBase` class runs 10 passes over the data, with 3 second
pauses between passes to let the JVM settle. It prints the elapsed time and number of correct matches
found on each pass, along with the processing rate in terms of misspelled words per second. At the end
of the 10 passes it ends with a summary of the test case and the best time from any pass.

To run timing tests of the two Java and four Scala edit distance code variations use
`mvn scala:run -Dlauncher={name} -DaddArgs={block size}`, where {name} selects the test code:

1. `basepool` - Simple Java thread pool, with thread count set to number of available cores
(`ThreadPoolDistance` class)
2. `forkjoin` - Java `ForkJoinPool` with recursive task splitting
(`ForkJoinDistance` class)
3. `parcol` - Scala using parallel collection (`ParallelCollectionDistance` class)
4. `dirblock` - Scala using default `ExecutionContext` with (`DirectBlockingDistance` class)
5. `futurefold` - Scala using default `ExecutionContext` with `Future.fold` helper method (`FutureFoldDistance` class)
6. `recursplit` - Scala using Java `ForkJoinPool` with recursive splitting of futures (`RecursiveSplitDistance` class)

and {block size} is the number of known words to include in each calculation task.

You can import the project into ScalaIDE with the standard Maven project import handling.

### Closure performance code

Besides the code discussed in the article, I've added ClosureTest.scala with some microbenchmark code
for checking performance of Scala closures. This didn't really show much of interest, but if you want
to try it out you can use the Maven launcher target `closures` to run it.

I also added a third variation of the Scala `editDistance` method, this one using a local `iterate` method
instead of the `Range.foreach` used by the idiomatic Scala code (as the translation of the `for (i <- 0 to x)`
constructs). On the Oracle Java 7 64-bit JVM this variation delivers about 85% of the performance of the
tail-recursion code (vs. about 45% for the idiomatic Scala).

concur1
============

This gives sample Java and Scala code for the first article in my JVM Concurrency series on IBM
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

To run timing tests of the two Java and three Scala edit distance code variations use
`mvn scala:run -Dlauncher={name} -DaddArgs={block size}`, where {name} selects the test code:

1. `basepool` - Simple Java thread pool, with thread count set to number of available cores
(`ThreadPoolDistance` class)
2. `forkjoin` - Java `ForkJoinPool` with recursive task splitting
(`ForkJoinDistance` class)
3. `parcol` - Scala using parallel collection (`ParallelCollectionDistance` class)
4. `futurefold` - Scala using default `ExecutionContext` with `Future.fold` helper method (`FutureFoldDistance` class)
5. `dirblock` - Scala using default `ExecutionContext` with (`DirectBlockingDistance` class)

and {block size} is the number of known words to include in each calculation task.

You can import the project into ScalaIDE with the standard Maven project import handling.

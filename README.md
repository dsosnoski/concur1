concur1
============

This gives sample Java and Scala code for the first article in my JVM Concurrency series on IBM
developerWorks. The project uses a Maven build, so just do the usual `mvn clean install` to get
everything to a working state. The code is all in the `com.sosnoski.concur.article1` package, but
is split between *main/java* and *main/scala* trees. The core code, including the test launcher
(`TimedTest`), is in the Scala tree. The *test/java* and *test/scala* trees include some basic unit tests
for the edit distance calculations.

The *data* directory contains the list of known words, and a list of misspelled words paired with
the corrected spellings. The limited list of known words included does not contain all the correct
spellings of the misspelled words, so many of the words will not be found - but that's fine, as long
as the same ones are always either found or not found. The list of known words was generated using
Spell Checking Oriented Word Lists (SCOWL) 7.1: http://wordlist.sourceforge.net/ The list of misspelled
words were a randomized selection from http://en.wikipedia.org/wiki/Wikipedia:Lists_of_common_misspellings/For_machines

The timing test code in the Scala `TimingTestBase` class runs 10 passes over the data, with a 3 second
pause between passes to let the JVM settle. It prints the number of correct matches found on each pass,
along with the processing rate in terms of misspelled words per second, and at the end prints the best
time from any pass.

To run timing tests of the two Java and one Scala edit distance code variations use
`mvn scala:run -Dlauncher={name} -DaddArgs={block size}`, where {name} is:

1. `pool` - Simple Java thread pool, with thread count set to number of available cores
(`ThreadPoolDistance` class)
2. `forkjoin` - Java `ForkJoinPool` with recursive task splitting
(`ForkJoinDistance` class)
3. `scala` - Scala using default `ExecutionContext`
(`ForkJoinDistance` class)

and {block size} is the number of known words to include in each calculation task.

You can import the project into ScalaIDE with the standard Maven project import handling.

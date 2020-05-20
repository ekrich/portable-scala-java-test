# Portable Scala Java Test

When porting Java platform code to Scala.js and Scala Native you create
unit tests. These tests should succeed if run against the JVM and also
against the code you are creating for Scala.js, Scala Native or both
platforms. 

**Warning: Please do not look at any Java source as it is GPL
licensed and is not compatible with the Scala.js and Scala Native code
bases.** You may use Apache Harmony or implement by scratch using the
Javadoc.

This project allows tests to be run against JVM, JS, and Scala Native. This
project is setup to allow you to add code and the tests that run against
your code. You can also test for multiple Scala versions and versions of
Scala.js. Currently, it is setup to only run one Scala Native version but
it can be easily changed like the Scala.js setup to support multiple versions.

Add your code in the shared project `scalacode` and also your test code into
the shared project `testSuite`. These are the directions needed if all your
code runs on all Scala.js and Scala Native platforms and your tests compile
and run on all three platforms. If this is not the case, you can put your
code in the appropriate cross platform area.

You can run the following commands in `sbt` to run for each platform and
version: `clean` `compile` `test`

If you would like to run one test suite against the JVM, Scala Native, and 
Scala.js respectively, these are examples:

```
sbt:portable-scala-java-test> testSuiteJVM/testOnly *PropertiesSuite
sbt:portable-scala-java-test> testSuiteNative/testOnly *PropertiesSuite
sbt:portable-scala-java-test> testSuiteJS/testOnly *PropertiesSuite
```

If you want to run all the test suites for individual platforms use
the following commands:

```
sbt:portable-scala-java-test> testSuiteJVM/test
sbt:portable-scala-java-test> testSuiteNative/test
sbt:portable-scala-java-test> testSuiteJS/test
```

Currently the project uses [MUnit](https://scalameta.org/munit/) to test
so you can't just copy paste to Scala.js that uses JUnit or Scala Native
that has its own test suite and doesn't support JUnit yet.
This is an inconvenience but the changes are pretty minor unless
you have a big test suite. This should be a more nimble environment
especially for Scala Native depending on your needs.

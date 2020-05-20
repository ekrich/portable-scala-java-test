import sbtcrossproject.crossProject

val scala211 = "2.11.12"
val scala212 = "2.12.11"
val scala213 = "2.13.1"

val versionsBase   = Seq(scala212) // just one
val versionsJS     = versionsBase
val versionsNative = Seq(scala211)
crossScalaVersions in ThisBuild := versionsBase

scalaVersion in ThisBuild := (crossScalaVersions in ThisBuild).value.head

val commonSettings: Seq[Setting[_]] = Seq(
  version := "0.0.1-SNAPSHOT",
  organization := "org.ekrich",
  scalacOptions ++= Seq("-deprecation", "-feature", "-Xfatal-warnings"),
  homepage := Some(url("http://github.com/ekrich/scala-native-java-test")),
  licenses += ("BSD New",
  url("http://github.com/ekrich/scala-native-java-test/blob/master/LICENSE")),
  scmInfo := Some(
    ScmInfo(
      url("http://github.com/ekrich/scala-native-java-test"),
      "scm:git:git@github.com:ekrich/scala-native-java-test.git",
      Some("scm:git:git@github.com:ekrich/scala-native-java-test.git")
    )
  )
)
lazy val root = (project in file("."))
  .settings(
    name := "portable-scala-java-test"
    )
  .aggregate(
    scalacodeJS,
    scalacodeNative,
    testSuiteJVM,
    testSuiteJS,
    testSuiteNative
  )

lazy val scalacode = crossProject(JSPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    mappings in (Compile, packageBin) ~= {
      _.filter(!_._2.endsWith(".class"))
    },
    exportJars := true,
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra := (
      <developers>
          <developer>
            <id>ekrich</id>
            <name>Eric Richardson</name>
            <url>https://github.com/ekrich/</url>
          </developer>
        </developers>
    ),
    pomIncludeRepository := { _ => false }
  )
  .nativeSettings(
    crossScalaVersions := versionsNative,
    scalaVersion := scala211, // allows to compile if scalaVersion set not 2.11
    nativeLinkStubs := true,
    logLevel := Level.Info // Info or Debug
  )

lazy val scalacodeJS = scalacode.js
lazy val scalacodeNative = scalacode.native
  .enablePlugins(ScalaNativePlugin)

lazy val testSuite = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .settings(commonSettings: _*)
  .settings(
    testFrameworks += new TestFramework("munit.Framework"),
    libraryDependencies +=
      "org.scalameta" %%% "munit" % "0.7.2" % Test,
    scalacOptions += "-target:jvm-1.8"
  )
  .jsSettings(
    name := "testSuite on JS",
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))
  )
  .jsConfigure(_.dependsOn(scalacodeJS))
  .jvmSettings(
    name := "testSuite on JVM"
  )
  .nativeSettings(
    name := "testSuite on Native",
    crossScalaVersions := versionsNative,
    scalaVersion := scala211 // allows to compile if scalaVersion set not 2.11
  )

lazy val testSuiteJS  = testSuite.js
lazy val testSuiteJVM = testSuite.jvm
lazy val testSuiteNative = testSuite.native
  .dependsOn(scalacodeNative)

name := """CerbatanPOS"""

version := "1.0.0"

scalaVersion := "2.11.5"

//crossScalaVersions := Seq("2.10.5", scalaVersion.value)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "0.8.1",
  "org.virtuslab" %% "unicorn-play" % "0.6.3",
  "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0",
  "com.h2database" % "h2" % "1.4.181" % "test",
  "ws.securesocial" %% "securesocial" % "master-SNAPSHOT"
  // Add your own project dependencies in the form:
  // "group" % "artifact" % "version"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots")
)

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

name := """CerbatanPOS"""

version := "1.0.0"

scalaVersion := "2.11.7"

//crossScalaVersions := Seq("2.10.5", scalaVersion.value)

libraryDependencies ++= Seq(
  ws,
  specs2 % Test,
  "com.typesafe.play" %% "play-slick" % "1.1.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.1.0",
  "org.virtuslab" %% "unicorn-play" % "1.0.0",
  "com.h2database" % "h2" % "1.4.190",
  "jp.t2v" %% "play2-auth" % "0.14.1",
  "jp.t2v" %% "play2-auth-test" % "0.14.1" % "test",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.webjars" %% "webjars-play" % "2.4.0-2",
  "org.webjars" % "requirejs-domready" % "2.0.1-2",
  "org.webjars" % "bootstrap" % "3.3.4",
  "org.webjars" % "font-awesome" % "4.3.0-2",
  "org.webjars" % "angularjs" % "1.3.16",
  "org.webjars" % "angular-ui-bootstrap" % "0.14.3",
  "org.webjars.bower" % "angular-scroll" % "0.7.1" exclude("org.webjars.bower", "angular"),
  "org.webjars" % "ng-tags-input" % "2.3.0",
  "org.webjars" % "angular-ui-select" % "0.12.1",
  "org.webjars" % "angular-toastr" % "1.5.0",
  "org.webjars" % "angular-strap" % "2.3.4"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)
routesGenerator := InjectedRoutesGenerator

//pipelineStages := Seq(rjs)

// for minified *.min.css files
LessKeys.compress := true
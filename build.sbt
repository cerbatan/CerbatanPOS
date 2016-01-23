name := """CerbatanPOS"""

version := "1.0.0"

scalaVersion := "2.11.7"

//crossScalaVersions := Seq("2.10.5", scalaVersion.value)

libraryDependencies ++= Seq(
  ws,
  specs2 % Test,
  "com.typesafe.play" %% "play-slick" % "1.1.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.1.1",
  "org.virtuslab" %% "unicorn-play" % "1.0.0",
  "org.postgresql" % "postgresql" % "9.4-1206-jdbc42",
  "jp.t2v" %% "play2-auth" % "0.14.1",
  "jp.t2v" %% "play2-auth-test" % "0.14.1" % "test",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.webjars" %% "webjars-play" % "2.4.0-2",
  "org.webjars" % "requirejs-domready" % "2.0.1-2",
  "org.webjars" % "bootstrap" % "3.3.6",
  "org.webjars" % "font-awesome" % "4.5.0",
  "org.webjars" % "angularjs" % "1.4.8",
  "org.webjars" % "angular-ui-bootstrap" % "1.0.3",
  "org.webjars.bower" % "angular-scroll" % "1.0.0" exclude("org.webjars.bower", "angular"),
  "org.webjars" % "ng-tags-input" % "2.3.0",
  "org.webjars" % "angular-ui-select" % "0.13.1",
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

ThisBuild / version := "0.1"

ThisBuild / scalaVersion := "2.13.15"

lazy val root = (project in file("."))
  .settings(
    name := "akka"
  )

lazy val akkaVersion = "2.7.0"
lazy val leveldbVersion = "0.7"
lazy val leveldbjniVersion = "1.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "org.mdedetrich" %% "akka-stream-json" % "0.8.2",
  "org.mdedetrich" %% "akka-http-json" % "0.8.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

javacOptions ++= Seq("-source", "11", "-target", "11")

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case PathList("reference.conf") => MergeStrategy.concat
  case x => MergeStrategy.first
}

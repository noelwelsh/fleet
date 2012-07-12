import sbt._
import sbt.Project.Initialize
import sbt.Keys._

object FleetBuild extends Build {

  val fleetVersion = "0.1"

  lazy val fleetResolvers = Seq(
    "Sonatype"            at "http://oss.sonatype.org/content/repositories/releases",
    "JBoss"               at "http://repository.jboss.org/nexus/content/groups/public",
    "Untyped"             at "http://repo.untyped.com/",
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
  )

  val fleetSettings = Seq(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" % "akka-actor" % "2.0.2"
    )
  )

  lazy val scalaz        = "org.scalaz"              %% "scalaz-core"     % "7.0-SNAPSHOT"
  lazy val specs2        = "org.specs2"              %% "specs2"          % "1.8.1"
  lazy val scalacheck    = "org.scala-tools.testing" %% "scalacheck"      % "1.9"

  lazy val fleet = Project(
    id = "fleet",
    base = file(".")
  ).settings(
    Project.defaultSettings ++
    fleetSettings
  )

}

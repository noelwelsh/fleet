import sbt._
import sbt.Project.Initialize
import sbt.Keys._

object FleetBuild extends Build {

  val fleetVersion = "0.1"

  lazy val fleetResolvers = Seq(
    "Sonatype"      at "http://oss.sonatype.org/content/repositories/releases",
    "Sonatype Snap" at "http://oss.sonatype.org/content/repositories/snapshots",
    "JBoss"         at "http://repository.jboss.org/nexus/content/groups/public",
    "Untyped"       at "http://repo.untyped.com/",
    "Typesafe"      at "http://repo.typesafe.com/typesafe/releases/"
  )

  lazy val scalaz        = "org.scalaz"              %% "scalaz-core"     % "7.0-SNAPSHOT"
  lazy val specs2        = "org.specs2"              %% "specs2"          % "1.8.1"
  lazy val scalacheck    = "org.scala-tools.testing" %% "scalacheck"      % "1.9"
  lazy val guava         = "com.google.guava"        %  "guava"           % "12.0"
  lazy val akka          = "com.typesafe.akka"       %  "akka-actor"      % "2.0.2"

  val fleetSettings = Seq(
    libraryDependencies ++= Seq(
      akka,
      guava,
      scalaz
    ),
    resolvers ++= fleetResolvers
  )


  lazy val fleet = Project(
    id = "fleet",
    base = file(".")
  ).settings(
    Project.defaultSettings ++
    fleetSettings : _*
  )

}

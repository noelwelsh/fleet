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
  lazy val specs2        = "org.specs2"              %% "specs2"          % "1.12.2"
  lazy val scalacheck    = "org.scala-tools.testing" %% "scalacheck"      % "1.9"
  lazy val guava         = "com.google.guava"        %  "guava"           % "12.0"
  lazy val akka          = "com.typesafe.akka"       %  "akka-actor"      % "2.0.2"
  lazy val blueeyesCore  = "com.github.jdegoes"      %  "blueeyes-core_2.9.1"   % "0.6.1-SNAPSHOT"
  lazy val blueeyesMongo = "com.github.jdegoes"      %  "blueeyes-mongo_2.9.1"  % "0.6.1-SNAPSHOT"
  lazy val blueeyesJson  = "com.github.jdegoes"      %  "blueeyes-json_2.9.1"   % "0.6.1-SNAPSHOT"


  val fleetSettings = Seq(
    libraryDependencies ++= Seq(
      akka,
      guava,
      scalaz,
      blueeyesCore,
      blueeyesJson,
      blueeyesMongo,
      specs2 % "test"
    ),
    resolvers ++= fleetResolvers,
    scalacOptions += "-Ydependent-method-types"
  )


  lazy val fleet = Project(
    id = "fleet",
    base = file(".")
  ).settings(
    Project.defaultSettings ++
    fleetSettings : _*
  )

  lazy val bigtopCore = Project(
    id = "bigtop-core",
    base = file("bigtop/core")
  ).settings(
    Project.defaultSettings ++
    mynaSettings ++
    blueeyesSettings ++
    Seq(
      exportJars := true
    ) : _*
  )

  lazy val bigtopUtil = Project(
    id = "bigtop-util",
    base = file("bigtop/util")
  ).settings(
    Project.defaultSettings ++
    mynaSettings ++
    blueeyesSettings ++
    Seq(
      exportJars := true
    ) : _*
  ).dependsOn(
    bigtopCore
  )

}

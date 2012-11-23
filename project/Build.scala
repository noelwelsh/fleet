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
  lazy val jbCrypt       = "org.mindrot"             %  "jbcrypt"         % "0.3m"
  lazy val redisclient   = "net.debasishg"           %% "redisclient"     % "2.4.2"
  lazy val twitterUtil   = "com.twitter"             %% "util-collection" % "1.12.12"
  lazy val jodaTime      = "joda-time"               %  "joda-time"       % "2.0"
  lazy val jodaConvert   = "org.joda"                %  "joda-convert"    % "1.2"
  lazy val slf4s         = "com.weiglewilczek.slf4s" %% "slf4s"           % "1.0.7"
  lazy val configrity    = "org.streum"              %% "configrity"      % "0.9.0"
  lazy val metricsCore   = "com.yammer.metrics"      %  "metrics-core"    % "2.1.2"
  lazy val metricsScala  = "com.yammer.metrics"      %  "metrics-scala_2.9.1"   % "2.1.2"

  lazy val blueeyesCore  = "com.github.jdegoes"      %  "blueeyes-core_2.9.1"   % "0.6.1-SNAPSHOT"
  lazy val blueeyesMongo = "com.github.jdegoes"      %  "blueeyes-mongo_2.9.1"  % "0.6.1-SNAPSHOT"
  lazy val blueeyesJson  = "com.github.jdegoes"      %  "blueeyes-json_2.9.1"   % "0.6.1-SNAPSHOT"


  lazy val baseSettings: Seq[Setting[_]] = Seq(
    organization := "untyped",
    resolvers ++= fleetResolvers,
    libraryDependencies ++= Seq(
      akka,
      scalaz,
      jbCrypt,
      specs2,
      jodaTime,
      jodaConvert,
      metricsCore,
      metricsScala
    )
  )


  lazy val blueeyesSettings: Seq[Setting[_]] = Seq(
    libraryDependencies ++= Seq(
      blueeyesCore,
      blueeyesJson,
      blueeyesMongo
    )
  )

  lazy val fleetSettings = Seq(
    libraryDependencies ++= Seq(
      guava
    ),
    resolvers ++= fleetResolvers,
    scalacOptions += "-Ydependent-method-types"
  )

  lazy val fleet = Project(
    id = "fleet",
    base = file(".")
  ).settings(
    Project.defaultSettings ++
    baseSettings ++
    blueeyesSettings ++
    fleetSettings : _*
  ).dependsOn(
    bigtopCore,
    bigtopUtil
  )

  lazy val bigtopCore = Project(
    id = "bigtop-core",
    base = file("bigtop/core")
  ).settings(
    Project.defaultSettings ++
    baseSettings ++
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
    baseSettings ++
    blueeyesSettings ++
    Seq(
      exportJars := true
    ) : _*
  ).dependsOn(
    bigtopCore
  )

}

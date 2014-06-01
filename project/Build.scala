import sbt._
import sbt.Keys._

object BuildSettings {

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "com.bokland",
    scalaVersion := "2.10.4",
    version := "0.3-SNAPSHOT"
  )

}

object Resolvers {
  val sonatypeSnaps = "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  val sonatypeRels = "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases"
  val sonatypeSTArch = "scalaTools Archive" at "https://oss.sonatype.org/content/groups/scala-tools"
  val mavenOrgRepo = "Maven.Org Repository" at "http://repo1.maven.org/maven2/org"
}

object Dependencies {

  val casbahVer = "2.5.0"

  val config = "com.typesafe" % "config" % "1.2.0"

  val scalatest = "org.scalatest" %% "scalatest" % "2.1.2" % "test"
  val specs2 = "org.specs2" %% "specs2" % "2.3.10" % "test"

  val slf4j = "org.slf4j" % "slf4j-log4j12" % "1.6.4"

  val elasticSearch = "org.elasticsearch" % "elasticsearch" % "1.1.1"

  val casbahCore = "org.mongodb" %% "casbah-core" % casbahVer
  val casbahQuery = "org.mongodb" %% "casbah-query" % casbahVer
  val casbahCommons = "org.mongodb" %% "casbah-commons" % casbahVer

  val lift_json = "net.liftweb" %% "lift-json" % "2.5.1"

}

object RubberCubeBuild extends Build {

  import BuildSettings._
  import Resolvers._
  import Dependencies._

  lazy val root = Project(
    "rubbercube",
    file("."),
    settings = buildSettings ++ Seq(
      resolvers ++= Seq(sonatypeSnaps, sonatypeRels, sonatypeSTArch, mavenOrgRepo),
      libraryDependencies ++= Seq(
        elasticSearch, config, scalatest, specs2, slf4j, lift_json,
        casbahCore, casbahQuery, casbahCommons
      )
    )
  )

}


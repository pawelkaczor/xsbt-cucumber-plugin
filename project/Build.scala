import sbt._
import sbt.Keys._
import xerial.sbt.Sonatype.sonatypeSettings
import sbtrelease.ReleasePlugin._

object Settings {
  val buildOrganization = "pl.newicom"
  val buildScalaVersion = "2.11.6"
  val crossBuildScalaVersions = Seq("2.10.4", "2.11.6")
  val buildVersion      = "1.0.0"

  val buildSettings = Defaults.defaultSettings ++ Publish.settings ++ releaseSettings ++
                      Seq (organization  := buildOrganization,
                           scalaVersion  := buildScalaVersion,
                           version       := buildVersion,
                           scalacOptions ++= Seq("-deprecation", "-unchecked", "-encoding", "utf8"),
                           publishMavenStyle in ThisBuild := true,
                           homepage          in ThisBuild := Some(new URL("http://github.com/pawelkaczor/xsbt-cucumber-plugin")),
                           licenses := Seq("Apache 2.0" -> url("http://github.com/pawelkaczor/xsbt-cucumber-plugin/blob/master/LICENSE")),
                           startYear := Some(2015)
                      )
}

object Dependencies {

  private val CucumberVersion = "1.2.0"

  def cucumberJvm(scalaVersion: String) =
    "info.cukes" %% "cucumber-scala" % CucumberVersion % "compile"

  val testInterface = "org.scala-tools.testing" % "test-interface" % "0.5" % "compile"
}

object Publish {

  lazy val settings = sonatypeSettings :+ (pomExtra :=
    <scm>
      <url>git@github.com:pawelkaczor/xsbt-cucumber-plugin.git</url>
      <connection>scm:git:git@github.com:pawelkaczor/xsbt-cucumber-plugin.git</connection>
      <developerConnection>scm:git:git@github.com:pawelkaczor/xsbt-cucumber-plugin.git</developerConnection>
    </scm>
      <developers>
        <developer>
          <id>newicom</id>
          <name>Pawel Kaczor</name>
        </developer>
      </developers>)
}

object Build extends Build {
  import Dependencies._
  import Settings._

  lazy val parentProject = Project("sbt-cucumber-parent", file ("."),
    settings = buildSettings).aggregate(pluginProject, integrationProject)

  lazy val pluginProject = Project("sbt-cucumber-plugin", file ("plugin"),
    settings = buildSettings ++
               Seq(
                 scalaVersion := "2.10.4",
                 crossScalaVersions := Seq.empty,
                 sbtPlugin := true))

  lazy val integrationProject = Project ("sbt-cucumber-integration", file ("integration"),
    settings = buildSettings ++
               Seq(crossScalaVersions := crossBuildScalaVersions,
               resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
               libraryDependencies <+= scalaVersion { sv => cucumberJvm(sv) },
               libraryDependencies += testInterface))
}


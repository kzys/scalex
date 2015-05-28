import sbt._, Keys._

// import org.scalex.sbt_plugin.ScalexSbtPlugin
import com.github.retronym.SbtOneJar

trait Resolvers {
  val typesafe = "typesafe.com" at "http://repo.typesafe.com/typesafe/releases/"
  val typesafeS = "typesafe.com snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
  val iliaz = "iliaz.com" at "http://scala.iliaz.com/"
  val sonatype = "sonatype" at "http://oss.sonatype.org/content/repositories/releases"
  val sonatypeS = "sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"
  val mandubian = "Mandubian snapshots" at "https://github.com/mandubian/mandubian-mvn/raw/master/snapshots/"
}

trait Dependencies {
  val compiler = "org.scala-lang" % "scala-compiler" % "2.10.5"
  val scalaz = "org.scalaz" %% "scalaz-core" % "7.0.7"
  val scalazContrib = "org.typelevel" %% "scalaz-contrib-210" % "0.1.5"
  val config = "com.typesafe" % "config" % "1.3.0"
  val scopt = "com.github.scopt" %% "scopt" % "3.3.0"
  val sbinary = "org.scala-tools.sbinary" % "sbinary_2.10" % "0.4.2"
  val elastic4s = "com.sksamuel.elastic4s" %% "elastic4s" % "0.90.5.5"
  val tiscaf = "org.gnieh" %% "tiscaf" % "0.8"
  object akka {
    val actor = "com.typesafe.akka" %% "akka-actor" % "2.3.11"
  }
  object play {
    val json = "com.typesafe.play" %% "play-json" % "2.3.9"
  }
  object apache {
    val io = "commons-io" % "commons-io" % "2.4"
  }

  val specs2 = "org.specs2" %% "specs2" % "2.3.1" % "test"
  // or "org.specs2" %% "specs2-core" % "3.6" % "test"
}

object ScalexBuild extends Build with Resolvers with Dependencies {

  private val buildSettings = Defaults.defaultSettings ++ Seq(
    offline := false,
    organization := "org.scalex",
    name := "scalex",
    version := "3.0-SNAPSHOT",
    scalaVersion := "2.10.5", // or "2.11.6"
    libraryDependencies ++= Seq(config),
    // libraryDependencies in test := Seq(specs2),
    sources in doc in Compile := List(),
    resolvers := Seq(
      typesafe, typesafeS, sonatype, sonatypeS, iliaz, mandubian,
      "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"
    ),
    scalacOptions := Seq("-deprecation", "-unchecked", "-feature", "-language:_"),
    publishTo := Some(Resolver.sftp(
      "iliaz",
      "scala.iliaz.com"
    ) as ("scala_iliaz_com", Path.userHome / ".ssh" / "id_rsa"))
  ) ++ SbtOneJar.oneJarSettings
  // ++ ScalexSbtPlugin.defaultSettings

  lazy val scalex = Project("scalex", file("."), settings = buildSettings).settings(
    libraryDependencies ++= Seq(
      compiler, config, scalaz, scalazContrib,
      "com.github.zafarkhaja" % "java-semver" % "0.9.0",
      scopt, sbinary, elastic4s, akka.actor, play.json,
      apache.io, specs2, tiscaf)
  )
}

import sbt._

object Dependencies {
  private val catsVersion       = "2.6.1"
  private val catsEffectVersion = "3.2.2"
  private val scalatestVersion  = "3.2.9"
  private val enumeratumVersion = "1.6.1"
  private val http4sVersion     = "0.23.0"
  private val circeVersion      = "0.14.1"
  private val doobieVersion     = "1.0.0-M1"

  val cats       = "org.typelevel" %% "cats-core"   % catsVersion
  val catsEffect = "org.typelevel" %% "cats-effect" % catsEffectVersion

  val enumeratum = "com.beachape" %% "enumeratum" % enumeratumVersion

  val scalatest = "org.scalatest" %% "scalatest" % scalatestVersion

  val postgresql = "org.postgresql" % "postgresql" % "9.3-1102-jdbc41"

  val slf4j = "org.slf4j" % "slf4j-nop" % "1.6.4"

  val log = "ch.qos.logback" % "logback-classic" % "1.2.3"

  val circeLibs: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-generic-extras",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)

  val http4sLibs: Seq[ModuleID] = Seq(
    "org.http4s" %% "http4s-dsl",
    "org.http4s" %% "http4s-blaze-server",
    "org.http4s" %% "http4s-blaze-client",
    "org.http4s" %% "http4s-circe"
  ).map(_ % http4sVersion)

  val doobieLibs: Seq[ModuleID] = Seq(
    "org.tpolecat" %% "doobie-core",
    "org.tpolecat" %% "doobie-hikari",
    "org.tpolecat" %% "doobie-postgres"
  ).map(_ % doobieVersion)
}

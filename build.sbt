import Dependencies._

lazy val root = project
  .in(file("."))
  .settings(commonSettings: _*)
  .settings(addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.0" cross CrossVersion.full))
  .settings(
    name := "e-shop",
    libraryDependencies ++= Seq(
      cats,
      catsEffect,
      enumeratum,
      scalatest,
      postgres,
      log,
      reactor,
      liquibase
    ) ++ circeLibs ++ http4sLibs ++ doobieLibs ++ tsecLibs
  )

lazy val commonSettings = Seq(
  organization := "com.project.e-shop",
  scalaVersion := "2.13.6"
)
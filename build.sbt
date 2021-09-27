import Dependencies._

lazy val root = project
  .in(file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "e-shop",
    libraryDependencies ++= Seq(
      cats,
      catsEffect,
      enumeratum,
      scalatest,
      postgresql,
      log
    ) ++ circeLibs ++ http4sLibs ++ doobieLibs
  )

lazy val commonSettings = Seq(
  organization := "com.project.e-shop",
  scalaVersion := "2.13.6"
)
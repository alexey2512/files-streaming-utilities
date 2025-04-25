import Dependencies.*

ThisBuild / scalaVersion := "2.13.16"

lazy val root = (project in file("."))
  .settings(
    name := "FilesStreamingUtilities",
    version := "1.0.0"
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Ymacro-annotations",
  "-Werror",
  "-Wextra-implicit",
  "-Wunused",
  "-Wvalue-discard",
  "-Xlint",
  "-Xlint:-byname-implicit",
  "-Xlint:-implicit-recursion"
)

libraryDependencies ++= singles ++ batches.flatMap(_.modules)

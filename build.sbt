ThisBuild / scalaVersion := "2.13.8"
ThisBuild / organization := "com.alejandrohdezma"

addCommandAlias("ci-test", "fix --check; mdoc; test")
addCommandAlias("ci-docs", "github; headerCreateAll; mdoc")
addCommandAlias("ci-publish", "github; ci-release")

lazy val documentation = project
  .enablePlugins(MdocPlugin)
  .settings(mdocOut := file("."))
  .settings(scalacOptions += "-Ymacro-annotations")
  .dependsOn(`tapir-anyof` % "compile->test")

lazy val `tapir-anyof` = module
  .settings(scalacOptions += "-Ymacro-annotations")
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-core" % "0.20.1")
  .settings(libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.9")
  .settings(libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.11" % Test)
  .settings(libraryDependencies += "com.alejandrohdezma" %% "http4s-munit" % "0.9.3" % Test)
  .settings(libraryDependencies += "io.circe" %% "circe-generic-extras" % "0.14.1" % Test)
  .settings(libraryDependencies += "org.http4s" %% "http4s-circe" % "0.23.11" % Test)
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-derevo" % "0.20.1" % Test)
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "0.20.1" % Test)
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "0.20.1" % Test)
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % "0.20.1" % Test)
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % "0.20.1" % Test)
  .settings(addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full))

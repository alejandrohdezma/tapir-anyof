ThisBuild / scalaVersion           := "2.13.14"
ThisBuild / organization           := "com.alejandrohdezma"
ThisBuild / versionPolicyIntention := Compatibility.BinaryCompatible

addCommandAlias("ci-test", "fix --check; versionPolicyCheck; mdoc; test")
addCommandAlias("ci-docs", "github; headerCreateAll; mdoc")
addCommandAlias("ci-publish", "versionCheck; github; ci-release")

lazy val documentation = project
  .enablePlugins(MdocPlugin)
  .settings(scalacOptions += "-Ymacro-annotations")
  .dependsOn(`tapir-anyof` % "compile->test")

lazy val `tapir-anyof` = module
  .settings(scalacOptions += "-Ymacro-annotations")
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-core" % "1.11.1")
  .settings(libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.12")
  .settings(libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.5.6" % Test)
  .settings(libraryDependencies += "com.alejandrohdezma" %% "http4s-munit" % "1.0.0" % Test)
  .settings(libraryDependencies += "io.circe" %% "circe-generic-extras" % "0.14.4" % Test)
  .settings(libraryDependencies += "org.http4s" %% "http4s-circe" % "0.23.27" % Test)
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-derevo" % "1.11.1" % Test)
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.11.1" % Test)
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "1.11.1" % Test)
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % "1.11.1" % Test)
  .settings(libraryDependencies += "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml" % "0.11.3" % Test)
  .settings(addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.13.3" cross CrossVersion.full))

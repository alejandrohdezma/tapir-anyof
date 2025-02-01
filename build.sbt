ThisBuild / scalaVersion           := "2.13.16"
ThisBuild / crossScalaVersions     := Seq("2.13.16", "3.3.5")
ThisBuild / organization           := "com.alejandrohdezma"
ThisBuild / versionPolicyIntention := Compatibility.BinaryCompatible

addCommandAlias("ci-test", "fix --check; versionPolicyCheck; mdoc; +tapir-anyof/test")
addCommandAlias("ci-docs", "github; headerCreateAll; mdoc")
addCommandAlias("ci-publish", "versionCheck; github; ci-release")

lazy val documentation = project
  .enablePlugins(MdocPlugin)
  .settings(scalacOptions += "-Ymacro-annotations")
  .settings(libraryDependencies += "io.circe" %% "circe-generic-extras" % "0.14.4")
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-derevo" % "1.11.1")
  .dependsOn(`tapir-anyof` % "compile->test")

lazy val `tapir-anyof` = module
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-core" % "1.11.13")
  .settings(libraryDependencies ++= scalaVersion.value.on(2)("com.chuusai" %% "shapeless" % "2.3.12"))
  .settings(libraryDependencies ++= scalaVersion.value.on(3)("org.typelevel" %% "shapeless3-deriving" % "3.4.3"))
  .settings(libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.5.16" % Test)
  .settings(libraryDependencies += "com.alejandrohdezma" %% "http4s-munit" % "1.1.0" % Test)
  .settings(libraryDependencies += "org.http4s" %% "http4s-circe" % "0.23.30" % Test)
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.11.13" % Test)
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "1.11.13" % Test)
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % "1.11.13" % Test)
  .settings(libraryDependencies += "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml" % "0.11.7" % Test)
  .settings(libraryDependencies ++= scalaVersion.value.on(2)(kindProjector))

def kindProjector = compilerPlugin("org.typelevel" %% "kind-projector" % "0.13.3").cross(CrossVersion.full)

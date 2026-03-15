ThisBuild / scalaVersion           := "2.13.18"
ThisBuild / crossScalaVersions     := Seq("2.13.18", "3.3.7")
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
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-core" % "1.13.11")
  .settings(libraryDependencies ++= scalaVersion.value.on(2)("com.chuusai" %% "shapeless" % "2.3.13"))
  .settings(libraryDependencies ++= scalaVersion.value.on(3)("org.typelevel" %% "shapeless3-deriving" % "3.5.0"))
  .settings(libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.5.32" % Test)
  .settings(libraryDependencies += "com.alejandrohdezma" %% "http4s-munit" % "1.1.0" % Test)
  .settings(libraryDependencies += "org.http4s" %% "http4s-circe" % "0.23.33" % Test)
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.13.11" % Test)
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "1.13.11" % Test)
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % "1.13.11" % Test)
  .settings(libraryDependencies += "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml" % "0.11.10" % Test)
  .settings(libraryDependencies ++= scalaVersion.value.on(2)(kindProjector))

def kindProjector = compilerPlugin("org.typelevel" %% "kind-projector" % "0.13.4").cross(CrossVersion.full)

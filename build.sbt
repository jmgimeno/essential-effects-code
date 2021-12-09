ThisBuild / scalaVersion := "2.13.7"
ThisBuild / organization := "com.innerproduct"
ThisBuild / version := "0.0.1-SNAPSHOT"
ThisBuild / fork := true

val CatsVersion = "2.7.0"
val CatsEffectVersion = "3.3.0"
val CatsTaglessVersion = "0.14.0"
val CirceVersion = "0.14.1"
val Http4sVersion = "0.23.7"
val LogbackVersion = "1.2.7"
val MunitVersion = "0.7.27"

val commonSettings =
  Seq(
    addCompilerPlugin(
      "org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full
    ),
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % MunitVersion % Test
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )

lazy val exercises = (project in file("exercises"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % CatsEffectVersion,
      "org.typelevel" %% "cats-effect-laws" % CatsEffectVersion % Test,
      "org.typelevel" %% "cats-effect-testkit" % CatsEffectVersion % Test
    ),
    // remove fatal warnings since exercises have unused and dead code blocks
    scalacOptions --= Seq(
      "-Xfatal-warnings"
    )
  )

lazy val petstore = (project in file("case-studies") / "petstore")
  .dependsOn(exercises % "test->test;compile->compile")
  .settings(commonSettings)
  .settings(
    scalacOptions += "-Ymacro-annotations", // required by cats-tagless-macros
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.typelevel" %% "cats-tagless-macros" % CatsTaglessVersion,
      "org.scalameta" %% "munit-scalacheck" % MunitVersion % Test
    )
  )

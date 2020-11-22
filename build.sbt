ThisBuild / name := "stonks"
ThisBuild / organization := "ru.stonks"

lazy val stonks = project
  .in(file("."))
  .aggregate(app)

lazy val app = project
  .in(file("app"))
  .settings(Settings.Common)
  .settings(
    Compile / mainClass := Some("ru.stonks.app.App"),
    libraryDependencies ++= Seq(
      Dependency.CatsCore,
      Dependency.CatsEffect,
      Dependency.HttpsBlazeServer,
      Dependency.HttpsDsl,
      Dependency.PureConfig,
      Dependency.PureConfigCatsEffect,
      Dependency.Slf4jApi,
      Dependency.Slf4jSimple,
      Dependency.LogbackClassic,
      Dependency.Log4catsSlf4j,
    )
  )
ThisBuild / name := "stonks"
ThisBuild / organization := "ru.stonks"

lazy val stonks = project
  .in(file("."))
  .enablePlugins(FlywayPlugin)
  .settings(Settings.Flyway)
  .settings(libraryDependencies ++= Seq(Dependency.Postgresql))
  .aggregate(
    app,
    entity
  )

lazy val app = project
  .in(file("app"))
  .settings(Settings.Common)
  .settings(
    Compile / mainClass := Some("ru.stonks.app.App"),
    libraryDependencies ++= Seq(
      Dependency.CatsCore,
      Dependency.CatsEffect,
      Dependency.HttpsDsl,
      Dependency.HttpsBlazeServer,
      Dependency.HttpsBlazeClient,
      Dependency.Pureconfig,
      Dependency.PureconfigCatsEffect,
      Dependency.Slf4jApi,
      Dependency.Slf4jSimple,
      Dependency.LogbackClassic,
      Dependency.Log4catsSlf4j,
      Dependency.DoobieHikari,
      Dependency.DoobiePostgres
    ))
  .dependsOn(
    entity,
    finance,
    nasdaq
  )

lazy val entity = project
  .in(file("entity"))
  .settings(Settings.Common)

lazy val finance = project
  .in(file("finance"))
  .settings(Settings.Common)
  .settings(
    libraryDependencies ++= Seq(
      Dependency.CatsCore,
      Dependency.CatsEffect,
      Dependency.MacwireMacros,
      Dependency.HttpsBlazeClient,
      Dependency.HttpsCirce,
      Dependency.CirceCore,
      Dependency.CirceGeneric,
      Dependency.CirceParser,
      Dependency.DoobieCore
    ))
  .dependsOn(
    entity,
    finance_core,
    nasdaq_core
  )

lazy val finance_core = project
  .in(file("finance_core"))
  .settings(Settings.Common)
  .dependsOn(entity)

lazy val nasdaq = project
  .in(file("nasdaq"))
  .settings(Settings.Common)
  .settings(
    libraryDependencies ++= Seq(
      Dependency.CatsCore,
      Dependency.CatsEffect,
      Dependency.MacwireMacros,
      Dependency.HttpsBlazeClient,
      Dependency.HttpsCirce,
      Dependency.CirceCore,
      Dependency.CirceGeneric,
      Dependency.CirceParser,
      Dependency.DoobieCore
    ))
  .dependsOn(
    entity,
    nasdaq_core
  )

lazy val nasdaq_core = project
  .in(file("nasdaq_core"))
  .settings(Settings.Common)
  .dependsOn(entity)
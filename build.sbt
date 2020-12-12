import sbtassembly.AssemblyKeys.assembly
import sbtassembly.{MergeStrategy, PathList}

ThisBuild / name := "stonks"
ThisBuild / organization := "ru.stonks"

lazy val stonks = project
  .in(file("."))
  .enablePlugins(FlywayPlugin)
  .settings(Settings.Flyway)
  .settings(libraryDependencies ++= Seq(Dependency.Postgresql))
  .aggregate(
    app,
    entity,
    finance,
    finance_core,
    nasdaq,
    nasdaq_core,
    algorithm,
    algorithm_core,
    user,
    user_core,
    bot,
    bot_core
  )

lazy val app = project
  .in(file("app"))
  .settings(Settings.Common)
  .settings(
    Compile / mainClass := Some("ru.stonks.app.App"),
    assembly / test := {},
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", _) => MergeStrategy.discard
      case _ => MergeStrategy.first
    },
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
    nasdaq,
    algorithm,
    user,
    bot
  )

lazy val entity = project
  .in(file("entity"))
  .settings(Settings.Common)
  .settings(
    libraryDependencies ++= Seq(
      Dependency.Enumeratum
    )
  )

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

lazy val algorithm = project
  .in(file("algorithm"))
  .settings(Settings.Common)
  .settings(
    libraryDependencies ++= Seq(
      Dependency.CatsCore,
      Dependency.CatsEffect,
      Dependency.MacwireMacros,
      Dependency.Scalatest,
      Dependency.Scalamock
    ))
  .dependsOn(
    entity,
    algorithm_core,
    finance_core
  )

lazy val algorithm_core = project
  .in(file("algorithm_core"))
  .settings(Settings.Common)
  .dependsOn(entity)

lazy val user = project
  .in(file("user"))
  .settings(Settings.Common)
  .settings(
    libraryDependencies ++= Seq(
      Dependency.CatsCore,
      Dependency.CatsEffect,
      Dependency.MacwireMacros,
      Dependency.DoobieCore
    ))
  .dependsOn(
    entity,
    user_core
  )

lazy val user_core = project
  .in(file("user_core"))
  .settings(Settings.Common)
  .dependsOn(entity)

lazy val bot = project
  .in(file("bot"))
  .settings(Settings.Common)
  .settings(
    libraryDependencies ++= Seq(
      Dependency.CatsCore,
      Dependency.CatsEffect,
      Dependency.MacwireMacros,
      Dependency.DoobieCore,
      Dependency.TelegramiumCore,
      Dependency.TelegramiumHigh
    ))
  .dependsOn(
    entity,
    bot_core,
    user_core,
    algorithm_core,
    finance_core
  )

lazy val bot_core = project
  .in(file("bot_core"))
  .settings(Settings.Common)
  .dependsOn(entity)

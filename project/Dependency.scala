import sbt._

object Dependency {

  object Version {
    val Scala       = "2.13.4"
    val Enumeratum  = "1.6.1"
    val CatsCore    = "2.2.0"
    val CatsEffect  = "2.2.0"
    val Http4s      = "0.21.7"
    val Circe       = "0.13.0"
    val Postgresql  = "42.2.18"
    val Doobie      = "0.9.4"
    val Pureconfig  = "0.14.0"
    val Slf4j       = "1.7.30"
    val Logback     = "1.2.3"
    val Log4cats    = "1.1.1"
    val Macwire     = "2.3.7"
    val Scalatest   = "3.2.2"
    val Scalamock   = "5.0.0"
    val Telegramium = "2.50.0"
  }

  val Enumeratum = "com.beachape" %% "enumeratum" % Version.Enumeratum

  val CatsCore   = "org.typelevel" %% "cats-core" % Version.CatsCore
  val CatsEffect = "org.typelevel" %% "cats-effect" % Version.CatsEffect

  val HttpsDsl         = "org.http4s" %% "http4s-dsl" % Version.Http4s
  val HttpsBlazeServer = "org.http4s" %% "http4s-blaze-server" % Version.Http4s
  val HttpsBlazeClient = "org.http4s" %% "http4s-blaze-client" % Version.Http4s
  val HttpsCirce       = "org.http4s" %% "http4s-circe" % Version.Http4s

  val CirceCore    = "io.circe" %% "circe-core" % Version.Circe
  val CirceParser  = "io.circe" %% "circe-parser" % Version.Circe
  val CirceGeneric = "io.circe" %% "circe-generic" % Version.Circe

  val Postgresql = "org.postgresql" % "postgresql" % Version.Postgresql

  val DoobieCore     = "org.tpolecat" %% "doobie-core" % Version.Doobie
  val DoobieHikari   = "org.tpolecat" %% "doobie-hikari" % Version.Doobie
  val DoobiePostgres = "org.tpolecat" %% "doobie-postgres" % Version.Doobie

  val Pureconfig           = "com.github.pureconfig" %% "pureconfig" % Version.Pureconfig
  val PureconfigCatsEffect = "com.github.pureconfig" %% "pureconfig-cats-effect" % Version.Pureconfig

  val Slf4jApi       = "org.slf4j" % "slf4j-api" % Version.Slf4j
  val Slf4jSimple    = "org.slf4j" % "slf4j-simple" % Version.Slf4j
  val LogbackClassic = "ch.qos.logback" % "logback-classic" % Version.Logback
  val Log4catsSlf4j  = "io.chrisdavenport" %% "log4cats-slf4j" % Version.Log4cats

  val MacwireMacros = "com.softwaremill.macwire" %% "macros" % Version.Macwire % Provided

  val TelegramiumCore = "io.github.apimorphism" %% "telegramium-core" % Version.Telegramium
  val TelegramiumHigh = "io.github.apimorphism" %% "telegramium-high" % Version.Telegramium

  val Scalatest = "org.scalatest" %% "scalatest" % Version.Scalatest % Test
  val Scalamock = "org.scalamock" %% "scalamock" % Version.Scalamock % Test
}

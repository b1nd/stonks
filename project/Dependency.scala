import sbt._

object Dependency {

  object Version {
    val Scala      = "2.13.4"
    val CatsCore   = "2.2.0"
    val CatsEffect = "2.2.0"
    val Http4s     = "0.21.11"
    val PureConfig = "0.14.0"
    val Slf4j      = "1.7.30"
    val Logback    = "1.2.3"
    val Log4cats   = "1.1.1"
  }

  val CatsCore   = "org.typelevel" %% "cats-core" % Version.CatsCore
  val CatsEffect = "org.typelevel" %% "cats-effect" % Version.CatsEffect

  val HttpsDsl         = "org.http4s" %% "http4s-dsl" % Version.Http4s
  val HttpsBlazeServer = "org.http4s" %% "http4s-blaze-server" % Version.Http4s

  val PureConfig           = "com.github.pureconfig" %% "pureconfig" % Version.PureConfig
  val PureConfigCatsEffect = "com.github.pureconfig" %% "pureconfig-cats-effect" % Version.PureConfig

  val Slf4jApi       = "org.slf4j" % "slf4j-api" % Version.Slf4j
  val Slf4jSimple    = "org.slf4j" % "slf4j-simple" % Version.Slf4j
  val LogbackClassic = "ch.qos.logback" % "logback-classic" % Version.Logback
  val Log4catsSlf4j  = "io.chrisdavenport" %% "log4cats-slf4j" % Version.Log4cats
}

import io.github.davidmweber.FlywayPlugin.autoImport._
import sbt.Keys._

object Settings {
  val Common = Seq(
    scalaVersion := Dependency.Version.Scala,
    scalacOptions ++= Compiler.Options
  )

  val Flyway = Seq(
    flywayUrl := "jdbc:postgresql://192.168.0.179:5432/stonks",
    flywayUser := "postgres",
    flywayPassword := "postgrespassword",
    flywayLocations := Seq("filesystem:db/migration/postgresql")
  )
}

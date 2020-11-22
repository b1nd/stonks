import sbt.Keys._

object Settings {
  val Common = Seq(
    scalaVersion := Dependency.Version.Scala,
    scalacOptions ++= Compiler.Options
  )
}

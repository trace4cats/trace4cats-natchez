import sbt._

object Dependencies {
  object Versions {
    val scala212 = "2.12.17"
    val scala213 = "2.13.10"
    val scala3 = "3.3.0"

    val trace4cats = "0.14.1"

    val natchez = "0.3.1"

    val kindProjector = "0.13.2"
    val betterMonadicFor = "0.3.1"
  }

  lazy val trace4catsCore = "io.janstenpickle" %% "trace4cats-core" % Versions.trace4cats

  lazy val natchez = "org.tpolecat" %% "natchez-core" % Versions.natchez

  lazy val kindProjector = ("org.typelevel" % "kind-projector"     % Versions.kindProjector).cross(CrossVersion.full)
  lazy val betterMonadicFor = "com.olegpy" %% "better-monadic-for" % Versions.betterMonadicFor
}

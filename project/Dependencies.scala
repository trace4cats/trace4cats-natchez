import sbt._

object Dependencies {
  object Versions {
    val scala212 = "2.12.14"
    val scala213 = "2.13.6"

    val trace4cats = "0.12.0-RC1+162-70070fb2"

    val natchez = "0.1.5"
  }

  lazy val trace4catsInject = "io.janstenpickle" %% "trace4cats-inject" % Versions.trace4cats

  lazy val natchez = "org.tpolecat" %% "natchez-core" % Versions.natchez
}

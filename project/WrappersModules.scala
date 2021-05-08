import sbt.Keys._
import sbt.{Project, Provided}

object WrappersModules {

  lazy val baseLoggingProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-wrappers-scala-logging")
    .settings(
      libraryDependencies ++= Seq(Dependencies.logback, Dependencies.scalaLogging)
    )

  lazy val playLoggingProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-wrappers-play-logging")
    .settings(libraryDependencies ++= Seq(Dependencies.play % Provided))

}

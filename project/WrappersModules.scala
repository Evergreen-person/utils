import sbt.Keys._
import sbt.{Project, Provided, Test}

object WrappersModules {

  lazy val baseLoggingProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-wrappers-scala-logging")
    .settings(
      libraryDependencies ++= Seq(Dependencies.logback, Dependencies.scalaLogging)
    )

  lazy val playErrorHandlers: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-wrappers-play-error-handlers")
    .settings(
      libraryDependencies ++=
        Seq(
          Dependencies.play      % Provided,
          Dependencies.playGuice % Test,
          Dependencies.config    % Test,
          Dependencies.guice,
          Dependencies.logbackSentry
        )
    )

}

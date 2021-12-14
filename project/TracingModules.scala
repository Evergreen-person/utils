import sbt.Keys._
import sbt.{CrossVersion, Project, Provided, addCompilerPlugin}

object TracingModules {

  lazy val coreProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-tracing-core")
    .settings(
      addCompilerPlugin(Dependencies.kindProjector.cross(CrossVersion.full))
    )
    .settings(libraryDependencies ++= Seq(Dependencies.catsEffect % Provided))

  lazy val noopProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-tracing-noop")
    .settings(
      libraryDependencies ++= Seq(Dependencies.cats, Dependencies.catsEffect)
    )

  lazy val aopProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-tracing-aop")
    .settings(
      addCompilerPlugin(Dependencies.kindProjector.cross(CrossVersion.full))
    )
    .settings(
      libraryDependencies ++=
        Seq(
          Dependencies.catsEffect % Provided,
          Dependencies.catsTaglessCore,
          Dependencies.catsTaglessMacros
        )
    )

  lazy val log4catsProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-tracing-log4cats")
    .settings(
      addCompilerPlugin(Dependencies.kindProjector.cross(CrossVersion.full))
    )
    .settings(
      libraryDependencies ++= Seq(Dependencies.log4catsSlf4j, Dependencies.cats)
    )

  lazy val opentracingProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-tracing-opentracing")
    .settings(
      libraryDependencies ++=
        Seq(
          Dependencies.opentracingApi,
          Dependencies.opentracingUtil,
          Dependencies.catsEffect % Provided
        )
    )

  lazy val endpoints4sCoreProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-tracing-endpoints4s-core")
    .settings(libraryDependencies ++= Seq(Dependencies.endpoints4s))

  lazy val endpoints4sHttp4sClientProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-tracing-endpoints4s-http4s-client")
    .settings(
      addCompilerPlugin(Dependencies.kindProjector.cross(CrossVersion.full))
    )
    .settings(
      libraryDependencies ++= Seq(Dependencies.endpoints4sHttp4sClient)
    )

  lazy val endpoints4sHttp4sServerProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-tracing-endpoints4s-http4s-server")
    .settings(
      addCompilerPlugin(Dependencies.kindProjector.cross(CrossVersion.full))
    )
    .settings(
      libraryDependencies ++= Seq(Dependencies.endpoints4sHttp4sServer)
    )

  lazy val endpoints4sPlayClientProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-tracing-endpoints4s-play-client")
    .settings(
      addCompilerPlugin(Dependencies.kindProjector.cross(CrossVersion.full))
    )
    .settings(
      libraryDependencies ++= Seq(Dependencies.endpoints4sPlayClient, Dependencies.catsEffect)
    )

  lazy val http4sClientProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-tracing-http4s-client")
    .settings(
      addCompilerPlugin(Dependencies.kindProjector.cross(CrossVersion.full))
    )
    .settings(libraryDependencies ++= Seq(Dependencies.http4sClient))

  lazy val http4sServerProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-tracing-http4s-server")
    .settings(
      addCompilerPlugin(Dependencies.kindProjector.cross(CrossVersion.full))
    )
    .settings(
      libraryDependencies ++= Seq(Dependencies.http4sDsl, Dependencies.http4sServer)
    )

  lazy val distageModulesProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-tracing-distage-modules")
    .settings(
      addCompilerPlugin(Dependencies.kindProjector.cross(CrossVersion.full))
    )
    .settings(
      libraryDependencies ++= Seq(Dependencies.jaegerClient, Dependencies.distageCore)
    )

}

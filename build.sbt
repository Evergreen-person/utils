ThisBuild / resolvers += "Artima Maven Repository".at("https://repo.artima.com/releases")

// zzzzzzzzzzzzzzzzzzzz Common Modules zzzzzzzzzzzzzzzzzzzz

lazy val scalaLogging = project
  .in(file("base/scala-logging"))
  .configure(BaseModules.scalaLoggingProfile)

lazy val reactivemongoBsonJodaTime = project
  .in(file("base/reactivemongo/bson-joda-time"))
  .configure(BaseModules.reactivemongoBsonJodaTimeProfile)

// zzzzzzzzzzzzzzzzzzzz Implicits Modules zzzzzzzzzzzzzzzzzzzz

lazy val implicitsBoolean = project
  .in(file("implicits/boolean"))
  .configure(ImplicitsModules.booleanProfile)

lazy val implicitsCollection = project
  .in(file("implicits/collection"))
  .configure(ImplicitsModules.collectionProfile)

// zzzzzzzzzzzzzzzzzzzz ZIO Modules zzzzzzzzzzzzzzzzzzzz

lazy val zioMonitoringPrometheus = project
  .in(file("zio/monitoring/prometheus"))
  .configure(ZioModules.monitoringPrometheusProfile)

lazy val zioReactivemongo = project
  .in(file("zio/reactivemongo"))
  .configure(ZioModules.reactivemongoProfile)

// zzzzzzzzzzzzzzzzzzzz Tracing Modules zzzzzzzzzzzzzzzzzzzz

lazy val tracingCore = project.in(file("tracing/core")).configure(TracingModules.coreProfile)

lazy val tracingNoOp = project
  .in(file("tracing/noop"))
  .configure(TracingModules.noopProfile)
  .dependsOn(tracingCore)

lazy val tracingAOP = project
  .in(file("tracing/aop"))
  .configure(TracingModules.aopProfile)
  .dependsOn(tracingCore)

lazy val tracingLog4Cats = project
  .in(file("tracing/log4cats"))
  .configure(TracingModules.log4catsProfile)
  .dependsOn(tracingCore)

lazy val tracingOpentracing = project
  .in(file("tracing/opentracing"))
  .configure(TracingModules.opentracingProfile)
  .dependsOn(tracingCore)

lazy val tracingEndpoints4sCore = project
  .in(file("tracing/endpoints4s/core"))
  .configure(TracingModules.endpoints4sCoreProfile)

lazy val tracingEndpoints4sHttp4sClient = project
  .in(file("tracing/endpoints4s/http4s/client"))
  .configure(TracingModules.endpoints4sHttp4sClientProfile)
  .dependsOn(tracingCore, tracingEndpoints4sCore)

lazy val tracingEndpoints4sHttp4sServer = project
  .in(file("tracing/endpoints4s/http4s/server"))
  .configure(TracingModules.endpoints4sHttp4sServerProfile)
  .dependsOn(tracingCore, tracingEndpoints4sCore)

lazy val tracingEndpoints4sPlayClient = project
  .in(file("tracing/endpoints4s/play/client"))
  .configure(TracingModules.endpoints4sPlayClientProfile)
  .dependsOn(tracingCore, tracingEndpoints4sCore)

lazy val tracingHttp4sClient = project
  .in(file("tracing/http4s/client"))
  .configure(TracingModules.http4sClientProfile)
  .dependsOn(tracingCore)

lazy val tracingHttp4sServer = project
  .in(file("tracing/http4s/server"))
  .configure(TracingModules.http4sServerProfile)
  .dependsOn(tracingCore)

lazy val tracingDistageModules = project
  .in(file("tracing/distage/modules"))
  .configure(TracingModules.distageModulesProfile)
  .dependsOn(tracingOpentracing, tracingNoOp, tracingLog4Cats)

// format: off
inThisBuild(
  List(
    organization := "io.kinoplan",
    homepage := Some(url("https://github.com/kinoplan/utils")),
    licenses := Seq("Apache-2.0" -> url("https://opensource.org/licenses/Apache-2.0")),
    developers := List(Developer("kinoplan", "Kinoplan", "job@kinoplan.ru", url("https://kinoplan.tech"))),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/kinoplan/utils"),
        "scm:git:git@github.com:kinoplan/utils.git"
      )
    )
  )
)

onChangedBuildSource in Global := ReloadOnSourceChanges
// format: on

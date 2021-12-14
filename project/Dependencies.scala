import sbt._

object Dependencies {

  object Versions {
    val catsTaglessV   = "0.14.0"
    val endpoints4sV   = "1.5.0"
    val http4sV        = "0.21.31"
    val opentracingV   = "0.33.0"
    val reactivemongoV = "1.0.8"
    val zioV           = "1.0.13"
    val zioConfigV     = "1.0.10"
  }

  import Versions._

  //A -> Z
  val cats                    = "org.typelevel"              %% "cats-core"              % "2.2.0"
  val catsEffect              = "org.typelevel"              %% "cats-effect"            % "2.5.4"
  val catsTaglessCore         = "org.typelevel"              %% "cats-tagless-core"      % catsTaglessV
  val catsTaglessMacros       = "org.typelevel"              %% "cats-tagless-macros"    % catsTaglessV
  val distageCore             = "io.7mind.izumi"             %% "distage-core"           % "1.0.8"
  val endpoints4s             = "org.endpoints4s"            %% "algebra"                % endpoints4sV
  val endpoints4sHttp4sClient = "org.endpoints4s"            %% "http4s-client"          % "4.0.0"
  val endpoints4sHttp4sServer = "org.endpoints4s"            %% "http4s-server"          % "5.0.0"
  val endpoints4sPlayClient   = "org.endpoints4s"            %% "play-client"            % "4.0.0"
  val http4sClient            = "org.http4s"                 %% "http4s-blaze-client"    % http4sV
  val http4sDsl               = "org.http4s"                 %% "http4s-dsl"             % http4sV
  val http4sServer            = "org.http4s"                 %% "http4s-blaze-server"    % http4sV
  val jaegerClient            = "io.jaegertracing"            % "jaeger-client"          % "1.5.0"
  val jodaTime                = "joda-time"                   % "joda-time"              % "2.10.13"
  val kindProjector           = "org.typelevel"              %% "kind-projector"         % "0.13.2"
  val log4catsSlf4j           = "org.typelevel"              %% "log4cats-slf4j"         % "1.4.0"
  val logback                 = "ch.qos.logback"              % "logback-classic"        % "1.2.8"
  val mockitoScala            = "org.scalatestplus"          %% "mockito-3-4"            % "3.2.10.0" % Test
  val opentracingApi          = "io.opentracing"              % "opentracing-api"        % opentracingV
  val opentracingUtil         = "io.opentracing"              % "opentracing-util"       % opentracingV
  val reactiveMongo           = "org.reactivemongo"          %% "reactivemongo"          % reactivemongoV
  val scalaLogging            = "com.typesafe.scala-logging" %% "scala-logging"          % "3.9.3"
  val scalatest               = "org.scalatest"              %% "scalatest"              % "3.2.10"   % Test
  val zio                     = "dev.zio"                    %% "zio"                    % zioV
  val zioConfig               = "dev.zio"                    %% "zio-config"             % zioConfigV
  val zioConfigTypesafe       = "dev.zio"                    %% "zio-config-typesafe"    % zioConfigV
  val zioLoggingSlf4j         = "dev.zio"                    %% "zio-logging-slf4j"      % "0.5.13"
  val zioMetricsPrometheus    = "dev.zio"                    %% "zio-metrics-prometheus" % zioV
  val zioPrelude              = "dev.zio"                    %% "zio-prelude"            % "1.0.0-RC8"
}

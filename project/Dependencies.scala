import sbt._

object Dependencies {

  object Versions {
    val playV = "2.8.8"
  }

  import Versions._

  //A -> Z
  val config        = "com.typesafe"                % "config"          % "1.4.1"
  val guice         = "com.google.inject"           % "guice"           % "5.0.1"
  val logback       = "ch.qos.logback"              % "logback-classic" % "1.2.6"
  val logbackSentry = "io.sentry"                   % "sentry-logback"  % "1.7.30" //TODO: 5.x.x
  val mockitoScala  = "org.scalatestplus"          %% "mockito-3-4"     % "3.2.9.0" % Test
  val play          = "com.typesafe.play"          %% "play"            % playV
  val playGuice     = "com.typesafe.play"          %% "play-guice"      % playV
  val scalaLogging  = "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.3"
  val scalatest     = "org.scalatest"              %% "scalatest"       % "3.2.9" % Test
}

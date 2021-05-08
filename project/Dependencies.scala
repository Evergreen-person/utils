import sbt._

object Dependencies {
  //A -> Z
  val logback      = "ch.qos.logback"              % "logback-classic" % "1.2.3"
  val mockitoScala = "org.scalatestplus"          %% "mockito-3-4"     % "3.2.8.0" % Test
  val play         = "com.typesafe.play"          %% "play"            % "2.8.8"
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.3"
  val scalatest    = "org.scalatest"              %% "scalatest"       % "3.2.8"   % Test
}

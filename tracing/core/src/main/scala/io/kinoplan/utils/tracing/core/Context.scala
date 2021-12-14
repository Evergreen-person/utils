package io.kinoplan.utils.tracing.core

trait Context {
  def correlationId: String

  def traceIdO: Option[String]

  def spanIdO: Option[String]

  def toHeaders: Map[String, String]
}

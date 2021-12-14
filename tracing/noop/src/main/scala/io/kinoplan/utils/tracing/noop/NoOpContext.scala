package io.kinoplan.utils.tracing.noop

import io.kinoplan.utils.tracing.core.{Context, ContextBuilder, Kernel}

class NoOpContext(override val correlationId: String) extends Context {
  override def traceIdO: Option[String] = None

  override def spanIdO: Option[String] = None

  override def toHeaders: Map[String, String] = Map("X-Request-Id" -> correlationId)
}

object NoOpContext {
  def apply(correlationId: String): NoOpContext = new NoOpContext(correlationId)

  def builder: ContextBuilder = (kernel: Kernel) => NoOpContext(kernel.getCorrelationId)
}

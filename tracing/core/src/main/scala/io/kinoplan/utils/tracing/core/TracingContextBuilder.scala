package io.kinoplan.utils.tracing.core

import java.util.UUID

import cats.effect.Resource

trait TracingContextBuilder[F[_]] {
  protected def newCorrelationId: String = UUID.randomUUID().toString

  def build(
    operationName: String,
    tags: Tags = Map.empty,
    correlationId: String = newCorrelationId
  ): Resource[F, TracingContext[F]]

  def continue(
    operationName: String,
    kernel: Kernel,
    tags: Tags = Map.empty
  ): Resource[F, TracingContext[F]]

}

object TracingContextBuilder {

  def apply[F[_]](implicit
    ev: TracingContextBuilder[F]
  ): TracingContextBuilder[F] = ev

}

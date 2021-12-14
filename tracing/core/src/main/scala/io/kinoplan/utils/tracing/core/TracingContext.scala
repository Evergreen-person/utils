package io.kinoplan.utils.tracing.core

import cats.effect.Resource

trait TracingContext[F[_]] {
  def context: Context

  def setOperationName(operationName: String): F[Unit]

  def addTags(tags: Tags): F[Unit]

  def span(operationName: String, tags: Tags = Map.empty): Resource[F, TracingContext[F]]
}

object TracingContext {

  def apply[F[_]](implicit
    ev: TracingContext[F]
  ): TracingContext[F] = ev

}

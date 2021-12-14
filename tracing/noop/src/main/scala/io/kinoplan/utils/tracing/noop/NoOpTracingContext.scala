package io.kinoplan.utils.tracing.noop

import cats.Applicative
import cats.effect.Resource

import io.kinoplan.utils.tracing.core._

class NoOpTracingContext[F[_]: Applicative](correlationId: String) extends TracingContext[F] {

  override def addTags(tags: Tags): F[Unit] = Applicative[F].unit

  override def span(operationName: String, tags: Tags): Resource[F, TracingContext[F]] = Resource
    .pure(NoOpTracingContext(correlationId))

  override def setOperationName(operationName: String): F[Unit] = Applicative[F].unit

  override def context: Context = NoOpContext(correlationId)
}

object NoOpTracingContext {
  def apply[F[_]: Applicative](correlationId: String) = new NoOpTracingContext[F](correlationId)

  def builder[F[_]: Applicative](): F[TracingContextBuilder[F]] = Applicative[F].pure {
    new TracingContextBuilder[F] {
      override def build(
        operationName: String,
        tags: Tags,
        correlationId: String
      ): Resource[F, TracingContext[F]] = Resource
        .pure(NoOpTracingContext(correlationId = correlationId))

      override def continue(
        operationName: String,
        kernel: Kernel,
        tags: Tags
      ): Resource[F, TracingContext[F]] = Resource.pure(NoOpTracingContext(kernel.getCorrelationId))
    }
  }

}

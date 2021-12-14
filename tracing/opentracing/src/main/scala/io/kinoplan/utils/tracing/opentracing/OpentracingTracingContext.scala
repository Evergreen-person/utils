package io.kinoplan.utils.tracing.opentracing

import java.util

import scala.collection.JavaConverters.mapAsJavaMapConverter

import cats.effect.{Resource, Sync}
import cats.effect.ExitCase.Error
import cats.implicits.{catsSyntaxOptionId, toFlatMapOps, toFunctorOps}
import io.opentracing.{Span, SpanContext, Tracer}
import io.opentracing.propagation.{Format, TextMapAdapter}
import io.opentracing.tag.{Tags => SpanTags}
import io.opentracing.util.GlobalTracer

import io.kinoplan.utils.tracing.core._

class OpentracingTracingContext[F[_]](tracer: Tracer, span: Span, correlationId: String)(implicit
  F: Sync[F]
) extends TracingContext[F] {

  override def addTags(tags: Tags): F[Unit] = F.delay(tags.foreach { case (key, value) =>
    span.setTag(key, value)
  })

  override def span(operationName: String, tags: Tags): Resource[F, TracingContext[F]] =
    OpentracingTracingContext(tracer, span.context().some, correlationId)(operationName, tags)

  override def setOperationName(operationName: String): F[Unit] = F
    .delay(span.setOperationName(operationName)).void

  override def context: Context = OpentracingContext(tracer, span.context().some, correlationId)
}

object OpentracingTracingContext {

  def apply[F[_]: Sync](
    tracer: Tracer,
    parentSpanContext: Option[SpanContext] = None,
    correlationId: String
  )(operationName: String, tags: Tags = Map.empty): Resource[F, OpentracingTracingContext[F]] =
    Resource.makeCase[F, Span] {
      Sync[F].delay {
        val span = tracer.buildSpan(operationName)
        parentSpanContext.fold(span)(span.asChildOf).start()
      }
    } {
      case (span, Error(e)) => Sync[F].delay(SpanTags.ERROR.set(span, true)).flatMap(_ =>
          Sync[F].delay(
            span.log(
              new util.HashMap[String, String]().put("message", e.getMessage)
            )
          )
        ).flatMap(_ => Sync[F].delay(span.finish()))
      case (span, _) => Sync[F].delay(span.finish())
    }.map(new OpentracingTracingContext[F](tracer, _, correlationId)).evalTap(_.addTags(tags))

  def builder[F[_]: Sync](tracer: Tracer): F[TracingContextBuilder[F]] = Sync[F]
    .delay(GlobalTracer.registerIfAbsent(tracer)).map { _ =>
      new TracingContextBuilder[F] {
        override def build(
          operationName: String,
          tags: Tags,
          correlationId: String
        ): Resource[F, TracingContext[F]] =
          OpentracingTracingContext(tracer, correlationId = correlationId)(operationName, tags)

        override def continue(
          operationName: String,
          kernel: Kernel,
          tags: Tags
        ): Resource[F, TracingContext[F]] = {
          val spanContextO: Option[SpanContext] = Option(
            tracer.extract(
              Format.Builtin.HTTP_HEADERS,
              new TextMapAdapter(kernel.toHeaders.asJava)
            )
          )

          OpentracingTracingContext(tracer, spanContextO, kernel.getCorrelationId)(
            operationName,
            tags
          )
        }
      }
    }

}

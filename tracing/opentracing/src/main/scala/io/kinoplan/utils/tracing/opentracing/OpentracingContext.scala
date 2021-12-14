package io.kinoplan.utils.tracing.opentracing

import scala.collection.JavaConverters.{mapAsJavaMapConverter, mapAsScalaMapConverter}

import io.opentracing.{SpanContext, Tracer}
import io.opentracing.propagation.{Format, TextMapAdapter}

import io.kinoplan.utils.tracing.core.{Context, ContextBuilder, Kernel}

class OpentracingContext(
  tracer: Tracer,
  spanContextO: Option[SpanContext],
  override val correlationId: String
) extends Context {

  override def traceIdO: Option[String] = spanContextO.map(_.toTraceId)

  override def spanIdO: Option[String] = spanContextO.map(_.toSpanId)

  override def toHeaders: Map[String, String] = spanContextO
    .fold(Map.empty[String, String]) { spanContext =>
      val hashMap = new java.util.HashMap[String, String]
      tracer.inject(
        spanContext,
        Format.Builtin.HTTP_HEADERS,
        new TextMapAdapter(hashMap)
      )
      hashMap.asScala.toMap
    } ++ Map("X-Request-Id" -> correlationId)

}

object OpentracingContext {

  def apply(
    tracer: Tracer,
    spanContextO: Option[SpanContext],
    correlationId: String
  ): OpentracingContext = new OpentracingContext(tracer, spanContextO, correlationId)

  def builder(tracer: Tracer): ContextBuilder = (kernel: Kernel) => {
    val spanContextO: Option[SpanContext] = Option(
      tracer.extract(
        Format.Builtin.HTTP_HEADERS,
        new TextMapAdapter(kernel.toHeaders.asJava)
      )
    )
    OpentracingContext(tracer, spanContextO, kernel.getCorrelationId)
  }

}

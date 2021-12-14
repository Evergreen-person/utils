package io.kinoplan.utils.tracing.distage.modules

import cats.effect.{Resource, Sync}
import distage.{ModuleDef, TagK}
import io.jaegertracing.Configuration
import io.opentracing
import izumi.distage.model.definition.Lifecycle

import io.kinoplan.utils.tracing.core._
import io.kinoplan.utils.tracing.core.Trace.Implicits.kleisliInstance
import io.kinoplan.utils.tracing.log4cats.Log4CatsTracingLogger.tracingContextInstance
import io.kinoplan.utils.tracing.opentracing.{OpentracingContext, OpentracingTracingContext}

object JaegerTracingModule {

  def apply[F[_]: TagK: Sync]: ModuleDef = new ModuleDef {
    make[opentracing.Tracer].fromResource(
      Lifecycle.fromCats(
        Resource.fromAutoCloseable(Sync[F].delay(Configuration.fromEnv().getTracer))
      )
    )
    make[TracingContextBuilder[F]].fromEffect(OpentracingTracingContext.builder[F] _)
      .modify(implicitly(_))
    make[ContextBuilder].from(OpentracingContext.builder _).modify(implicitly(_))

    addImplicit[TracingLogger[Traced[F, *]]]
    addImplicit[Trace[Traced[F, *]]]
  }

}

package io.kinoplan.utils.tracing.distage.modules

import cats.effect.Sync
import distage.TagK
import izumi.distage.model.definition.ModuleDef

import io.kinoplan.utils.tracing.core._
import io.kinoplan.utils.tracing.core.Trace.Implicits.kleisliInstance
import io.kinoplan.utils.tracing.log4cats.Log4CatsTracingLogger.tracingContextInstance
import io.kinoplan.utils.tracing.noop.{NoOpContext, NoOpTracingContext}

object NoOpTracingModule {

  def apply[F[_]: TagK: Sync]: ModuleDef = new ModuleDef {
    make[TracingContextBuilder[F]].fromEffect(NoOpTracingContext.builder()).modify(implicitly(_))
    make[ContextBuilder].from(NoOpContext.builder).modify(implicitly(_))

    addImplicit[Trace[Traced[F, *]]]
    addImplicit[TracingLogger[Traced[F, *]]]
  }

}

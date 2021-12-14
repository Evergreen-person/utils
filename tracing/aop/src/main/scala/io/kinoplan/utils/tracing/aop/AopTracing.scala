package io.kinoplan.utils.tracing.aop

import cats.{Show, ~>}
import cats.data.Kleisli
import cats.effect.Sync
import cats.implicits.catsSyntaxFlatMapOps
import cats.tagless.aop.Aspect
import cats.tagless.aop.Aspect.Weave

import io.kinoplan.utils.tracing.core.{Tags, Traced}

object AopTracing {

  def traceWithMethodNamesAndArgs[F[_]: Sync](
    tags: Tags = Map.empty
  ): Aspect.Weave.Function[F, Show, *] ~> Traced[F, *] =
    new (Aspect.Weave.Function[F, Show, *] ~> Traced[F, *]) {

      override def apply[A](fa: Weave.Function[F, Show, A]): Traced[F, A] =
        Kleisli { parentContext =>
          parentContext.span(
            operationName = s"${fa.algebraName}.${fa.instrumentation.methodName}"
          ).use { context =>
            val args = fa.domain.flatMap(
              _.map(arg => s"args.${arg.name}" -> arg.instance.show(arg.target.value))
            )
            context.addTags(args.toMap ++ tags) >> fa.codomain.target
          }
        }

    }

}

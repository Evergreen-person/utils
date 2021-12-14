package io.kinoplan.utils.tracing.aop.instances

import scala.concurrent.Future

import cats.{Show, ~>}
import cats.effect.{ContextShift, IO, LiftIO, Sync}
import cats.tagless.aop.Aspect
import cats.tagless.implicits._

import io.kinoplan.utils.tracing.aop.AopTracing.traceWithMethodNamesAndArgs
import io.kinoplan.utils.tracing.core.Trace

object MongoTracingAop {

  def liftToTrace[G[_[_]], F[_]: LiftIO: Sync](db: String, collection: String)(
    dao: G[Future[*]]
  )(implicit
    cs: ContextShift[IO],
    A: Aspect.Function[G, Show]
  ): G[Trace[F, *]] = dao.mapK[F](
    λ[Future ~> F](future => LiftIO[F].liftIO(IO.fromFuture(IO(future))))
  ).weaveFunction.mapK(
    traceWithMethodNamesAndArgs(
      Map(
        "db.instance" -> db,
        "db.type" -> "mongodb",
        "db.collection" -> collection
      )
    )
  )

}

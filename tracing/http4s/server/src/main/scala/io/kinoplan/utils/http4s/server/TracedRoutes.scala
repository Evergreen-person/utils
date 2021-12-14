package io.kinoplan.utils.http4s.server

import cats.{Applicative, Defer}
import cats.data.{Kleisli, OptionT}
import cats.implicits.toTraverseOps
import org.http4s.Response

object TracedRoutes {

  type TracedRoutes[F[_]] = Kleisli[OptionT[F, *], TracedRequest[F], Response[F]]

  def of[F[_]](pf: PartialFunction[TracedRequest[F], F[Response[F]]])(implicit
    F: Defer[F],
    FA: Applicative[F]
  ): Kleisli[OptionT[F, *], TracedRequest[F], Response[F]] =
    Kleisli(req => OptionT(F.defer(pf.lift(req).sequence)))

}

package io.kinoplan.utils.tracing.core

import cats.data.Kleisli
import cats.effect.Bracket
import cats.implicits.catsSyntaxApplicativeId

trait Trace[F[_]] {
  def context: F[Context]

  def runWithSpan[A](operationName: String, tags: Tags = Map.empty)(fa: F[A]): F[A]

  def addTags(tags: Tags): F[Unit]
}

object Trace {

  def apply[F[_]](implicit
    ev: Trace[F]
  ): Trace[F] = ev

  object Implicits {

    implicit def kleisliInstance[F[_]](implicit
      ev: Bracket[F, Throwable]
    ): KleisliTrace[F] = new KleisliTrace[F]

    final class KleisliTrace[F[_]](implicit
      ev: Bracket[F, Throwable]
    ) extends Trace[Traced[F, *]] {
      override def context: Traced[F, Context] = Kleisli(_.context.pure[F])

      override def runWithSpan[A](operationName: String, tags: Tags)(
        fa: Traced[F, A]
      ): Traced[F, A] = Kleisli(_.span(operationName, tags).use(fa.run))

      override def addTags(tags: Tags): Traced[F, Unit] = Kleisli(_.addTags(tags))
    }

  }

}

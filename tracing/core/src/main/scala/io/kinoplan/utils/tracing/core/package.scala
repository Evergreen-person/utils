package io.kinoplan.utils.tracing

import cats.data.Kleisli

package object core {
  type Tags = Map[String, String]
  type Traced[F[_], A] = Kleisli[F, TracingContext[F], A]
}

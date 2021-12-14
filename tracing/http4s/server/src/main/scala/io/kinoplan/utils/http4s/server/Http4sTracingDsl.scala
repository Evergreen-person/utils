package io.kinoplan.utils.http4s.server

import org.http4s.dsl.Http4sDsl

trait Http4sTracingDsl[F[_]] extends Http4sDsl[F] with TracingDsl

object Http4sTracingDsl {
  def apply[F[_]]: Http4sTracingDsl[F] = new Http4sTracingDsl[F] {}
}

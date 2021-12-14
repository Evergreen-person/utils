package io.kinoplan.utils.http4s.server

import org.http4s.Request

import io.kinoplan.utils.tracing.core.TracingContext

trait TracingDsl {

  object using {

    def unapply[F[_]](tr: TracedRequest[F]): Option[(Request[F], TracingContext[F])] =
      Some(tr.request -> tr.tracingContext)

  }

}

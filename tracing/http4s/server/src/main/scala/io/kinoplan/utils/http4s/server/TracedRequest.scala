package io.kinoplan.utils.http4s.server

import org.http4s.Request

import io.kinoplan.utils.tracing.core.TracingContext

case class TracedRequest[F[_]](tracingContext: TracingContext[F], request: Request[F])

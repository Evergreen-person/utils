package io.kinoplan.utils.tracing.http4s.client.middleware

import cats.effect.Concurrent
import org.http4s.client.Client
import org.http4s.client.middleware.Logger

import io.kinoplan.utils.tracing.core.{Kernel, Traced, TracingContextBuilder, TracingLogger}

object TracedLogger {

  def apply[F[_]: Concurrent: TracingContextBuilder](logHeaders: Boolean, logBody: Boolean)(
    client: Client[F]
  )(implicit
    L: TracingLogger[Traced[F, *]]
  ): Client[F] = Client { req =>
    val headers = req.headers.toList.map(header => header.name.value -> header.value).toMap
    for {
      context <- TracingContextBuilder[F].continue(
        operationName = "client.http4s.request",
        kernel = Kernel(headers)
      )
      loggedClient = Logger[F](
        logHeaders,
        logBody,
        logAction = Option(message => L.info[this.type](message).run(context))
      )(client)
      res <- loggedClient.run(req)
    } yield res
  }

}

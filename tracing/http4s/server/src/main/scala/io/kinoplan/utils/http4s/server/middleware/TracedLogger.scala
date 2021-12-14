package io.kinoplan.utils.http4s.server.middleware

import scala.concurrent.duration.MILLISECONDS

import cats.data.Kleisli
import cats.effect.{Clock, Concurrent, Sync}
import cats.syntax.all._
import org.http4s.{HttpApp, Response}
import org.http4s.server.middleware.{RequestLogger, ResponseLogger}

import io.kinoplan.utils.tracing.core.{Kernel, Traced, TracingContextBuilder, TracingLogger}

object TracedLogger {

  def apply[F[_]: Concurrent: Clock: TracingContextBuilder](logHeaders: Boolean, logBody: Boolean)(
    http: HttpApp[F]
  )(implicit
    L: TracingLogger[Traced[F, *]]
  ): HttpApp[F] = Kleisli { req =>
    val headers = req.headers.toList.map(header => header.name.value -> header.value).toMap
    TracingContextBuilder[F].continue(
      operationName = "server.http4s.request",
      kernel = Kernel(headers)
    ).use { context =>
      val logRequest = RequestLogger.httpApp[F](
        logHeaders = logHeaders,
        logBody = logBody,
        logAction = Option(message =>
          L.info[this.type](
            Map(
              "request_method" -> req.method.name,
              "request_path" -> req.pathInfo,
              "request_remote_address" -> req.remoteAddr.getOrElse("")
            )
          )(s"Request: $message").run(context)
        )
      )(http)
      for {
        result <- performance(logRequest(req))
        (res, time) = result
        logResponse = ResponseLogger.httpApp[F, Response[F]](
          logHeaders = logHeaders,
          logBody = logBody,
          logAction = Option(message =>
            L.info[this.type](
              Map(
                "response_time" -> time.toString,
                "response_status" -> res.status.code.toString,
                "response_length" -> res.contentLength.getOrElse(0L).toString
              )
            )(s"Response: $message").run(context)
          )
        )(Kleisli.pure[F, Response[F], Response[F]](res))
        loggedRes <- logResponse(res)
      } yield loggedRes
    }
  }

  private def performance[F[_]: Sync: Clock, A](fa: F[A]): F[(A, Long)] = for {
    start <- Clock[F].monotonic(MILLISECONDS)
    result <- fa
    finish <- Clock[F].monotonic(MILLISECONDS)
  } yield (result, finish - start)

}

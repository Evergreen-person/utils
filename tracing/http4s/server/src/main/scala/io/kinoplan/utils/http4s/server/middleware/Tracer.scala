package io.kinoplan.utils.http4s.server.middleware

import java.util.UUID

import cats.data.{Kleisli, OptionT}
import cats.effect.Sync
import cats.syntax.all._
import org.http4s.{Header, HttpRoutes, Request, Response}
import org.http4s.util.CaseInsensitiveString

import io.kinoplan.utils.http4s.server.TracedRequest
import io.kinoplan.utils.tracing.core.TracingContextBuilder

object Tracer {
  final private val correlationIdHeaderName = "X-Request-Id"

  private def enrichRequest[F[_]: Sync](request: Request[F]): F[(Request[F], String)] = {
    val correlationIdHeaderO = request.headers.get(CaseInsensitiveString(correlationIdHeaderName))
    val createCorrelationId: F[(request.Self, String)] = Sync[F].pure(UUID.randomUUID().toString)
      .map { correlationId =>
        (request.putHeaders(Header(correlationIdHeaderName, correlationId).parsed), correlationId)
      }
    correlationIdHeaderO.fold(createCorrelationId) { correlationIdHeader =>
      Sync[F].pure((request, correlationIdHeader.value))
    }
  }

  def apply[F[_]: Sync](routes: Kleisli[OptionT[F, *], TracedRequest[F], Response[F]])(implicit
    builder: TracingContextBuilder[F]
  ): HttpRoutes[F] = Kleisli { req =>
    OptionT {
      enrichRequest[F](req).flatMap { case (enrichedRequest, correlationId) =>
        val url = enrichedRequest.uri.path
        val tags = Map(
          "span.kind" -> "server",
          "http.method" -> enrichedRequest.method.name,
          "http.url" -> url
        )
        builder.build(url, tags, correlationId).use { tracingContext =>
          routes.run(TracedRequest(tracingContext, enrichedRequest)).semiflatMap { response =>
            val tags = Map("http.status_code" -> response.status.code.toString) ++
              response.headers.toList.map(h => s"http.response.header.${h.name}" -> h.value).toMap
            tracingContext.addTags(tags)
              .map(_ => response.putHeaders(Header(correlationIdHeaderName, correlationId).parsed))
          }.value
        }
      }
    }
  }

}

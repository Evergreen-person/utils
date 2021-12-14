package io.kinoplan.utils.endpoints4s.http4s.client

import cats.data.Kleisli
import cats.effect.Sync
import cats.implicits.catsSyntaxFunction1FlatMap
import cats.syntax.all._
import endpoints4s.http4s.client.Endpoints
import org.http4s
import org.http4s.{Header, Headers, Status, Uri}
import org.http4s.client.Client

import io.kinoplan.utils.tracing.core.Trace
import io.kinoplan.utils.tracing.endpoints4s.core.Tracing

abstract class TracedEndpoints[F[_]: Sync](host: Uri, client: Client[F])
    extends Endpoints[F](host, client) with Tracing {

  override type TracedEndpoint[A, B] = Kleisli[Trace[Effect, *], A, B]

  def tracedEndpoint[A, B](
    request: A => Effect[http4s.Request[Effect]],
    response: (Status, Headers) => Option[http4s.Response[Effect] => Effect[B]],
    docs: EndpointDocs
  ): TracedEndpoint[A, B] = Kleisli { a =>
    Kleisli { tracingContext =>
      val headers = tracingContext.context.toHeaders.map((Header.apply _).tupled).map(_.parsed)
        .toList

      val tracedResponse: (Status, Headers) => Option[http4s.Response[Effect] => Effect[B]] = {
        case (status, headers) => response(status, headers).map {
            _.composeF { http4sResponse =>
              tracingContext.addTags(
                Map("http.status_code" -> http4sResponse.status.code.toString)
              ).map(_ => http4sResponse)
            }
          }
      }

      val tracedRequest = request.map(
        _.flatMap { req =>
          headers.pure[F].map(req.putHeaders(_: _*))
            .flatTap(_ => tracingContext.setOperationName(req.uri.path)).flatTap(_ =>
              tracingContext.addTags(
                Map(
                  "span.kind" -> "client",
                  "http.method" -> req.method.name,
                  "http.url" -> req.uri.path
                )
              )
            )
        }
      )

      endpoint[A, B](tracedRequest, tracedResponse, docs).run(a)
    }
  }

}

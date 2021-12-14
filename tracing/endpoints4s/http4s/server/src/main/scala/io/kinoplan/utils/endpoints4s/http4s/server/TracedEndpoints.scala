package io.kinoplan.utils.endpoints4s.http4s.server

import scala.util.control.NonFatal

import cats.effect.Sync
import cats.syntax.all._
import endpoints4s.http4s.server.Endpoints
import org.http4s

import io.kinoplan.utils.tracing.core.{Kernel, Trace, TracingContextBuilder}
import io.kinoplan.utils.tracing.endpoints4s.core.Tracing

abstract class TracedEndpoints[F[_]: Sync: TracingContextBuilder]
    extends Endpoints[F] with Tracing {

  case class TracedEndpoint[A, B](
    request: Request[A],
    response: Response[B],
    operationId: Option[String]
  ) {

    def implementedByEffect(
      f: A => Trace[F, B]
    ): PartialFunction[http4s.Request[F], F[http4s.Response[F]]] = Function
      .unlift { http4sRequest =>
        request.lift.apply(http4sRequest).map { handler =>
          TracingContextBuilder[F].continue(
            http4sRequest.uri.path,
            Kernel(
              http4sRequest.headers.toList.map(header => header.name.value -> header.value).toMap
            ),
            Map(
              "span.kind" -> "server",
              "http.method" -> http4sRequest.method.name,
              "http.url" -> http4sRequest.uri.path
            )
          ).use { tracingContext =>
            handler.flatMap {
              case Right(a) => f(a).map(response).run(tracingContext)
                  .recoverWith { case NonFatal(t) => handleServerError(t) }
              case Left(err) => Sync[F].delay(err)
            }.flatTap(res =>
              tracingContext.addTags(Map("http.status_code" -> res.status.code.toString))
            )
          }
        }
      }

  }

  def tracedEndpoint[A, B](
    request: Request[A],
    response: Response[B],
    docs: EndpointDocs
  ): TracedEndpoint[A, B] = TracedEndpoint(request, response, docs.operationId)

}

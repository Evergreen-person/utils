package io.kinoplan.utils.tracing.endpoints4s.play.client

import scala.concurrent.{ExecutionContext, Future}

import cats.data.Kleisli
import cats.syntax.all._
import endpoints4s.play.client.Endpoints
import endpoints4s.play.client.Endpoints.futureFromEither
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import io.kinoplan.utils.tracing.core.Traced
import io.kinoplan.utils.tracing.endpoints4s.core.Tracing

abstract class TracedEndpoints(host: String, wsClient: WSClient)(implicit
  val ec: ExecutionContext
) extends Endpoints(host, wsClient) with Tracing {

  type TracedEndpoint[A, B] = Kleisli[Traced[Future, *], A, B]

  override def tracedEndpoint[A, B](
    request: Request[A],
    response: Response[B],
    docs: EndpointDocs
  ): TracedEndpoint[A, B] = Kleisli { a =>
    Kleisli { context =>
      val headers = context.context.toHeaders

      val tracedRequest: A => Future[WSRequest] = request
        .andThen(_.addHttpHeaders(headers.toSeq: _*)).andThen { req =>
          context.setOperationName(req.uri.getPath).flatTap(_ =>
            context.addTags(
              Map(
                "span.kind" -> "client",
                "http.method" -> req.method,
                "http.url" -> req.uri.getPath
              )
            )
          ).map(_ => req)
        }

      tracedRequest(a).flatMap(_.execute()).flatMap { wsResp =>
        futureFromEither(
          _decodeResponse(response, wsResp).flatMap(entity => entity(wsResp))
        ).flatTap(_ => context.addTags(Map("http.status_code" -> wsResp.status.toString)))
      }
    }
  }

  private def _mapPartialResponseEntity[A, B](entity: ResponseEntity[A])(
    f: A => Either[Throwable, B]
  ): ResponseEntity[B] = wsResp => entity(wsResp).flatMap(f)

  private def _decodeResponse[A](
    response: Response[A],
    wsResponse: WSResponse
  ): Either[Throwable, ResponseEntity[A]] = {
    val maybeResponse = response(wsResponse.status, wsResponse.headers)
    def maybeClientErrors: Option[WSResponse => Either[Throwable, A]] =
      clientErrorsResponse(wsResponse.status, wsResponse.headers).map(
        _mapPartialResponseEntity[ClientErrors, A](_)(clientErrors =>
          Left(
            new Exception(clientErrorsToInvalid(clientErrors).errors.mkString(". "))
          )
        )
      )
    def maybeServerError: Option[WSResponse => Either[Throwable, A]] =
      serverErrorResponse(wsResponse.status, wsResponse.headers).map(
        _mapPartialResponseEntity[ServerError, A](_)(serverError =>
          Left(serverErrorToThrowable(serverError))
        )
      )
    maybeResponse.orElse(maybeClientErrors).orElse(maybeServerError).toRight(
      new Throwable(s"Unexpected response status: ${wsResponse.status}")
    )
  }

}

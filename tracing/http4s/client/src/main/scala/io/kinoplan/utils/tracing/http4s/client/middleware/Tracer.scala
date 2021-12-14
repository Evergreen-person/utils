package io.kinoplan.utils.tracing.http4s.client.middleware

import cats.data.Kleisli
import cats.effect.Sync
import org.http4s.Header
import org.http4s.client.Client

import io.kinoplan.utils.tracing.core.Traced

object Tracer {

  def apply[F[_]: Sync](client: Client[F]): Traced[F, Client[F]] = Kleisli { tracingContext =>
    Sync[F].delay {
      Client[F] { request =>
        tracingContext.span(
          request.uri.path,
          Map(
            "span.kind" -> "client",
            "http.method" -> request.method.name,
            "http.url" -> request.uri.path
          )
        ).flatMap { clientContext =>
          val headers = tracingContext.context.toHeaders.toList.map((Header.apply _).tupled)
            .map(_.parsed)

          client.run(request.putHeaders(headers: _*)).evalTap { response =>
            clientContext.addTags(Map("http.status_code" -> response.status.code.toString))
          }
        }
      }
    }
  }

}

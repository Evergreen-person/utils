package io.kinoplan.utils.wrappers.play.error.handlers.filters

import scala.concurrent.Future

import play.api.http.HttpErrorHandler
import play.api.mvc._
import play.api.mvc.Results._

import io.kinoplan.utils.wrappers.base.logging.Loggable

class DevErrorHandler extends HttpErrorHandler with Loggable {

  def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] =
    Future.successful(Status(statusCode)(message))

  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    val stackTrace = exception.getStackTrace.mkString("\n")

    logger.error(s"$exception, stack trace: $stackTrace")

    Future.successful(
      InternalServerError("A server error occurred: " + exception.getMessage)
    )
  }

}

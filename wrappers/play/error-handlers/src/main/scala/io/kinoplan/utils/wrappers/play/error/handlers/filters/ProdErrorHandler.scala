package io.kinoplan.utils.wrappers.play.error.handlers.filters

import javax.inject.Inject

import scala.concurrent.Future

import play.api.http.HttpErrorHandler
import play.api.mvc.{RequestHeader, Result}
import play.api.mvc.Results._

import io.kinoplan.utils.wrappers.base.logging.Loggable
import io.kinoplan.utils.wrappers.play.error.handlers.reporters.ExceptionReporter

class ProdErrorHandler @Inject() (exceptionReporter: ExceptionReporter)
    extends HttpErrorHandler with Loggable {

  def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] =
    Future.successful(Status(statusCode)(message))

  def onServerError(request: RequestHeader, exception: Throwable): Future[Status] = {
    val stackTrace = exception.getStackTrace.mkString("\n")

    logger.error(stackTrace)

    exceptionReporter.report(exception)

    Future.successful(InternalServerError)
  }

}

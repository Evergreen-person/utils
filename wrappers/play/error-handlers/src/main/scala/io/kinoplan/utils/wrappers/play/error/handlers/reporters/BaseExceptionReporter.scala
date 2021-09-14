package io.kinoplan.utils.wrappers.play.error.handlers.reporters

import play.Environment

abstract class BaseExceptionReporter(environment: Environment) extends ExceptionReporter {

  protected def sendException(ex: Throwable): Unit

  final override def report(ex: Throwable): Unit = if (environment.isProd) sendException(ex)

  final override def report(message: String): Unit =
    if (environment.isProd) sendException(new Throwable(message))

}

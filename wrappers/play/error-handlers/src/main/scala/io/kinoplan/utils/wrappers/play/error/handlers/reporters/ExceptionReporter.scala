package io.kinoplan.utils.wrappers.play.error.handlers.reporters

trait ExceptionReporter {

  def report(ex: Throwable): Unit

  def report(message: String): Unit

}

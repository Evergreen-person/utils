package io.kinoplan.utils.wrappers.play.error.handlers.modules

import scala.annotation.nowarn

import com.google.inject.AbstractModule

import io.kinoplan.utils.wrappers.play.error.handlers.reporters.{
  ExceptionReporter,
  SentryExceptionReporter
}

class ExceptionReporterModule extends AbstractModule {

  @nowarn
  override def configure(): Unit = bind(classOf[ExceptionReporter])
    .to(classOf[SentryExceptionReporter])

}

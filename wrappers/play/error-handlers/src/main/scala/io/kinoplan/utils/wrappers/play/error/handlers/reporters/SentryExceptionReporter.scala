package io.kinoplan.utils.wrappers.play.error.handlers.reporters

import javax.inject.{Inject, Singleton}

import io.sentry.SentryClientFactory
import play.Environment
import play.api.Configuration

@Singleton
class SentryExceptionReporter @Inject() (environment: Environment, config: Configuration)
    extends BaseExceptionReporter(environment) {
  private[handlers] val sentryUri = config.get[String]("sentry.uri")
  private[handlers] val sentryClient = SentryClientFactory.sentryClient(sentryUri)

  override protected[handlers] def sendException(ex: Throwable): Unit = sentryClient
    .sendException(ex)

}

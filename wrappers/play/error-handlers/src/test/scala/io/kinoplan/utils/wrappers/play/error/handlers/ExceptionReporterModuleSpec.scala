package io.kinoplan.utils.wrappers.play.error.handlers

import com.typesafe.config.ConfigFactory
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.{Configuration, Mode}
import play.api.inject.guice.GuiceApplicationBuilder

import io.kinoplan.utils.wrappers.base.logging.Loggable
import io.kinoplan.utils.wrappers.play.error.handlers.reporters.{
  ExceptionReporter,
  SentryExceptionReporter
}

class ExceptionReporterModuleSpec extends AnyWordSpec with Matchers with Loggable {
  val devSentryUri = "https://123:456@sentry.test.com/257"
  val prodSentryUri = "https://123:456@sentry.test.com/256"

  val uuidLength = 36

  "ExceptionReporterModule#configure" should {
    "bind ExceptionReporter for dev" in {
      val application = new GuiceApplicationBuilder().in(Mode.Dev)
        .configure(Configuration(ConfigFactory.load("application-dev.conf"))).build()

      val instance = application.injector.instanceOf(classOf[SentryExceptionReporter])

      instance mustBe a[ExceptionReporter]
      instance.sentryUri mustBe devSentryUri
      instance.report("Test")
      instance.report(new Throwable("Test"))
    }

    "bind ExceptionReporter for prod and send message string" in {
      val application = new GuiceApplicationBuilder().in(Mode.Prod).build()

      val instance = application.injector.instanceOf(classOf[SentryExceptionReporter])

      instance mustBe a[ExceptionReporter]
      instance.sentryUri mustBe prodSentryUri
      instance.report("Test")
      instance.sentryClient.getContext.getLastEventId.toString.length mustBe uuidLength
    }

    "bind ExceptionReporter for prod and send message throwable" in {
      val application = new GuiceApplicationBuilder().in(Mode.Prod).build()

      val instance = application.injector.instanceOf(classOf[SentryExceptionReporter])

      instance mustBe a[ExceptionReporter]
      instance.sentryUri mustBe prodSentryUri
      instance.report(new Throwable("Test"))
      instance.sentryClient.getContext.getLastEventId.toString.length mustBe uuidLength
    }
  }
}

package io.kinoplan.utils.wrappers.play.logging.request

import javax.inject.{Inject, Singleton}

import scala.concurrent.ExecutionContext

import org.slf4j.MarkerFactory
import play.api.mvc.{EssentialAction, EssentialFilter}

import io.kinoplan.utils.wrappers.base.logging.Loggable
import io.kinoplan.utils.wrappers.base.logging.context.{MapContext, MarkerContext}

@Singleton
class LoggingFilter @Inject() (implicit
  ec: ExecutionContext
) extends EssentialFilter with Loggable {

  implicit val markerContext: MarkerContext = MarkerFactory.getMarker("REQUEST")

  override def apply(next: EssentialAction): EssentialAction = EssentialAction { request =>
    val startTime = System.currentTimeMillis

    implicit val mapContext: MapContext = RequestMapContext.extractMapContext(request)
    val requestWithMapContext = request
      .addAttr(RequestMapContext.Keys.MapContextTypedKey, mapContext)

    logger.info(
      s"[START] ${requestWithMapContext.method} ${requestWithMapContext.uri}"
    )

    next(requestWithMapContext).map { result =>
      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime

      mapContext.put(
        "response_time" -> requestTime,
        "response_status" -> result.header.status,
        "response_length" -> result.body.contentLength.getOrElse(0L)
      )

      logger.info(
        s"""[END] ${requestWithMapContext.method} ${requestWithMapContext.uri}
        took ${requestTime}ms and returned ${result.header.status}"""
      )

      result
    }
  }

}

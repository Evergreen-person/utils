package io.kinoplan.utils.tracing.core

import java.util.UUID

final case class Kernel(toHeaders: Map[String, String]) {
  val getCorrelationId: String = toHeaders.getOrElse("X-Request-Id", UUID.randomUUID().toString)
}

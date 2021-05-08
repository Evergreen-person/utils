package io.kinoplan.utils.wrappers.play.logging.request

import play.api.libs.typedmap.TypedKey
import play.api.mvc.RequestHeader
import play.api.mvc.request.RequestAttrKey

import io.kinoplan.utils.wrappers.base.logging.context.MapContext

object RequestMapContext {

  object Headers {
    val RequestIdHeader = "X-Request-Id"
  }

  object Keys {
    val MapContextTypedKey: TypedKey[MapContext] = TypedKey[MapContext]
    val RequestId = "request_id"
    val RequestInternalId = "request_internal_id"
    val RequestRemoteAddress = "request_remote_address"
    val RequestMethod = "request_method"
    val RequestPath = "request_path"
  }

  implicit class RequestMapContextExtended(request: RequestHeader) {

    def putMapContext(pairs: (String, Any)*): MapContext = {
      val mapContext = extractMapContext(request).put(pairs: _*)

      request.addAttr(RequestMapContext.Keys.MapContextTypedKey, mapContext)

      mapContext
    }

  }

  implicit def extractMapContext(implicit
    request: RequestHeader
  ): MapContext = {
    import request._

    request.attrs.get(Keys.MapContextTypedKey).getOrElse {
      // github.com/playframework/playframework/issues/8947
      val internalRequestId =
        (if (attrs.contains(RequestAttrKey.Id)) id else 0L).toHexString.toUpperCase

      val globalRequestId = headers.get(Headers.RequestIdHeader).getOrElse(internalRequestId)

      MapContext(
        Map(
          Keys.RequestId -> globalRequestId,
          Keys.RequestInternalId -> internalRequestId,
          Keys.RequestRemoteAddress -> remoteAddress,
          Keys.RequestMethod -> method,
          Keys.RequestPath -> path
        ) ++ request.queryString.collect {
          case (key, values) if values.nonEmpty =>
            s"request_param_${key.toLowerCase}" -> values.head
        }
      )
    }
  }

}

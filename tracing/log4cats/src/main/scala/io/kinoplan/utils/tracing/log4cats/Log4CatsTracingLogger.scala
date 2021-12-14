package io.kinoplan.utils.tracing.log4cats

import scala.reflect.ClassTag

import cats.data.Kleisli
import cats.effect.Sync
import org.typelevel.log4cats.slf4j.Slf4jLogger

import io.kinoplan.utils.tracing.core.{Context, Traced, TracingLogger}

object Log4CatsTracingLogger {

  implicit def tracingContextInstance[F[_]: Sync]: TracingLogger[Traced[F, *]] =
    new TracingLogger[Traced[F, *]] {

      private def extractTags(context: Context): Map[String, String] = context.traceIdO
        .fold(Map.empty[String, String])(traceId => Map("trace_id" -> traceId)) ++
        context.spanIdO.fold(Map.empty[String, String])(spanId => Map("span_id" -> spanId)) ++
        Map("request_id" -> context.correlationId)

      override def info[A](value: => String)(implicit
        evidence: ClassTag[A]
      ): Traced[F, Unit] = Kleisli { tracingContext =>
        Slf4jLogger.getLoggerFromClass[F](evidence.runtimeClass)
          .info(extractTags(tracingContext.context))(value)
      }

      override def info[A](ctx: Map[String, String])(value: => String)(implicit
        evidence: ClassTag[A]
      ): Traced[F, Unit] = Kleisli { tracingContext =>
        Slf4jLogger.getLoggerFromClass[F](evidence.runtimeClass)
          .info(extractTags(tracingContext.context) ++ ctx)(value)
      }

      override def error[A](value: => String)(implicit
        evidence: ClassTag[A]
      ): Traced[F, Unit] = Kleisli { tracingContext =>
        Slf4jLogger.getLoggerFromClass[F](evidence.runtimeClass)
          .error(extractTags(tracingContext.context))(value)
      }

      override def error[A](err: Throwable)(value: => String)(implicit
        evidence: ClassTag[A]
      ): Traced[F, Unit] = Kleisli { tracingContext =>
        Slf4jLogger.getLoggerFromClass[F](evidence.runtimeClass)
          .error(extractTags(tracingContext.context), err)(value)
      }

      override def warn[A](value: => String)(implicit
        evidence: ClassTag[A]
      ): Traced[F, Unit] = Kleisli { tracingContext =>
        Slf4jLogger.getLoggerFromClass[F](evidence.runtimeClass)
          .warn(extractTags(tracingContext.context))(value)
      }

      override def warn[A](err: Throwable)(value: => String)(implicit
        evidence: ClassTag[A]
      ): Traced[F, Unit] = Kleisli { tracingContext =>
        Slf4jLogger.getLoggerFromClass[F](evidence.runtimeClass)
          .warn(extractTags(tracingContext.context), err)(value)
      }

    }

}

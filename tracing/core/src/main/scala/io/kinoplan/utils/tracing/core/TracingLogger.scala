package io.kinoplan.utils.tracing.core

import scala.reflect.ClassTag

trait TracingLogger[F[_]] {
  def info[A: ClassTag](value: => String): F[Unit]
  def info[A: ClassTag](ctx: Map[String, String])(value: => String): F[Unit]
  def error[A: ClassTag](value: => String): F[Unit]
  def error[A: ClassTag](err: Throwable)(value: => String): F[Unit]
  def warn[A: ClassTag](value: => String): F[Unit]
  def warn[A: ClassTag](err: Throwable)(value: => String): F[Unit]
}

package io.kinoplan.utils.tracing.core

trait ContextBuilder {
  def extract(kernel: Kernel): Context
}

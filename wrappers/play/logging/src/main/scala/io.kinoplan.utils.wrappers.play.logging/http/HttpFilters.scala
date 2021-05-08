package io.kinoplan.utils.wrappers.play.logging.http

import javax.inject.Inject

import play.api.http.DefaultHttpFilters

import io.kinoplan.utils.wrappers.play.logging.request.LoggingFilter

class HttpFilters @Inject() (loggingFilter: LoggingFilter) extends DefaultHttpFilters(loggingFilter)

package io.kinoplan.utils.tracing.endpoints4s.core

import endpoints4s.algebra.Endpoints

trait Tracing extends Endpoints {

  type TracedEndpoint[A, B]

  def tracedEndpoint[A, B](
    request: Request[A],
    response: Response[B],
    docs: EndpointDocs = EndpointDocs()
  ): TracedEndpoint[A, B]

}

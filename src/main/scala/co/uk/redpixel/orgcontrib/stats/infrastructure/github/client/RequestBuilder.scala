package co.uk.redpixel.orgcontrib.stats.infrastructure.github.client

import eu.timepit.refined.types.string.NonEmptyString
import org.http4s.Method.GET
import org.http4s.headers.{AgentProduct, Authorization, `User-Agent`}
import org.http4s.{BasicCredentials, Method, Request, Uri}
import eu.timepit.refined.auto._

object RequestBuilder {

  def buildWith[F[_]](uri: Uri, token: NonEmptyString, method: Method = GET): Request[F]#Self =
    Request[F](method = method, uri = uri).putHeaders(
      Authorization(BasicCredentials(token)),
      `User-Agent`(AgentProduct("Contributors_Stats_API"))
    )
}

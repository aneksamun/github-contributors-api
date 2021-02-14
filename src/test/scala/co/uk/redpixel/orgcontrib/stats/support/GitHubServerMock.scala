package co.uk.redpixel.orgcontrib.stats.support

import cats.effect.{Resource, Sync}
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.{configureFor, get, ok, stubFor, urlEqualTo}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options

import java.net.URL

final class GitHubServerMock private(server: WireMockServer) extends AutoCloseable {
  server.start()

  configureFor(serverUrl.getProtocol, serverUrl.getHost, serverUrl.getPort)
  stubFor(get(urlEqualTo("/"))
    .willReturn(ok().withBody(ResponseBody.Index.load)))

  lazy val serverUrl = new URL(server.baseUrl())

  def close(): Unit = server.stop()
}

object GitHubServerMock {

  def resource[F[_]](implicit F: Sync[F]): Resource[F, GitHubServerMock] = Resource.fromAutoCloseable(
    F.delay(new GitHubServerMock(new WireMockServer(options().dynamicPort())))
  )
}

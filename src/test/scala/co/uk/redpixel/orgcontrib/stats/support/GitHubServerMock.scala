package co.uk.redpixel.orgcontrib.stats.support

import cats.effect.{Resource, Sync}
import co.uk.redpixel.orgcontrib.stats.algebra.data.Organisation
import co.uk.redpixel.orgcontrib.stats.support.scalacheck.genToken
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options

import java.net.URL

final class GitHubServerMock private(server: WireMockServer) extends AutoCloseable {
  server.start()
  configureFor(baseUrl.getProtocol, baseUrl.getHost, baseUrl.getPort)

  lazy val baseUrl = new URL(server.baseUrl())
  lazy val token = genToken.sample.get

  def configure(organisation: Organisation): Unit = {
    stubFor(get(urlEqualTo(s"/orgs/${organisation.name}/repos?page=1&per_page=100"))
      .willReturn(ok().withBody(Template.OrganisationDetails.of(baseUrl, organisation))))

    stubFor(get(urlEqualTo(s"/orgs/${organisation.name}/repos?page=2&per_page=100"))
      .willReturn(ok().withBody("[]")))

    //--gumtree--
    stubFor(get(urlEqualTo(s"/repos/${organisation.name}/gumtree/contributors?page=1&per_page=100"))
      .willReturn(ok().withBody(Template.GumtreeContributors.load)))

    stubFor(get(urlEqualTo(s"/repos/${organisation.name}/gumtree/contributors?page=2&per_page=100"))
      .willReturn(ok().withBody("[]")))

    //--cgum--
    stubFor(get(urlEqualTo(s"/repos/${organisation.name}/cgum/contributors?page=1&per_page=100"))
      .willReturn(ok().withBody(Template.CgumContributors.load)))

    stubFor(get(urlEqualTo(s"/repos/${organisation.name}/cgum/contributors?page=2&per_page=100"))
      .willReturn(ok().withBody("[]")))

    //--samples--
    stubFor(get(urlEqualTo(s"/repos/${organisation.name}/samples/contributors?page=1&per_page=100"))
      .willReturn(ok().withBody(Template.SamplesContributors.load)))

    stubFor(get(urlEqualTo(s"/repos/${organisation.name}/samples/contributors?page=2&per_page=100"))
      .willReturn(ok().withBody("[]")))

    //--Roslyn-ML--
    stubFor(get(urlEqualTo(s"/repos/${organisation.name}/RoslynML/contributors?page=1&per_page=100"))
      .willReturn(ok().withBody(Template.RoslynMLContributors.load)))

    stubFor(get(urlEqualTo(s"/repos/${organisation.name}/RoslynML/contributors?page=2&per_page=100"))
      .willReturn(ok().withBody("[]")))

    //--pythonparser--
    stubFor(get(urlEqualTo(s"/repos/${organisation.name}/pythonparser/contributors?page=1&per_page=100"))
      .willReturn(ok().withBody(Template.PythonParserContributors.load)))

    stubFor(get(urlEqualTo(s"/repos/${organisation.name}/pythonparser/contributors?page=2&per_page=100"))
      .willReturn(ok().withBody("[]")))
  }

  def close(): Unit = server.stop()
}

object GitHubServerMock {

  def resource[F[_]](implicit F: Sync[F]): Resource[F, GitHubServerMock] = Resource.fromAutoCloseable(
    F.delay(new GitHubServerMock(new WireMockServer(options().dynamicPort())))
  )
}

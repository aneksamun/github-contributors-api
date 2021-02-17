package co.uk.redpixel.orgcontrib.stats.it

import cats.effect.IO
import co.uk.redpixel.orgcontrib.stats.algebra.data.Contributor
import co.uk.redpixel.orgcontrib.stats.http.route.Contributions
import co.uk.redpixel.orgcontrib.stats.infrastructure.github.GitHubContributorAlg
import co.uk.redpixel.orgcontrib.stats.support.Organisations.GumtreeDiff
import co.uk.redpixel.orgcontrib.stats.support.fixture.FakeGitHubServer._
import co.uk.redpixel.orgcontrib.stats.support.fixture.{FakeGitHubServer, HttpClient}
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.{Request, Uri}
import org.scalatest.featurespec.AsyncFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.GivenWhenThen
import scalacache.CatsEffect.modes.async

import scala.concurrent.duration.DurationInt

class ContributorStatsSpec extends AsyncFeatureSpec
  with FakeGitHubServer
  with HttpClient
  with GivenWhenThen
  with Matchers {

  Feature("Contribution statistics") {

    info("As system user")
    info("I wish to see the list of contributors for the specific organisation")
    info("So that I can observe the most valuable ones")

    Scenario("Listing top contributors") {
      Given("an algebra")
      val algebra = GitHubContributorAlg.strictOf(config(serverMock))(cacheExpiry = 5 seconds)(client)

      When(s"I make request to view a ${GumtreeDiff.name} contributors")
      serverMock.configure(GumtreeDiff)

      val request = Request[IO](uri = Uri.unsafeFromString(s"/org/${GumtreeDiff.name}/contributors"))
      val response = Contributions.routes(algebra).orNotFound(request)

      Then("I get the top contributors sorted by the number of contributions")
      response.flatMap(_.as[List[Contributor]]).asserting(_ should be(List(
        Contributor("jrfaller", 685),
        Contributor("morandat", 102),
        Contributor("npalix", 9),
        Contributor("cdmihai", 7),
        Contributor("narmion", 6),
        Contributor("martinezmatias", 4),
        Contributor("monperrus", 4),
        Contributor("caiusb", 4),
        Contributor("FlorianLehmann", 4),
        Contributor("Symbolk", 3),
        Contributor("BalzGuenat", 3),
        Contributor("JuliaLawall", 3),
        Contributor("UlrichThomasGabor", 2),
        Contributor("jawilliam", 2),
        Contributor("VHellendoorn", 1),
        Contributor("staslev", 1),
        Contributor("macsj200", 1),
        Contributor("glGarg", 1),
        Contributor("pvojtechovsky", 1),
        Contributor("yanamal", 1),
        Contributor("CaptainEmerson", 1),
        Contributor("Deadlyelder", 1),
        Contributor("hayleefay", 1),
        Contributor("algomaster99", 1)
      )))
    }
  }
}

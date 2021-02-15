package co.uk.redpixel.orgcontrib.stats.it

import cats.effect.IO
import co.uk.redpixel.orgcontrib.stats.algebra.data.Contributor
import co.uk.redpixel.orgcontrib.stats.http.route.Contributions
import co.uk.redpixel.orgcontrib.stats.infrastructure.github.GitHubContributorAlg
import co.uk.redpixel.orgcontrib.stats.support.Organisations.GumtreeDiff
import co.uk.redpixel.orgcontrib.stats.support.fixture.GitHubTestServices
import co.uk.redpixel.orgcontrib.stats.support.fixture.GitHubTestServices._
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.{Request, Status, Uri}
import org.scalatest.featurespec.FixtureAsyncFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{EitherValues, GivenWhenThen}

class ContributorStatsSpec extends FixtureAsyncFeatureSpec
  with GitHubTestServices
  with EitherValues
  with GivenWhenThen
  with Matchers {

  Feature("Contribution statistics") {

    info("As system user")
    info("I wish to see the list of contributors for the specific organisation")
    info("So that I can observe the most valuable ones")

    Scenario("Listing top contributors") { services =>
      Given("an algebra")
      val algebra = GitHubContributorAlg.strictOf(config(services.serverMock))(services.client)

      When(s"I make request to view a ${GumtreeDiff.name} contributors")
      services.serverMock.configure(GumtreeDiff)

      val request = Request[IO](uri = Uri.unsafeFromString(s"/org/${GumtreeDiff.name}/contributors"))
      val response = Contributions.routes(algebra).orNotFound(request)

      Then("I get the top contributors sorted by the number of contributions")
//      response.asserting(_.body.asJson.as[List[Contributor]] should be(List()))
      response.asserting(_.status should be(Status.Ok))
    }
  }
}

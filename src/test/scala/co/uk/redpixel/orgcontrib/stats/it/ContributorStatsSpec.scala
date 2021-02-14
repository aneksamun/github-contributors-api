package co.uk.redpixel.orgcontrib.stats.it

import co.uk.redpixel.orgcontrib.stats.infrastructure.github.GitHubContributorAlg
import co.uk.redpixel.orgcontrib.stats.support.fixture.{FakeGitHubServer, HttpClient}
import org.http4s.Http
import org.scalatest.{BeforeAndAfterAll, GivenWhenThen}
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class ContributorStatsSpec extends FixtureAnyFeatureSpec
  with FakeGitHubServer
//  with HttpClient
  with GivenWhenThen
  with BeforeAndAfterAll
  with Matchers {

  Feature("Contribution statistics") {

    info("As system user")
    info("I wish to see the list of contributors for the specific organisation")
    info("So that I can observe the most valuable ones")

    Scenario("Listing top contributors") { server =>
      Given("An algebra")
      server.serverUrl
//      val algebra = GitHubContributorAlg()
    }
  }
}

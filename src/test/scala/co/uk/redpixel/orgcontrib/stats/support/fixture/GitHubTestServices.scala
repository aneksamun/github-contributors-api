package co.uk.redpixel.orgcontrib.stats.support.fixture

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import co.uk.redpixel.orgcontrib.stats.config.GitHubConfig
import co.uk.redpixel.orgcontrib.stats.support.GitHubServerMock
import co.uk.redpixel.orgcontrib.stats.support.fixture.GitHubTestServices.TestSuite
import eu.timepit.refined.api.Refined
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.scalatest.{FixtureAsyncTestSuite, FutureOutcome}

import scala.concurrent.ExecutionContext.global

trait GitHubTestServices extends AsyncIOSpec {
  this: FixtureAsyncTestSuite =>

  type FixtureParam = TestSuite

  def withFixture(test: OneArgAsyncTest): FutureOutcome = {
    val resource = for {
      client <- BlazeClientBuilder[IO](global).resource
      server <- GitHubServerMock.resource[IO]
    } yield TestSuite(server, client)

    resource.use(suite => IO(test(suite))).unsafeRunSync()
  }
}

object GitHubTestServices {

  def config(mock: GitHubServerMock, maxConcurrent: Int = 5) =
    GitHubConfig(mock.baseUrl, Refined.unsafeApply(mock.token), Refined.unsafeApply(maxConcurrent))

  final case class TestSuite(serverMock: GitHubServerMock, client: Client[IO])
}

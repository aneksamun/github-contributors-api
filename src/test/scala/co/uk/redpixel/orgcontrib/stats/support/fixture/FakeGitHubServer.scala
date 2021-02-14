package co.uk.redpixel.orgcontrib.stats.support.fixture

import cats.effect.IO
import co.uk.redpixel.orgcontrib.stats.support.GitHubServerMock
import org.scalatest.{FixtureTestSuite, Outcome}

trait FakeGitHubServer {
  this: FixtureTestSuite =>

  type FixtureParam = GitHubServerMock

  protected def withFixture(test: OneArgTest): Outcome = {
    GitHubServerMock.resource[IO].use(mock => IO(test(mock))).unsafeRunSync()
  }
}

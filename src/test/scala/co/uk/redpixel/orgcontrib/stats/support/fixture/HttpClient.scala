package co.uk.redpixel.orgcontrib.stats.support.fixture

import cats.effect.IO
import org.http4s.client.Client
import org.scalatest.{FixtureTestSuite, Outcome}

trait HttpClient extends FixtureTestSuite {
  this: FixtureTestSuite =>

  type FixtureParam = Client[IO]

  protected def withFixture(test: OneArgTest): Outcome =
    ???
}

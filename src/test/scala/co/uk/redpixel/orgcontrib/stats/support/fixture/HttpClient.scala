package co.uk.redpixel.orgcontrib.stats.support.fixture

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import org.http4s.client.blaze.BlazeClientBuilder
import org.scalatest.{AsyncTestSuite, BeforeAndAfterAll}

import scala.concurrent.ExecutionContext.global

trait HttpClient extends AsyncIOSpec with BeforeAndAfterAll {
  this: AsyncTestSuite =>

  val (client, clientFinalizer) = BlazeClientBuilder[IO](global)
    .withCheckEndpointAuthentication(false)
    .resource.allocated.unsafeRunSync()

  override def afterAll(): Unit = clientFinalizer.unsafeRunSync()
}

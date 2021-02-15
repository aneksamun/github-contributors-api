package co.uk.redpixel.orgcontrib.stats.support.fixture

import cats.effect.IO
import co.uk.redpixel.orgcontrib.stats.config.GitHubConfig
import co.uk.redpixel.orgcontrib.stats.support.GitHubServerMock
import eu.timepit.refined.api.Refined
import org.scalatest.{AsyncTestSuite, BeforeAndAfterAll}

import java.net.URL

trait FakeGitHubServer extends BeforeAndAfterAll {
  this: AsyncTestSuite =>

  val (serverMock, serverMockFinalizer) = GitHubServerMock.resource[IO].allocated.unsafeRunSync()

  override def afterAll(): Unit = serverMockFinalizer.unsafeRunSync()
}

object FakeGitHubServer {

  def config(mock: GitHubServerMock, maxConcurrent: Int = 5) = {
    GitHubConfig(
      new URL(s"${mock.baseUrl}/orgs/{org}/repos"),
      Refined.unsafeApply(mock.token),
      Refined.unsafeApply(maxConcurrent)
    )
  }
}

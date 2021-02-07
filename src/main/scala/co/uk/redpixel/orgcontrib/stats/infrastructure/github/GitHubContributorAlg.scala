package co.uk.redpixel.orgcontrib.stats.infrastructure.github

import cats.data.OptionT
import co.uk.redpixel.orgcontrib.stats.algebra.ContributorAlg
import co.uk.redpixel.orgcontrib.stats.algebra.data.{Contributor, Organisation}
import co.uk.redpixel.orgcontrib.stats.config.GitHubConfig
import org.http4s.Uri
import org.http4s.client.Client

import java.net.URL

class GitHubContributorAlg[F[_]](config: GitHubConfig)
                                (implicit client: Client[F]) extends ContributorAlg[F] {

  def stats(org: Organisation): OptionT[F, Seq[Contributor]] = {

    def listRepositories(org: Organisation) = {
      val uri = config.apiUrl / ""
    }

//    println(config.apiUrl.toString)
//    OptionT.none
    client.toString + config.toString
    ???
  }
}

object GitHubContributorAlg {

  def apply[F[_]](config: GitHubConfig)(implicit client: Client[F]) =
    new GitHubContributorAlg[F](config)

  implicit def urlToHttp4sUri(url: URL) = Uri.unsafeFromString(url.toString)
}

// - define repositories URL
// - handle not found response
// - walk throw responses and execute concurrent requests
// - reduce data to Map
// - do recursively
package co.uk.redpixel.orgcontrib.stats.infrastructure.github

import cats.data.{EitherT, OptionT}
import cats.effect.ConcurrentEffect
import cats.syntax.all._
import co.uk.redpixel.orgcontrib.stats.algebra.data.{Contributor, Name, Organisation, Total}
import co.uk.redpixel.orgcontrib.stats.algebra.{BootstrapAlgebraError, ContributorAlg}
import co.uk.redpixel.orgcontrib.stats.config.GitHubConfig
import co.uk.redpixel.orgcontrib.stats.infrastructure.github.client.UriBuilder._
import co.uk.redpixel.orgcontrib.stats.infrastructure.github.client._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.all.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Json
import io.circe.generic.auto._
import monix.catnap.CircuitBreaker
import org.http4s.Uri
import org.http4s.circe.CirceEntityCodec._
import org.http4s.client.Client
import scalacache.caffeine.CaffeineCache
import scalacache.{Cache, Mode}

import scala.concurrent.duration.Duration

class GitHubContributorAlg[F[_]] private(yieldOrgReposUri: UriBuilder, token: NonEmptyString, maxConcurrent: PosInt)
                                        (client: Client[F], circuitBreaker: CircuitBreaker[F])
                                        (cacheExpiry: Duration)
                                        (implicit
                                         F: ConcurrentEffect[F],
                                         C: Cache[Seq[Contributor]],
                                         M: Mode[F]) extends ContributorAlg[F] {
  import scalacache._

  def stats(org: Organisation): OptionT[F, Seq[Contributor]] = {

    def fetchWhileNonEmpty[G[_], A <: Iterable[_]](f: Page => G[A]): fs2.Stream[G, A] =
      fs2.Stream.iterate(1)(_ + 1)
        .map(Page)
        .evalMap(f)
        .takeWhile(_.nonEmpty)

    def getContributorUrls(org: Organisation): OptionT[F, List[String]] = {
      def getContributorUrlsPerPage(page: Page, perPage: PerPage = PerPage.max) =
        OptionT {
          circuitBreaker.protect(
            client.expectOption[Json](
              RequestBuilder.buildWith[F](
                yieldOrgReposUri
                  .withOrg(org)
                  .withPaginationParams(page, perPage),
                token
              )))
        }.map { json =>
          json \\ "contributors_url" flatMap (_.asString)
        }

      fetchWhileNonEmpty(getContributorUrlsPerPage(_)).compile.foldMonoid
    }

    def getContributors(urls: List[String]): F[List[GitHubContributor]] = {
      def getContributorsPerPage(url: String, page: Page, perPage: PerPage) =
        circuitBreaker.protect(
          client.expect[List[GitHubContributor]](
            RequestBuilder.buildWith[F](UriBuilder(url).withPaginationParams(page, perPage), token)
          )
        )

      fs2.Stream.emits(urls)
        .covary[F]
        .map(contributorUrl =>
          fetchWhileNonEmpty(getContributorsPerPage(contributorUrl, _, PerPage.max))
        )
        .parJoin(maxConcurrent)
        .compile
        .foldMonoid
    }

    def computeStats(contributors: List[GitHubContributor]): Seq[Contributor] = {
      val figures = contributors.foldLeft(Map.empty[Name, Total])((stats, contributor) => {
        val current = stats.getOrElse(contributor.login, 0)
        val updated = current + contributor.contributions
        stats + (contributor.login -> updated)
      })

      figures.map(pair => Contributor(pair._1, pair._2)).toSeq sortBy (-_.contributions)
    }

    OptionT(get(org.name)) orElse (for {
      contributorUrls <- getContributorUrls(org)
      contributors    <- OptionT.liftF(getContributors(contributorUrls))
      stats           =  computeStats(contributors)
      _               <- OptionT.liftF(put(org.name)(stats, cacheExpiry.some))
    } yield stats)
  }
}

object GitHubContributorAlg {

  private implicit val statsCache = CaffeineCache[Seq[Contributor]]

  def apply[F[_]](config: GitHubConfig)(cacheExpiry: Duration)(client: Client[F], fuse: CircuitBreaker[F])
                 (implicit F: ConcurrentEffect[F], M: Mode[F]): EitherT[F, BootstrapAlgebraError, GitHubContributorAlg[F]] = {
    def toUriBuilder: String => UriBuilder = resource =>
      UriBuilder(resource)

    EitherT.fromOptionF(
      client.expect[Json](RequestBuilder.buildWith[F](Uri.unsafeFromString(config.apiUrl.toString), config.token))
        .map(_ \\ "organization_repositories_url")
        .map(_.headOption.flatMap(_.asString)
          .map(toUriBuilder)
          .map(new GitHubContributorAlg[F](_, config.token, config.maxConcurrent)(client, fuse)(cacheExpiry))),
      BootstrapAlgebraError("Could not resolve organisation repositories URL")
    )
  }

  def strictOf[F[_] : ConcurrentEffect : Mode](config: GitHubConfig)(cacheExpiry: Duration)
                                              (client: Client[F], fuse: CircuitBreaker[F]) =
    new GitHubContributorAlg(UriBuilder(config.apiUrl.toString), config.token, config.maxConcurrent)(client, fuse)(cacheExpiry)
}

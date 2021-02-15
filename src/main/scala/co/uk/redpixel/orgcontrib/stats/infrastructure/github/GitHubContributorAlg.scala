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
import org.http4s.Uri
import org.http4s.circe.CirceEntityCodec._
import org.http4s.client.Client

class GitHubContributorAlg[F[_]] private(yieldOrgReposUri: UriBuilder, token: NonEmptyString, maxConcurrent: PosInt)
                                        (client: Client[F])
                                        (implicit F: ConcurrentEffect[F]) extends ContributorAlg[F] {

  def stats(org: Organisation): OptionT[F, Seq[Contributor]] = {

    def fetchWhileNonEmpty[M[_], A <: Iterable[_]](f: Page => M[A]): fs2.Stream[M, A] =
      fs2.Stream.iterate(1)(_ + 1)
        .map(Page)
        .evalMap(f)
        .takeWhile(_.nonEmpty)

    def getContributorUrls(org: Organisation): OptionT[F, List[String]] = {
      def getContributorUrlsPerPage(page: Page, perPage: PerPage = PerPage.max) =
        OptionT {
          client.expectOption[Json](
            RequestBuilder.buildWith[F](
              yieldOrgReposUri
                .withOrg(org)
                .withPaginationParams(page, perPage),
              token
            ))
        }.map { json =>
          json \\ "contributors_url" flatMap (_.asString)
        }

      fetchWhileNonEmpty(getContributorUrlsPerPage(_)).compile.foldMonoid
    }

    def getContributors(urls: List[String]): F[List[GitHubContributor]] = {
      def getContributorsPerPage(url: String, page: Page, perPage: PerPage) =
        client.expect[List[GitHubContributor]](
          RequestBuilder.buildWith[F](
            UriBuilder(url).withPaginationParams(page, perPage), token
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

    for {
      contributorUrls <- getContributorUrls(org)
      contributors    <- OptionT(getContributors(contributorUrls).map(_.some))
      stats           =  contributors.foldLeft(Map.empty[Name, Total])((stats, contributor) =>
        stats + (contributor.login -> contributor.contributions)
      )
    } yield {
      stats.map(pair => Contributor(pair._1, pair._2)).toSeq.sortBy(_.contributions)
    }
  }
}

object GitHubContributorAlg {

  def apply[F[_]](config: GitHubConfig)(client: Client[F])
                 (implicit F: ConcurrentEffect[F]): EitherT[F, BootstrapAlgebraError, GitHubContributorAlg[F]] = {
    def toUriBuilder: String => UriBuilder = resource =>
      UriBuilder(resource)

    EitherT.fromOptionF(
      client.expect[Json](RequestBuilder.buildWith[F](Uri.unsafeFromString(config.apiUrl.toString), config.token))
        .map(_ \\ "organization_repositories_url")
        .map(_.headOption.flatMap(_.asString)
          .map(toUriBuilder)
          .map(new GitHubContributorAlg[F](_, config.token, config.maxConcurrent)(client))),
      BootstrapAlgebraError("Could not resolve organisation repositories URL")
    )
  }

  def strictOf[F[_]](config: GitHubConfig)(client: Client[F])(implicit F: ConcurrentEffect[F]) =
    new GitHubContributorAlg(UriBuilder(config.apiUrl.toString), config.token, config.maxConcurrent)(client)
}

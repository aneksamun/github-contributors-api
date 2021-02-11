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

    def getContributorUrls(org: Organisation, page: Page, perPage: PerPage = PerPage.max): OptionT[F, List[String]] = {
      OptionT {
        client.expectOption[Json](
          RequestBuilder.buildWith[F](
            yieldOrgReposUri
              .withOrg(org)
              .withPaginationParams(page, perPage),
            token
          ))
      }
      .map(_ \\ "contributors_url")
      .map(_.flatMap(_.asString))
    }

    def getContributors(url: String, page: Page, perPage: PerPage = PerPage.max): F[List[GitHubContributor]] = {
      client.expect[List[GitHubContributor]](
        RequestBuilder.buildWith[F](
          UriBuilder(url).withPaginationParams(page, perPage), token
        )
      )
    }

    fs2.Stream.iterate(1)(_ + 1)
      .map(Page)
      .evalMap(getContributorUrls(org, _))
      .takeWhile(_.nonEmpty)
      .compile
      .foldMonoid
      .flatMap(urls => OptionT {
        fs2.Stream.emits(urls)
          .covary[F]
          .map(contributorUrl =>
            fs2.Stream.iterate(1)(_ + 1)
              .map(Page)
              .evalMap(getContributors(contributorUrl, _))
              .takeWhile(_.nonEmpty))
          .parJoin(maxConcurrent)
          .map(_.foldLeft(Map.empty[Name, Total])((stats, contributor) =>
            stats + (contributor.login -> contributor.contributions))
            .map(pair => Contributor(pair._1, pair._2)).toSeq
            .sortBy(_.contributions)
            .some)
          .compile
          .foldMonoid
      })
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
}

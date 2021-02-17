package co.uk.redpixel.orgcontrib.stats

import cats.effect.{ConcurrentEffect, Timer}
import cats.syntax.all._
import co.uk.redpixel.orgcontrib.stats.config.ApplicationConfig
import co.uk.redpixel.orgcontrib.stats.http.route.{Contributions, HealthCheck}
import co.uk.redpixel.orgcontrib.stats.infrastructure.github.GitHubContributorAlg
import eu.timepit.refined.auto._
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import scalacache.Mode

import scala.concurrent.ExecutionContext.global
import scala.concurrent.duration.DurationInt

object ContributionsStatsServer {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F], M: Mode[F]): Stream[F, Nothing] = {
    for {
      // configuration
      config <- Stream.eval(ApplicationConfig.loadOrThrow[F])

      // services
      client  <- BlazeClientBuilder[F](global).withIdleTimeout(config.server.clientTimeout).stream
      algebra <- Stream.eval(GitHubContributorAlg[F](config.github)(config.server.cacheExpiry)(client).valueOr(terminate()))

      // routes
      routes = (Contributions.routes[F](algebra) <+> HealthCheck.routes[F]).orNotFound

      // request logging
      httpApp = Logger.httpApp(logHeaders = true, logBody = true)(routes)

      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(config.server.httpPort, "0.0.0.0")
        .withIdleTimeout(config.server.idleTimeout)
        .withResponseHeaderTimeout(config.server.idleTimeout minus (20 seconds))
        .withHttpApp(httpApp)
        .serve
    } yield exitCode
  }.drain


  def terminate[A](): Throwable => A = error =>
    throw error
}

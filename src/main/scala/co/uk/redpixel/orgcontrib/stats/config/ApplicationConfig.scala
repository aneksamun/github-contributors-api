package co.uk.redpixel.orgcontrib.stats.config

import cats.Applicative
import com.typesafe.config.ConfigFactory
import pureconfig.syntax.ConfigReaderOps

final case class ApplicationConfig(server: ServerConfig, github: GitHubConfig)

object ApplicationConfig {
  import pureconfig.generic.auto._
  import eu.timepit.refined.pureconfig._

  def loadOrThrow[F[_]](implicit F: Applicative[F]): F[ApplicationConfig] =
    F.pure(ConfigFactory.load.toOrThrow[ApplicationConfig])
}

package co.uk.redpixel.orgcontrib.stats

import cats.effect.{ExitCode, IO, IOApp}
import scalacache.CatsEffect.modes.async

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    ContributionsStatsServer.stream[IO]
      .compile
      .drain
      .as(ExitCode.Success)
}

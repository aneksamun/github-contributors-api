package co.uk.redpixel.orgcontrib.stats.http.route

import cats.Monad
import cats.effect.Sync
import co.uk.redpixel.orgcontrib.stats.algebra.ContributorAlg
import co.uk.redpixel.orgcontrib.stats.algebra.data.Organisation
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

object Contributions {

  def routes[F[_] : Sync : Monad](algebra: ContributorAlg[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "org" / name / "contributors" =>
        algebra.stats(Organisation(name)).foldF(NotFound())(Ok(_))
    }
  }
}

package co.uk.redpixel.orgcontrib.stats.http.route

import cats.effect.Sync
import io.circe.Json
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

object HealthCheck {

  def routes[F[_] : Sync]: HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "internal" / "status" =>
        Ok(Json.obj("healthy" := true))
    }
  }
}

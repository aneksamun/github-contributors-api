package co.uk.redpixel.orgcontrib.stats.it

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import co.uk.redpixel.orgcontrib.stats.http.route.HealthCheck
import fs2.text.{lines, utf8Decode}
import io.circe.Json
import org.http4s.Request
import org.http4s.implicits._
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AsyncFeatureSpec
import org.scalatest.matchers.should.Matchers

class HealthCheckSpec extends AsyncFeatureSpec
  with GivenWhenThen
  with AsyncIOSpec
  with Matchers {

  Feature("Health check") {

    info("As system owner")
    info("I wish to have service health check")
    info("So that I can integrate it with deployment services like k8s")

    Scenario("Checking service availability") {
      When("I make health check request")
      val status = HealthCheck.routes[IO].orNotFound(Request[IO](uri = uri"/internal/status"))

      Then("the status should be healthy")
      status.flatMap {
        _.body
          .through(utf8Decode)
          .through(lines)
          .compile
          .foldMonoid
      }
      .asserting(_ should be(Json.obj("healthy" -> Json.fromBoolean(true)).noSpaces))
    }
  }
}

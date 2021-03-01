package co.uk.redpixel.orgcontrib.stats.config

import eu.timepit.refined.types.all.PosInt

import scala.concurrent.duration.FiniteDuration

final case class CircuitBreakerConfig(maxFailures: PosInt, resetTimeout: FiniteDuration)

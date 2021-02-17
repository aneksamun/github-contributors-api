package co.uk.redpixel.orgcontrib.stats.config

import eu.timepit.refined.types.all.NonSystemPortNumber

import scala.concurrent.duration.Duration

final case class ServerConfig(httpPort: NonSystemPortNumber,
                              cacheExpiry: Duration,
                              idleTimeout: Duration,
                              clientTimeout: Duration)

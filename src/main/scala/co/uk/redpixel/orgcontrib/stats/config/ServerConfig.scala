package co.uk.redpixel.orgcontrib.stats.config

import eu.timepit.refined.types.all.NonSystemPortNumber

final case class ServerConfig(httpPort: NonSystemPortNumber)

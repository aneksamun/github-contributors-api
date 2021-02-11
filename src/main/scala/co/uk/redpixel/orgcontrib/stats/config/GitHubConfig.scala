package co.uk.redpixel.orgcontrib.stats.config

import eu.timepit.refined.types.all.{NonEmptyString, PosInt}

import java.net.URL

final case class GitHubConfig(apiUrl: URL, token: NonEmptyString, maxConcurrent: PosInt)

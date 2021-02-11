package co.uk.redpixel.orgcontrib.stats.infrastructure.github.client

import co.uk.redpixel.orgcontrib.stats.algebra.data.{Name, Total}

final case class GitHubContributor(login: Name, contributions: Total)

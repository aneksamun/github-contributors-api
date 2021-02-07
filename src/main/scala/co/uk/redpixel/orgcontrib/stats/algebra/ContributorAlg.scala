package co.uk.redpixel.orgcontrib.stats.algebra

import cats.data.OptionT
import co.uk.redpixel.orgcontrib.stats.algebra.data.{Contributor, Organisation}

trait ContributorAlg[F[_]] {

  def stats(org: Organisation): OptionT[F, Seq[Contributor]]
}

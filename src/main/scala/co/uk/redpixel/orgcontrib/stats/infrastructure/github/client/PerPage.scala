package co.uk.redpixel.orgcontrib.stats.infrastructure.github.client

final case class PerPage(value: Int) extends AnyVal

object PerPage {

  def max: PerPage = PerPage(100)
}

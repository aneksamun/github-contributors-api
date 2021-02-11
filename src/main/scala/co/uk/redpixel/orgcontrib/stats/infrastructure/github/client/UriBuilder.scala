package co.uk.redpixel.orgcontrib.stats.infrastructure.github.client

import co.uk.redpixel.orgcontrib.stats.algebra.data.Organisation
import org.http4s.Uri

final case class UriBuilder(pattern: String) extends AnyVal {

  def withOrg(org: Organisation): UriBuilder =
    copy(pattern.replace("{org}", org.name))

  def withPaginationParams(page: Page, perPage: PerPage): UriBuilder =
    copy(pattern.concat(s"?page=${page.value}&per_page=${perPage.value}"))

  def create: Uri = {
    // Regex removes text in curly brackets if it contains a `page` parameter
    // For example, should remove something like {?type,page,per_page,sort} from the URL
    Uri.unsafeFromString(pattern.replaceAll("\\{[^}]*\\bpage\\b[^}]*}", ""))
  }
}

object UriBuilder {

  implicit def builderToUri(builder: UriBuilder): Uri =
    builder.create
}

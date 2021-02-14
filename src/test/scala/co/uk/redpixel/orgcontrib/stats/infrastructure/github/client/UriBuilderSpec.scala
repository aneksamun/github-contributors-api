package co.uk.redpixel.orgcontrib.stats.infrastructure.github.client

import co.uk.redpixel.orgcontrib.stats.support.scalacheck._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class UriBuilderSpec extends AnyWordSpec
  with ScalaCheckPropertyChecks
  with Matchers {

  "The URI builder" should {
    "successfully format an URL" in {
      forAll(genOrganisation, genPage, genPerPage) { (org, page, perPage) =>
        val url = UriBuilder("https://api.github.com/orgs/{org}/repos{?type,page,per_page,sort}")
          .withOrg(org)
          .withPaginationParams(page, perPage)
          .create.toString

        url should be(s"https://api.github.com/orgs/${org.name}/repos?page=${page.value}&per_page=${perPage.value}")
      }
    }
  }
}

package co.uk.redpixel.orgcontrib.stats.support

import co.uk.redpixel.orgcontrib.stats.algebra.data.Organisation
import co.uk.redpixel.orgcontrib.stats.infrastructure.github.client.{Page, PerPage}
import org.scalacheck.Gen

package object scalacheck {

  // --core--

  def genNonEmptyString(gc: Gen[Char]): Gen[String] =
    Gen.choose(3, 8).flatMap(n => Gen.stringOfN(n, gc))

  // --domain--

  def genOrganisation: Gen[Organisation] =
    genNonEmptyString(Gen.alphaChar).map(Organisation)

  def genPage: Gen[Page] =
    Gen.posNum[Int].map(Page)

  def genPerPage: Gen[PerPage] =
    Gen.chooseNum(1, PerPage.max.value).map(PerPage(_))

  def genToken: Gen[String] = Gen.stringOfN(40, Gen.alphaNumChar)
}

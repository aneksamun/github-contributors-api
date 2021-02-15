package co.uk.redpixel.orgcontrib.stats.support

import co.uk.redpixel.orgcontrib.stats.algebra.data.Organisation
import enumeratum.values.{StringEnum, StringEnumEntry}

import java.net.URL
import scala.io.Source
import scala.util.Using

sealed abstract class Template(val value: String) extends StringEnumEntry {
  def of(baseUrl: URL, organisation: Organisation): String =
    load replace("{baseUrl}", baseUrl.toString) replace("{org}", organisation.name)

  def load: String = {
    Using.resource(getClass.getResourceAsStream(s"/templates/$value")) { stream =>
      Source.fromInputStream(stream).mkString
    }
  }
}

object Template extends StringEnum[Template] {

  case object OrganisationDetails extends Template("organisation-details.json")
  case object GumtreeContributors extends Template("gumtree-contributors.json")
  case object CgumContributors extends Template("cgum-contributors.json")
  case object PythonParserContributors extends Template("pythonparser-contributors.json")
  case object SamplesContributors extends Template("samples-contributors.json")
  case object RoslynMLContributors extends Template("RoslynML-contributors.json")

  def values: IndexedSeq[Template] = findValues
}


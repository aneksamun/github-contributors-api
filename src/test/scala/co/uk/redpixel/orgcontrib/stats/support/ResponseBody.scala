package co.uk.redpixel.orgcontrib.stats.support

import cats.Show
import cats.syntax.show._
import enumeratum.values.{StringEnum, StringEnumEntry}

import scala.io.Source
import scala.util.Using

sealed abstract class ResponseBody(val value: String) extends StringEnumEntry {
  def load: String = value.show
}

object ResponseBody extends StringEnum[ResponseBody] {

  case object Index extends ResponseBody("/index.json")

  def values: IndexedSeq[ResponseBody] = findValues

  implicit val showResponse: Show[ResponseBody] = (response: ResponseBody) => {
    Using.resource(getClass.getResourceAsStream(response.value)) { stream =>
      Source.fromInputStream(stream).mkString
    }
  }
}

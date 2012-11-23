package fleet
package json

import bigtop.json.{JsonFormatters, JsonWriter}
import blueeyes.json.JsonAST._
import blueeyes.json.JsonDSL._
import fleet.frequent.SpaceSaver
import fleet.base.Counter

trait JsonWriters extends JsonFormatters {

  implicit def spaceSaverWriter[A](implicit itemWriter: JsonWriter[A]): JsonWriter[SpaceSaver[A]] = new JsonWriter[SpaceSaver[A]] {

    def write(in: SpaceSaver[A]): JValue = {
      val items = in.top()

        items.foldLeft(Seq[JValue]("typename" -> "frequent-items")) {

          (accum, item) => (("item" -> item._1.toJson) ~ ("count" -> item._2)) +: accum
        }
    }

  }

  implicit def counterWriter: JsonWriter[Counter] = new JsonWriter[Counter] {

    def write(in: Counter): JValue = {
      ("typename" -> "counter") ~ ("count" -> in.count)
    }

  }

}

object JsonWriters extends JsonWriters

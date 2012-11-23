package fleet
package json

import bigtop.json.{JsonFormatters, JsonWriter}
import blueeyes.json.JsonAST._
import blueeyes.json.JsonDSL._
import fleet.frequent.SpaceSaver

trait JsonWriters extends JsonFormatters {

  implicit def spaceSaverWriter[A](implicit itemWriter: JsonWriter[A]): JsonWriter[SpaceSaver[A]] = new JsonWriter[SpaceSaver[A]] {

    def write(in: SpaceSaver[A]): JValue = {
      val items = in.top()
      items.map {
        case (item, count) => ("item" -> item.toJson) ~ ("count" -> count)
      }
    }

  }

}

object JsonWriters extends JsonWriters

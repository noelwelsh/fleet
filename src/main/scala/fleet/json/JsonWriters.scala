package fleet
package json

import bigtop.json.JsonWriter
import blueeyes.json.JsonAST._
import blueeyes.json.JsonDSL._

trait JsonWriters {

  implicit def spaceSaverWriter[A](implicit itemWriter: JsonWriter[A]): JsonWriter[SpaceSaver[A]] = new JsonWriter[SpaceSaver[A]] {

    def write(in: SpaceSaver[A]): JValue = {
      val items = in.top()
      in.map {
        case (item, count) => ("item" -> item.toJson) ~ ("count" -> count)
      }
    }

  }

}

object JsonWriters extends JsonWriters

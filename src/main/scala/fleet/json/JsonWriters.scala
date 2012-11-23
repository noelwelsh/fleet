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

  implicit val counterWriter: JsonWriter[Counter] = new JsonWriter[Counter] {

    def write(in: Counter): JValue = {
      ("typename" -> "counter") ~ ("count" -> in.count)
    }

  }

  implicit def accumWriter[A](implicit itemWriter: JsonWriter[A]): JsonWriter[Accumulator[A]] = new JsonWriter[Accumulator[A]] {

    def write(in: Accumulator[A]): JValue = {
      in match {
        case counter:Counter =>
          counterWriter.write(counter)
        case spaceSaver:SpaceSaver[A] =>
          spaceSaverWriter(itemWriter).write(spaceSaver)
        case AccumulatorNull() =>
          (JArray.empty : JValue)
        case AccumulatorPair(head, tail) => {
          (this.write(head), this.write(tail)) match {
            case (obj@JObject(f1), JArray(f2))  => JArray(obj +: f2)
            case (JArray(f1),      JArray(f2))  => JArray(f1 ++ f2)
            case bad => sys.error("accumWriter expected JObject or JArray but got " + bad)
          }
        }
      }
    }

  }

}

object JsonWriters extends JsonWriters

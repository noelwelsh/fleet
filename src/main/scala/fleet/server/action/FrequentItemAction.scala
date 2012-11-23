package fleet
package server
package action

import bigtop.concurrent.FutureImplicits
import bigtop.util.Uuid
import bigtop.problem.Problem
import bigtop.json.JsonFormatters
import blueeyes.json.JsonAST._
import fleet.frequent.SpaceSaver
import fleet.json.JsonWriters
import scalaz.syntax.validation._

trait FrequentItemAction extends Action with JsonWriters with FutureImplicits with JsonFormatters {

  val sink = new SpaceSaver[Uuid](1000)

  def event(in: JValue) = {
    for {
      uuid <- in.mandatory[Uuid]("uuid")
    } yield {
      sink += uuid
      Ok
    }
  }

  def view(in: JValue) = {
    sink.toJson.success[Problem]
  }

}

package fleet
package server
package action

import akka.actor.ActorSystem
import akka.util.duration._
import bigtop.concurrent.FutureImplicits
import bigtop.util.Uuid
import bigtop.problem.Problem
import bigtop.json.JsonFormatters
import blueeyes.json.JsonAST._
import fleet.base.Timebox
import fleet.frequent.SpaceSaver
import fleet.json.JsonWriters
import scalaz.syntax.validation._

class FrequentItemAction(system: ActorSystem) extends Action with JsonWriters with FutureImplicits with JsonFormatters {

  def create = new SpaceSaver[Uuid](1000)

  val sink: Accumulator[Uuid] =
    Timebox(1 minutes, system) { create } +:
    Timebox(15 minutes, system) { create } +:
    Timebox(1 hour, system) { create } +:
    Timebox(1 day, system) { create }



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

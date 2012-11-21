package fleet
package server
package action

import bigtop.problem.Problem
import blueeyes.json.JsonAST._
import blueeyes.json.JsonDSL._
import scalaz.Validation

trait Action {

  val Ok: JValue = ("typename", "ok")

  def event(in: JValue): Validation[Problem, JValue]

  def view(in: JValue): Validation[Problem, JValue]

}

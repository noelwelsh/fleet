package fleet.server

import akka.actor.Actor
import bigtop.util.Problem
import spray.routing._
import spray.json._
import scalaz.Validation

// A simple HTTP service for your statistics

class ServiceActor extends Actor {

  def actorRefFactory = context

  def receive = runRoute(...)

  def handler(in: JsValue): Validation[Problem,JsValue]
}

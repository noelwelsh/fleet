package fleet
package server
package service

import akka.dispatch.{Future, Promise}
import blueeyes.{BlueEyesServiceBuilder, BlueEyesServer}
import blueeyes.core.http.{HttpRequest, HttpResponse, HttpStatus}
import bigtop.util.ExceptionHandlerCombinators
import bigtop.concurrent.FutureValidation
import bigtop.concurrent.FutureImplicits._
import bigtop.http._
import bigtop.http.JsonServiceImplicits._
import bigtop.problem._
import fleet.server.action.{Action, FrequentItemAction}
import com.weiglewilczek.slf4s.Logger

case class FleetConfig()

trait FleetService extends BlueEyesServiceBuilder
  with JsonRequestHandlerCombinators
  with ExceptionHandlerCombinators
{
  implicit val log: Logger

  val frequentItems = new FrequentItemAction {}

  def dispatch(key: String): Validation[Problem, String] = {
    key match {
      case "request" => frequentItems.success[Problem]
      case _         => Problems.notFound(key).fail[Problem]
    }
  }

  val fleetService = {
    service("fleet", "1.0.0") {
      startup {
        Promise.successful(FleetConfig())
      } ->
      request {
        config: FleetConfig =>
          path("/event") {
            json {
              post {
                req: HttpRequest[Future[JValue]] =>
                  (for {
                    json <- req.json
                    key  <- json.mandatory[String]("key").fv
                    resp <- dispatch(key).event(json)
                  } yield resp).toResponse
              }
            }
          } ~
          path("/view") {
            json {
              post {
                req: HttpRequest[Future[JValue]] =>
                  (for {
                    json <- req.json
                    key  <- json.mandatory[String]("key").fv
                    resp <- dispatch(key).view(json)
                  } yield resp).toResponse
              }
            }
          }
      } ~
      shutdown {
        config => Promise.successful(())
      }
    }
  }
}

package fleet
package server
package service

import akka.actor.ActorSystem
import akka.dispatch.{Future, Promise}
import blueeyes.{BlueEyesServiceBuilder, BlueEyesServer}
import blueeyes.core.http.{HttpRequest, HttpResponse, HttpStatus}
import blueeyes.core.data._
import blueeyes.json.JsonAST._
import bigtop.concurrent.FutureValidation
import bigtop.concurrent.FutureImplicits._
import bigtop.http._
import bigtop.json.JsonFormatters
import bigtop.problem._
import com.weiglewilczek.slf4s.Logger
import fleet.server.action.{Action, FrequentItemAction}
import org.streum.configrity.Configuration
import scala.collection.JavaConversions._
import scalaz.Validation
import scalaz.syntax.validation._

case class FleetConfig(config: Configuration) {
  val akkaConfig = com.typesafe.config.ConfigFactory.parseMap(config.detach("agents").data)

}

trait FleetService extends BlueEyesServiceBuilder
  with JsonRequestHandlerCombinators
  with JsonServiceImplicits
  with ExceptionHandlerCombinators
  with JsonFormatters
  with BijectionsChunkJson
  with SafeBijectionsChunkFutureJson
{
  implicit val log: Logger
  implicit val system: ActorSystem = ActorSystem("fleet")

  val frequentItems: Action = new FrequentItemAction(system)

  def dispatch(key: String): Validation[Problem, Action] = {
    key match {
      case "request" => frequentItems.success[Problem]
      case _         => Problems.Client.notFound(key).fail[Action]
    }
  }

  val fleetService = {
    service("fleet", "1.0.0") {
      context =>
        startup {
          Promise.successful(FleetConfig(context.config))
        } ->
        request {
          config: FleetConfig => {
            path("/event") {
              json {
                post {
                  req: HttpRequest[Future[JValue]] =>
                    (for {
                      json   <- req.json
                      key    <- json.mandatory[String]("key")
                      action <- dispatch(key)
                      resp   <- action.event(json)
                    } yield resp).toResponse
                }
              }
            } ~
            path("/view") {
              json {
                post {
                  req: HttpRequest[Future[JValue]] =>
                    (for {
                      json   <- req.json
                      key    <- json.mandatory[String]("key").fv
                      action <- dispatch(key)
                      resp   <- action.view(json)
                    } yield resp).toResponse
                }
              }
            }
          }
        } ->
        shutdown {
          config => Promise.successful(())
        }
      }
  }
}

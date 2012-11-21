package fleet.server

import akka.actor._
import fleet.frequent.SpaceSaver
import spray.can.server._
import spray.io._
import spray.routing._
import spray.json._
import spray.http._
import spray.httpx.marshalling._
import spray.httpx.unmarshalling._
import spray.httpx.SprayJsonSupport
import StatusCodes._
import HttpHeaders._
import MediaTypes._
import scalaz.Validation


// Example server implementing frequent item countings
//
// Send to /update
//
// Send Json like {"uuid":"auuid", "count": 2}
//
// Get stats from /view
class FrequentItemsService extends Actor with HttpService with SprayJsonSupport with DefaultJsonProtocol {

  case class Counter(uuid: String, count: Int)
  implicit val counterFormat = jsonFormat2(Counter.apply)

  val capacity = 1000 // Hard-code for now
  val spaceSaver = new SpaceSaver[String](1000)

  val okResponse = HttpResponse(status = OK)
  val notFoundResponse = HttpResponse(status = NotFound)

  def actorRefFactory = context

  def receive = runRoute(route)

  def route =
    post {
      path("update") {
        entity(as[String]) {
          json => {
            ctx => {
              val counter = json.asJson.convertTo[Counter]
              for(i <- 0 until counter.count) { spaceSaver += counter.uuid }
              ctx.complete(okResponse)
            }
          }
        }
      }
    } ~
    get {
      path("view") {
        ctx => {
          spaceSaver.top(capacity).toJson
        }
      }
    }

}


object FrequentItemsApp extends App {
  val system = ActorSystem("frequent-items")

  // every spray-can HttpServer (and HttpClient) needs an IOBridge for low-level network IO
  // (but several servers and/or clients can share one)
  val ioBridge = new IOBridge(system).start()

  val service = system.actorOf(Props[FrequentItemsService], "frequent-items-service")

  // create and start the spray-can HttpServer, telling it that
  // we want requests to be handled by our singleton service actor
  val httpServer = system.actorOf(
    Props(new HttpServer(ioBridge, SingletonHandler(service))),
    name = "http-server"
  )

  // a running HttpServer can be bound, unbound and rebound
  // initially to need to tell it where to bind to
  httpServer ! HttpServer.Bind("localhost", 8080)

  // finally we drop the main thread but hook the shutdown of
  // our IOBridge into the shutdown of the applications ActorSystem
  system.registerOnTermination {
    ioBridge.stop()
  }
}

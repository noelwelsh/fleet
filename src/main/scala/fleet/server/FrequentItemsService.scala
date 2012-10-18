package fleet.server

import akka.actor.{Props, ActorSystem}
import spray.can.server.HttpServer
import spray.io._
import spray.json._
import spray.routing._

// Example server implementing frequent item countings

class FrequentItemsService extends Service {

  val capacity = 1000 // Hard-code for now
  val spaceSaver = new SpaceSaver(1000)

  def handler(in: JsValue) =

  // Convert to a tuple (case class) uuid x count
  // Update

  // String

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

package fleet
package base

import akka.actor.{ActorSystem, Scheduler}
import akka.util.Duration
import org.specs2.mutable._

class TimeboxSpec extends Specification {

  val system = ActorSystem("test")
  implicit val scheduler = system.scheduler

  "Timebox" should {

    "Propagate accumulation to inner" in {
      var counter: Counter = null
      val box = Timebox(Duration(5, "seconds")){
        val newCounter = Counter()
        counter = newCounter
        newCounter
      }

      box += 3
      counter.count mustEqual 3
    }

  }

}

package fleet
package base

import akka.actor.{ActorSystem, Scheduler}
import akka.util.Duration

/** A Timebox is a wrapper around an accumulator that resets the wrapped accumulator at a regular interval. Useful to stats on per minute/day/etc. frequencies */
class Timebox[A](create: => Accumulator[A], period: Duration, scheduler: Scheduler) extends Accumulator[A] {

  scheduler.schedule(period, period) { this.reset() }

  var inner = create

  def +=(item: A): Unit = inner += item

  private def reset() = inner = create

}

object Timebox {

  def apply[A](period: Duration)(create: => Accumulator[A])(implicit scheduler: Scheduler): Timebox[A] =
    new Timebox(create, period, scheduler)

}

package fleet
package base

case class Counter(var count: Int) extends Accumulator[Int] {
  def +=(item: Int) = count += item
}

object Counter {

  def apply(): Counter = Counter(0)

}

package fleet
package base

case class Counter(var count: Int) extends Accumulator[Int] {
  def +=(item: Int) = count += item
}

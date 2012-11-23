package fleet

/** Trait for mutable accumulators */
trait Accumulator[A] {
  def +=(item: A): Unit
  def +:(head: Accumulator[A]): AccumulatorSeq[A] =
    head +: (this +: AccumulatorNull())
}

object Accumulator {
  def apply[A](accums: Accumulator[A]*): Accumulator[A] =
    accums.foldLeft(AccumulatorNull[A]() : AccumulatorSeq[A]) {
      (seq, item) => item +: seq
    }
}

sealed trait AccumulatorSeq[A] extends Accumulator[A] {
 override def +:(head: Accumulator[A]) = AccumulatorPair[A](head, this)
}

case class AccumulatorNull[A]() extends AccumulatorSeq[A] {
  def +=(item: A) = ()
}

case class AccumulatorPair[A](head: Accumulator[A], tail: AccumulatorSeq[A]) extends AccumulatorSeq[A] {
  def +=(item: A): Unit = {
    head += item
    tail += item
  }
}

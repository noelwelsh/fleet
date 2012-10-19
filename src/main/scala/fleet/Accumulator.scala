package fleet

/** Trait for mutable accumulators */
trait Accumulator[A] {
  def +=(item: A): Unit
}

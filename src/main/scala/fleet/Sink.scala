package fleet

/** Abstract data sink */
trait Sink[A] {

  /** Add an item to this sink. */
  def add(in: A): Unit

  def +(in: A): Unit = add(in)

}

package fleet
package hash

trait Hash32[A] {

  def hash32(in: A, seed: Int): Int
  def hash32(in: A): Int = hash32(in, 0)
}

package fleet
package hash

trait Hash32[A] {

  def hash32(in: A, seed: Int): Int

}

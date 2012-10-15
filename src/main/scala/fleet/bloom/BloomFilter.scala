package fleet
package bloom

import fleet.hash.Hash32
import scala.collection.mutable.BitSet

class BloomFilter[A](size: Int, nHashes: Int, hash: Hash32[A]) {

  private val bitSet = new BitSet(size)

  /**
   * Add an element to the BloomFilter
   *
   * @return true if the element was not in the filter, false otherwise
   */
  def add(elt: A): Boolean = {
    foldHashes(elt, true) {
      case (hash, wasNotPresent) =>
        wasNotPresent && bitSet.add(hash)
    }
  }

  def clear(): Unit = bitSet.clear()

  def contains(elt: A): Boolean = {
    foldHashes(elt, true) {
      case (hash, wasPresent) =>
        wasPresent && bitSet.contains(hash)
    }
  }

  def apply(element: A): Boolean = contains(element)

  private def foldHashes[B](elt: A, seed: B)(f: (Int, B) => B): B = {
    val hash0 = hash.hash32(elt, 0)
    val hash1 = hash.hash32(elt, hash0)

    var accum = seed
    for(i <- 0 until nHashes) {
      val hash = (hash0 + (i * hash1)) % size
      accum = f(hash, accum)
    }

    accum
  }

}

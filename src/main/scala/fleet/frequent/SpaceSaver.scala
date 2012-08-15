package fleet
package frequent

import scala.collection.mutable.Hash

/**
 * The Space Saver algorithm for calculating frequent or top-k items.
 *  Described in http://www.cs.ucsb.edu/research/tech_reports/reports/2005-23.pdf
 *
 * This implementation uses mutable state, so be careful with sharing etc.
 */
class SpaceSaver[A](val capacity: Int, val hash: Hash[A]) {

  // Interface

  def observe(item: A): SpaceSaver[A]

  def count(item: A): Int

  def top(k: Int): Seq[(A, Int)]

  // Internals

  /** How many elements (up to maximum of capacity) have been allocated */
  var usage: Int = 0

  /** Find elements by hash code */
  var elements: Map[Int, Element[A]] = new Map()

  /** The head (lowest count) element of the bucket doubly-linked list */
  var bucketHead: Bucket[A] = Bucket(0, Element(..., ..., null), None, None)

  case class Bucket[A](val count: Int, val children: Element[A], var prev: Option[Bucket[A]], var next: Option[Bucket[A]])

  case class Element[A](var bucket: Bucket[A], var next: Element[A], var elt: A)
}

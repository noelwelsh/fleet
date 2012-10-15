package fleet
package frequent

import scala.annotation.tailrec
import scala.collection.mutable.{DoubleLinkedList, Map}
import scalaz.std.option._

/**
 * The Space Saver algorithm for calculating frequent or top-k items.
 *  Described in http://www.cs.ucsb.edu/research/tech_reports/reports/2005-23.pdf
 *
 * This implementation uses mutable state, so be careful with sharing etc.
 *
 * This is not a correct implementation of the Stream Summary algorithm.
 * There should be a two level tree which allows faster increments of elements.
 */
class SpaceSaver[A](val capacity: Int) {

  case class Element[A](var count: Int, val elem: A)

  // Interface

  /** Add one count for the item */
  def +(item: A): Unit = {
    elements.get(item) match {
      case Some(elt) =>
        // increment count, bubble up bucketsHead, possibly reset bucketsHead to new bucket
        val wasHead = (bucketsHead == elt)
        val next = elt.next
        elt.elem.count = elt.elem.count + 1
        insert(elt)
        if(wasHead && elt.next != next)
          bucketsHead = next

      case None =>
        // add if we've below capacity, otherwise replace smallest element
        if(usage < capacity) {
          usage = usage + 1
          val elt = new DoubleLinkedList(Element(1, item), bucketsHead)
          bucketsHead = elt
          elements += (item -> elt)
        } else {
          val removed = bucketsHead.elem
          val elt = new DoubleLinkedList(Element(removed.count, item), bucketsHead.next)
          bucketsHead = elt
          elements += (item -> elt)
          elements -= removed.elem
        }
    }
  }

  /** Gets the count for the item, if we have one */
  def get(item: A): Option[Int] = {
    elements.get(item).map(_.elem.count)
  }

  /** Gets the top k items (or fewer if we haven't stored k) */
  def top(k: Int): Seq[(A, Int)] = {
    if(bucketsHead == null) {
      Seq()
    } else {
      val selected = (if(k > usage) bucketsHead else (bucketsHead.drop(usage - k)))
      selected.foldLeft(Seq[(A, Int)]()){
        (accum, elem) => (elem.elem, elem.count) +: accum
      }
    }
  }

  // Internals

  /** How many elements (up to maximum of capacity) have been allocated */
  var usage: Int = 0

  /** Find counts by element */
  val elements: Map[A, DoubleLinkedList[Element[A]]] = Map()

  /** The head (lowest count) element of the bucket doubly-linked list */
  var bucketsHead: DoubleLinkedList[Element[A]] = null

  /** Assumes elt is already positioned in a sorted list. Sets elt to its correct position, assuming counts only increase */
  @tailrec final def insert(elt: DoubleLinkedList[Element[A]]): Unit = {
    if(elt.next == null)
      () // We've reached the end of the list
    if(elt.elem.count <= elt.next.elem.count)
      () // It's in the correct position
    else { // Move up one
      val next = elt.next
      next.prev = elt.prev
      elt.prev = next
      elt.next = next.next
      next.next = elt
      insert(elt)
    }
  }

}

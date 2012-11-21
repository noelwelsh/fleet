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
 * This is not a correct implementation of the Stream Summary data structure.
 * There should be a two level tree which allows faster increments of elements.
 */
class SpaceSaver[A](val capacity: Int) extends Sink[A] {

  case class Element[A](var count: Int, val elem: A)

  // Interface

  /** Add one count for the item */
  def add(item: A): Unit = {
    //println("adding "+item+" to "+bucketsHead)
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
  def top(k: Int = usage): Seq[(A, Int)] = {
    //println("have "+bucketsHead+" asked for "+k+" usage is "+usage+" dropping "+(usage - k)+" giving "+bucketsHead.drop(usage - k))
    val selected = (if(k > usage) bucketsHead else (bucketsHead.drop(usage - k)))
    selected.foldLeft(Seq[(A, Int)]()){
      (accum, elem) => (elem.elem, elem.count) +: accum
    }
  }

  // Internals

  /** How many elements (up to maximum of capacity) have been allocated */
  var usage: Int = 0

  /** Find counts by element */
  val elements: Map[A, DoubleLinkedList[Element[A]]] = Map()

  /** The head (lowest count) element of the bucket doubly-linked list */
  var bucketsHead: DoubleLinkedList[Element[A]] = new DoubleLinkedList()

  /** Assumes elt is already positioned in a sorted list. Sets elt to its correct position, assuming counts only increase */
  @tailrec final def insert(elt: DoubleLinkedList[Element[A]]): Unit = {
    if(elt.next.next == elt.next)
      () // We've reached the end of the list
    else if(elt.elem.count <= elt.next.elem.count)
      () // It's in the correct position
    else { // Move up one
      val next = elt.next
      val nextNext = next.next
      val prev = elt.prev

      elt.prev = next
      elt.next = nextNext

      next.prev = prev
      next.next = elt

      // prev can be null if elt is the head of the list
      if(prev != null) prev.next = next

      insert(elt)
    }
  }

}

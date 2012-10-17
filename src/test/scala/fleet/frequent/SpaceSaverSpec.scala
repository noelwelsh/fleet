package fleet
package frequent

import org.specs2.mutable._

class SpaceSaverSpec extends Specification {

  "SpaceSaver" should {

    "store top K elements given sufficient space" in {
      val spaceSaver = new SpaceSaver[String](5)
      val data = Seq("X", "X", "Y", "Z", "A", "B", "A", "C", "X", "X", "A", "A", "A", "C")

      data.foreach(spaceSaver + _)

      spaceSaver.top(3) mustEqual Seq(("A", 5), ("X", 4), ("C", 2))
    }

    "store approx top K elements given insufficient space" in {
      val spaceSaver = new SpaceSaver[String](3)
      val data = Seq("X", "X", "Y", "Z", "A", "B", "A", "C", "X", "X", "A", "A", "A", "C")

      data.foreach(spaceSaver + _)

      spaceSaver.top(3) mustEqual Seq(("X", 4), ("A", 3), ("C", 1))
    }
  }

}

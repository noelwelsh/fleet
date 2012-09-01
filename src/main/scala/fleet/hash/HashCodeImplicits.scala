package fleet
package hash

import com.google.common.hash._

trait HashCodeImplicits {

  implicit def hashCodeToInt(in: HashCode): Int = in.asInt()

}

object HashCodeImplicits extends HashCodeImplicits

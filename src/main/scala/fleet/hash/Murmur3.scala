package fleet
package hash

import com.google.common.hash._

object Murmur3 extends HashCodeImplicits with Hash32[Int] with Hash32[Array[Byte]] with Hash32[Long] {

  def hash32(bytes: Array[Byte], seed: Int = 0) =
    Hashing.murmur3_32(seed).hashBytes(bytes)

  def hash32(in: Int, seed: Int = 0) =
    Hashing.murmur3_32(seed).hashInt(in)

  def hash32(in: Long, seed: Int = 0) =
    Hashing.murmur3_32(seed).hashLong(in)


  def hash128(bytes: Array[Byte], seed: Int = 0) =
    Hashing.murmur3_128(seed).hashBytes(bytes)

  def hash128(in: Int, seed: Int = 0) =
    Hashing.murmur3_128(seed).hashInt(in)

  def hash128(in: Long, seed: Int = 0) =
    Hashing.murmur3_128(seed).hashLong(in)

}

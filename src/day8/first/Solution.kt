package day8.first

import tools.timeSolution

const val IMAGE_WIDTH = 25
const val IMAGE_HEIGHT = 6
const val TOTAL_PIXELS = IMAGE_HEIGHT * IMAGE_WIDTH

fun main() = timeSolution {
  val (ones, twos) = readLine()!!.chunked(TOTAL_PIXELS).minBy { it.count { char -> char == '0' } }!!
    .fold(Pair(0, 0)) { (ones, twos), char ->
      when (char) {
        '1' -> Pair(ones + 1, twos)
        '2' -> Pair(ones, twos + 1)
        else -> Pair(ones, twos)
      }
    }
  println(ones * twos)
}

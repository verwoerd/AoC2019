package day8.second

import day8.first.IMAGE_WIDTH
import day8.first.TOTAL_PIXELS
import tools.timeSolution

fun main() = timeSolution {
  val result = readLine()!!.chunked(TOTAL_PIXELS).fold("2".repeat(TOTAL_PIXELS).toMutableList()) { acc, layer ->
    acc.mapIndexed { index, c ->
      when (c) {
        '2' -> when (layer[index]) {
          '1' -> 'X'
          '2' -> '2'
          else -> ' '
        }
        else -> c
      }
    }.toMutableList()
  }
  result.chunked(IMAGE_WIDTH).forEach { println(it.joinToString("")) }
}

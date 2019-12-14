package day14.second

import day14.first.readReactionInput
import day14.first.solver
import tools.timeSolution

const val target = 1000000000000L

fun main() = timeSolution {
  val reactionList = readReactionInput()
  var bound = 1L

  while (bound < Long.MAX_VALUE / 2 && solver(reactionList, bound) < target) {
    bound *= 2
  }

  var low = bound / 2
  var high = bound - 1
  var index = -1L
  var result: Long
  loop@ while (low <= high) {
    index = (high + low) / 2
    result = solver(reactionList, index)
    when (result) {
      target -> break@loop
      in target + 1..Long.MAX_VALUE -> high = index - 1
      in Long.MIN_VALUE until target -> low = index + 1
    }
  }
  println(index)
}



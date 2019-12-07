package day7.first

import tools.executeProgram
import tools.readInput
import tools.timeSolution
import java.util.Collections.swap

fun main() = timeSolution {
  val code = readInput()
    val permutations = generatePermutations(mutableListOf(0,1,2,3,4))
    val result = permutations.map {
      // acctuator 1
      var output = executeProgram(code, listOf(it[0],0))
      // actuator 2
      output = executeProgram(code, listOf(it[1],output.first()))
      //actuator 3
      output = executeProgram(code, listOf(it[2],output.first()))
      // actuator 4
      output = executeProgram(code, listOf(it[3],output.first()))
      // actuator 5
      output = executeProgram(code, listOf(it[4],output.first()))
      Pair(it.joinToString(","), output.first())
    }.maxBy (Pair<String, Int>::second)

  println(result)
}

fun generatePermutations(list: MutableList<Int>) = generatePermutation(list, 0, list.size, mutableListOf())

fun generatePermutation(list: MutableList<Int>, start:Int, end: Int, result: MutableList<IntArray>): MutableList<IntArray> {
  if (start == end) {
    result.add(list.toIntArray())
    return result
  }
  for (i in start until end) {
    swap(list, start, i)
    generatePermutation(list, start+1, end, result)
    swap(list, start, i)
  }
  return result
}

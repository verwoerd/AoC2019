package day2.second

import tools.timeSolution

fun main() = timeSolution {
  val code = readLine()!!.split(",").map { it.toInt() }.toIntArray()
  val target = 19690720
  loop@ for (noun in 0..100) {
    for (verb in 0..100) {
      if (executeProgram(code.copyOf(), noun, verb) == target) {
        println("Solution: ${100 * noun + verb}")
        break@loop
      }
    }
  }
}

fun executeProgram(code: IntArray, noun: Int, verb: Int): Int {
  var pointer = 0
  code[1] = noun
  code[2] = verb
  while (code[pointer] != 99) {
    code[code[pointer + 3]] = when (code[pointer]) {
      1 -> code[code[pointer + 1]] + code[code[pointer + 2]]
      2 -> code[code[pointer + 1]] * code[code[pointer + 2]]
      else -> throw IllegalArgumentException("Invalid opcode ${code[pointer]}")
    }
    pointer += 4
  }
  return code[0]
}

// Observed behaviour
// verb only increased the outcome by 1
fun maybeFasterSearch() = timeSolution {
  val code = readLine()!!.split(",").map { it.toInt() }.toIntArray()
  val target = 19690720
  val rangeEnd = 100
  loop@ for (noun in 0..rangeEnd) {
    when (val verb = target - executeProgram(code.copyOf(), noun, 0) ) {
      in 0..rangeEnd -> {
        println("Solution: ${100 * noun + verb}")
        break@loop
      }
    }
  }
}



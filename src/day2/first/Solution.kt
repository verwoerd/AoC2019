package day2.first

import tools.timeSolution

fun main() = timeSolution {
  val code = readLine()!!.split(",").map { it.toInt() }.toIntArray()
  var pointer = 0
  code[1] = 12
  code[2] = 2
  val step = 4
  while (code[pointer] != 99) {
    code[code[pointer + 3]] = when (code[pointer]) {
      1 -> code[code[pointer + 1]] + code[code[pointer + 2]]
      2 -> code[code[pointer + 1]] * code[code[pointer + 2]]
      else -> throw IllegalArgumentException("Invalid opcode ${code[pointer]}")
    }
    pointer += step
  }
  println(code[0])
}

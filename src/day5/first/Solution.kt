package day5.first

import tools.timeSolution

fun main() = timeSolution {
  executeProgram(readLine()!!.split(",").map { it.toInt() }.toIntArray())
}

fun executeProgram(code: IntArray): Int {
  var pointer = 0

  loop@ while (true) {
    val current = code[pointer]
    var mode3 = current / 10_000 % 10
    val mode2 = current / 1_000 % 10
    val mode1 = current / 100 % 10
    val opcode = current % 100
    when (opcode) {
      1 -> setValue(
        code,
        getValue(code, code[pointer + 1], mode1) + getValue(code, code[pointer + 2], mode2),
        code[pointer + 3],
        1
                   )
      2 -> setValue(
        code,
        getValue(code, code[pointer + 1], mode1) * getValue(code, code[pointer + 2], mode2),
        code[pointer + 3],
        1
                   )
      3 -> setValue(code, readLine()!!.toInt(), code[pointer + 1], 1)
      4 -> println(getValue(code, code[pointer + 1], mode1))
      99 -> break@loop
      else -> throw IllegalArgumentException("Invalid opcode ${code[pointer]}")
    }

    pointer += when (opcode) {
      in 1..2 -> 4
      in 3..4 -> 2
      else -> throw IllegalArgumentException("Invalid step $opcode")

    }
  }
  return code[0]
}

fun getValue(code: IntArray, value: Int, mode: Int): Int {
  return when (mode) {
    0 -> code[value]
    1 -> value
    else -> throw java.lang.IllegalArgumentException("Illegal paarameter mode $mode")
  }
}

fun setValue(code: IntArray, value: Int, address: Int, mode: Int) {
  when (mode) {
    0 -> code[code[address]] = value
    1 -> code[address] = value
  }
}


package day5.second

import tools.boolToInt
import tools.timeSolution

fun main() = timeSolution {
  executeProgram(readLine()!!.split(",").map { it.toInt() }.toIntArray())
}

fun executeProgram(code: IntArray): Int {
  var pointer = 0

  loop@ while (true) {
    val current = code[pointer]
    val startPointer = pointer
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
      5 -> if (getValue(code, code[pointer + 1], mode1) != 0) pointer = getValue(code, code[pointer + 2], mode2)
      6 -> if (getValue(code, code[pointer + 1], mode1) == 0) pointer = getValue(code, code[pointer + 2], mode2)
      7 -> setValue(
        code,
        boolToInt(getValue(code, code[pointer + 1], mode1) < getValue(code, code[pointer + 2], mode2)),
        code[pointer + 3],
        1
                   )
      8 -> setValue(
        code,
        boolToInt(getValue(code, code[pointer + 1], mode1) == getValue(code, code[pointer + 2], mode2)),
        code[pointer + 3],
        1
                   )
      99 -> break@loop
      else -> throw IllegalArgumentException("Invalid opcode ${code[pointer]}")
    }
    if (pointer == startPointer) {
      pointer += when (opcode) {
        in 1..2, in 7..8 -> 4
        in 3..4 -> 2
        in 5..6 -> 3
        else -> throw IllegalArgumentException("Invalid step $opcode")
      }
    }
  }
  return code[0]
}

fun getValue(code: IntArray, value: Int, mode: Int): Int {
  return when (mode) {
    0 -> code[value]
    1 -> value
    else -> throw java.lang.IllegalArgumentException("Illegal parameter mode $mode")
  }
}

fun setValue(code: IntArray, value: Int, address: Int, mode: Int) {
  when (mode) {
    0 -> code[code[address]] = value
    1 -> code[address] = value
  }
}

package tools

import kotlinx.coroutines.channels.Channel

/**
 * @author verwoerd
 * @since 7-12-2019
 */
suspend fun executeAsyncProgram(orig: IntArray, input: Channel<Int>, output: Channel<Int>) {
  // My biggest bug, don't let multiple threads edit the same array
  val code = orig.copyOf()
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
      3 -> setValue(code, input.receive(), code[pointer + 1], 1)
      4 -> output.send(getValue(code, code[pointer + 1], mode1))
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
}

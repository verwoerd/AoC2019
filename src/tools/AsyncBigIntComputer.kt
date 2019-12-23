package tools

import day17.second.runOutputListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author verwoerd
 * @since 7-12-2019
 */
fun executeBigProgram(orig: Map<Long, Long>, input: () -> Long, output: (Long) -> Unit) {
  val code = orig.toMutableMap()
  var relativeBase = 0L
  var pointer = 0L
  loop@ while (true) {
    val current = code[pointer] ?: throw IllegalArgumentException("Invalid instruction encountered")
    val startPointer = pointer
    val mode3 = current / 10_000 % 10
    val mode2 = current / 1_000 % 10
    val mode1 = current / 100 % 10
    val opcode = current % 100
    val operand1 = code[pointer + 1] ?: 0
    val operand2 = code[pointer + 2] ?: 0
    val operand3 = code[pointer + 3] ?: 0
    when (opcode) {
      1L -> setValue(
        code,
        getValue(code, operand1, mode1, relativeBase) + getValue(code, operand2, mode2, relativeBase),
        operand3,
        mode3, relativeBase
                    )
      2L -> setValue(
        code,
        getValue(code, operand1, mode1, relativeBase) * getValue(code, operand2, mode2, relativeBase),
        operand3,
        mode3, relativeBase
                    )
      3L -> setValue(code, input(), operand1, mode1, relativeBase)
      4L -> output(getValue(code, operand1, mode1, relativeBase))
      5L -> if (getValue(code, operand1, mode1, relativeBase) != 0L) pointer =
        getValue(code, operand2, mode2, relativeBase)
      6L -> if (getValue(code, operand1, mode1, relativeBase) == 0L) pointer =
        getValue(code, operand2, mode2, relativeBase)
      7L -> setValue(
        code,
        boolToLong(
          getValue(code, operand1, mode1, relativeBase) < getValue(
            code,
            operand2,
            mode2,
            relativeBase
                                                                  )
                  ),
        operand3,
        mode3, relativeBase
                    )
      8L -> setValue(
        code,
        boolToLong(
          getValue(code, operand1, mode1, relativeBase) == getValue(
            code,
            operand2,
            mode2,
            relativeBase
                                                                   )
                  ),
        operand3,
        mode3, relativeBase
                    )
      9L -> relativeBase += getValue(code, operand1, mode1, relativeBase)

      99L -> break@loop
      else -> throw IllegalArgumentException("Invalid opcode ${code[pointer]}")
    }
    if (pointer == startPointer) {
      pointer += when (opcode) {
        in 1..2, in 7..8 -> 4
        in 3..4, 9L -> 2
        in 5..6 -> 3
        else -> throw IllegalArgumentException("Invalid step $opcode")
      }
    }
  }
}

fun setValue(code: MutableMap<Long, Long>, value: Long, address: Long, mode: Long, base: Long) {
  return when (mode) {
    in 0L..1L -> code[address] = value
    2L -> code[base + address] = value
    else -> throw java.lang.IllegalArgumentException("Illegal parameter mode $mode")
  }
}

fun getValue(code: MutableMap<Long, Long>, value: Long, mode: Long, base: Long): Long {
  return when (mode) {
    0L -> code[value] ?: 0
    1L -> value
    2L -> code[base + value] ?: 0
    else -> throw IllegalArgumentException("Illegal parameter mode $mode")
  }
}

suspend fun executeBigProgramAsync(orig: Map<Long, Long>, input: ReceiveChannel<Long>, output: SendChannel<Long>) {
  val code = orig.toMutableMap()
  var relativeBase = 0L
  var pointer = 0L
  loop@ while (true) {
    val current = code[pointer] ?: throw IllegalArgumentException("Invalid instruction encountered")
    val startPointer = pointer
    val mode3 = current / 10_000 % 10
    val mode2 = current / 1_000 % 10
    val mode1 = current / 100 % 10
    val opcode = current % 100
    val operand1 = code[pointer + 1] ?: 0
    val operand2 = code[pointer + 2] ?: 0
    val operand3 = code[pointer + 3] ?: 0
    when (opcode) {
      1L -> setValue(
        code,
        getValue(code, operand1, mode1, relativeBase) + getValue(code, operand2, mode2, relativeBase),
        operand3,
        mode3, relativeBase
                    )
      2L -> setValue(
        code,
        getValue(code, operand1, mode1, relativeBase) * getValue(code, operand2, mode2, relativeBase),
        operand3,
        mode3, relativeBase
                    )
      3L -> setValue(code, input.receive(), operand1, mode1, relativeBase)
      4L -> output.send(getValue(code, operand1, mode1, relativeBase))
      5L -> if (getValue(code, operand1, mode1, relativeBase) != 0L) pointer =
        getValue(code, operand2, mode2, relativeBase)
      6L -> if (getValue(code, operand1, mode1, relativeBase) == 0L) pointer =
        getValue(code, operand2, mode2, relativeBase)
      7L -> setValue(
        code,
        boolToLong(
          getValue(code, operand1, mode1, relativeBase) < getValue(
            code,
            operand2,
            mode2,
            relativeBase
                                                                  )
                  ),
        operand3,
        mode3, relativeBase
                    )
      8L -> setValue(
        code,
        boolToLong(
          getValue(code, operand1, mode1, relativeBase) == getValue(
            code,
            operand2,
            mode2,
            relativeBase
                                                                   )
                  ),
        operand3,
        mode3, relativeBase
                    )
      9L -> relativeBase += getValue(code, operand1, mode1, relativeBase)

      99L -> break@loop
      else -> throw IllegalArgumentException("Invalid opcode ${code[pointer]}")
    }
    if (pointer == startPointer) {
      pointer += when (opcode) {
        in 1..2, in 7..8 -> 4
        in 3..4, 9L -> 2
        in 5..6 -> 3
        else -> throw IllegalArgumentException("Invalid step $opcode")
      }
    }
  }
}

fun loadLongAsyncCode() =
  readLine()!!.split(",").mapIndexed { index, it -> index.toLong() to it.toLong() }.toMap().toMutableMap()

suspend fun sendCharCommands(commands: String, channel: SendChannel<Long>) {
  delay(100)
  commands.toCharArray().forEach {
    channel.send(it.toLong())
    print(it)
  }
  channel.send(10L)
  println()
}

suspend fun startComputerWithCharOutput(
  code: MutableMap<Long, Long> = loadLongAsyncCode(),
  input: Channel<Long> = Channel(),
  output: Channel<Long> = Channel(UNLIMITED),
  afterStart: suspend (program: Deferred<Boolean>, input: SendChannel<Long>, output: ReceiveChannel<Long>) -> Unit
                                       ) = coroutineScope {
  val program = async {
    executeBigProgramAsync(code, input, output)
    output.close()
  }
  launch { runOutputListener(output) }
  afterStart(program, input, output)
}

fun CoroutineScope.startComputerWithProducerInput(
  code: MutableMap<Long, Long> = loadLongAsyncCode(),
  input: ReceiveChannel<Long>,
  output: Channel<Long> = Channel(UNLIMITED),
  afterStart: (program: Deferred<Boolean>, output: ReceiveChannel<Long>) -> Unit
                                                 ) {

  val program = async {
    executeBigProgramAsync(code, input, output)
    input.cancel()
    output.close()
  }
  afterStart(program, output)
}

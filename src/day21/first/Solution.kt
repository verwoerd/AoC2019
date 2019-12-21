package day21.first

import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.runBlocking
import tools.sendCharCommands
import tools.startComputerWithCharOutput
import tools.timeSolution

fun main() = timeSolution {
  runBlocking {
    startComputerWithCharOutput { program, input, _ ->
      sendClauses(input)
      sendCharCommands("WALK", input)
      program.join()
    }
  }
}

suspend fun sendClauses(input: SendChannel<Long>) {
  // hole ahead in range 3
  sendCharCommands("NOT C J", input)
  sendCharCommands("NOT B T", input)
  sendCharCommands("OR T J", input)
  sendCharCommands("NOT A T", input)
  sendCharCommands("OR T J", input)
  // landing spot is clear
  sendCharCommands("AND D J", input)
}

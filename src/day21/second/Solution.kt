package day21.second

import day21.first.sendClauses
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.runBlocking
import tools.sendCharCommands
import tools.startComputerWithCharOutput
import tools.timeSolution

fun main() = timeSolution {
  runBlocking {
    startComputerWithCharOutput { program, input, _ ->
      sendClauses(input)
      sendRunningClauses(input)
      sendCharCommands("RUN", input)
      program.join()
    }
  }
}

suspend fun sendRunningClauses(input: SendChannel<Long>) {
  // There is a follow up path EF or E jump to I
  sendCharCommands("OR F T", input)
  sendCharCommands("OR I T", input)
  sendCharCommands("AND E T", input)
  // or a save jump From D to H
  sendCharCommands("OR H T", input)
  sendCharCommands("AND T J", input)
}

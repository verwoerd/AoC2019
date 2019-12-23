package day23.first

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tools.FinishedSignal
import tools.loadLongAsyncCode
import tools.startComputerWithProducerInput
import tools.timeSolution

fun main() = timeSolution {
  val code = loadLongAsyncCode()
  runBlocking {
    val jobs = mutableListOf<Job>()
    repeat(50) { number ->
      messages[number.toLong()] = mutableListOf()
      startComputerWithProducerInput(code, inputRouter(number.toLong())) { program, output ->
        launch { outputRouter(number.toLong(), output) }
        jobs.add(program)
      }
    }
    jobs.joinAll()

  }
}

val messages = mutableMapOf<Long, MutableList<Package>>()

suspend fun outputRouter(number: Long, channel: ReceiveChannel<Long>) {
  while (!channel.isClosedForReceive) {
    val address = channel.receive()
    val x = channel.receive()
    val y = channel.receive()
    println("[$number] received $address ($x,$y)")
    if (address == 255L) {
      throw FinishedSignal()
    }
    messages.getValue(address).add(Package(address, x, y))
  }
}

data class Package(val address: Long, val x: Long, val y: Long)

fun CoroutineScope.inputRouter(number: Long) =
  produce {
    send(number)
    loop@ while (isActive) {
      val pack = when {
        messages.getValue(number).isNotEmpty() -> messages.getValue(number).removeAt(0).also { println("[$number] send $it") }
        else -> {
          send(-1L)
          continue@loop
        }
      }
      send(pack.x)
      send(pack.y)
    }
  }


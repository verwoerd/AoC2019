package day23.second

import day23.first.Package
import day23.first.inputRouter
import day23.first.messages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tools.loadLongAsyncCode
import tools.startComputerWithProducerInput
import tools.timeSolution

fun main() = timeSolution {
  val code = loadLongAsyncCode()
  runBlocking {

    repeat(50) { number ->
      messages[number.toLong()] = mutableListOf()
      startComputerWithProducerInput(code, inputRouter(number.toLong())) { program, output ->
        launch { outputRouter(number.toLong(), output) }
      }
    }
    val nat = launch { natRouter() }
    nat.join()
    cancel()

  }
}

var natPackage = Package(-1, 0, 0)

suspend fun outputRouter(number: Long, channel: ReceiveChannel<Long>) {
  while (!channel.isClosedForReceive) {
    val address = channel.receive()
    val x = channel.receive()
    val y = channel.receive()
    println("[$number] received $address ($x,$y)")
    if (address == 255L) {
      natPackage = Package(255, x, y)
    } else {
      messages.getValue(address).add(Package(address, x, y))
    }
  }
}

val seenY = mutableSetOf<Long>()

suspend fun CoroutineScope.natRouter() {
  var finished = false
  while (natPackage.address != 255L) {
    delay(10)
  }
  while (!finished && isActive) {
    if (messages.values.all { it.isEmpty() }) {
      println("[NAT] Network is idle")
      messages.getValue(0).add(natPackage)
      if (!seenY.add(natPackage.y)) {
        println("[Solution: $natPackage")
        finished = true
      }
    }
    delay(5)
  }
}

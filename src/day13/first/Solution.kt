package day13.first

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tools.Coordinate
import tools.executeBigProgramAsync
import tools.timeSolution

fun main() = timeSolution {
  val code = readLine()!!.split(",").mapIndexed { index, s -> index.toLong() to s.toLong() }.toMap()
  val input = Channel<Long>(UNLIMITED)
  val output = Channel<Long>(UNLIMITED)
  val locations = runBlocking {
    val program = launch {
      executeBigProgramAsync(code, input, output)
    }
    arcadeCabinet(program, output)
  }
  println(locations.count { it.value == 2 })
}

suspend fun arcadeCabinet(
  program: Job,
  channel: ReceiveChannel<Long>
                         ): MutableMap<Coordinate, Int> {
  val locations = mutableMapOf<Coordinate, Int>()
  while (program.isActive || !channel.isEmpty) {
    val x = channel.receive().toInt()
    val y = channel.receive().toInt()
    val type = channel.receive().toInt()
    locations[Coordinate(x, y)] = type
  }
  return locations
}

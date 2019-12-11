package day11.second
import day11.first.runRobot
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tools.Coordinate
import tools.executeBigProgramAsync
import tools.timeSolution

fun main() = timeSolution {
  val code = readLine()!!.split(",").mapIndexed { index, it -> index.toLong() to it.toLong() }.toMap()
  val outputChannel = Channel<Long>(Channel.UNLIMITED)
  val inputChannel = Channel<Long>(Channel.UNLIMITED)
  var area = mutableMapOf<Coordinate, Long>()

  runBlocking {
    val program = launch {
      executeBigProgramAsync(code, inputChannel, outputChannel)
    }
    program.start()
    inputChannel.send(1L)
    area = runRobot(program, inputChannel, outputChannel)
  }

  val startX = area.keys.minBy { it.x }!!.x
  val endX = area.keys.maxBy { it.x }!!.x
  val startY = area.keys.minBy { it.y }!!.y
  val endY = area.keys.maxBy { it.y }!!.y
  for (y in startY..endY) {
    for (x in startX..endX)
      print(when (area[Coordinate(x,y)] ?: 0L) {
              1L -> 'X'
              else -> ' '
            })
    println()
  }
}

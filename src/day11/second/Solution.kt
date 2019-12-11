import day11.first.Direction
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
  val area = mutableMapOf<Coordinate, Long>()
  var currentDirection = Direction.UP
  var current = Coordinate(0, 0)
  val path = mutableListOf<Coordinate>()
  runBlocking {
    val program = launch {
      executeBigProgramAsync(code, inputChannel, outputChannel)
    }
    program.start()

    inputChannel.send(1L)
    while (program.isActive) {

      val paint = outputChannel.receive()
      val direction = outputChannel.receive()
      area[current] = paint
      path.add(current)
      val result = when (direction) {
        0L -> currentDirection.turnLeft(current)
        1L -> currentDirection.turnRight(current)
        else -> throw IllegalArgumentException("Invalid direction recieved")
      }
      current = result.first
      currentDirection = result.second
      inputChannel.send(area[current]?: 0L)
    }
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

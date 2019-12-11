package day11.first

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tools.Coordinate
import tools.executeBigProgramAsync
import tools.timeSolution

fun main() = timeSolution {
  val code = readLine()!!.split(",").mapIndexed { index, it -> index.toLong() to it.toLong() }.toMap()
  val outputChannel = Channel<Long>(UNLIMITED)
  val inputChannel = Channel<Long>(UNLIMITED)
  var area: Map<Coordinate, Long> = mutableMapOf()
  runBlocking {
    val program = launch {
      executeBigProgramAsync(code, inputChannel, outputChannel)
    }
    program.start()
    inputChannel.send(0L)
    area =runRobot(program, inputChannel, outputChannel)
  }
  println(area.keys.size)
}

suspend fun runRobot(
  program: Job,
  inputChannel: Channel<Long>,
  outputChannel: Channel<Long>
                    ): MutableMap<Coordinate, Long> {
  val area = mutableMapOf<Coordinate, Long>()
  var currentDirection = Direction.UP
  var current = Coordinate(0, 0)
  while (program.isActive) {
    val paint = outputChannel.receive()
    val direction = outputChannel.receive()
    area[current] = paint
    val result = when (direction) {
      0L -> currentDirection.turnLeft(current)
      1L -> currentDirection.turnRight(current)
      else -> throw IllegalArgumentException("Invalid direction recieved")
    }
    current = result.first
    currentDirection = result.second
    inputChannel.send(area[current]?: 0L)
  }
  return area
}


enum class Direction {
  UP, DOWN, LEFT, RIGHT;

  fun turnLeft(location: Coordinate): Pair<Coordinate, Direction> = when (this) {
    UP -> Coordinate(location.x - 1, location.y) to LEFT
    DOWN -> Coordinate(location.x + 1, location.y) to RIGHT
    LEFT -> Coordinate(location.x, location.y + 1) to DOWN
    RIGHT -> Coordinate(location.x, location.y - 1) to UP
  }

  fun turnRight(location: Coordinate): Pair<Coordinate, Direction> = when (this) {
    UP -> Coordinate(location.x + 1, location.y) to RIGHT
    DOWN -> Coordinate(location.x - 1, location.y) to LEFT
    LEFT -> Coordinate(location.x, location.y - 1) to UP
    RIGHT -> Coordinate(location.x, location.y + 1) to DOWN
  }
}

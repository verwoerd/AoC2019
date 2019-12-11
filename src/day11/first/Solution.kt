package day11.first

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
  val area = mutableMapOf<Coordinate, Long>()
  var currentDirection = Direction.UP
  var current = Coordinate(0, 0)
  val path = mutableListOf<Coordinate>()
  runBlocking {
    val program = launch {
      executeBigProgramAsync(code, inputChannel, outputChannel)
    }
    program.start()

    inputChannel.send(0L)
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
  println(area.keys.size)
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

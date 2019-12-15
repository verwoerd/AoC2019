package day15.first

import day15.first.Direction.EAST
import day15.first.Direction.NORTH
import day15.first.Direction.SOUTH
import day15.first.Direction.WEST
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking
import tools.Coordinate
import tools.executeBigProgramAsync
import tools.timeSolution
import tools.xRange
import tools.yRange
import java.util.PriorityQueue
import java.util.SortedMap
import java.util.Stack

val coordinates = sortedMapOf<Coordinate, Boolean>()
var currentCoordinate = Coordinate(0, 0)
var endLocation = Coordinate(0, 0)
val stack = Stack<Direction>()
val path = Stack<Direction>()
val seen = mutableSetOf(currentCoordinate)

fun main() = timeSolution {
  val code = readLine()!!.split(",").mapIndexed { index, it -> index.toLong() to it.toLong() }.toMap()
  generateMaze(code)
  println(findPath())
}

fun generateMaze(code: Map<Long, Long>) {
  val output = Channel<Long>()
  runBlocking {
    val input = searchMaze(output)
    val program = async {
      executeBigProgramAsync(code, input, output)
      output.close()
    }
    program.join()
  }
}

fun CoroutineScope.searchMaze(channel: ReceiveChannel<Long>) = produce {
  coordinates[currentCoordinate] = true
  // bootstrap to not end up in a wall the first move
  Direction.values().asSequence().first {
    send(it.move)
    when (channel.receive()) {
      0L -> markWall(it).run { false }
      1L -> executeMove(it).run { true }
      2L -> executeMove(it).also { end -> endLocation = end }.run { true }
      else -> error("Invalid response")
    }
  }
  loop@ while (!stack.isEmpty()) {
    val currentDirection = stack.pop()
    send(currentDirection.move)
    when (channel.receive()) {
      0L -> markWall(currentDirection)
      1L -> executeMove(currentDirection)
      2L -> executeMove(currentDirection).also { endLocation = it }
      else -> error("Invalid response")
    }
  }
  // let the program terminate gracefully by sending the correct opcode to finish
  send(99L)
  coordinates.renderMaze()
  println("EXPLORATION FINISHED")
}

fun SortedMap<Coordinate, Boolean>.renderMaze() {
  val (minY, maxY) = yRange()
  val (minX, maxX) = xRange()
  for (y in maxY downTo minY) {
    for (x in minX..maxX) {
      print(
        when {
          endLocation.x == x && endLocation.y == y -> "S"
          currentCoordinate.x == x && currentCoordinate.y == y -> "D"
          !containsKey(Coordinate(x, y)) -> "?"
          else -> when (getValue(Coordinate(x, y))) {
            true -> "."
            else -> "#"
          }
        }
           )
    }
    println()
  }
}

enum class Direction(val move: Long, val diff: Coordinate, val back: Long) {
  NORTH(1, Coordinate(0, 1), 2),
  SOUTH(2, Coordinate(0, -1), 1),
  WEST(3, Coordinate(-1, 0), 4),
  EAST(4, Coordinate(1, 0), 3);

  fun moveFromCoordinate(coordinate: Coordinate) = Coordinate(coordinate.x + diff.x, coordinate.y + diff.y)
  fun returnDirection() = directionFromNumber(back)
}

fun directionFromNumber(number: Long) = when (number) {
  1L -> NORTH
  2L -> SOUTH
  3L -> WEST
  4L -> EAST
  else -> error("Invalid direction")
}


fun executeMove(direction: Direction): Coordinate {
  path.add(direction)
  currentCoordinate = direction.moveFromCoordinate(currentCoordinate)
  if (!seen.contains(currentCoordinate)) {
    // we are not backtracking adding new directions
    coordinates[currentCoordinate] = true
    stack.add(direction.returnDirection())
    stack.addAll(Direction.values().filter { !seen.contains(it.moveFromCoordinate(currentCoordinate)) })
    seen.add(currentCoordinate)
  }
  return currentCoordinate
}

fun markWall(direction: Direction) {
  val nextCoordinate = direction.moveFromCoordinate(currentCoordinate)
  seen.add(nextCoordinate)
  coordinates[nextCoordinate] = false
}

fun findPath(): Long {
  val queue = PriorityQueue<Pair<Long, Coordinate>>(Comparator { (l1), (l2) -> l1.compareTo(l2) })
  queue.add(0L to Coordinate(0, 0))
  val seen = mutableSetOf(Coordinate(0, 0))
  while (queue.isNotEmpty()) {
    val (length, coordinate) = queue.poll()
    when (coordinate) {
      endLocation -> return length
    }
    val next = Direction.values().map { it.moveFromCoordinate(coordinate) }.filter { it !in seen }
      .filter { coordinates[it] ?: false }
      .map { length + 1 to it }
    seen.addAll(next.map { it.second })
    queue.addAll(next)
  }
  error("No path found")
}

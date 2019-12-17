package day17.second

import day17.first.readMaze
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.RENDEZVOUS
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tools.Coordinate
import tools.executeBigProgramAsync
import tools.loadLongAsyncCode
import tools.timeSolution

fun main() = timeSolution {
  val code = loadLongAsyncCode().withDefault { 0L }
  code[0L] = 2L
  runBlocking {
    val input = Channel<Long>(RENDEZVOUS)
    val output = Channel<Long>(UNLIMITED)
    val resultProvider = async { readMaze(output) }
    val program = async {
      executeBigProgramAsync(code, input, output)
      output.close()
    }

    val result = resultProvider.await().toMutableMap().withDefault { '.' }

    val outputListener = async { runOutputListener(output) }
    launch { runFindYourPath(result, input) }
    program.await()
    outputListener.await()
  }

}


suspend fun runOutputListener(channel: ReceiveChannel<Long>) {
  while (!channel.isClosedForReceive) {
    print(
      when (val output = channel.receive()) {
        in 0..255 -> output.toChar()
        else -> output
      }
         )
  }
}

fun pathToAllPlaces(map: MutableMap<Coordinate, Char>): MutableList<Long> {
  val (coordinate, direction) = map.findRobotLocation()
  var currentCoordinate = coordinate
  var robot = direction
  val path = mutableListOf<Long>()
  while (true) {
    val (nextDirection) = map.findPossibleDirections(currentCoordinate).firstOrNull {
      it.first.differentAxis(robot)
    } ?: break
    path.add(robot.turnTo(nextDirection).toLong())
    path.add(','.toLong())  // How can I be so stupid, excluding this made run in circles for hours
    val (next, moves) = map.distanceToCorner(currentCoordinate, nextDirection)
    when (moves) {
      in 10L..Long.MAX_VALUE -> {
        path.add('1'.toLong())
        path.add('0'.toLong() + (moves % 10))
      }
      else -> path.add('0'.toLong() + moves)
    }
    map[currentCoordinate] = '#'
    map[next] = nextDirection.symbol
    currentCoordinate = next
    robot = nextDirection
    path.add(','.toLong())
  }
  return path
}

suspend fun runFindYourPath(map: MutableMap<Coordinate, Char>, channel: SendChannel<Long>) {
  val path = pathToAllPlaces(map)

  val subPaths = path.indices.flatMap { index ->
    (20 downTo 2).map { path.drop(index).take(it) to index }
  }.groupBy { it.first }.mapValues { entry -> entry.value.map { it.second }.toSet() }.filter { it.value.size > 1 }
    .toMutableMap()

  val result = subPaths.filter { 0 in it.value }.flatMap { (aSegment, aIndices) ->
    subPaths.filter { it.value != aSegment }.flatMap { (bSegment, bIndices) ->
      subPaths.filter { it != aSegment && it != bSegment }.mapNotNull { (cSegment, cIndices) ->
        val (found, result) = constructSolution(path, aIndices, aSegment, bIndices, bSegment, cIndices, cSegment)
        when {
          found -> {
            result.flatMap { listOf(it, 44L) }.dropLast(1) to Triple(
              aSegment.dropLast(1),
              bSegment.dropLast(1),
              cSegment.dropLast(1)
                                                                    )
          }
          else -> null
        }
      }
    }
  }

  val (command, functions) = result.minBy { it.first.size }!!
  val (a, b, c) = functions

  sendCommands(command, channel)
  sendCommands(a, channel)
  sendCommands(b, channel)
  sendCommands(c, channel)
  channel.send('n'.toLong())
  channel.send(10L)
  println("n")
}

suspend fun sendCommands(commands: Collection<Long>, channel: SendChannel<Long>) {
  delay(100)
  commands.forEach {
    channel.send(it)
    print(it.toChar())
  }
  channel.send(10L)
  println()
}

private fun Map<Coordinate, Char>.distanceToCorner(
  coordinate: Coordinate,
  nextDirection: Direction
                                                  ): Pair<Coordinate, Long> {
  var i = 0
  var finger = nextDirection.moveFromCoordinate(coordinate)
  while (getValue(finger) == '#') {
    finger = nextDirection.moveFromCoordinate(finger)
    i++
  }
  return nextDirection.moveBack(finger) to i.toLong()
}

private fun Map<Coordinate, Char>.findPossibleDirections(coordinate: Coordinate) =
  Direction.values().map { it to it.moveFromCoordinate(coordinate) }.filter { (_, coordinate) -> getValue(coordinate) == '#' }

private fun Map<Coordinate, Char>.findRobotLocation() =
  this.filter { (_, it) -> it == '>' || it == '<' || it == '^' || it == 'v' }
    .map { (coordinate, value) -> coordinate to value.toDirection() }.first()

enum class Direction(val move: Long, val diff: Coordinate, val back: Long, val symbol: Char) {
  UP(1, Coordinate(0, 1), 2, 'v'),
  DOWN(2, Coordinate(0, -1), 1, '^'),
  LEFT(3, Coordinate(-1, 0), 4, '<'),
  RIGHT(4, Coordinate(1, 0), 3, '>');

  fun moveFromCoordinate(coordinate: Coordinate) = Coordinate(coordinate.x + diff.x, coordinate.y + diff.y)
  fun moveBack(coordinate: Coordinate) = Coordinate(coordinate.x - diff.x, coordinate.y - diff.y)

  fun turnTo(nextDirection: Direction) = when (this) {
    UP -> when (nextDirection) {
      UP -> error("We are already facint this direction")
      DOWN -> error("we need two moves to do this")
      LEFT -> 'R'
      RIGHT -> 'L'
    }
    DOWN -> when (nextDirection) {
      UP -> error("we need two moves to do this")
      DOWN -> error("We are already facint this direction")
      LEFT -> 'L'
      RIGHT -> 'R'
    }
    LEFT -> when (nextDirection) {
      UP -> 'L'
      DOWN -> 'R'
      LEFT -> error("We are already facint this direction")
      RIGHT -> error("we need two moves to do this")
    }
    RIGHT -> when (nextDirection) {
      UP -> 'R'
      DOWN -> 'L'
      LEFT -> error("we need two moves to do this")
      RIGHT -> error("We are already facint this direction")
    }
  }

  fun differentAxis(robot: Direction): Boolean = when (this) {
    UP, DOWN -> robot != UP && robot != DOWN
    else -> robot != RIGHT && robot != LEFT
  }

}

fun Char.toDirection() = Direction.values().first { it.symbol == this }

fun constructSolution(
  path: MutableList<Long>,
  aIndices: Set<Int>,
  aSegment: List<Long>,
  bIndices: Set<Int>,
  bSegment: List<Long>,
  cIndices: Set<Int>,
  cSegment: List<Long>
                     ): Pair<Boolean, MutableList<Long>> =
  constructSubSolution(path, aIndices, aSegment, bIndices, bSegment, cIndices, cSegment, 0, mutableListOf())

fun constructSubSolution(
  path: MutableList<Long>,
  aIndices: Set<Int>,
  aSegment: List<Long>,
  bIndices: Set<Int>,
  bSegment: List<Long>,
  cIndices: Set<Int>,
  cSegment: List<Long>,
  index: Int,
  command: MutableList<Long>
                        ): Pair<Boolean, MutableList<Long>> {
  if (index == path.size) {
    return true to command
  } else if (index < path.size) {
    if (aIndices.contains(index)) {
      val (result, commandResult) = constructSubSolution(
        path,
        aIndices,
        aSegment,
        bIndices,
        bSegment,
        cIndices,
        cSegment,
        index + aSegment.size,
        command.toMutableList().also { it.add('A'.toLong()) })
      if (result) {
        return result to commandResult
      }
    }
    if (bIndices.contains(index)) {
      val (result, commandResult) = constructSubSolution(
        path,
        aIndices,
        aSegment,
        bIndices,
        bSegment,
        cIndices,
        cSegment,
        index + bSegment.size,
        command.toMutableList().also { it.add('B'.toLong()) })
      if (result) {
        return result to commandResult
      }
    }
    if (cIndices.contains(index)) {
      return constructSubSolution(
        path,
        aIndices,
        aSegment,
        bIndices,
        bSegment,
        cIndices,
        cSegment,
        index + cSegment.size,
        command.toMutableList().also { it.add('C'.toLong()) })
    }

  }
  return false to command
}

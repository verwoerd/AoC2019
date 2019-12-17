package day17.first

import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.runBlocking
import tools.Coordinate
import tools.executeBigProgramAsync
import tools.loadLongAsyncCode
import tools.timeSolution
import tools.xRange
import tools.yRange

fun main() = timeSolution {
  val code = loadLongAsyncCode()
  runBlocking {
    val input = Channel<Long>()
    val output = Channel<Long>(Channel.UNLIMITED)
    val resultProvider = async { readMaze(output) }
    val program = async {
      executeBigProgramAsync(code, input, output)
    }
    program.await()
    val result = resultProvider.await()
    result.render()
    println(calculateIntersections(result).map { (coordinate, _) -> coordinate.x * coordinate.y }.sum())
  }
}

fun <V> Map<Coordinate, V>.render() {
  val (yMin, yMax) = this.yRange()
  val (xMin, xMax) = this.xRange()
  for (y in yMin..yMax) {
    for (x in xMin..xMax) {
      print(this[Coordinate(x, y)] ?: '?')
    }
    println()
  }
}

suspend fun readMaze(channel: ReceiveChannel<Long>): Map<Coordinate, Char> {
  var y = 0
  var x = 0
  val result = mutableMapOf<Coordinate, Char>()
  loop@ while (!channel.isClosedForReceive) {
    when (val input = channel.receive()) {
      10L -> {
        if (x == 0) {
          break@loop
        }
        y++
        x = 0
      }
      else -> {
        result[Coordinate(x, y)] = input.toChar()
        x++
      }
    }
    if (channel.isEmpty) {
      break@loop
    }
  }
  return result
}

fun calculateIntersections(result: Map<Coordinate, Char>) =
  result.filter { (_, value) -> value == '#' }
    .filter { (coordinate, _) ->
      result[Coordinate(coordinate.x - 1, coordinate.y)] == '#' &&
          result[Coordinate(coordinate.x + 1, coordinate.y)] == '#' &&
          result[Coordinate(coordinate.x, coordinate.y + 1)] == '#' &&
          result[Coordinate(coordinate.x, coordinate.y - 1)] == '#'
    }



package day13.second

import day13.second.Tile.BALL
import day13.second.Tile.EMPTY
import day13.second.Tile.PADDLE
import day13.second.Tile.values
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import tools.Coordinate
import tools.executeBigProgramAsync
import tools.timeSolution
import kotlin.math.sign

fun main() = timeSolution {
  val code = readLine()!!.split(",").mapIndexed { index, s ->
    index.toLong() to when (index) {
      0 -> 2L
      else -> s.toLong()
    }
  }.toMap()

  val output = Channel<Long>(UNLIMITED)

  runBlocking {
    val input = playArcadeCabinet()
    val program = async {
      executeBigProgramAsync(code, input, output)
      output.close()
      // Biggest bug, program did not stop, waiting on an infinite loop.
      input.cancel()
    }
    launch { renderBoard(output) }
    program.join()
  }
  println("game over, Score: $score")
}

val locations = sortedMapOf<Coordinate, Tile>()
var score = -1L
var currentBallLocation = Coordinate(0, 0)
var currentPaddleLocation = Coordinate(0, 0)
val mutex = Mutex()

suspend fun renderBoard(channel: ReceiveChannel<Long>) {
  while (!channel.isClosedForReceive) {
    mutex.withLock {
      while (!channel.isEmpty && !channel.isClosedForReceive) {
        val x = channel.receive().toInt()
        val y = channel.receive().toInt()
        val type = channel.receive()
        when {
          x == -1 && y == 0 -> {
            score = type
          }
          else -> {
            val tile = tileFrom(type)
            val coordinate = Coordinate(x, y)
            when (tile) {
              BALL -> currentBallLocation = coordinate
              PADDLE -> currentPaddleLocation = coordinate
            }
            locations[Coordinate(x, y)] = tile
          }
        }
      }
    }
  }
}

fun CoroutineScope.playArcadeCabinet(): ReceiveChannel<Long> = produce {
  while (isActive) {
    mutex.withLock {
      // locations.render()
      send((currentBallLocation.x - currentPaddleLocation.x).sign.toLong())
    }
  }
}

private fun <V : Tile> Map<Coordinate, V>.render() {
  println("")
  val minY = keys.minBy { it.y }!!.y
  val minX = keys.minBy { it.x }!!.x
  val maxY = keys.maxBy { it.y }!!.y
  val maxX = keys.maxBy { it.x }!!.x
  for (y in minY..maxY) {
    for (x in minX..maxX) {
      print(this[Coordinate(x, y)] ?: EMPTY)
    }
    println()
  }
}

fun tileFrom(typeId: Long): Tile {
  return values().find { it.id == typeId }!!
}

enum class Tile(val id: Long, val char: Char) {
  EMPTY(0L, ' '), WALL(1L, '#'), BLOCK(2L, 'X'), PADDLE(3L, '='), BALL(4L, '*');

  override fun toString(): String = char.toString()
}

package day19.second

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tools.Coordinate
import tools.executeBigProgramAsync
import tools.loadLongAsyncCode
import tools.timeSolution
import tools.xIncrement

fun main() = timeSolution {
  val code = loadLongAsyncCode()
  val input = Channel<Long>()
  val output = Channel<Long>(Channel.UNLIMITED)
  // bootstrap the beam to check for delta's
  for (y in 0..10) {
    for (x in 0..10) {
      checkCoordinate(Coordinate(x, y), code, input, output)
    }
  }
  // calculate the end coordinate of the beam
  val endOfBeam = cache.filter { it.value == 1L }.map { it.key }.first { cache[it + xIncrement] == 1L }

  // the first y where beam is atleast 200 from the start
  val first = endOfBeam * 150
  // search in an area of 100 around it
  val last = first + endOfBeam * 100
  var startX = 0
  y@ for (y in first.y..last.y) {
    var beamFound = false
    x@ for (x in startX..last.x) {
      val check = checkCoordinate(Coordinate(x, y), code, input, output)
      when {
        check -> {
          println("Found Coordinate ($x,$y) ${10000 * x + y}")
          break@y
        }
        !beamFound && cache.getValue(Coordinate(x, y)) == 1L -> {
          beamFound = true
          startX = x
        }
        beamFound && cache.getValue(Coordinate(x, y)) == 0L -> continue@y
      }
    }
  }
}


val cache = mutableMapOf<Coordinate, Long>()

fun checkCoordinate(
  coordinate: Coordinate,
  code: MutableMap<Long, Long>,
  input: Channel<Long>,
  output: Channel<Long>
                   ) =
  // we start counting at 1 not at zero
  sequenceOf(coordinate, coordinate + 99, coordinate plusY 99, coordinate plusX 99).all {
    cache.computeIfAbsent(it) {
      runBlocking {
        launch {
          executeBigProgramAsync(code, input, output)
        }
        input.send(it.x.toLong())
        input.send(it.y.toLong())
        output.receive()
      }
    } == 1L
  }





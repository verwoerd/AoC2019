package day10.second

import day10.first.bestStationLocation
import day10.first.readAsteroidsMap
import tools.Coordinate
import tools.timeSolution
import java.util.concurrent.LinkedBlockingDeque
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

fun main() = timeSolution {
  val map = readAsteroidsMap()
  val stationLocation = bestStationLocation(map).second

  // Create a Linked Double Ended Queue, ordered by the relative angles and closes asteroids first as tie breaker
  val angleList = LinkedBlockingDeque(map.filter { stationLocation != it }
                                        .map {
                                          val xDiff = (it.x - stationLocation.x).toDouble()
                                          val yDiff = (it.y - stationLocation.y).toDouble()
                                          // Calculate the angle relative to up
                                          abs(atan2(xDiff, yDiff) - PI) to it
                                        }.sortedWith(Comparator { o1, o2 ->
      // sort by angle, then by closest x, then by closest y (relative to the stationLocation)
      when (val diff = o1.first.compareTo(o2.first)) {
        0 ->  when(val xDiff = (stationLocation.x - o1.second.x).compareTo(stationLocation.x - o2.second.x)) {
          0 -> (stationLocation.y - o1.second.y).compareTo(stationLocation.y - o2.second.y)
          else -> xDiff
        }
        else -> diff
      }
    }))

  var count = 0
  var lastAngle: Double = -0.1
  var lastCoordinate = Coordinate(0, 0)

  while (angleList.isNotEmpty() && count < 200) {
    val angle = angleList.removeFirst()
    when (angle.first) {
      // we just shot and asteroid at this angle already, move the turret first, so back of the line
      lastAngle -> angleList.add(angle)
      // shoot down the asteroid
      else -> {
        lastAngle = angle.first
        lastCoordinate = angle.second
        count++
      }
    }
  }
  println(lastCoordinate.x *100 + lastCoordinate.y)
}
